package com.steamedbunx.android.whathaveidone.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.*

@Dao
interface TaskDatabaseDao {
    @Insert
    suspend fun insert(taskRecord: TaskRecord)

    @Query("SELECT * from task_log_table WHERE time_start_milli BETWEEN :dateBegin AND :dateEnd")
    fun getRecordWithinDateRange(dateBegin: Long, dateEnd: Long):LiveData<List<TaskRecord>>

//    @Query("SELECT name, sum(duration_sec) from task_log_table WHERE time_start_milli BETWEEN :dateBegin AND :dateEnd GROUP BY name" )
//    suspend fun getCombinedRecordWithinDateRange(dateBegin: Long, dateEnd: Long):LiveData<List<TaskRecord>>


}