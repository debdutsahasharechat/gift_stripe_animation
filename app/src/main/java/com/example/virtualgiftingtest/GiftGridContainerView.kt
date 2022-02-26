package com.example.virtualgiftingtest

import android.content.Context
import android.util.AttributeSet
import android.widget.GridLayout
import com.example.virtualgiftingtest.VgGiftStripeConstant.DEFAULT_COLUMN_COUNT
import com.example.virtualgiftingtest.VgGiftStripeConstant.DEFAULT_ROW_COUNT

class GiftGridContainerView constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
):GridLayout(context,attributeSet,defStyle){
    init {
        columnCount = DEFAULT_COLUMN_COUNT
        rowCount = DEFAULT_ROW_COUNT
    }
    fun setRowColumn(column:Int,row:Int){
        columnCount = column
        rowCount = row
    }
}