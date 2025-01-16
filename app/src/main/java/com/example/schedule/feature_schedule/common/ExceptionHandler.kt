package com.example.schedule.feature_schedule.common

fun handleException(operation: String, e: Throwable): Resource.Error<String> {
    return Resource.Error("$operation failed: ${e.message ?: "Unknown Error"}")
}