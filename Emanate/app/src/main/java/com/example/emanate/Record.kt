package com.example.emanate

import com.google.gson.annotations.SerializedName

data class Record(
    @SerializedName("nodeId") val dbNodeId: String,
    val altitude: String,
    val gas: String,
    val humidity: String,
    val localPressure: String,
    val locale: String,
    val temperature: String,
)