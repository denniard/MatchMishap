package com.example.matchmishap.model

data class Player(
    val id: Int,
    val name: String,
    val isHuman: Boolean,
    val hand: List<Card> = emptyList(),
    val teamId: Int
) {
    val cardCount: Int
        get() = hand.size

    fun hasPlayableCard(topCard: Card, activeColor: CardColor, penalty: Int = 0): Boolean {
        return hand.any { it.canPlayOn(topCard, activeColor, penalty) }
    }
}
