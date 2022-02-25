package com.example.virtualgiftingtest.vgrecyclerview.grid


import android.graphics.Color
import android.view.ViewGroup
import com.example.virtualgiftingtest.Constants.DEFAULT_COLUMN_COUNT
import com.example.virtualgiftingtest.Constants.DEFAULT_ROW_COUNT
import com.example.virtualgiftingtest.GiftData
import com.example.virtualgiftingtest.GiftGridContainerView
import com.example.virtualgiftingtest.GiftGridViewPool
import com.example.virtualgiftingtest.VirtualGiftListHelperInterface
import com.example.virtualgiftingtest.vgrecyclerview.GiftRecyclerViewAdapter

class GiftGridAdapter : GiftRecyclerViewAdapter<GiftGridViewHolder>() {
    private var data: List<List<GiftData>> = listOf(listOf())
    var column = DEFAULT_COLUMN_COUNT
    var row = DEFAULT_ROW_COUNT
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftGridViewHolder {
        val view = GiftGridContainerView(context = parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
            setRowColumn(column,row)
        }
        return GiftGridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GiftGridViewHolder, position: Int) {
        holder.onBind(data = data[position], position = position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(data: List<List<GiftData>>) {
        this.data = data
        notifyDataSetChanged()
    }

}