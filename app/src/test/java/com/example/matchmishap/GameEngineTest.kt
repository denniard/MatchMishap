package com.example.matchmishap

import com.example.matchmishap.engine.GameEngine
import com.example.matchmishap.model.Card
import com.example.matchmishap.model.CardColor
import com.example.matchmishap.model.CardType
import com.example.matchmishap.model.GameMode
import com.example.matchmishap.model.PlayDirection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {

    @Test
    fun testInitGameFreeForAll() {
        val state = GameEngine.initGame(GameMode.FREE_FOR_ALL)
        assertEquals(4, state.players.size)
        assertEquals(7, state.players[0].hand.size)
        assertEquals(0, state.currentPlayerIndex)
        assertEquals(PlayDirection.CLOCKWISE, state.playDirection)
        assertFalse(state.isGameOver)
    }

    @Test
    fun testPauseCardSkipsPlayer() {
        val state = GameEngine.initGame(GameMode.FREE_FOR_ALL)
        val pauseCard = Card("pause1", state.activeColor, CardType.PAUSE)

        val nextState = GameEngine.playCard(state, pauseCard)
        // Player 0 plays Pause -> turn skips Player 1 -> Player 2's turn
        assertEquals(2, nextState.currentPlayerIndex)
    }

    @Test
    fun testUTurnReversesDirection() {
        val state = GameEngine.initGame(GameMode.FREE_FOR_ALL)
        val uTurnCard = Card("uturn1", state.activeColor, CardType.U_TURN)

        val nextState = GameEngine.playCard(state, uTurnCard)
        // Direction toggles to COUNTER_CLOCKWISE -> turn goes 0 -> 3
        assertEquals(PlayDirection.COUNTER_CLOCKWISE, nextState.playDirection)
        assertEquals(3, nextState.currentPlayerIndex)
    }

    @Test
    fun testDoubleUpAccumulatesPenalty() {
        val state = GameEngine.initGame(GameMode.FREE_FOR_ALL)
        val doubleUpCard = Card("double1", state.activeColor, CardType.DOUBLE_UP)

        val nextState = GameEngine.playCard(state, doubleUpCard)
        assertEquals(2, nextState.accumulatedPenalty)
        assertEquals(1, nextState.currentPlayerIndex)

        // Player 1 plays another Double Up
        val doubleUp2 = Card("double2", CardColor.PURPLE, CardType.DOUBLE_UP)
        val state2 = GameEngine.playCard(nextState, doubleUp2)
        assertEquals(4, state2.accumulatedPenalty)
        assertEquals(2, state2.currentPlayerIndex)
    }

    @Test
    fun testWinCondition() {
        var state = GameEngine.initGame(GameMode.FREE_FOR_ALL)
        val singleCard = Card("last1", state.activeColor, CardType.NUMBER, 1)

        // Set player 0 hand to 1 card
        val playersWith1Card = state.players.mapIndexed { idx, p ->
            if (idx == 0) p.copy(hand = listOf(singleCard)) else p
        }
        state = state.copy(players = playersWith1Card)

        val finalState = GameEngine.playCard(state, singleCard)
        assertTrue(finalState.isGameOver)
        assertNotNull(finalState.winner)
        assertEquals(0, finalState.winner?.id)
    }
}
