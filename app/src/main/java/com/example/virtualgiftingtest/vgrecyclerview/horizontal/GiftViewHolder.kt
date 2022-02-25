package com.example.virtualgiftingtest.vgrecyclerview.horizontal

import com.example.virtualgiftingtest.CommonUtils
import com.example.virtualgiftingtest.GiftData
import com.example.virtualgiftingtest.GiftDimension
import com.example.virtualgiftingtest.databinding.GiftItemBinding
import com.example.virtualgiftingtest.vgrecyclerview.GiftBaseViewHolder

class GiftViewHolder(private val itemBinding: GiftItemBinding) :
    GiftBaseViewHolder(itemView = itemBinding.root) {
    fun onBind(data: GiftData) {
        itemBinding.root.pivotX = 0f
        itemBinding.root.pivotY = 0f
        itemBinding.itemImageView.setImageResource(data.image)
    }

    fun animate(startPoint: GiftDimension, endPoint: GiftDimension, dragFraction: Float,onDismiss:Boolean) {
        val xPos =
            CommonUtils.lerp(start = startPoint.x, stop = endPoint.x, fraction = dragFraction)
        val yPos =
            CommonUtils.lerp(start = 0, stop = (startPoint.y - endPoint.y), fraction = dragFraction)
        val alphaVG = CommonUtils.lerp(start = 1f, stop = 0f, fraction = dragFraction)
        val scaleYVG = CommonUtils.lerp(
            start = 1f,
            stop = endPoint.height.div(startPoint.height),
            fraction = dragFraction
        )
        val scaleXVG = CommonUtils.lerp(
            start = 1f,
            stop = endPoint.width.div(startPoint.width),
            fraction = dragFraction
        )
        if(onDismiss.not()) {
            itemBinding
                .root
                .apply {
                    x = xPos.toFloat()
                    translationY = -yPos.toFloat()
                    alpha = alphaVG
                    scaleX = scaleXVG
                    scaleY = scaleYVG
                }
        }else{
            itemBinding
                .root
                .animate()
                .x(xPos.toFloat())
                .translationY(-yPos.toFloat())
                .alpha(alphaVG)
                .scaleX(scaleXVG)
                .scaleY(scaleYVG)
                .start()
        }
    }
}