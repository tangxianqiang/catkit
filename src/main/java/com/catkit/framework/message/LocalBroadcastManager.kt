package com.catkit.framework.message

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Message

/**
 * Handler message inner app, it likes using Broadcast and BroadcastReceiver in own app.
 * But LocalBroadcastManager is more high-efficiency and more secure. There is a different with
 * android-support-LocalBroadcastManager that it uses callback rather than BroadcastReceiver.
 * <p>
 *     Note: It can not work on different progress.
 * </p>
 */
internal class LocalBroadcastManager(private val appCtx: Context) {
    private var mHandler: Handler
    private val mReceivers = HashMap<(Context, Intent) -> Unit, ArrayList<LocalBroadcastManager.ReceiverRecord>>()
    private val mActions = HashMap<String, ArrayList<ReceiverRecord>>()
    private val mPendingBroadcasts: ArrayList<LocalBroadcastManager.BroadcastRecord> = ArrayList()

    init {
        mHandler = object : Handler(appCtx.mainLooper) {
            override fun handleMessage(msg: Message?) {
                when (msg?.what) {
                    MSG_EXEC_PENDING_BROADCASTS -> {
                        executePendingBroadcasts()
                    }
                    else -> {
                        super.handleMessage(msg)
                    }
                }
            }
        }
    }

    companion object {
        const val MSG_EXEC_PENDING_BROADCASTS = 1
    }

    fun registerReceiver(receiver: (Context, Intent) -> Unit, filter: IntentFilter) {
        synchronized(mReceivers) {
            val entry = ReceiverRecord(filter, receiver)
            var filters = mReceivers[receiver]
            if (filters == null) {
                filters = ArrayList(1)
                mReceivers[receiver] = filters
            }
            filters.add(entry)

            for (i in 0 until filter.countActions()) {
                val action = filter.getAction(i)
                var entries = mActions[action]
                if (entries == null) {
                    entries = ArrayList(1)
                    mActions[action] = entries
                }
                entries.add(entry)
            }
        }
    }

    fun unregisterReceiver(receiver: (Context, Intent) -> Unit) {
        synchronized(mReceivers) {
            mReceivers.remove(receiver)?.forEach { filter ->
                filter.dead = true
                for (index in 0 until filter.filter.countActions()) {
                    filter.filter.getAction(index).also { action ->
                        mActions[action]?.run {
                            indices.reversed().filter {
                                this[it].receiver == receiver
                            }.forEach {
                                this.removeAt(it)
                            }
                            if (size <= 0) {
                                mActions.remove(action)
                            }
                        }

                    }
                }

            }
        }
    }

    /**
     * Send the message which is wrapped by intent. Every receiver has a instance of IntentFilter.
     * so if the intent has match the filter, receiver will be called back.
     * <p>
     *     filter match: match action -> match data -> match category.
     * </p>
     */
    fun sendBroadcast(intent: Intent): Boolean {
        synchronized(mReceivers) {
            val action = intent.action ?: "null"
            val type = intent.resolveTypeIfNeeded(appCtx.contentResolver)
            val data = intent.data
            val scheme = intent.scheme
            val categories = intent.categories

            val entries = mActions[action] ?: return false
            var receivers: ArrayList<ReceiverRecord>? = null
            for (receiver in entries) {
                if (receiver.broadcasting) {
                    continue
                }
                val match = receiver.filter.match(action, type, scheme, data, categories, "LocalBroadcastManager")
                if (match >= 0) {
                    if (receivers == null) {
                        receivers = ArrayList()
                    }
                    receivers.add(receiver)
                    receiver.broadcasting = true
                } else {

                }
            }

            if (receivers != null) {
                receivers.forEach {
                    it.broadcasting = false
                }
                mPendingBroadcasts.add(BroadcastRecord(intent, receivers))
                mHandler.run {
                    if (!hasMessages(MSG_EXEC_PENDING_BROADCASTS)) {
                        sendEmptyMessage(MSG_EXEC_PENDING_BROADCASTS)
                    }
                }
                return true
            }
        }
        return false
    }

    /**
     * Send message synchronous. Like {@link #sendBroadcast(Intent)}, but if there are any receivers for
     * the Intent this function will block and immediately dispatch them before returning.
     */
    fun sendBroadcastSync(intent: Intent) {
        if (sendBroadcast(intent)) { // true will return before mHandler running
            //so ,executePendingBroadcasts will execute in current thread
            executePendingBroadcasts()
        }
    }

    /**
     * Execute all the message: call back the lambda.
     */
    private fun executePendingBroadcasts() {
        while (true) {
            synchronized(mReceivers) {
                mPendingBroadcasts.run {
                    if (size > 0) {
                        toTypedArray().apply {
                            this@run.clear()
                        }
                    } else {
                        null
                    }
                }
            }?.forEach {
                it.receivers.forEach { rr ->
                    rr.receiver.invoke(appCtx, it.intent)
                }
            } ?: return
        }
    }

    /**
     * It is a static class that record receiver and  filter. Every receiver has a one-to-one relationship
     * with IntentFilter.
     */
    private class ReceiverRecord(val filter: IntentFilter, val receiver: (Context, Intent) -> Unit) {
        var broadcasting: Boolean = false
        var dead: Boolean = false

        override fun toString(): String {
            val sb = StringBuilder()
            sb.append("Receiver{")
                    .append(this.receiver)
                    .append("filter=")
                    .append(this.filter)
            if (dead) {
                sb.append("DEAD")
            }
            sb.append("}")
            return sb.toString()
        }
    }

    /**
     * Record ReceiverRecords and intent. so manager can use intent to notify all receiver to
     * call back.
     */
    private class BroadcastRecord(val intent: Intent, val receivers: ArrayList<ReceiverRecord>)
}