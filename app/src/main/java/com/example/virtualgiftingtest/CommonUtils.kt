package com.example.virtualgiftingtest

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.GridLayout
import androidx.core.view.updateLayoutParams
import com.example.virtualgiftingtest.Constants.DEFAULT_ROW_COUNT
import kotlin.math.roundToInt

object CommonUtils {
    fun targetThreshold(context: Context,rowCount: Int = DEFAULT_ROW_COUNT):Int{
        return calculateTargetHeight(context,rowCount)
    }
    fun viewPagerHeight(context:Context,rowCount: Int = DEFAULT_ROW_COUNT):Int{
        return calculateTargetHeight(context,rowCount)
    }

    fun lerp(start: Int, stop: Int, fraction: Float): Int {
        return start + ((stop - start) * fraction.toDouble()).roundToInt()
    }
    fun lerp(start: Float, stop: Float, fraction: Float):Float{
        return start + ((stop - start) * fraction)
    }
    private fun itemMarginCollapsed(context: Context):Int{
        return context.dimen(R.dimen.divider_width)
    }
    private fun itemMarginExpanded(context: Context):Int{
        return context.dimen(R.dimen.virtual_gift_item_margin)
    }
    fun targetWidth(context: Context, column: Int): Int {
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        return screenWidth.div(column) - (column.times(2).times(itemMarginExpanded(context)))
    }
    fun targetHeight(context: Context):Int{
        return itemHeight(context,isExpanded = true) - textHeight(context)
    }
    fun itemHeight(context: Context,isExpanded:Boolean = true):Int{
        return if(isExpanded) context.dimen(R.dimen.virtual_gift_item_expanded) else context.dimen(R.dimen.virtual_gift_item_collapsed)
    }

    fun translateHeight(context: Context):Int{
        return targetThreshold(context)-itemHeight(context,isExpanded = false)
    }
    fun maxElements(column:Int,row:Int):Int{
        return column*row
    }
    private fun textHeight(context: Context):Int{
        return context.dimen(R.dimen.virtual_gift_item_text_height)
    }
    private fun calculateTargetHeight(context: Context, rowCount:Int):Int{
        //One item - (top margin + item height + text height + bottom margin)
        val margin = itemMarginExpanded(context)
        val itemHeight = itemHeight(context)
        val textHeight = textHeight(context)
        return rowCount*(margin+itemHeight+textHeight+margin)
    }

    fun setItemMargin(context: Context,layoutParams:GridLayout.LayoutParams):GridLayout.LayoutParams{
        val margin = itemMarginExpanded(context)
        layoutParams.topMargin = margin
        layoutParams.bottomMargin = margin
        layoutParams.leftMargin = margin
        layoutParams.rightMargin = margin
        return layoutParams
    }

    fun calculateTargetItemHeight(context: Context){

    }

    fun calculateTargetItemWidth(context: Context){

    }

}

fun Context.dimen(id:Int):Int{
    return resources.getDimension(id).toInt()
}

fun View.animateHeight(
    currentHeight: Int,
    targetHeight: Int,
    interpolator: Interpolator = AccelerateDecelerateInterpolator(),
    duration:Long = 300
) {
    val slideAnimator = ValueAnimator
        .ofInt(currentHeight, targetHeight)
        .setDuration(duration)
    slideAnimator.addUpdateListener { animation1 ->
        val value = animation1.animatedValue as Int
        updateLayoutParams {
            this.height = value
        }
        requestLayout()
    }
    val animationSet = AnimatorSet()
    animationSet.interpolator = interpolator
    animationSet.play(slideAnimator)
    animationSet.start()
}



