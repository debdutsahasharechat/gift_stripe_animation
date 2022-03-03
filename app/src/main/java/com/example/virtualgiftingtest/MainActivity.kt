package com.example.virtualgiftingtest


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {
    private val TAG = "TAG"
    private var dpToPx by Delegates.notNull<Float>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val giftView = findViewById<VirtualGiftingListContainer>(R.id.gift_container)
        val listOfUrls = listOf(
            "https://cdn.sharechat.com/369759ca_1597684420570.png",
            "https://cdn.sharechat.com/9072d07_1597480433719.png",
            "https://cdn.sharechat.com/b8c829e_1597684442136.png"
        )
        giftView.setData(data = buildList {
            for(i in 0 until 28){
                add(GiftData(image = listOfUrls.random()?:"", coin = Random().nextInt(200), coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            }
        })
    }

    fun <E> List<E>.random(): E? = if (size > 0) get(Random().nextInt(size)) else null
}