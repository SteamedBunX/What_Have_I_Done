package com.steamedbunx.android.whathaveidone.ui.changeTaskWithText

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChangeTaskWithTextViewModel : ViewModel() {
    var currentInput = ""
    private val _currentSearchList = MutableLiveData<List<String>>()
    val currentSearchList: LiveData<List<String>>
        get() = _currentSearchList

    private fun updateSearchList(charSequence: CharSequence?) {

    }

    fun getTextWatcher(): TextWatcher {
        return object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                currentInput = s.toString()
                updateSearchList(s)
            }

            override fun afterTextChanged(s: Editable?) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }
    }
}