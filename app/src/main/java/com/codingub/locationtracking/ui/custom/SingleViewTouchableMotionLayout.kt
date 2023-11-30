package com.codingub.locationtracking.ui.custom

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import com.codingub.locationtracking.R

class SingleViewTouchableMotionLayout(context: Context, attributeSet: AttributeSet? = null) : MotionLayout(context, attributeSet) {

    private val viewToDetectTouch by lazy {
        findViewById<View>(R.id.gpsInfoPlaceHolder) //TODO move to Attributes
    }
    private val viewRect = Rect()
    private var touchStarted = false

    init {
        setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                touchStarted = false
            }

            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) = Unit

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) = Unit
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                touchStarted = false
                return super.onTouchEvent(event)
            }
        }
        if (!touchStarted) {
            viewToDetectTouch.getHitRect(viewRect)
            touchStarted = viewRect.contains(event.x.toInt(), event.y.toInt())
        }
        return touchStarted && super.onTouchEvent(event)
    }
}