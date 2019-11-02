package com.steamedbunx.android.whathaveidone.util

import android.content.Context
import com.steamedbunx.android.whathaveidone.R
import com.steamedbunx.android.whathaveidone.dataModel.TaskModel
import java.util.*

class UserPrefUtil private constructor(){

    companion object {
        private val instance = UserPrefUtil()

        fun getInstance(): UserPrefUtil {
            return instance
        }
    }

    fun storeLastTask(context: Context?, taskName: String, timeStart: Date){
        val sharedPrefEditor = context
            ?.getSharedPreferences(context?.getString(R.string.pref_key_last_task)
                ,Context.MODE_PRIVATE)?.edit()
        with(sharedPrefEditor){
            this?.putString(context?.getString(R.string.current_task_name), taskName)
            this?.putLong(context?.getString(R.string.current_task_time_start), timeStart.time)
            this?.apply()
        }
    }

    fun getLastTask(context: Context?): TaskModel {
        val sharedPref = context
            ?.getSharedPreferences(context?.getString(R.string.pref_key_last_task)
                ,Context.MODE_PRIVATE)
        with(sharedPref){
            val taskName = this?.getString(context?.getString(R.string.current_task_name),"Nothing")
            val taskTimeStart = this?.getLong(context?.getString(R.string.current_task_time_start), Date().time)


            return TaskModel(taskName?: "Nothing" ,Date(taskTimeStart ?: Date().time) )
        }
    }
}