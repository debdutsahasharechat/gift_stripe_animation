package com.example.virtualgiftingtest

import android.content.Context
import android.view.LayoutInflater
import com.example.virtualgiftingtest.databinding.VgGridItemBinding

class GiftGridViewPool(
    private val column: Int,
    private val rowCount: Int,
    context: Context
) {
    val listOfViews: MutableList<VgGridItemBinding> = mutableListOf()

    init {
        resetViewPool(column, rowCount, context)
    }

    fun resetViewPool(column: Int, rowCount: Int, context: Context) {
        if (this.column != column || this.rowCount != rowCount || listOfViews.size == 0) {
            listOfViews.clear()
            for (i in 0 until (column * rowCount)) {
                val view = VgGridItemBinding.inflate(LayoutInflater.from(context))
                listOfViews.add(view)
            }
        }
    }
}