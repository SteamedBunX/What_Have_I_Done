package com.steamedbunx.android.whathaveidone.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.steamedbunx.android.whathaveidone.Util.CountUpTimer

class MainViewModel : ViewModel() {
    val timer:CountUpTimer = CountUpTimer()

    init{
        val timerListner = object: CountUpTimer.OnTickListener{
            override fun onTick(timeString: String) {
                updateTimeString(timeString)
            }
        }
        timer.onTickListener = timerListner
    }

    private val  _currentTask: MutableLiveData<String> = MutableLiveData<String>("Nothing")
    val currentTask:LiveData<String>
        get() = _currentTask

    private val _currentTimeString: MutableLiveData<String> = MutableLiveData<String>("00:00:00")
    val currentTimeString:LiveData<String>
        get() = _currentTimeString

    fun loadLastTask(){

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
