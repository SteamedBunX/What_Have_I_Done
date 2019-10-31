package com.steamedbunx.android.whathaveidone.ui.main

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.steamedbunx.android.whathaveidone.MainActivity
import com.steamedbunx.android.whathaveidone.Util.CountUpTimer
import com.steamedbunx.android.whathaveidone.Util.UserPrefUtil
import com.steamedbunx.android.whathaveidone.dataModel.TaskModel

class MainViewModel(app: Application) : AndroidViewModel(app) {

    val prefUtil = UserPrefUtil.getInstence()

    val timer:CountUpTimer = CountUpTimer()

    private val  _currentTask: MutableLiveData<String> = MutableLiveData<String>("Nothing")
    val currentTask:LiveData<String>
        get() = _currentTask

    private val _currentTimeString: MutableLiveData<String> = MutableLiveData<String>("00:00:00")
    val currentTimeString:LiveData<String>
        get() = _currentTimeString

    init{
        val timerListner = object: CountUpTimer.OnTickListener{
            override fun onTick(timeString: String) {
                updateTimeString(timeString)
            }
        }
        timer.onTickListener = timerListner
    }



    fun loadLastTask(){
        val lastTask = prefUtil.getLastTask(getApplication<Application>().applicationContext)
        timer.startTime = lastTask.taskStartTime
        _currentTask.value = lastTask.taskName
    }

    fun changeTask(taskName: String): Boolean{
        if(taskName != currentTask.value
            && !taskName.isBlank()) {
            storeTaskToLog()
            _currentTask.value = taskName
            timer.reset()
            return true
        }
        return false
    }

    fun storeTaskToLog(){

    }

    fun updateTimeString(timeString: String){
        _currentTimeString.value = timeString
    }

    //region Listener

    //endregion

}
