package com.steamedbunx.android.whathaveidone.recyclerviewComponent

import androidx.recyclerview.widget.RecyclerView
import android.R.attr.bottom
import android.graphics.Rect
import android.view.View


class VerticalSpaceItemDecoration(var verticalSpaceHeight: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) != (parent.adapter?.itemCount ?: 0) - 1) {
            outRect.bottom = verticalSpaceHeight
        }
    }

}