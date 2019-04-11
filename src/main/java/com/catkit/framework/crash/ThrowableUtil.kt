package com.catkit.framework.crash

import java.io.PrintWriter
import java.io.StringWriter
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

object ThrowableUtil {
    /**
     * char: \n--adapt windows and linux device
     */
    private val LINE_SEP: String?
        get() = System.getProperty("line.separator")

    /**
     * Get full error trace
     */
    fun getFullStackTrace(throwable: Throwable?): String {
        var e = throwable
        val throwables: ArrayList<Throwable> = ArrayList()
        while (e != null && !throwables.contains(e)) {
            throwables.add(e)
            e = e.cause
        }

        val throwableSize = throwables.size
        val allTraces: ArrayList<String> = ArrayList()
        var nextTraces: ArrayList<String> = getLineTraces(throwables[throwableSize - 1])
        var iterator = throwableSize - 1
        while (iterator >= 0) {
            val oldTrace = nextTraces
            if (iterator != 0) {
                nextTraces = getLineTraces(throwables[iterator - 1])
                removeSameTrace(oldTrace, nextTraces)
            }
            if (iterator == throwableSize - 1) {
                allTraces.add(throwables[iterator].toString())
            } else {
                allTraces.add("Caused By ${throwables[iterator]}")
            }
            allTraces.addAll(oldTrace)
            iterator--
        }
        val strBuilder = StringBuilder()
        allTraces.forEach {
            strBuilder.append(it).append(LINE_SEP)
        }
        return strBuilder.toString()

    }

    /**
     * Get every line trace
     */
    private fun getLineTraces(throwable: Throwable): ArrayList<String> {
        val allLineTraces: ArrayList<String> = ArrayList()
        val sw = StringWriter()
        val pw = PrintWriter(sw, true)
        throwable.printStackTrace(pw)
        val stackTrace = sw.toString()
        val lineTraces = StringTokenizer(stackTrace, LINE_SEP)
        var token: String
        var atTip: Int
        var traceStarted = false
        while (lineTraces.hasMoreTokens()) {
            token = lineTraces.nextToken()
            atTip = token.indexOf("at")
            if (atTip != -1 && token.substring(0, atTip).trim().isEmpty()) {
                traceStarted = true
                allLineTraces.add(token)
            } else if (traceStarted) {
                break
            }
        }
        return allLineTraces
    }

    /**
     * remove the same trace
     */
    private fun removeSameTrace(causeTrace: ArrayList<String>, wrapperTrace: ArrayList<String>) {
        var causeIndex = causeTrace.size - 1
        var wrapperIndex = wrapperTrace.size - 1
        var cause: String
        var wrapper: String
        while (causeIndex >= 0 && wrapperIndex >= 0) {
            cause = causeTrace[causeIndex]
            wrapper = wrapperTrace[wrapperIndex]
            if (cause == wrapper) {
                causeTrace.remove(cause)
            }
            causeIndex--
            wrapperIndex--
        }
    }
}