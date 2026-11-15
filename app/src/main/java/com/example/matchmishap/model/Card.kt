package com.example.matchmishap.model

data class Card(
    val id: String,
    val color: CardColor,
    val type: CardType,
    val numberValue: Int? = null
) {
    /**
     * Checks if this card can be legally played on top of topCard given the current active color
     * and current draw penalty.
     */
    fun canPlayOn(topCard: Card, activeColor: CardColor, currentPenalty: Int = 0): Boolean {
        if (currentPenalty > 0) {
            // Stackable penalty rule: when there is an active penalty, only DOUBLE_UP cards can be stacked
            return type == CardType.DOUBLE_UP
        }

        // Color Chaos / Wild cards can always be played
        if (type == CardType.COLOR_CHAOS || color == CardColor.WILD) {
            return true
        }

        // Color matching active color
        if (color == activeColor) {
            return true
        }

        // Action card matching type (e.g. Pause on Pause, U-Turn on U-Turn, Double Up on Double Up)
        if (type != CardType.NUMBER && type == topCard.type) {
            return true
        }

        // Number card matching same number
        if (type == CardType.NUMBER && topCard.type == CardType.NUMBER && numberValue != null && numberValue == topCard.numberValue) {
            return true
        }

        return false
    }

    val displayTitle: String
        get() = when (type) {
            CardType.NUMBER -> "${color.displayName} ${numberValue ?: 0}"
            CardType.PAUSE -> "${color.displayName} Pause"
            CardType.U_TURN -> "${color.displayName} U-Turn"
            CardType.DOUBLE_UP -> "${color.displayName} +2"
            CardType.COLOR_CHAOS -> "Color Chaos"
        }
}
