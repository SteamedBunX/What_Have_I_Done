package com.steamedbunx.android.whathaveidone.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

    // region view state

    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    // endregion

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.main_fragment, container, false
        )
        return binding.root
    }

    // region onActivityCreated
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navController = requireView().findNavController()
        setupViewModel()
        setupListeners()
        setupBottomSheet()
        setupCustomFab()
        setupObservers()
    }

    private fun setupCustomFab() {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val farReleaseY = (displayMetrics.heightPixels) / 3f
        binding.fabChangeTask.setFarRelease(farReleaseY)
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.layerBottomSheetLogToday)
        bottomSheetBehavior.setBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(p0: View, p1: Float) {

                }

                override fun onStateChanged(p0: View, p1: Int) {
                    if (p1 == BottomSheetBehavior.STATE_HIDDEN) {
                        viewModel.setBottomSheetHidden()
                    } else if (p1 == BottomSheetBehavior.STATE_EXPANDED) {
                        viewModel.setBottomSheetVisible()
                    }
                }
            })
    }


    private fun setupListeners() {
        binding.fabChangeTask.setCustomListener(customDFABListener)
        binding.fabShowTodayLog.setOnClickListener {
            viewModel.setBottomSheetVisible()
        }
    }

    private fun setupViewModel() {
        val factory = MainViewModelFactory(requireActivity().application)
        viewModel = requireActivity().run {
            ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        }
    }

    private fun setupObservers() {
        viewModel.currentTimeString.observe(this, Observer {
            binding.textTimer.text = it
        })
        viewModel.currentTask.observe(this, Observer {
            binding.textCurrentTask.text = it
        })
        viewModel.isBottomSheetVisible.observe(this, Observer {
            bottomSheetBehavior.state =
                if (it) {
                    BottomSheetBehavior.STATE_EXPANDED
                } else {
                    BottomSheetBehavior.STATE_HIDDEN
                }
        })
    }
// endregion


    //region Listeners
    private val customDFABListener: CustomDraggableFloatingActionButtonListener =
        object : CustomDraggableFloatingActionButtonListener {
            override fun onClick() {
                navController.navigate(R.id.action_mainFragment_to_changeTaskWithTextFragment)
            }

            override fun onYMove(yDelta: Float) {
                setOverlayAlpha(yDelta / binding.root.height * 2)
            }

            override fun onNearRelease() {
                fadeOutOverlay()
            }

            override fun onFarRelease() {
                if (navController.currentDestination?.id == R.id.mainFragment) {
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

    // region lifecycle
    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
// endregion

}
