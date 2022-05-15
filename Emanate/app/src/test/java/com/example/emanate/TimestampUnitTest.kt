package com.example.emanate

import org.junit.Test
import org.junit.Assert.*

class TimestampUnitTest {

    @Test
    fun timestampIsConvertedToDatetime(){
        val converter = TimeConverter()

        val timestamp = "1652447211"
        val expectedResult = "14:06:51 - 13/05/2022"

        val result = converter.getDateTime(timestamp)

        assertEquals(expectedResult, result)
    }

    // All other functions accessed the Android class, so were not feasible for testing
}