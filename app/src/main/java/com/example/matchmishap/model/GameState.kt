package com.example.matchmishap.model

data class GameState(
    val players: List<Player> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val drawPile: List<Card> = emptyList(),
    val discardPile: List<Card> = emptyList(),
    val activeColor: CardColor = CardColor.TEAL,
    val accumulatedPenalty: Int = 0,
    val playDirection: PlayDirection = PlayDirection.CLOCKWISE,
    val gameMode: GameMode = GameMode.FREE_FOR_ALL,
    val isGameOver: Boolean = false,
    val winner: Player? = null,
    val winningTeam: String? = null,
    val statusMessage: String = "Game Started!",
    val isWaitingForColorChoice: Boolean = false,
    val lastPlayedCard: Card? = null,
    val drawnCardThisTurn: Card? = null
) {
    val currentPlayer: Player
        get() = players[currentPlayerIndex]

    val topCard: Card
        get() = discardPile.last()

    val humanPlayer: Player
        get() = players.first { it.isHuman }

    val isHumanTurn: Boolean
        get() = currentPlayer.isHuman && !isGameOver

    fun getNextPlayerIndex(step: Int = 1): Int {
        val total = players.size
        if (total == 0) return 0
        val increment = if (playDirection == PlayDirection.CLOCKWISE) step else -step
        return Math.floorMod(currentPlayerIndex + increment, total)
    }

    fun getPlayerAfter(index: Int, step: Int = 1): Player {
        val total = players.size
        val increment = if (playDirection == PlayDirection.CLOCKWISE) step else -step
        val targetIdx = Math.floorMod(index + increment, total)
        return players[targetIdx]
    }
}
