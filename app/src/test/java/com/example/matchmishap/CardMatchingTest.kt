package com.example.matchmishap

import com.example.matchmishap.model.Card
import com.example.matchmishap.model.CardColor
import com.example.matchmishap.model.CardType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardMatchingTest {

    @Test
    fun testColorAndNumberMatching() {
        val redFive = Card("1", CardColor.RED, CardType.NUMBER, 5)
        val redEight = Card("2", CardColor.RED, CardType.NUMBER, 8)
        val blueFive = Card("3", CardColor.TEAL, CardType.NUMBER, 5)
        val greenNine = Card("4", CardColor.GREEN, CardType.NUMBER, 9)

        // Matching color
        assertTrue(redEight.canPlayOn(redFive, CardColor.RED))

        // Matching number
        assertTrue(blueFive.canPlayOn(redFive, CardColor.RED))

        // No match
        assertFalse(greenNine.canPlayOn(redFive, CardColor.RED))
    }

    @Test
    fun testActionAndWildMatching() {
        val redPause = Card("1", CardColor.RED, CardType.PAUSE)
        val greenPause = Card("2", CardColor.GREEN, CardType.PAUSE)
        val wildChaos = Card("3", CardColor.WILD, CardType.COLOR_CHAOS)

        // Same action type matching across colors
        assertTrue(greenPause.canPlayOn(redPause, CardColor.RED))

        // Wild Chaos card playable anytime
        assertTrue(wildChaos.canPlayOn(redPause, CardColor.RED))
    }

    @Test
    fun testPenaltyStacking() {
        val redDoubleUp = Card("1", CardColor.RED, CardType.DOUBLE_UP)
        val greenDoubleUp = Card("2", CardColor.GREEN, CardType.DOUBLE_UP)
        val redFive = Card("3", CardColor.RED, CardType.NUMBER, 5)

        // When penalty > 0, only DOUBLE_UP can be played
        assertTrue(greenDoubleUp.canPlayOn(redDoubleUp, CardColor.RED, currentPenalty = 2))
        assertFalse(redFive.canPlayOn(redDoubleUp, CardColor.RED, currentPenalty = 2))
    }
}
