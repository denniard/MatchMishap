package com.example.matchmishap.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchmishap.engine.AiEngine
import com.example.matchmishap.engine.AudioAnnouncer
import com.example.matchmishap.engine.BackgroundMusicPlayer
import com.example.matchmishap.engine.GameEngine
import com.example.matchmishap.model.Card
import com.example.matchmishap.model.CardColor
import com.example.matchmishap.model.CardType
import com.example.matchmishap.model.GameMode
import com.example.matchmishap.model.GameState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val audioAnnouncer = AudioAnnouncer(application)
    private val backgroundMusicPlayer = BackgroundMusicPlayer(application)
    private val _uiState = MutableStateFlow(GameEngine.initGame(GameMode.FREE_FOR_ALL))
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    private var aiTurnJob: Job? = null

    init {
        backgroundMusicPlayer.start()
        checkAndTriggerAiTurn()
    }

    private fun updateState(newState: GameState) {
        val oldState = _uiState.value
        _uiState.value = newState
        if (newState.statusMessage.isNotBlank() && newState.statusMessage != oldState.statusMessage) {
            audioAnnouncer.speakForHuman(newState.statusMessage, newState.humanPlayer.name)
        }
    }

    fun playCard(card: Card) {
        val currentState = _uiState.value
        if (!currentState.isHumanTurn || currentState.isWaitingForColorChoice) return

        if (!card.canPlayOn(currentState.topCard, currentState.activeColor, currentState.accumulatedPenalty)) {
            return
        }

        val newState = GameEngine.playCard(currentState, card)
        updateState(newState)

        if (!newState.isWaitingForColorChoice) {
            checkAndTriggerAiTurn()
        }
    }

    fun selectWildColor(color: CardColor) {
        val currentState = _uiState.value
        if (!currentState.isWaitingForColorChoice) return

        val newState = GameEngine.selectWildColor(currentState, color)
        updateState(newState)

        checkAndTriggerAiTurn()
    }

    fun drawCard() {
        val currentState = _uiState.value
        if (!currentState.isHumanTurn || currentState.isWaitingForColorChoice) return

        val newState = GameEngine.drawCard(currentState)
        updateState(newState)

        if (!newState.isHumanTurn) {
            checkAndTriggerAiTurn()
        }
    }

    fun passDrawnCard() {
        val currentState = _uiState.value
        if (!currentState.isHumanTurn || currentState.drawnCardThisTurn == null) return

        val newState = GameEngine.passTurn(currentState)
        updateState(newState)

        checkAndTriggerAiTurn()
    }

    fun toggleGameMode() {
        val currentMode = _uiState.value.gameMode
        val newMode = if (currentMode == GameMode.FREE_FOR_ALL) GameMode.TWO_VS_TWO else GameMode.FREE_FOR_ALL
        restartGame(newMode)
    }

    fun restartGame(mode: GameMode = _uiState.value.gameMode) {
        aiTurnJob?.cancel()
        val newState = GameEngine.initGame(mode)
        updateState(newState)
        checkAndTriggerAiTurn()
    }

    private fun checkAndTriggerAiTurn() {
        aiTurnJob?.cancel()

        val currentState = _uiState.value
        if (currentState.isGameOver || currentState.isWaitingForColorChoice || currentState.isHumanTurn) {
            return
        }

        aiTurnJob = viewModelScope.launch {
            processAiTurn()
        }
    }

    private suspend fun processAiTurn() {
        while (true) {
            val currentState = _uiState.value
            if (currentState.isGameOver || currentState.isWaitingForColorChoice || currentState.isHumanTurn) {
                break
            }

            delay(1000L)

            val aiPlayer = currentState.currentPlayer
            val nextPlayer = currentState.getPlayerAfter(currentState.currentPlayerIndex, 1)

            val chosenCard = AiEngine.chooseCardToPlay(
                aiPlayer = aiPlayer,
                topCard = currentState.topCard,
                activeColor = currentState.activeColor,
                accumulatedPenalty = currentState.accumulatedPenalty,
                gameMode = currentState.gameMode,
                nextPlayer = nextPlayer
            )

            if (chosenCard != null) {
                if (chosenCard.color.isWild || chosenCard.type == CardType.COLOR_CHAOS) {
                    val wildColor = AiEngine.chooseWildColor(aiPlayer)
                    val stateAfterPlay = GameEngine.playCard(currentState, chosenCard, wildColor)
                    updateState(stateAfterPlay)
                } else {
                    val stateAfterPlay = GameEngine.playCard(currentState, chosenCard)
                    updateState(stateAfterPlay)
                }
            } else {
                val stateAfterDraw = GameEngine.drawCard(currentState)
                updateState(stateAfterDraw)

                if (stateAfterDraw.drawnCardThisTurn != null) {
                    delay(500L)
                    val drawnCard = stateAfterDraw.drawnCardThisTurn
                    val refreshedAiPlayer = stateAfterDraw.currentPlayer
                    val refreshedNextPlayer = stateAfterDraw.getPlayerAfter(stateAfterDraw.currentPlayerIndex, 1)

                    val playDrawnCard = AiEngine.chooseCardToPlay(
                        aiPlayer = refreshedAiPlayer,
                        topCard = stateAfterDraw.topCard,
                        activeColor = stateAfterDraw.activeColor,
                        accumulatedPenalty = 0,
                        gameMode = stateAfterDraw.gameMode,
                        nextPlayer = refreshedNextPlayer
                    )

                    if (playDrawnCard != null && playDrawnCard.id == drawnCard.id) {
                        val wildColor = if (drawnCard.color.isWild) AiEngine.chooseWildColor(refreshedAiPlayer) else null
                        val stateAfterDrawnPlay = GameEngine.playCard(stateAfterDraw, drawnCard, wildColor)
                        updateState(stateAfterDrawnPlay)
                    } else {
                        val stateAfterPass = GameEngine.passTurn(stateAfterDraw)
                        updateState(stateAfterPass)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        audioAnnouncer.shutdown()
        backgroundMusicPlayer.stop()
    }
}
