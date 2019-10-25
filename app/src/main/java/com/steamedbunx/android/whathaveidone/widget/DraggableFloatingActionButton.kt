package com.steamedbunx.android.whathaveidone.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.security.KeyStore
import android.view.ViewGroup
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import kotlin.math.abs
import kotlin.math.max


class DraggableFloatingActionButton : FloatingActionButton, View.OnTouchListener {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        setOnTouchListener(this)
    }

    // Often, there will be a slight, unintentional, drag when the user taps the FAB, so we need to account for this.
    val CLICK_DRAG_TOLERANCE = 20f
    // keep track of the original position so it can go back to it when button is released
    var ogX: Float = 0f
    var ogY: Float = 0f

    var downRawX: Float = 0f
    var downRawY = 0f
    var dX = 0f
    var dY = 0f

    // Y coordinate for what's counted as far release
    var farReleaseY = 10000f

    // listener
    var customListener: CustomDraggableFloatingActionButtonListener? = null

    fun setCustomListner(customDraggableFloatingActionButtonListener: CustomDraggableFloatingActionButtonListener) {
        customListener = customDraggableFloatingActionButtonListener
    }

    // when it's drawn, get the original position
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        ogX = x
        ogY = y
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        val action = motionEvent.action
        if (action == MotionEvent.ACTION_DOWN) {


            downRawX = motionEvent.rawX
            downRawY = motionEvent.rawY

            dX = view.x - downRawX
            dY = view.y - downRawY

            return true // Consumed

        } else if (action == MotionEvent.ACTION_MOVE) {

            val viewWidth = view.width
            val viewHeight = view.height

            val viewParent = view.parent as View
            val parentWidth = viewParent.width
            val parentHeight = viewParent.height

            var newX = motionEvent.rawX + dX
            newX = Math.max(
                layoutParams.leftMargin.toFloat(),
                newX
            ) // Don't allow the FAB past the left hand side of the parent
            newX = Math.min(
                (parentWidth - viewWidth - layoutParams.rightMargin).toFloat(),
                newX
            ) // Don't allow the FAB past the right hand side of the parent

            var newY = motionEvent.rawY + dY
            newY = Math.max(
                layoutParams.topMargin.toFloat(),
                newY
            ) // Don't allow the FAB past the top of the parent
            newY = Math.min(
                (parentHeight - viewHeight - layoutParams.bottomMargin).toFloat(),
                newY
            ) // Don't allow the FAB past the bottom of the parent

            view.animate()
                .x(newX)
                .y(newY)
                .setDuration(0)
                .start()

            customListener?.onYMove(max(ogY - newY, 0f))

            return true // Consumed

        } else if (action == MotionEvent.ACTION_UP) {
            val upRawX = motionEvent.rawX
            val upRawY = motionEvent.rawY

            val upDX = upRawX - downRawX
            val upDY = upRawY - downRawY

            if (Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE) { // A click
                performClick()
            } else {
                restoreLocation(view)
                if (upDY > farReleaseY) {
                    customListener?.onFarRelease()
                } else {
                    customListener?.onNearRelease()
                }
                return true // Consumed
            }
        }
        return super.onTouchEvent(motionEvent)
    }

    fun restoreLocation(view: View) {
        view.animate()
            .x(ogX)
            .y(ogY)
            .setDuration(500)
            .start()
    }
}

interface CustomDraggableFloatingActionButtonListener {
    fun onYMove(yDelta: Float)
    fun onNearRelease()
    fun onFarRelease()
}