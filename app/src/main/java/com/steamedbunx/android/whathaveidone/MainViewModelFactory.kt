package com.steamedbunx.android.whathaveidone

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.steamedbunx.android.whathaveidone.database.TaskDatabaseDao
import com.steamedbunx.android.whathaveidone.ui.main.MainViewModel

class MainViewModelFactory(
    private val dataSource: TaskDatabaseDao,
    private val app: Application)
    : ViewModelProvider.AndroidViewModelFactory(app){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(dataSource, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

