package com.example.virtualgiftingtest.vgrecyclerview.grid

import android.view.LayoutInflater
import android.widget.GridLayout
import com.example.virtualgiftingtest.VgGiftStripeUtils
import com.example.virtualgiftingtest.GiftData
import com.example.virtualgiftingtest.GiftGridContainerView
import com.example.virtualgiftingtest.databinding.VgGridItemBinding
import com.example.virtualgiftingtest.vgrecyclerview.GiftBaseViewHolder

class GiftGridViewHolder(val view: GiftGridContainerView) : GiftBaseViewHolder(view) {
    fun onBind(data: List<GiftData>, position: Int) {
        repeat(data.size) {
            val giftGridView = VgGridItemBinding.inflate(LayoutInflater.from(view.context))
            var layoutParams = GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED), GridLayout.spec(GridLayout.UNDEFINED,1f))
            layoutParams.width = 0
            layoutParams = VgGiftStripeUtils.setItemMargin(context = view.context, layoutParams = layoutParams)
            giftGridView.gridImageItem.setImageResource(data[it].image)
            view.addView(giftGridView.root,layoutParams)
        }
    }
}