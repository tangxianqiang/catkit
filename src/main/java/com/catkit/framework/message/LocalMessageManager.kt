package com.catkit.framework.message

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable
import com.catkit.framework.CatkitApplication
import java.io.Serializable
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * The local message manager which is safe-data in apps. It uses LocalBroadcastManager to
 * handler the message.
 * <p>
 *     Note: It can not work on different progress.
 * </p>
 */
class LocalMessageManager {

    /**
     * The local message map that save the message which will be executed in future.
     * <p>
     *     It is worked on multi-thread, so HashTable is selected. And The Action which is
     *     to distinguish between with other message has one-to-many relationship with a
     *     lambda action. It means that we can use a message in many place. Besides, CopyOnWriteArrayList
     *     is need. It can used on multi-thread and more-read-less-write is need.
     * </p>
     * <p>
     *     ArrayList: not thread-safe, high efficiency.
     *     Vector: thread-safe(but need synchronized itself), low efficiency, high efficiency on mush data.
     *     CopyOnWriteArrayList： thread-safe(write-lock, not red-lock, use ReentrantLock), more-read-less-write
     *     is need, consistency(same data in every thread) in final but every time.
     * </p>
     */
    private val messageMap = Hashtable<String, CopyOnWriteArrayList<(Context, Intent) -> Unit>>()
    /**
     * The filter to response the message.
     */
    private val mIntentFilter: IntentFilter = IntentFilter()
    private var lbm: LocalBroadcastManager? = null

    companion object {
        private val mLock = Any()
        private lateinit var mInstance: LocalMessageManager

        fun getInstance(): LocalMessageManager {
            synchronized(mLock) {
                if (mInstance == null) {
                    mInstance = LocalMessageManager()
                }
                return mInstance
            }
        }
    }

    /**
     * Record the action that to response.
     */
    private fun initIntentFilter(actions: ArrayList<String>?) {
        actions?.forEach { action ->
            mIntentFilter.addAction(action)
        }
    }

    /**
     * Init the LocalBroadcastManager and register all the "receiver".
     * The filters[mIntentFilter] has a one-to-one relationship with receiver.
     */
    fun initMessageManager(actions: ArrayList<String>?) {
        initIntentFilter(actions)
        onDestroy()
        synchronized(this) {
            lbm = LocalBroadcastManager(CatkitApplication.appCtx)
        }
        lbm?.registerReceiver(this@LocalMessageManager::receive, mIntentFilter)
    }

    /**
     * All the response is done in this method. so it is called by LocalBroadcastManager's[LocalBroadcastManager.sendBroadcast] back.
     */
    private fun receive(context: Context, intent: Intent) {
        messageMap[intent.action]?.forEach {
            //likes call messageMap[intent.action][index](context,intent)
            it?.invoke(context, intent)
        }
    }

    /**
     * Register a receiver which perform likes a callback when send method called.
     * @param receiver it is called by LocalBroadcastManager's [receive] back.
     */
    @Synchronized
    fun register(action: String, receiver: (Context, Intent) -> Unit) {
        var list = messageMap[action]
        if (list == null) {
            list = CopyOnWriteArrayList()
            messageMap[action] = list
        }

        list.add(receiver)
    }

    @Synchronized
    fun unRegister(action: String, callback: (Context, Intent) -> Unit) = messageMap[action]?.remove(callback)

    fun send(action: String) {
        send(Intent(action))
    }

    fun send(action: String, key: String, value: Any?) {
        val intent = Intent(action)
        putExtraValue(intent, key, value)
        send(intent)
    }

    fun send(action: String, params: Map<String, Any?>) {
        val intent = Intent(action)
        params.forEach { (key, value) -> putExtraValue(intent, key, value) }
        send(intent)
    }

    private fun putExtraValue(intent: Intent, key: String, value: Any?) {
        when (value) {
            null -> intent.putExtra(key, null as Serializable?)

            is Boolean -> intent.putExtra(key, value)
            is BooleanArray -> intent.putExtra(key, value)

            is Byte -> intent.putExtra(key, value)
            is ByteArray -> intent.putExtra(key, value)

            is Short -> intent.putExtra(key, value)
            is ShortArray -> intent.putExtra(key, value)

            is Char -> intent.putExtra(key, value)
            is CharArray -> intent.putExtra(key, value)

            is Int -> intent.putExtra(key, value)
            is IntArray -> intent.putExtra(key, value)

            is Long -> intent.putExtra(key, value)
            is LongArray -> intent.putExtra(key, value)

            is Double -> intent.putExtra(key, value)
            is DoubleArray -> intent.putExtra(key, value)

            is Float -> intent.putExtra(key, value)
            is FloatArray -> intent.putExtra(key, value)

            is String -> intent.putExtra(key, value)
            is CharSequence -> intent.putExtra(key, value)

            is Bundle -> intent.putExtra(key, value)
            is Serializable -> intent.putExtra(key, value)
            is Parcelable -> intent.putExtra(key, value)

            is Array<*> -> when {
                value.isArrayOf<Parcelable>() -> intent.putExtra(key, value)
                value.isArrayOf<String>() -> intent.putExtra(key, value)
                value.isArrayOf<CharSequence>() -> intent.putExtra(key, value)
                else -> throw IllegalArgumentException("Intent extra $key has wrong type ${value.javaClass.name}")
            }

            else -> throw IllegalArgumentException("Intent extra $key has wrong type ${value.javaClass.name}")
        }
    }

    /**
     * Call send[LocalBroadcastManager.sendBroadcast]. To notify all the "receiver" which match the
     * intent to work. synchronized is not need.
     */
    fun send(intent: Intent) {
        synchronized(LocalMessageManager::class.java) {
            lbm?.sendBroadcast(intent)
        }
    }

    fun onDestroy() {
        messageMap.clear()
        synchronized(this) {
            lbm?.unregisterReceiver(this@LocalMessageManager::receive)
            lbm = null
        }
    }

}