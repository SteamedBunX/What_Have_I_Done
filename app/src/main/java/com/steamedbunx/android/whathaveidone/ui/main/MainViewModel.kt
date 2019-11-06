package com.steamedbunx.android.whathaveidone.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.steamedbunx.android.whathaveidone.RecordDisplayMode
import com.steamedbunx.android.whathaveidone.TAG
import com.steamedbunx.android.whathaveidone.database.TaskDatabaseDao
import com.steamedbunx.android.whathaveidone.database.TaskRecord
import com.steamedbunx.android.whathaveidone.util.CountUpTimer
import com.steamedbunx.android.whathaveidone.util.UserPrefUtil
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max


class MainViewModel(
    val database: TaskDatabaseDao,
    app: Application
) : AndroidViewModel(app) {


    //region coroutine

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //endregion


    private val prefUtil = UserPrefUtil.getInstance()
    val timer: CountUpTimer = CountUpTimer()
    var todayRecordSeparate = listOf(TaskRecord())
    var todayRecordCombined = listOf(TaskRecord())

    //region livedata

    private val _currentTask = MutableLiveData<String>()
    val currentTask: LiveData<String>
        get() = _currentTask

    private val _currentTimeString = MutableLiveData<String>("00:00:00")
    val currentTimeString: LiveData<String>
        get() = _currentTimeString

    private val _isBottomSheetVisible = MutableLiveData<Boolean>(false)
    val isBottomSheetVisible: LiveData<Boolean>
        get() = _isBottomSheetVisible

    private val _taskRecordDisplayMode =
        MutableLiveData<RecordDisplayMode>(RecordDisplayMode.SEPARATED_BY_TIME)
    val taskRecordDisplayMode: LiveData<RecordDisplayMode>
        get() = _taskRecordDisplayMode

    private val _taskRecordListForDisplay = MutableLiveData<List<TaskRecord>>()
    val taskRecordListForDisplay: LiveData<List<TaskRecord>>
        get() = _taskRecordListForDisplay

    //endregion

    //region Database

    fun getFirstSecondOfToday(): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = Date().time
        cal.set(Calendar.HOUR_OF_DAY, 0) //set hours to zero
        cal.set(Calendar.MINUTE, 0) // set minutes to zero
        cal.set(Calendar.SECOND, 0) //set seconds to zero
        return cal.timeInMillis
    }

    fun getLastSecondOfToday(): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = Date().time
        cal.set(Calendar.HOUR_OF_DAY, 0) //set hours to zero
        cal.set(Calendar.MINUTE, 0) // set minutes to zero
        cal.set(Calendar.SECOND, 0) //set seconds to zero
        cal.add(Calendar.DATE, 1)
        cal.add(Calendar.MILLISECOND, -1)
        return cal.timeInMillis
    }

    //endregion

    init {
        val timerListner = object : CountUpTimer.OnTickListener {
            override fun onTick(timeString: String) {
                updateTimeString(timeString)
            }
        }
        timer.onTickListener = timerListner
        loadLastTask()
        loadTodayRecord()
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
            storeTaskToLog()
            _currentTask.value = taskName
            timer.reset()
            updateTimeString(timer.getTimerString())
            prefUtil.storeLastTask(getApplication(), taskName, timer.startTime)
            return true
        }
        return false
    }

    fun storeTaskToLog() {
        if (currentTask.value != null && currentTask.value != "" && currentTask.value != "Nothing") {
            val name = currentTask.value ?: ""
            val date = timer.startTime.time
            val length = timer.getTotalRunTime()
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    database.insert(
                        TaskRecord(
                            name = name,
                            date = date,
                            length = length
                        )
                    )
                }
                updateTodayRecord()
            }
        }
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

    fun changeRecordDisplayMode() {
        when (taskRecordDisplayMode.value) {
            RecordDisplayMode.SEPARATED_BY_TIME ->
                _taskRecordDisplayMode.value = RecordDisplayMode.SEPARATED_BY_LENGTH
            RecordDisplayMode.SEPARATED_BY_LENGTH ->
                _taskRecordDisplayMode.value = RecordDisplayMode.COMBINED_BY_TIME
            RecordDisplayMode.COMBINED_BY_TIME ->
                _taskRecordDisplayMode.value = RecordDisplayMode.COMBINED_BY_LENGTH
            RecordDisplayMode.COMBINED_BY_LENGTH ->
                _taskRecordDisplayMode.value = RecordDisplayMode.SEPARATED_BY_TIME
        }
        updateRecordListDisplay()
    }

    fun updateRecordListDisplay() {
        when (taskRecordDisplayMode.value) {
            RecordDisplayMode.SEPARATED_BY_TIME -> loadListSeparatedByTime()
            RecordDisplayMode.SEPARATED_BY_LENGTH -> loadListSeperatedByLength()
            RecordDisplayMode.COMBINED_BY_TIME -> loadListCombinedByTime()
            RecordDisplayMode.COMBINED_BY_LENGTH -> loadListCombinedByLength()
        }
    }

    suspend fun getCombinedList(): List<TaskRecord> {
        val result = ArrayList<TaskRecord>()
        withContext(Dispatchers.IO) {
            todayRecordSeparate
                .forEach { item ->
                    val position = result.indexOfFirst { it.name == item.name }
                    if (position == -1) {
                        val newRecord = TaskRecord(item.id, item.name, item.date, item.length)
                        result.add(newRecord)
                    } else {
                        result[position].length = result[position].length + item.length
                        result[position].date = max(result[position].date, item.date)
                    }
                }
        }
        return result.toList()
    }

    fun loadTodayRecord() {
        uiScope.launch {
            updateTodayRecord()
        }
    }

    private suspend fun updateTodayRecord() {
        withContext(Dispatchers.IO) {
            todayRecordSeparate = database
                .getRecordWithinDateRange(getFirstSecondOfToday(), getLastSecondOfToday())
            todayRecordCombined = getCombinedList()
        }
    }

    fun loadListSeparatedByTime() {
        uiScope.launch {
            _taskRecordListForDisplay.value = todayRecordSeparate.sortedBy { it.date }
        }
    }

    fun loadListSeperatedByLength() {
        uiScope.launch {
            _taskRecordListForDisplay.value = todayRecordSeparate.sortedBy { it.length }
        }
    }

    fun loadListCombinedByTime() {
        uiScope.launch {
            _taskRecordListForDisplay.value = todayRecordCombined.sortedBy { it.date }
        }
    }

    fun loadListCombinedByLength() {
        uiScope.launch {
            _taskRecordListForDisplay.value = todayRecordCombined.sortedBy { it.length }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
