package com.steamedbunx.android.whathaveidone.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.steamedbunx.android.whathaveidone.TAG
import com.steamedbunx.android.whathaveidone.Util.CountUpTimer
import com.steamedbunx.android.whathaveidone.Util.UserPrefUtil

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val prefUtil = UserPrefUtil.getInstance()

    val timer:CountUpTimer = CountUpTimer()

    private val  _currentTask: MutableLiveData<String> = MutableLiveData<String>()
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
        loadLastTask()
    }

    fun loadLastTask(){
        val lastTask = prefUtil.getLastTask(getApplication<Application>().applicationContext)
        timer.setNewStartTime(lastTask.taskTimeStart)
        _currentTask.value = lastTask.taskName
        updateTimeString(timer.getTimerString())
    }

    fun changeTask(taskName: String): Boolean{
        if(taskName != currentTask.value
            && !taskName.isBlank()) {
            _currentTask.value = taskName
            timer.reset()
            updateTimeString(timer.getTimerString())
            storeTaskToLog()
            prefUtil.storeLastTask(getApplication(),taskName, timer.startTime)
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
