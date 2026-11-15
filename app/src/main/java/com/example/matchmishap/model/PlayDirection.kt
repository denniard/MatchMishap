package com.example.matchmishap.model

enum class PlayDirection {
    CLOCKWISE,
    COUNTER_CLOCKWISE;

    fun toggle(): PlayDirection = when (this) {
        CLOCKWISE -> COUNTER_CLOCKWISE
        COUNTER_CLOCKWISE -> CLOCKWISE
    }
}
