package com.steamedbunx.android.whathaveidone.ui.main

import android.app.Application
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.steamedbunx.android.whathaveidone.database.TaskRecord
import com.steamedbunx.android.whathaveidone.util.CountUpTimer
import com.steamedbunx.android.whathaveidone.util.UserPrefUtil

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val prefUtil = UserPrefUtil.getInstance()

    val timer: CountUpTimer = CountUpTimer()

    private val _currentTask = MutableLiveData<String>()
    val currentTask: LiveData<String>
        get() = _currentTask

    private val _currentTimeString = MutableLiveData<String>("00:00:00")
    val currentTimeString: LiveData<String>
        get() = _currentTimeString

    private val _isBottomSheetVisible = MutableLiveData<Boolean>(false)
    val isBottomSheetVisible: LiveData<Boolean>
        get() = _isBottomSheetVisible

    private val _taskRecordList = MutableLiveData<List<TaskRecord>>()
    val taskRecordList:LiveData<List<TaskRecord>>
        get() = _taskRecordList

    init {
        val timerListner = object : CountUpTimer.OnTickListener {
            override fun onTick(timeString: String) {
                updateTimeString(timeString)
            }
        }
        timer.onTickListener = timerListner
        loadLastTask()
    }

    fun loadLastTask() {
        val lastTask = prefUtil.getLastTask(getApplication<Application>().applicationContext)
        timer.setNewStartTime(lastTask.taskTimeStart)
        _currentTask.value = lastTask.taskName
        updateTimeString(timer.getTimerString())
    }

    fun changeTask(taskName: String): Boolean {
        if (taskName != currentTask.value
            && !taskName.isBlank()
        ) {
            _currentTask.value = taskName
            timer.reset()
            updateTimeString(timer.getTimerString())
            storeTaskToLog()
            prefUtil.storeLastTask(getApplication(), taskName, timer.startTime)
            return true
        }
        return false
    }

    fun storeTaskToLog() {

    }

    fun updateTimeString(timeString: String) {
        _currentTimeString.value = timeString
    }

    //region Listener

    //endregion

    //region lifecycle
    fun onResume() {
        timer.onResume()
    }

    fun onPause() {
        timer.onPause()
    }
    //endregion

    fun setBottomSheetVisible() {
        if (_isBottomSheetVisible.value == false) {
            _isBottomSheetVisible.value = true
        }
    }

    fun setBottomSheetHidden() {
        if (_isBottomSheetVisible.value == true) {
            _isBottomSheetVisible.value = false
        }
    }


    fun createFakeList(){
        var taskTaskrecords = ArrayList<TaskRecord>()
        taskTaskrecords.add(TaskRecord(
            0,"tasl2",2343463L,142345
        ))
        taskTaskrecords.add(TaskRecord(
            1,"tasl1",2343463L,142345
        ))
        taskTaskrecords.add(TaskRecord(
            2,"tasl5",2343463L,142345
        ))
        taskTaskrecords.add(TaskRecord(
            3,"tasl3",2343463L,142345
        ))
        taskTaskrecords.add(TaskRecord(
            4,"tasl4",2343463L,142345
        ))
        _taskRecordList.value = taskTaskrecords
    }

}
