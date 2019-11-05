package com.steamedbunx.android.whathaveidone.ui.changeTaskWithText

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment

import com.steamedbunx.android.whathaveidone.databinding.ChangeTaskWithTextFragmentBinding
import com.steamedbunx.android.whathaveidone.ui.main.MainViewModel
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.steamedbunx.android.whathaveidone.R


class ChangeTaskWithTextFragment : DialogFragment() {

    lateinit var binding: ChangeTaskWithTextFragmentBinding
    lateinit var mainViewModel: MainViewModel

    companion object {
        @JvmStatic
        fun newInstance() =
            ChangeTaskWithTextFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.change_task_with_text_fragment, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editTextNewTask.setOnEditorActionListener(getOnEditorActionListener())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainViewModel = requireActivity().run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        }
    }

    fun getOnEditorActionListener(): TextView.OnEditorActionListener {
        return object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                tryChangeTask(v?.text.toString())
                return false
            }
        }
    }

    fun tryChangeTask(newTask: String) {
        if (!mainViewModel.changeTask(newTask)) {
            showChangeFailedDialog()
        } else{
            view?.findNavController()?.navigate(R.id.action_changeTaskWithTextFragment_to_mainFragment)
        }
    }

    private fun showChangeFailedDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder
            .setTitle("Change Task Failed")
            .setMessage("Please make sure your input is not empty or the same as the current task!")
            .setNeutralButton("OK", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.dismiss()
                }
            })
            .show()
    }
}
