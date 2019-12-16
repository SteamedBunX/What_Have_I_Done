package com.steamedbunx.android.whathaveidone

import android.icu.text.SimpleDateFormat
import android.os.Build
import java.lang.StringBuilder
import java.util.*

class Utils private constructor() {

    companion object {
        private val instance = Utils()

        fun getInstence(): Utils {
            return instance
        }
    }

    fun dateDidNotChange(startTime: Date): Boolean {
        val startingDay = Calendar.getInstance()
        startingDay.time = startTime
        val today = Calendar.getInstance()
        today.time = Date()
        return startingDay.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                && startingDay.get(Calendar.YEAR) == today.get(Calendar.YEAR)
    }

    fun getTimerString(time: Int): String {
        var _time = if (time < 0) {
            0
        } else {
            time
        }
        val hour: Int = _time / 3600
        val hourString: String =
            if (hour >= 10) {
                (hour).toString()
            } else {
                "0$hour"
            }
        val minute: Int = _time % 3600 / 60
        val minuteString: String =
            if (minute >= 10) {
                (minute).toString()
            } else {
                "0$minute"
            }
        val second = _time % 60
        val secondString =
            if (second >= 10) {
                "$second"
            } else {
                "0$second"
            }
        return "$hourString:$minuteString:$secondString"
    }

    fun getDateString(date: Long) :String{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val simpleDate = SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss")
            return simpleDate.format(Date(date))
        }
        return ""
    }

    fun processStringForStorage(inputString:String):String{
        return inputString
            .trim()
            .replace("\\s+".toRegex(), " ")
            .toLowerCase()
    }

    fun processStringForDisplay(inputString:String):String{
        var result:StringBuilder = StringBuilder()
        var startOfWord = true
        inputString.forEach {
            if(startOfWord){
                result.append(it.toUpperCase())
                startOfWord = false
            }else{
                result.append(it)
                if(it == ' '){
                    startOfWord = true
                }
            }
        }
        return result.toString()
    }

}