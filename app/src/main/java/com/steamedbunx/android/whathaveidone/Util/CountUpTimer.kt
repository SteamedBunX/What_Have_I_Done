package com.steamedbunx.android.whathaveidone.Util

import android.os.Handler
import com.steamedbunx.android.whathaveidone.Utils
import java.util.*


class CountUpTimer(var startTime: Date = Date()) {

    var onTickListener: OnTickListener? = null
    val utils = Utils.getInstence()

    init {
        startTime = startTime
    }

    // region runnable object
    // create
    val handler = Handler()
    val runnable: Runnable = object : Runnable {
        override fun run() {
            tick()
            handler.postDelayed(this, 1000)
        }
    }
    // endregion

    // region timer controls
    var time = 0
        private set

    fun start() {
        handler.postDelayed(runnable, 1000)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
    }

    fun tick() {
        time++
        onTickListener?.onTick(getTimerString())
    }

    fun reset() {
        startTime = Date()
        onTickListener?.onTick(getTimerString())
    }

    // When the program restart from the background
    // the timer will catch up to the correct time
    fun catchUp() {
        // my time is by the second, not by millisecond
        // also this casting is safe because this number will never be higher than
        // Int.Max which is 24855 days or 68 years
        time = (Date().time - startTime.time).toInt() / 1000
        start()
    }

    // endregion

    // region return informations

    fun getTimerString(): String {
        return utils.getTimerString(time)
    }

    // When moving to the next task, get the relatively precise time the task has
    // been running for
    fun getTotalRunTime(): Int {
        return ((Date().time - startTime.time) / 1000).toInt()
    }

    fun getStartDay(): Date {
        return startTime
    }
    // endregion

    interface OnTickListener {
        fun onTick(timeString: String)
    }
}