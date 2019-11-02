package com.steamedbunx.android.whathaveidone.widget

import android.content.Context
import android.content.res.ColorStateList
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
import com.steamedbunx.android.whathaveidone.R
import com.steamedbunx.android.whathaveidone.TAG
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class DraggableFloatingActionButton : FloatingActionButton, View.OnTouchListener {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    init {
        setOnClickListener {}
        setOnTouchListener(this)
    }

    // Often, there will be a slight, unintentional, drag when the user taps the FAB, so we need to account for this.
    private val CLICK_DRAG_TOLERANCE = 15f
    // keep track of the original position so it can go back to it when button is released
    private var ogX = 0f
    private var ogY = 0f
    private var positionSet = false

    private var downRawX = 0f
    private var downRawY = 0f
    private var dX = 0f
    private var dY = 0f

    // Y coordinate for what's counted as far release
    private var farReleaseY = 10000f

    // listener
    private var customListener: CustomDraggableFloatingActionButtonListener? = null

    fun setCustomListener(customDraggableFloatingActionButtonListener: CustomDraggableFloatingActionButtonListener) {
        customListener = customDraggableFloatingActionButtonListener
    }

    fun setFarRelease(y: Float) {
        farReleaseY = y
    }

    // when it's drawn, get the original position
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!positionSet) {
            ogX = x
            ogY = y
            positionSet = true
        }
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {


        //super.onTouchEvent(motionEvent)
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        val action = motionEvent.action
        if (action == MotionEvent.ACTION_DOWN) {


            downRawX = motionEvent.rawX
            downRawY = motionEvent.rawY

            dX = view.x - downRawX
            dY = view.y - downRawY

            return view?.onTouchEvent(motionEvent) // Consumed

        } else if (action == MotionEvent.ACTION_MOVE) {
            val viewWidth = view.width
            val viewHeight = view.height

            val viewParent = view.parent as View
            val parentWidth = viewParent.width
            val parentHeight = viewParent.height

            var newX = motionEvent.rawX + dX
            newX = max(
                layoutParams.leftMargin.toFloat(),
                newX
            ) // Don't allow the FAB past the left hand side of the parent
            newX = min(
                (parentWidth - viewWidth - layoutParams.rightMargin).toFloat(),
                newX
            ) // Don't allow the FAB past the right hand side of the parent

            var newY = motionEvent.rawY + dY
            newY = max(
                layoutParams.topMargin.toFloat(),
                newY
            ) // Don't allow the FAB past the top of the parent
            newY = min(
                (parentHeight - viewHeight - layoutParams.bottomMargin).toFloat(),
                newY
            ) // Don't allow the FAB past the bottom of the parent


            view.animate()
                .x(newX)
                .y(newY)
                .setDuration(0)
                .start()

            customListener?.onYMove(max(ogY - newY, 0f))

            return view?.onTouchEvent(motionEvent) // Consumed

        } else if (action == MotionEvent.ACTION_UP) {
            val upRawX = motionEvent.rawX
            val upRawY = motionEvent.rawY

            val upDX = upRawX - downRawX
            val upDY = upRawY - downRawY

            if (abs(upDX) < CLICK_DRAG_TOLERANCE && abs(upDY) < CLICK_DRAG_TOLERANCE) { // A click
                restoreLocation(view)
                customListener?.onClick()
            } else {
                Log.i(TAG, "updy = $upDY , farReleaseY = $farReleaseY")
                if (-upDY > farReleaseY) {
                    customListener?.onFarRelease()
                } else {
                    customListener?.onNearRelease()
                }
                restoreLocation(view)

                return view?.onTouchEvent(motionEvent) // Consumed
            }
        }
        return view?.onTouchEvent(motionEvent)
    }

    private fun restoreLocation(view: View) {
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
    fun onClick()
}