package com.example.virtualgiftingtest

import java.util.*

data class GiftData(
    val image:String,
    val uniqueId:String = UUID.randomUUID().toString(),
    val coin:Int,
    val coinImage:String
)

