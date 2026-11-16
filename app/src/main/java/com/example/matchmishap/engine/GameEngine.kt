package com.example.matchmishap.engine

import com.example.matchmishap.model.Card
import com.example.matchmishap.model.CardColor
import com.example.matchmishap.model.CardType
import com.example.matchmishap.model.GameMode
import com.example.matchmishap.model.GameState
import com.example.matchmishap.model.PlayDirection
import com.example.matchmishap.model.Player

object GameEngine {

    fun initGame(gameMode: GameMode): GameState {
        val rawDeck = DeckManager.createDeck(shuffle = true)

        val players = listOf(
            Player(id = 0, name = "You", isHuman = true, teamId = 0),
            Player(id = 1, name = "Bot 1", isHuman = false, teamId = 1),
            Player(id = 2, name = if (gameMode == GameMode.TWO_VS_TWO) "Partner Bot" else "Bot 2", isHuman = false, teamId = if (gameMode == GameMode.TWO_VS_TWO) 0 else 2),
            Player(id = 3, name = if (gameMode == GameMode.TWO_VS_TWO) "Opponent 2" else "Bot 3", isHuman = false, teamId = if (gameMode == GameMode.TWO_VS_TWO) 1 else 3)
        )

        val dealtPlayers = mutableListOf<Player>()
        val drawPile = rawDeck.toMutableList()

        for (player in players) {
            val hand = drawPile.take(7)
            repeat(7) { drawPile.removeAt(0) }
            dealtPlayers.add(player.copy(hand = hand))
        }

        // Setup top discard card (ensure non-wild for starting active color if possible)
        val discardPile = mutableListOf<Card>()
        val topCard = drawPile.removeAt(0)
        discardPile.add(topCard)

        val initialActiveColor = if (topCard.color.isWild) CardColor.TEAL else topCard.color

        val modeLabel = if (gameMode == GameMode.TWO_VS_TWO) "2v2 Partner Mode" else "Free-For-All"

        return GameState(
            players = dealtPlayers,
            currentPlayerIndex = 0,
            drawPile = drawPile,
            discardPile = discardPile,
            activeColor = initialActiveColor,
            accumulatedPenalty = 0,
            playDirection = PlayDirection.CLOCKWISE,
            gameMode = gameMode,
            isGameOver = false,
            winner = null,
            winningTeam = null,
            statusMessage = "Game started in $modeLabel! Your turn.",
            isWaitingForColorChoice = false,
            lastPlayedCard = null,
            drawnCardThisTurn = null
        )
    }

    fun playCard(
        currentState: GameState,
        card: Card,
        chosenWildColor: CardColor? = null
    ): GameState {
        if (currentState.isGameOver) return currentState

        val currentP = currentState.currentPlayer

        // Remove card from player hand
        val updatedHand = currentP.hand.filterNot { it.id == card.id }
        val updatedPlayer = currentP.copy(hand = updatedHand)

        val newDiscard = currentState.discardPile + card
        val updatedPlayers = currentState.players.map {
            if (it.id == currentP.id) updatedPlayer else it
        }

        // Check for win condition
        if (updatedHand.isEmpty()) {
            val winningTeamLabel = if (currentState.gameMode == GameMode.TWO_VS_TWO) {
                if (updatedPlayer.teamId == 0) "Team You & Partner 1" else "Team Opponents"
            } else {
                updatedPlayer.name
            }

            return currentState.copy(
                players = updatedPlayers,
                discardPile = newDiscard,
                isGameOver = true,
                winner = updatedPlayer,
                winningTeam = winningTeamLabel,
                statusMessage = "🎉 ${updatedPlayer.name} played ${card.displayTitle} and won the game!",
                lastPlayedCard = card
            )
        }

        // If card is Color Chaos / Wild +4 and no wild color chosen yet
        if ((card.type == CardType.COLOR_CHAOS || card.type == CardType.PLUS_FOUR || card.color.isWild) && chosenWildColor == null) {
            return currentState.copy(
                players = updatedPlayers,
                discardPile = newDiscard,
                isWaitingForColorChoice = true,
                lastPlayedCard = card,
                statusMessage = "${currentP.name} played ${card.displayTitle}! Pick a color."
            )
        }

        var nextActiveColor = currentState.activeColor
        var nextDirection = currentState.playDirection
        var nextPenalty = currentState.accumulatedPenalty
        var turnStep = 1
        var actionText = "played ${card.displayTitle}"

        when (card.type) {
            CardType.COLOR_CHAOS -> {
                val wildCol = chosenWildColor ?: CardColor.TEAL
                nextActiveColor = wildCol
                actionText = "played Color Chaos and set color to ${wildCol.displayName}"
            }
            CardType.PLUS_FOUR -> {
                val wildCol = chosenWildColor ?: CardColor.TEAL
                nextActiveColor = wildCol
                nextPenalty += 4
                actionText = "played Wild +4 and set color to ${wildCol.displayName} (+4 penalty is now $nextPenalty!)"
            }
            CardType.DOUBLE_UP -> {
                nextPenalty += 2
                nextActiveColor = card.color
                actionText = "played ${card.displayTitle} (+2 penalty is now $nextPenalty!)"
            }
            CardType.PAUSE -> {
                nextActiveColor = card.color
                turnStep = 2 // Skips next player
                val skippedPlayer = currentState.getPlayerAfter(currentState.currentPlayerIndex, step = 1)
                actionText = "played ${card.displayTitle} and skipped ${skippedPlayer.name}'s turn!"
            }
            CardType.U_TURN -> {
                nextActiveColor = card.color
                nextDirection = currentState.playDirection.toggle()
                actionText = "played ${card.displayTitle} and reversed direction of play!"
            }
            CardType.NUMBER -> {
                nextActiveColor = card.color
            }
        }

        val total = updatedPlayers.size
        val increment = if (nextDirection == PlayDirection.CLOCKWISE) turnStep else -turnStep
        val nextIndex = Math.floorMod(currentState.currentPlayerIndex + increment, total)

        val nextPlayerName = updatedPlayers[nextIndex].name

        return currentState.copy(
            players = updatedPlayers,
            currentPlayerIndex = nextIndex,
            discardPile = newDiscard,
            activeColor = nextActiveColor,
            accumulatedPenalty = nextPenalty,
            playDirection = nextDirection,
            statusMessage = "${currentP.name} $actionText. $nextPlayerName's turn.",
            isWaitingForColorChoice = false,
            lastPlayedCard = card,
            drawnCardThisTurn = null
        )
    }

