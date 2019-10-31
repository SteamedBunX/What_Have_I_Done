package com.steamedbunx.android.whathaveidone.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.steamedbunx.android.whathaveidone.MainViewModelFactory
import com.steamedbunx.android.whathaveidone.R
import com.steamedbunx.android.whathaveidone.databinding.MainFragmentBinding
import com.steamedbunx.android.whathaveidone.widget.CustomDraggableFloatingActionButtonListener

class MainFragment : Fragment() {

    lateinit var navController: NavController

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
        navController = requireView().findNavController()
        setupViewModel()

        setupListeners()

        val displayMatrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMatrics)
        val farReleaseY = (displayMatrics.heightPixels) /3f
        binding.fabChangeTask.setFarRelease(farReleaseY)

        setupObservers()
    }

    private fun setupListeners() {
        binding.fabChangeTask.setCustomListener(customDFABListener)
        binding.fabChangeTask.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_mainFragment_to_changeTaskWithTextFragment)
        )
    }

    private fun setupViewModel() {
        val factory = MainViewModelFactory(requireActivity().application)
        viewModel = requireActivity().run {
            ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        }
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
                setOverlayAlpha(yDelta / binding.root.height * 2)
            }

            override fun onNearRelease() {
                fadeOutOverlay()
            }

            override fun onFarRelease() {
                if(navController.currentDestination?.id == R.id.mainFragment) {
                    navController.navigate(R.id.action_mainFragment_to_changeTaskWithGestureFragment)
                }
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
