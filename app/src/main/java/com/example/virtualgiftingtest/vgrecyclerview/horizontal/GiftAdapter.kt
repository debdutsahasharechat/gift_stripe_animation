package com.example.virtualgiftingtest.vgrecyclerview.horizontal

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.virtualgiftingtest.GiftData
import com.example.virtualgiftingtest.databinding.GiftItemBinding
import com.example.virtualgiftingtest.vgrecyclerview.GiftRecyclerViewAdapter

class GiftAdapter: GiftRecyclerViewAdapter<GiftViewHolder>(){
    private var data:List<GiftData> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftViewHolder {
        val giftBinding = GiftItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return GiftViewHolder(giftBinding)
    }

    override fun onBindViewHolder(holder: GiftViewHolder, position: Int) {
        holder.onBind(data = data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
    fun setData(data:List<GiftData>){
        this.data = data
        notifyDataSetChanged()
    }
}