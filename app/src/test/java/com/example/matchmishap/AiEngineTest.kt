package com.example.matchmishap

import com.example.matchmishap.engine.AiEngine
import com.example.matchmishap.model.Card
import com.example.matchmishap.model.CardColor
import com.example.matchmishap.model.CardType
import com.example.matchmishap.model.GameMode
import com.example.matchmishap.model.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class AiEngineTest {

    @Test
    fun testAiMustPlayDoubleUpWhenPenaltyActive() {
        val doubleUpCard = Card("d1", CardColor.RED, CardType.DOUBLE_UP)
        val normalCard = Card("n1", CardColor.RED, CardType.NUMBER, 5)
        val aiPlayer = Player(id = 1, name = "Opponent A", isHuman = false, hand = listOf(doubleUpCard, normalCard), teamId = 1)
        val nextPlayer = Player(id = 2, name = "Partner 1", isHuman = false, teamId = 0)

        val chosen = AiEngine.chooseCardToPlay(
            aiPlayer = aiPlayer,
            topCard = Card("top", CardColor.RED, CardType.DOUBLE_UP),
            activeColor = CardColor.RED,
            accumulatedPenalty = 2,
            gameMode = GameMode.FREE_FOR_ALL,
            nextPlayer = nextPlayer
        )

        assertNotNull(chosen)
        assertEquals(CardType.DOUBLE_UP, chosen?.type)
    }

    @Test
    fun testAiSelectsDominantColorForWild() {
        val hand = listOf(
            Card("1", CardColor.PURPLE, CardType.NUMBER, 1),
            Card("2", CardColor.PURPLE, CardType.NUMBER, 2),
            Card("3", CardColor.PURPLE, CardType.NUMBER, 3),
            Card("4", CardColor.RED, CardType.NUMBER, 4),
            Card("5", CardColor.WILD, CardType.COLOR_CHAOS)
        )
        val aiPlayer = Player(id = 1, name = "Opponent A", isHuman = false, hand = hand, teamId = 1)

        val wildColor = AiEngine.chooseWildColor(aiPlayer)
        assertEquals(CardColor.PURPLE, wildColor)
    }

    @Test
    fun testAiReturnsNullWhenNoPlayableCard() {
        val redFive = Card("1", CardColor.RED, CardType.NUMBER, 5)
        val aiPlayer = Player(id = 1, name = "Opponent A", isHuman = false, hand = listOf(redFive), teamId = 1)
        val nextPlayer = Player(id = 2, name = "Partner 1", isHuman = false, teamId = 0)

        val chosen = AiEngine.chooseCardToPlay(
            aiPlayer = aiPlayer,
            topCard = Card("top", CardColor.GREEN, CardType.NUMBER, 9),
            activeColor = CardColor.GREEN,
            accumulatedPenalty = 0,
            gameMode = GameMode.FREE_FOR_ALL,
            nextPlayer = nextPlayer
        )

        assertNull(chosen)
    }
}
