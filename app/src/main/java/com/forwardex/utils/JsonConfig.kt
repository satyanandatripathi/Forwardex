package com.forwardex.utils

import kotlinx.serialization.json.Json

val AppJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = true
}
