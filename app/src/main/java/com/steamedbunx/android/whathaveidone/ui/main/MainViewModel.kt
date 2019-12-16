package com.steamedbunx.android.whathaveidone.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.steamedbunx.android.whathaveidone.RecordDisplayMode
import com.steamedbunx.android.whathaveidone.Utils
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

    val utils = Utils.getInstence()
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
        val today = Date()
        return getFirstSecondOfDay(today)
    }

    private fun getFirstSecondOfDay(today: Date): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = today.time
        cal.set(Calendar.HOUR_OF_DAY, 0) //set hours to zero
        cal.set(Calendar.MINUTE, 0) // set minutes to zero
        cal.set(Calendar.SECOND, 0) //set seconds to zero
        cal.set(Calendar.MILLISECOND, 0) //set miliseconds to zero
        return cal.timeInMillis
    }

    fun getLastSecondOfToday(): Long {
        val today = Date()
        return getLastSecondOfDay(today)
    }

    private fun getLastSecondOfDay(today: Date): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = today.time
        cal.set(Calendar.HOUR_OF_DAY, 0) //set hours to zero
        cal.set(Calendar.MINUTE, 0) // set minutes to zero
        cal.set(Calendar.SECOND, 0) //set seconds to zero
        cal.set(Calendar.MILLISECOND, 0) //set miliseconds to zero
        cal.add(Calendar.DAY_OF_YEAR, 1)
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

    fun changeTask(inputTaskName: String): Boolean {
        val taskNameForStorage = utils.processStringForStorage(inputTaskName)
        if (taskNameForStorage != currentTask.value
            && !taskNameForStorage.isBlank()
        ) {
            storeTaskToLog()
            _currentTask.value = taskNameForStorage
            timer.reset()
            updateTimeString(timer.getTimerString())
            prefUtil.storeLastTask(getApplication(), taskNameForStorage, timer.startTime)
            return true
        }
        return false
    }

    fun storeTaskToLog() {
        if (currentTask.value != null &&
            currentTask.value != "" &&
            currentTask.value.toString().toLowerCase() != "nothing") {
            val name = currentTask.value ?: ""
            val startTime = timer.startTime
            val endTime = Date()

            if(isSameDay(startTime, endTime)){
                uiScope.launch {
                    withContext(Dispatchers.IO) {
                        val taskToStore =  getRecordBasedOnStartAndEndTime(name,startTime,endTime)
                        database.insert(taskToStore)
                    }
                    updateTodayRecord()
                }
            }else{
                uiScope.launch {
                    storeMultiDayTask(name,startTime,endTime)
                    updateTodayRecord()
                }
            }
        }
    }

    suspend fun storeMultiDayTask(name:String, startTime:Date, endTime: Date){
        val records = ArrayList<TaskRecord>()
        withContext(Dispatchers.IO){
            records.add(getRecordBasedOnStartAndEndTime(name, startTime.time, getLastSecondOfDay(startTime)))
            getDaysInBetween(startTime,endTime).forEach {
                records.add(getRecordBasedOnStartAndEndTime(
                    name,
                    getFirstSecondOfDay(Date(it)),
                    getLastSecondOfDay(Date(it))
                ))
            }
            records.add(getRecordBasedOnStartAndEndTime(name, getFirstSecondOfDay(endTime),endTime.time))
            storeMutiDayTaskIntoDatabase(records)
        }
    }

    fun getRecordBasedOnStartAndEndTime(name:String, startTime:Date, endTime: Date):TaskRecord{
        val length = ((endTime.time - startTime.time)/1000).toInt()
        return TaskRecord(name = name,date = startTime.time,length = length)
    }

    fun getRecordBasedOnStartAndEndTime(name:String, startTime:Long, endTime: Long):TaskRecord{
        val length = ((endTime - startTime)/1000).toInt()
        return TaskRecord(name = name,date = startTime,length = length)
    }

    suspend fun storeMutiDayTaskIntoDatabase(records:List<TaskRecord>){
        records.forEach {
            database.insert(it)
        }
    }

    fun isSameDay(date1: Date, date2: Date):Boolean{
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        return (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
    }

    fun getDaysInBetween(startDay: Date, endDay: Date): List<Long>{
        var result = ArrayList<Long>()
        val cal = Calendar.getInstance()
        cal.timeInMillis = startDay.time
        cal.add(Calendar.DAY_OF_YEAR, 1)
        while(!isSameDay(cal.time, endDay)){
            result.add(cal.time.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return result
    }

    fun updateTimeString(timeString: String) {
        _currentTimeString.value = timeString
    }

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

    fun setRecordDisplayMode(newMode: Int) {
        when (newMode) {
            1 ->
                _taskRecordDisplayMode.value = RecordDisplayMode.SEPARATED_BY_LENGTH
            2 ->
                _taskRecordDisplayMode.value = RecordDisplayMode.COMBINED_BY_TIME
            3 ->
                _taskRecordDisplayMode.value = RecordDisplayMode.COMBINED_BY_LENGTH
            else ->
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
            updateRecordListDisplay()
        }
    }

    private suspend fun updateTodayRecord() {
        withContext(Dispatchers.IO) {
            val startTime = getFirstSecondOfToday()
            val endTime = getLastSecondOfToday()
            todayRecordSeparate = database
                .getRecordWithinDateRange(startTime, endTime)
            // Ima leave this here in case i need to check database
//            database.getAllRecords().forEach{
//                Log.i(TAG, "name = ${it.name}, startTime = ${utils.getDateString(it.date)}, length = ${utils.getTimerString(it.length)}")
//            }
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
