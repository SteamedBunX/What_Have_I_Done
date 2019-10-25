package com.steamedbunx.android.whathaveidone.Util

import android.os.Handler
import com.steamedbunx.android.whathaveidone.DataModel.TaskRecord
import java.util.*


class CountUpTimer(){
    lateinit var startTime: Date
    init{
        startTime = Date()
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

    fun start(){
        handler.postDelayed(runnable, 1000)
    }

    fun stop(){
        handler.removeCallbacks(runnable)
    }

    fun tick() {
        time++
    }

    // When the program restart from the background
    // the timer will catch up to the correct time
    fun catchUp(){
        // my time is by the second, not by millisecond
        // also this casting is safe because this number will never be higher than
        // Int.Max which is 24855 days or 68 years
        time = (Date().time - startTime.time).toInt()/1000
    }

    // endregion

    // region return informations
    fun getTimerString(): String {
        val minuteUntilFinished : String =
            if (time / 60 >= 10) {
                (time / 60).toString()
            } else {
                "0" + time / 60
            }
        val secondsInMinuteUntilFinished = time % 60
        return "$minuteUntilFinished : ${
        if (secondsInMinuteUntilFinished >= 10) {
            "$secondsInMinuteUntilFinished"
        } else {
            "0$secondsInMinuteUntilFinished"
        }}"
    }

    // When moving to the next task, get the relatively precise time the task has
    // been running for
    fun getTotalRunTime():Int{
        return (Date().time - startTime.time).toInt()/1000
    }

    fun getRecords(): List<TaskRecord>{
        val startingDay = Calendar.getInstance()
        startingDay.time = startTime
        val today = Calendar.getInstance()
        today.time = Date()
        val sameDay = startingDay.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                && startingDay.get(Calendar.YEAR) == today.get(Calendar.YEAR)

        if(sameDay){
            val returnList = ArrayList<TaskRecord>()

            return returnList
        }
        return emptyList()
    }
    // endregion
}