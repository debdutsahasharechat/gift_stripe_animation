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
            add(GiftData(image = "https://cdn.sharechat.com/369759ca_1597684420570.png", coin = 45, coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/9072d07_1597480433719.png", coin = 78,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/b8c829e_1597684442136.png", coin = 89,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/369759ca_1597684420570.png", coin = 477,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/9072d07_1597480433719.png", coin = 234,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/b8c829e_1597684442136.png", coin = 45,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/369759ca_1597684420570.png", coin = 90,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/9072d07_1597480433719.png", coin = 412,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/b8c829e_1597684442136.png", coin = 23,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/9072d07_1597480433719.png", coin = 34,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/369759ca_1597684420570.png", coin = 90,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/b8c829e_1597684442136.png", coin = 45,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/9072d07_1597480433719.png", coin = 233,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/369759ca_1597684420570.png", coin = 45,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/b8c829e_1597684442136.png", coin = 67,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/9072d07_1597480433719.png", coin = 45,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/369759ca_1597684420570.png", coin = 45,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/9072d07_1597480433719.png", coin = 87,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/b8c829e_1597684442136.png", coin = 23,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/9072d07_1597480433719.png", coin = 45,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/9072d07_1597480433719.png", coin = 34,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/123fb68d_1627668173803_sc.png", coin = 45,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/9072d07_1597480433719.png", coin = 67,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/123fb68d_1627668173803_sc.png", coin = 45,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/9072d07_1597480433719.png", coin = 12,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/123fb68d_1627668173803_sc.png", coin = 45,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
            add(GiftData(image = "https://cdn.sharechat.com/9072d07_1597480433719.png", coin = 411,coinImage = "https://cdn.sharechat.com/react-native-assets/virtual-gifting/coin-active.png"))
        })
    }
}