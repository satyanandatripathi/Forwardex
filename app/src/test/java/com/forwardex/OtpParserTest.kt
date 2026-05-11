package com.forwardex

import com.google.common.truth.Truth.assertThat
import com.forwardex.parsers.OtpParser
import org.junit.Test

class OtpParserTest {
    private val parser = OtpParser()

    @Test
    fun extractsOtpWithCategory() {
        val extraction = parser.extract("Your Bank OTP is 123456 valid for 5 min", "BANK")
        assertThat(extraction).isNotNull()
        assertThat(extraction?.code).isEqualTo("123456")
        assertThat(extraction?.category).isEqualTo("banking")
    }

    @Test
    fun returnsNullWhenNoOtpSignal() {
        val extraction = parser.extract("hello there", "FRIEND")
        assertThat(extraction).isNull()
    }
}
