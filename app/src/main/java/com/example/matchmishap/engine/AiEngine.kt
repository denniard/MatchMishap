package com.example.matchmishap.engine

import com.example.matchmishap.model.Card
import com.example.matchmishap.model.CardColor
import com.example.matchmishap.model.CardType
import com.example.matchmishap.model.GameMode
import com.example.matchmishap.model.Player

object AiEngine {

    fun chooseCardToPlay(
        aiPlayer: Player,
        topCard: Card,
        activeColor: CardColor,
        accumulatedPenalty: Int,
        gameMode: GameMode,
        nextPlayer: Player
    ): Card? {
        val playable = aiPlayer.hand.filter { it.canPlayOn(topCard, activeColor, accumulatedPenalty) }
        if (playable.isEmpty()) return null

        // If penalty is active, AI must play Double Up if available
        if (accumulatedPenalty > 0) {
            val doubleUp = playable.firstOrNull { it.type == CardType.DOUBLE_UP }
            return doubleUp // if null, AI will draw penalty
        }

        val isNextOpponent = nextPlayer.teamId != aiPlayer.teamId

        val doubleUps = playable.filter { it.type == CardType.DOUBLE_UP }
        val pauses = playable.filter { it.type == CardType.PAUSE }
        val uTurns = playable.filter { it.type == CardType.U_TURN }
        val normalCards = playable.filter { it.type != CardType.COLOR_CHAOS && it.color != CardColor.WILD }
        val wildCards = playable.filter { it.type == CardType.COLOR_CHAOS || it.color == CardColor.WILD }

        // Color frequency counts in hand to prefer cards of dominant color
        val colorCounts = aiPlayer.hand
            .filter { !it.color.isWild }
            .groupBy { it.color }
            .mapValues { it.value.size }

        // 1. If next player is opponent, prioritize attacking with Double Up or Pause
        if (isNextOpponent) {
            if (doubleUps.isNotEmpty()) {
                return doubleUps.maxByOrNull { colorCounts[it.color] ?: 0 } ?: doubleUps.first()
            }
            if (pauses.isNotEmpty()) {
                return pauses.maxByOrNull { colorCounts[it.color] ?: 0 } ?: pauses.first()
            }
        } else {
            // Next player is partner: avoid Double Up / Pause if possible
            if (normalCards.isNotEmpty()) {
                return normalCards.maxByOrNull { colorCounts[it.color] ?: 0 } ?: normalCards.first()
            }
            if (uTurns.isNotEmpty()) {
                return uTurns.first()
            }
        }

        // 2. Play normal color-matching card with AI's most common color
        if (normalCards.isNotEmpty()) {
            return normalCards.maxByOrNull { colorCounts[it.color] ?: 0 } ?: normalCards.first()
        }

        // 3. Play action cards if available
        if (uTurns.isNotEmpty()) return uTurns.first()
        if (pauses.isNotEmpty()) return pauses.first()
        if (doubleUps.isNotEmpty()) return doubleUps.first()

        // 4. Save Wild cards for last, or play if only option
        if (wildCards.isNotEmpty()) {
            return wildCards.first()
        }

        return playable.firstOrNull()
    }

    fun chooseWildColor(aiPlayer: Player): CardColor {
        val playableColors = listOf(
            CardColor.TEAL,
            CardColor.PURPLE,
            CardColor.YELLOW,
            CardColor.GREEN,
            CardColor.RED
        )
        val colorCounts = aiPlayer.hand
            .filter { !it.color.isWild }
            .groupBy { it.color }
            .mapValues { it.value.size }

        return playableColors.maxByOrNull { colorCounts[it] ?: 0 } ?: CardColor.TEAL
    }
}
