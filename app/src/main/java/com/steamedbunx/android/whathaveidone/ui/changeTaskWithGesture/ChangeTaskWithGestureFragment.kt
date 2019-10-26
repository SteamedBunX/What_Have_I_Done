package com.steamedbunx.android.whathaveidone.ui.changeTaskWithGesture

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.steamedbunx.android.whathaveidone.R

class ChangeTaskWithGestureFragment : Fragment() {

    companion object {
        fun newInstance() = ChangeTaskWithGestureFragment()
    }

    private lateinit var viewModel: ChangeTaskWithGestureViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.change_task_with_gesture_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ChangeTaskWithGestureViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
