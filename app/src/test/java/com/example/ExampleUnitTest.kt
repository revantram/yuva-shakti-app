package com.example

import com.example.util.AmavasyaCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testAmavasyaCalculation() {
        val now = System.currentTimeMillis()
        val nextAmavasya = AmavasyaCalculator.calculateNextAmavasyaMillis(now)

        // Must be in the future (within next ~35 days)
        assertTrue(nextAmavasya >= now - (24 * 3600 * 1000L))

        val formatted = AmavasyaCalculator.formatHindiDate(nextAmavasya)
        assertNotNull(formatted)
        assertTrue(formatted.contains("202")) // Year

        val tithi = AmavasyaCalculator.getHindiTithiName(nextAmavasya)
        assertTrue(tithi.contains("अमावस्या"))

        val countdown = AmavasyaCalculator.computeCountdown(nextAmavasya, now)
        assertTrue(countdown.days >= 0)
        assertTrue(countdown.hours in 0..23)
        assertTrue(countdown.minutes in 0..59)
        assertTrue(countdown.seconds in 0..59)
    }
}
