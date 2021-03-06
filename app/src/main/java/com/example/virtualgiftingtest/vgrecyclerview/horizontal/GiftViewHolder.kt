package com.example.virtualgiftingtest.vgrecyclerview.horizontal

import com.bumptech.glide.Glide
import com.example.virtualgiftingtest.VgGiftStripeUtils
import com.example.virtualgiftingtest.GiftData
import com.example.virtualgiftingtest.GiftDimension
import com.example.virtualgiftingtest.Logger
import com.example.virtualgiftingtest.databinding.GiftItemBinding
import com.example.virtualgiftingtest.vgrecyclerview.GiftBaseViewHolder

class GiftViewHolder(private val itemBinding: GiftItemBinding) :
    GiftBaseViewHolder(itemView = itemBinding.root) {
    fun onBind(data: GiftData) {
        itemBinding.root.pivotX = 0f
        itemBinding.root.pivotY = 0f
        itemBinding.root.clipToPadding = false
        Glide
            .with(itemBinding.root.context)
            .load(data.image)
            .into(itemBinding.itemImageView)
    }

    fun animate(startPoint: GiftDimension, endPoint: GiftDimension, dragFraction: Float,onDismiss:Boolean,afterAnimation:(Int,Float)->Unit) {
        val xPos =
            VgGiftStripeUtils.lerp(start = startPoint.x, stop = endPoint.x, fraction = dragFraction)
        val yPos =
            VgGiftStripeUtils.lerp(start = 0, stop = (startPoint.y - endPoint.y), fraction = dragFraction)
        val scaleYVG = VgGiftStripeUtils.lerp(
            start = 1f,
            stop = endPoint.height.div(startPoint.height),
            fraction = dragFraction
        )
        val scaleXVG = VgGiftStripeUtils.lerp(
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
                    scaleX = scaleXVG
                    scaleY = scaleYVG
                }
        }else{
            itemBinding
                .root
                .apply {
                    animate()
                    .withEndAction {
                        afterAnimation.invoke(adapterPosition,dragFraction)
                    }
                    .x(xPos.toFloat())
                    .translationY(-yPos.toFloat())
                    .scaleX(scaleXVG)
                    .scaleY(scaleYVG)
                    .start()
                }
        }
    }

    fun fadingOut(dragFraction: Float){
        val alphaVG = VgGiftStripeUtils.lerp(start = 1f, stop = 0f, fraction = dragFraction)
        itemBinding
            .root
            .apply {
                alpha = alphaVG
            }
    }
}