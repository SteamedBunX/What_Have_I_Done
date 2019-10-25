package com.steamedbunx.android.whathaveidone

import java.util.*

class Utils {

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



}