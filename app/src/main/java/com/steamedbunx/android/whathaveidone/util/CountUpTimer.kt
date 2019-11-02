package com.steamedbunx.android.whathaveidone.util

import android.os.Handler
import com.steamedbunx.android.whathaveidone.Utils
import java.util.*


class CountUpTimer(var startTime: Date = Date()) {

    var onTickListener: OnTickListener? = null
    private val utils = Utils.getInstence()

    // region runnable object
    // create
    val handler = Handler()
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            tick()
            handler.postDelayed(this, 1000)
        }
    }
    // endregion

    // region timer controls
    private var time = 0

    fun start() {
        stop()
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
        time = 0
        onTickListener?.onTick(getTimerString())
    }

    fun setNewStartTime(newStartTime:Date){
        startTime = newStartTime
        catchUp()
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

    // region get information
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

    // region lifecycle
    fun onResume(){
        catchUp()
        start()
    }

    fun onPause() {
        stop()
    }
    // endregion

    // region interface

    interface OnTickListener {
        fun onTick(timeString: String)
    }
    // endregion
}