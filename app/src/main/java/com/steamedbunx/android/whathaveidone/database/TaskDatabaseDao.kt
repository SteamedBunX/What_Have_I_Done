package com.steamedbunx.android.whathaveidone.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.*

@Dao
interface TaskDatabaseDao {
    @Insert
    fun insert(taskRecord: TaskRecord)

    @Query("SELECT * from task_log_table WHERE time_start_milli BETWEEN :dateBegin AND :dateEnd")
    fun getRecordWithinDateRange(dateBegin: Long, dateEnd: Long):List<TaskRecord>


}