package com.example.virtualgiftingtest

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomLLMOnMeasure(
    context:Context,
    orientation:Int,
    reverseLayout:Boolean,
    private val onLayoutCompleted:()->Unit
):LinearLayoutManager(context,orientation,reverseLayout){

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        onLayoutCompleted()
        super.onLayoutCompleted(state)
    }
}