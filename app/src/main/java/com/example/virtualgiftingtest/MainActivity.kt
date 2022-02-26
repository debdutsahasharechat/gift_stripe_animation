package com.example.virtualgiftingtest


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {
    private val TAG = "TAG"
    private var dpToPx by Delegates.notNull<Float>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val giftView = findViewById<VirtualGiftingListContainer>(R.id.gift_container)
        giftView.setData(data = buildList {
            for(i in 0 until 20){
                add(GiftData(image = R.drawable.ic_launcher_background))
            }
        })
    }
}