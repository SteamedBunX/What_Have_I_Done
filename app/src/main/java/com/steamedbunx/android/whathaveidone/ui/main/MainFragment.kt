package com.steamedbunx.android.whathaveidone.ui.main

import android.annotation.SuppressLint
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.steamedbunx.android.whathaveidone.MainViewModelFactory
import com.steamedbunx.android.whathaveidone.R
import com.steamedbunx.android.whathaveidone.RecordDisplayMode
import com.steamedbunx.android.whathaveidone.Utils
import com.steamedbunx.android.whathaveidone.database.TaskDatabase
import com.steamedbunx.android.whathaveidone.databinding.MainFragmentBinding
import com.steamedbunx.android.whathaveidone.recyclerviewComponent.VerticalSpaceItemDecoration
import com.steamedbunx.android.whathaveidone.widget.CustomDraggableFloatingActionButtonListener

class MainFragment : Fragment() {

    lateinit var navController: NavController
    val utils = Utils.getInstence()

    companion object {
        fun newInstance() = MainFragment()
    }

    //region lateinit

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding
    private lateinit var taskRecordAdapter: TaskRecordAdapter

    //endregion

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
        initTextGroupAnimationDestinationYs()
        setupViewModel()
        setupRecyclerView()
        setupBottomSheet()
        setupListeners()
        setupCustomFab()
        setupObservers()
    }

    private fun setupRecyclerView() {
        taskRecordAdapter = TaskRecordAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = taskRecordAdapter
            val VERTICAL_ITEM_SPACE = 10
            addItemDecoration(VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE))
        }

    }

    private fun setupCustomFab() {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val farReleaseY = (displayMetrics.heightPixels) / 3f
        binding.fabChangeTask.setFarRelease(farReleaseY)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.layerBottomSheetLogToday)
        bottomSheetBehavior.setBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomShee: View, slideOffset: Float) {
                    if (slideOffset.isNaN()) {
                        setTextGroupY(1f)
                    } else {
                        setTextGroupY(1 + (slideOffset))
                    }
                }

                override fun onStateChanged(bottomShee: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        viewModel.setBottomSheetHidden()
                    } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
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
        binding.buttonMinimizeLog.setOnClickListener {
            viewModel.setBottomSheetHidden()
        }
        binding.buttonChangeDisplayMode.setOnClickListener {
            showChooseDisplayModeDialog()
        }
    }

    private fun setupViewModel() {
        val dataSource = TaskDatabase.getInstance(requireActivity().application).taskDatabaseDao
        val factory = MainViewModelFactory(dataSource, requireActivity().application)
        viewModel = requireActivity().run {
            ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        }
    }

    private fun setupObservers() {
        viewModel.currentTimeString.observe(this, Observer {
            binding.textTimer.text = it
        })
        viewModel.currentTask.observe(this, Observer {
            binding.textCurrentTask.text = utils.processStringForDisplay(it)
        })
        viewModel.isBottomSheetVisible.observe(this, Observer {
            bottomSheetBehavior.state =
                if (it) {
                    elevateTextGroupY()
                    viewModel.updateRecordListDisplay()
                    BottomSheetBehavior.STATE_EXPANDED
                } else {
                    BottomSheetBehavior.STATE_HIDDEN
                }
        })
        viewModel.taskRecordListForDisplay.observe(this, Observer {
            taskRecordAdapter.submitList(it)
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

    var textGroupOgY = 0f
    var textGroupElevatedYDelta = 0f

    // region textGroup
    fun initTextGroupAnimationDestinationYs() {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        textGroupOgY = (displayMetrics.heightPixels) * 0.2f
        textGroupElevatedYDelta = (displayMetrics.heightPixels) * 0.15f
    }

    fun setTextGroupY(offset: Float) {
        binding.layerTexts.animate()
            .y(textGroupOgY - textGroupElevatedYDelta * offset)
            .setDuration(0)
            .start()
    }

    fun elevateTextGroupY() {
        binding.layerTexts.animate()
            .y(textGroupOgY - textGroupElevatedYDelta)
            .setDuration(200)
            .start()
    }
    // endregion

    //endregion

    //region dialog
    private var currentChoice:Int = 0

    fun showChooseDisplayModeDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val choices =
            listOf<CharSequence>(
                "Separate and Sort by Time", "Separate and Sort by Length",
                "Combined and Sort by Time", "Combined and Sort by Length"
            ).toTypedArray()
        currentChoice = when (viewModel.taskRecordDisplayMode.value) {
            RecordDisplayMode.SEPARATED_BY_TIME -> 0
            RecordDisplayMode.SEPARATED_BY_LENGTH -> 1
            RecordDisplayMode.COMBINED_BY_TIME -> 2
            RecordDisplayMode.COMBINED_BY_LENGTH -> 3
            null -> 0
        }
        builder.setTitle("Select List Display Mode")
            .setSingleChoiceItems(choices, currentChoice,
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        currentChoice = which
                    }
                })
            .setPositiveButton("OK",
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        viewModel.setRecordDisplayMode(currentChoice)
                    }

                })
            .setNegativeButton("Cancel",
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        viewModel.setRecordDisplayMode(which)
                    }
                })
            .create()
            .show()
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
