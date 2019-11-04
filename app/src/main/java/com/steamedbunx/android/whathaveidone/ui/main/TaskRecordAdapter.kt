package com.steamedbunx.android.whathaveidone.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.steamedbunx.android.whathaveidone.R
import com.steamedbunx.android.whathaveidone.Utils
import com.steamedbunx.android.whathaveidone.database.TaskRecord
import kotlinx.android.synthetic.main.task_record_item.view.*

class TaskRecordAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items:List<TaskRecord> = ArrayList<TaskRecord>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TaskRecordViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.task_record_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is TaskRecordViewHolder ->{
                holder.bind(items.get(position))
            }
        }
    }

    fun submitList(newList: List<TaskRecord>){
        items = newList
    }

    class TaskRecordViewHolder constructor(itemView: View)
        : RecyclerView.ViewHolder(itemView){

        val taskNameText = itemView.text_task_name
        val taskLengthText = itemView.text_task_length

        fun bind(taskRecord: TaskRecord){
            taskNameText.text = taskRecord.name
            taskLengthText.text = Utils.getInstence().getTimerString(taskRecord.length)
        }
    }
}