package com.example.emanate

import java.text.SimpleDateFormat
import java.util.*

class TimeConverter {
    // Conversion from timestamp to datetime
    fun getDateTime(s: String): String? {
        return try {
            val sdf = SimpleDateFormat("H:mm:s - dd/MM/yyyy", Locale.UK)
            val netDate = Date(s.toLong() * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
           "Unreadable time"
        }
    }
}