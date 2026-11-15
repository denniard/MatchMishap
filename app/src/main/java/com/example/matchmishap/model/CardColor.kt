package com.example.matchmishap.model

enum class CardColor(val displayName: String) {
    TEAL("Blue"),
    PURPLE("Purple"),
    YELLOW("Yellow"),
    GREEN("Green"),
    RED("Red"),
    WILD("Wild");

    val isWild: Boolean
        get() = this == WILD
}
