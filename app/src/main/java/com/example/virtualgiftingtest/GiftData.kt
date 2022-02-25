package com.example.virtualgiftingtest

import java.util.*

data class GiftData(
    val image:Int,
    val uniqueId:String = UUID.randomUUID().toString()
)

