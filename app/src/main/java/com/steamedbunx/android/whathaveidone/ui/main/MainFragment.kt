package com.steamedbunx.android.whathaveidone.ui.main

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.steamedbunx.android.whathaveidone.R
import com.steamedbunx.android.whathaveidone.databinding.MainFragmentBinding
import com.steamedbunx.android.whathaveidone.widget.CustomDraggableFloatingActionButtonListener
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.main_fragment, container, false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        binding.fabChangeTask.customListener = customDFABListener

        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.timer.catchUp()
    }

    override fun onStop() {
        super.onStop()
        viewModel.timer.stop()
    }

    fun setupObservers(){
        viewModel.currentTimeString.observe(this, Observer {
            binding.textTimer.text = it
        })
        viewModel.currentTask.observe(this, Observer {
            binding.textCurrentTask.text = it
        })
    }


    //region Listeners
    val customDFABListener: CustomDraggableFloatingActionButtonListener =
        object : CustomDraggableFloatingActionButtonListener {
            override fun onYMove(yDelta: Float) {
                setOverlayAlpha(yDelta / binding.root.height)
            }

            override fun onNearRelease() {
                fadeOutOverlay()
            }

            override fun onFarRelease() {
                fadeOutOverlay()
            }

        }


    //endregion

    //region animation

    fun setOverlayAlpha(alpha: Float) {
        binding.blackOverlay.animate()
            .alpha(alpha)
            .setDuration(0)
            .start()
        binding.blackOverlay.alpha = alpha
    }

    fun fadeOutOverlay() {
        binding.blackOverlay.animate()
            .alpha(0f)
            .setDuration(500)
            .start()
    }
    //endregion


}
