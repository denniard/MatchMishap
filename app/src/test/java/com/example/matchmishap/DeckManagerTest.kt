package com.example.matchmishap

import com.example.matchmishap.engine.DeckManager
import com.example.matchmishap.model.CardColor
import com.example.matchmishap.model.CardType
import org.junit.Assert.assertEquals
import org.junit.Test

class DeckManagerTest {

    @Test
    fun testDeckCreation() {
        val deck = DeckManager.createDeck(shuffle = false)
        assertEquals(133, deck.size)

        val wildCards = deck.filter { it.type == CardType.COLOR_CHAOS }
        assertEquals(8, wildCards.size)

        val tealCards = deck.filter { it.color == CardColor.TEAL }
        assertEquals(25, tealCards.size)
    }

    @Test
    fun testDrawCardsAndReshuffle() {
        val deck = DeckManager.createDeck(shuffle = true)
        val initialDrawPile = deck.take(10)
        val discardPile = deck.drop(10)
        val topDiscard = discardPile.last()

        val result = DeckManager.drawCards(initialDrawPile, discardPile, 15)

        assertEquals(15, result.drawnCards.size)
        // Discard pile should retain only the top card
        assertEquals(1, result.newDiscardPile.size)
        assertEquals(topDiscard, result.newDiscardPile.first())
    }
}
