package com.example.virtualgiftingtest

import android.content.Context
import android.util.AttributeSet
import android.widget.GridLayout
import com.example.virtualgiftingtest.Constants.DEFAULT_COLUMN_COUNT
import com.example.virtualgiftingtest.Constants.DEFAULT_ROW_COUNT

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
    override fun onFinishInflate() {
        Logger.d("FINISHED INFLATE....")
        super.onFinishInflate()
    }
    fun allViewAdded(){

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if(changed) {
            for (i in 0 until childCount) {
                val view = getChildAt(i)
                Logger.d("CHILD: ${view.x} ${view.y}")
            }
        }
    }
}