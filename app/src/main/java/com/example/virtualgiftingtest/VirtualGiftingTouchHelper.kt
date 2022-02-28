package com.example.virtualgiftingtest

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import com.example.virtualgiftingtest.VgGiftStripeConstant.DISMISS_THRESHOLD
import com.example.virtualgiftingtest.VgGiftStripeConstant.VIEW_COUNT
import kotlin.math.abs
import kotlin.properties.Delegates

abstract class VirtualGiftingTouchHelper @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet) {
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isViewDragged = false
    var targetThreshold = 0f
    var originalHeight = 0f
    var topInset:Int = 0
    var totalDrag = 0f
    private var onLayoutTime = 1 //How many time on layout triggers here it's 2 because 2 views are being added to the view group
    private var pointerId by Delegates.notNull<Int>()

    /**
     * Here just calculating the topInset and the original height of the frame layout for future usage
     * We are adding two views into the frame layout so onLayout initially called 2 times which implies that my all child views are drawn into the screen properly
     */
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if(originalHeight == 0f && topInset == 0) {
            originalHeight = (bottom-top).toFloat()
            topInset = top
        }
        if(onLayoutTime == VIEW_COUNT){
            onLayoutChange()
        }
        onLayoutTime++
    }

    /**
     * Here just we are calculating if up or down gesture is triggered or not
     * Based on the condition we are deciding whether we need to consume the touch event or not
     */
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        pointerId = ev?.getPointerId(0) ?: 0
        when (ev?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initialTouchX = ev.getX(pointerId)
                initialTouchY = ev.getY(pointerId)
            }
            MotionEvent.ACTION_UP -> {
                val finalY = ev.getY(pointerId)
                val finalX = ev.getX(pointerId)
                val distanceX = initialTouchX - finalX
                val distanceY = initialTouchY - finalY
                //Up and down swipe gesture
                if (abs(distanceX) < abs(distanceY)) {
                    isViewDragged = true
                    performDrag(distanceY)
                    return true
                }
            }
            MotionEvent.ACTION_MOVE->{
                val finalY = ev.getY(pointerId)
                val finalX = ev.getX(pointerId)
                val distanceX = initialTouchX - finalX
                val distanceY = initialTouchY - finalY
                //Up and down swipe gesture
                if (abs(distanceX) < abs(distanceY)) {
                    isViewDragged = true
                    return true
                }
            }
            else -> {
                isViewDragged = false
                return false
            }
        }
        return false
    }

    /**
     * Intercepting the needfull touches for calculating the dragFraction
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val finalY = event?.getY(pointerId)?:0f
        when(event?.actionMasked){
            MotionEvent.ACTION_MOVE->{
                performDrag(drag = (initialTouchY-finalY))
            }
            MotionEvent.ACTION_UP->{
                onDismiss(drag = (initialTouchY-finalY))
            }
        }
        return isViewDragged
    }

    open fun performDrag(drag:Float){
        //Direction is downward and the frame layout is expanded
        performDragFraction(dragFraction = calculateDragFraction(drag))
    }

    open fun onDismiss(drag:Float){
        val dragFraction = calculateDragFraction(drag)
        totalDrag = if(dragFraction<DISMISS_THRESHOLD){
            0f
        }else{
            targetThreshold
        }
        onDismissFraction(dragFraction = dragFraction)
    }

    private fun calculateDragFraction(drag:Float):Float{
        return (totalDrag+drag).div(targetThreshold).coerceIn(minimumValue = 0f, maximumValue = 1f)
    }

    open fun performDragFraction(dragFraction:Float){}
    open fun onDismissFraction(dragFraction: Float){}
    open fun onLayoutChange(){}
}