    fun selectWildColor(currentState: GameState, color: CardColor): GameState {
        val nextDirection = currentState.playDirection
        val turnStep = 1

        val total = currentState.players.size
        val increment = if (nextDirection == PlayDirection.CLOCKWISE) turnStep else -turnStep
        val nextIndex = Math.floorMod(currentState.currentPlayerIndex + increment, total)

        val currentP = currentState.currentPlayer
        val nextPlayerName = currentState.players[nextIndex].name

        return currentState.copy(
            currentPlayerIndex = nextIndex,
            activeColor = color,
            isWaitingForColorChoice = false,
            statusMessage = "${currentP.name} chose ${color.displayName}. $nextPlayerName's turn."
        )
    }

    fun drawCard(currentState: GameState): GameState {
        if (currentState.isGameOver) return currentState

        val currentP = currentState.currentPlayer

        // If active penalty, player must draw penalty cards
        if (currentState.accumulatedPenalty > 0) {
            val penaltyCount = currentState.accumulatedPenalty
            val drawResult = DeckManager.drawCards(
                drawPile = currentState.drawPile,
                discardPile = currentState.discardPile,
                count = penaltyCount
            )

            val updatedHand = currentP.hand + drawResult.drawnCards
            val updatedPlayers = currentState.players.map {
                if (it.id == currentP.id) it.copy(hand = updatedHand) else it
            }

            val nextIndex = currentState.getNextPlayerIndex(step = 1)
            val nextPlayerName = updatedPlayers[nextIndex].name

            return currentState.copy(
                players = updatedPlayers,
                currentPlayerIndex = nextIndex,
                drawPile = drawResult.newDrawPile,
                discardPile = drawResult.newDiscardPile,
                accumulatedPenalty = 0,
                statusMessage = "${currentP.name} drew $penaltyCount penalty cards! $nextPlayerName's turn.",
                drawnCardThisTurn = null
            )
        }

        // Draw 1 card
        val drawResult = DeckManager.drawCards(
            drawPile = currentState.drawPile,
            discardPile = currentState.discardPile,
            count = 1
        )

        if (drawResult.drawnCards.isEmpty()) {
            // No cards left to draw anywhere
            val nextIndex = currentState.getNextPlayerIndex(step = 1)
            return currentState.copy(
                currentPlayerIndex = nextIndex,
                statusMessage = "Deck is completely empty! ${currentState.players[nextIndex].name}'s turn."
            )
        }

        val drawnCard = drawResult.drawnCards.first()
        val updatedHand = currentP.hand + drawnCard
        val updatedPlayers = currentState.players.map {
            if (it.id == currentP.id) it.copy(hand = updatedHand) else it
        }

        val isPlayable = drawnCard.canPlayOn(
            topCard = currentState.topCard,
            activeColor = currentState.activeColor,
            currentPenalty = 0
        )

        if (isPlayable) {
            // Player gets a chance to play the drawn card or pass
            return currentState.copy(
                players = updatedPlayers,
                drawPile = drawResult.newDrawPile,
                discardPile = drawResult.newDiscardPile,
                drawnCardThisTurn = drawnCard,
                statusMessage = "${currentP.name} drew ${drawnCard.displayTitle} (Playable!)."
            )
        } else {
            // Unplayable, pass turn
            val nextIndex = currentState.getNextPlayerIndex(step = 1)
            val nextPlayerName = updatedPlayers[nextIndex].name

            return currentState.copy(
                players = updatedPlayers,
                currentPlayerIndex = nextIndex,
                drawPile = drawResult.newDrawPile,
                discardPile = drawResult.newDiscardPile,
                drawnCardThisTurn = null,
                statusMessage = "${currentP.name} drew a card and passed. $nextPlayerName's turn."
            )
        }
    }

    fun passTurn(currentState: GameState): GameState {
        val nextIndex = currentState.getNextPlayerIndex(step = 1)
        val nextPlayerName = currentState.players[nextIndex].name

        return currentState.copy(
            currentPlayerIndex = nextIndex,
            drawnCardThisTurn = null,
            statusMessage = "${currentState.currentPlayer.name} passed turn. $nextPlayerName's turn."
        )
    }
}
