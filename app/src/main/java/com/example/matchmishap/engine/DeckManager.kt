package com.example.matchmishap.engine

import com.example.matchmishap.model.Card
import com.example.matchmishap.model.CardColor
import com.example.matchmishap.model.CardType
import java.util.UUID

object DeckManager {

    fun createDeck(shuffle: Boolean = true): List<Card> {
        val cards = mutableListOf<Card>()
        val playableColors = listOf(
            CardColor.TEAL,
            CardColor.PURPLE,
            CardColor.YELLOW,
            CardColor.GREEN,
            CardColor.RED
        )

        for (color in playableColors) {
            // 0 Card (1 per color)
            cards.add(Card(id = UUID.randomUUID().toString(), color = color, type = CardType.NUMBER, numberValue = 0))

            // 1 to 9 Cards (2 per color)
            for (num in 1..9) {
                cards.add(Card(id = UUID.randomUUID().toString(), color = color, type = CardType.NUMBER, numberValue = num))
                cards.add(Card(id = UUID.randomUUID().toString(), color = color, type = CardType.NUMBER, numberValue = num))
            }

            // PAUSE Cards (Buffed: 3 per color)
            repeat(3) {
                cards.add(Card(id = UUID.randomUUID().toString(), color = color, type = CardType.PAUSE))
            }

            // U_TURN Cards (Buffed: 3 per color)
            repeat(3) {
                cards.add(Card(id = UUID.randomUUID().toString(), color = color, type = CardType.U_TURN))
            }

            // DOUBLE_UP Cards (Buffed: 3 per color)
            repeat(3) {
                cards.add(Card(id = UUID.randomUUID().toString(), color = color, type = CardType.DOUBLE_UP))
            }
        }

        // COLOR_CHAOS Wild Cards (Buffed: 10 cards)
        repeat(10) {
            cards.add(Card(id = UUID.randomUUID().toString(), color = CardColor.WILD, type = CardType.COLOR_CHAOS))
        }

        return if (shuffle) cards.shuffled() else cards
    }

    data class DrawResult(
        val drawnCards: List<Card>,
        val newDrawPile: List<Card>,
        val newDiscardPile: List<Card>
    )

    fun drawCards(
        drawPile: List<Card>,
        discardPile: List<Card>,
        count: Int
    ): DrawResult {
        val currentDrawPile = drawPile.toMutableList()
        val currentDiscardPile = discardPile.toMutableList()
        val drawnCards = mutableListOf<Card>()

        var needed = count
        while (needed > 0) {
            if (currentDrawPile.isNotEmpty()) {
                drawnCards.add(currentDrawPile.removeAt(0))
                needed--
            } else {
                if (currentDiscardPile.size > 1) {
                    val topCard = currentDiscardPile.removeAt(currentDiscardPile.lastIndex)
                    val toReshuffle = currentDiscardPile.shuffled()
                    currentDrawPile.addAll(toReshuffle)
                    currentDiscardPile.clear()
                    currentDiscardPile.add(topCard)
                } else {
                    break
                }
            }
        }

        return DrawResult(
            drawnCards = drawnCards,
            newDrawPile = currentDrawPile,
            newDiscardPile = currentDiscardPile
        )
    }
}
