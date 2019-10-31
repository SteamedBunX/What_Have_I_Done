package com.steamedbunx.android.whathaveidone

import java.util.*

class Utils private constructor() {

    companion object {
        private val instance = Utils()

        fun getInstence(): Utils {
            return instance
        }
    }

    fun dateDidNotChange(startTime:Date):Boolean{
        val startingDay = Calendar.getInstance()
        startingDay.time = startTime
        val today = Calendar.getInstance()
        today.time = Date()
        return startingDay.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                && startingDay.get(Calendar.YEAR) == today.get(Calendar.YEAR)
    }

    fun getTimerString(time: Int): String {
        val hour: Int = time / 3600
        val hourString: String =
            if(hour >= 10)
            {
                (hour).toString()
            } else
            {
                "0$hour"
            }
        val minute: Int = time % 3600 / 60
        val minuteString: String =
            if (minute >= 10) {
                (minute).toString()
            } else {
                "0$minute"
            }
        val second = time % 60
        val secondString =
            if (second >= 10) {
                "$second"
            } else {
                "0$second"
            }
        return "$hourString:$minuteString:$secondString"
    }


}