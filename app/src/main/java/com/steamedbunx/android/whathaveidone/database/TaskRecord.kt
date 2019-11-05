package com.steamedbunx.android.whathaveidone.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "task_log_table",
    indices = [Index(value = ["time_start_milli"])]
)
data class TaskRecord(
    @PrimaryKey(autoGenerate = true)        val id: Int = 0,
    @ColumnInfo(name = "name")              val name:String = "",
    @ColumnInfo(name = "time_start_milli")  var date: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "duration_sec")      var length: Int = 0
)