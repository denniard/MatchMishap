package com.example.matchmishap.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchmishap.engine.AiEngine
import com.example.matchmishap.engine.AudioAnnouncer
import com.example.matchmishap.engine.BackgroundMusicPlayer
import com.example.matchmishap.engine.GameEngine
import com.example.matchmishap.engine.GameStateSerializer
import com.example.matchmishap.engine.OnlineGameManager
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
    private var onlineObservationJob: Job? = null
    private var activeOnlineRoomId: String? = null
    private var activeOnlinePlayerName: String? = null
    private var isHostPlayer: Boolean = false

    val isLocalHumanTurn: Boolean
        get() {
            val name = activeOnlinePlayerName
            return if (name != null) {
                _uiState.value.currentPlayer.name.equals(name, ignoreCase = true)
            } else {
                _uiState.value.isHumanTurn
            }
        }

    init {
        backgroundMusicPlayer.start()
        checkAndTriggerAiTurn()
    }

    fun pauseBackgroundAudio() {
        backgroundMusicPlayer.stop()
        aiTurnJob?.cancel()
    }

    fun resumeBackgroundAudio() {
        backgroundMusicPlayer.start()
        checkAndTriggerAiTurn()
    }

    fun startOnlineObservation(roomId: String, playerName: String, initialJson: String = "") {
        activeOnlineRoomId = roomId
        activeOnlinePlayerName = playerName

        if (initialJson.isNotBlank()) {
            val parsedState = GameStateSerializer.fromJson(initialJson)
            if (parsedState != null) {
                applyParsedOnlineState(parsedState, playerName)
            }
        }

        onlineObservationJob?.cancel()
        onlineObservationJob = viewModelScope.launch {
            OnlineGameManager.observeRoom(roomId).collect { room ->
                if (room != null) {
                    isHostPlayer = (room.hostId.isNotBlank() && room.players.firstOrNull { it.isHost }?.name.equals(playerName, ignoreCase = true))
                    if (room.gameStateJson.isNotBlank()) {
                        val parsedState = GameStateSerializer.fromJson(room.gameStateJson)
                        if (parsedState != null) {
                            applyParsedOnlineState(parsedState, playerName)
                        }
                    }
                }
            }
        }
    }

    private fun applyParsedOnlineState(parsedState: GameState, playerName: String) {
        val adjustedPlayers = parsedState.players.map { p ->
            p.copy(isHuman = p.name.equals(playerName, ignoreCase = true))
        }
        val adjustedState = parsedState.copy(players = adjustedPlayers)
        _uiState.value = adjustedState
        checkAndTriggerAiTurn()
    }

    private fun updateState(newState: GameState) {
        val oldState = _uiState.value
        _uiState.value = newState

        if (newState.statusMessage.isNotBlank() && newState.statusMessage != oldState.statusMessage) {
            audioAnnouncer.speakForHuman(newState.statusMessage, newState.humanPlayer.name)
        }

        val roomId = activeOnlineRoomId
        if (roomId != null) {
            viewModelScope.launch {
                OnlineGameManager.syncGameState(roomId, newState)
            }
        }
    }

    fun playCard(card: Card) {
        val currentState = _uiState.value
        if (!isLocalHumanTurn || currentState.isWaitingForColorChoice) return

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
        if (!isLocalHumanTurn || currentState.isWaitingForColorChoice) return

        viewModelScope.launch {
            val countToDraw = if (currentState.accumulatedPenalty > 0) currentState.accumulatedPenalty else 1
            val isPenalty = currentState.accumulatedPenalty > 0

            for (i in 1..countToDraw) {
                val state = _uiState.value
                val newState = GameEngine.drawSingleCard(state, isPenalty = isPenalty)
                updateState(newState)
                if (i < countToDraw) {
                    delay(300L)
                }
            }

            if (!isLocalHumanTurn) {
                checkAndTriggerAiTurn()
            }
        }
    }

    fun passDrawnCard() {
        val currentState = _uiState.value
        if (!isLocalHumanTurn || currentState.drawnCardThisTurn == null) return

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
        val roomId = activeOnlineRoomId
        if (roomId != null) {
            if (isHostPlayer) {
                viewModelScope.launch {
                    OnlineGameManager.restartOnlineMatch(roomId)
                }
            }
        } else {
            aiTurnJob?.cancel()
            onlineObservationJob?.cancel()
            activeOnlineRoomId = null
            activeOnlinePlayerName = null
            isHostPlayer = false

            val newState = GameEngine.initGame(mode)
            updateState(newState)
            checkAndTriggerAiTurn()
        }
    }

    private fun checkAndTriggerAiTurn() {
        aiTurnJob?.cancel()

        val currentState = _uiState.value
        if (currentState.isGameOver || currentState.isWaitingForColorChoice) {
            return
        }

        if (activeOnlineRoomId != null) {
            if (!isHostPlayer) return
            val currentP = currentState.currentPlayer
            val isActualAi = currentP.name.startsWith("Bot ", ignoreCase = true) || currentP.name.startsWith("ai_", ignoreCase = true)
            if (!isActualAi) return
        } else {
            if (currentState.isHumanTurn) return
        }

        aiTurnJob = viewModelScope.launch {
            processAiTurn()
        }
    }

    private suspend fun processAiTurn() {
        while (true) {
            val currentState = _uiState.value
            if (currentState.isGameOver || currentState.isWaitingForColorChoice) {
                break
            }

            if (activeOnlineRoomId != null) {
                if (!isHostPlayer) break
                val currentP = currentState.currentPlayer
                val isActualAi = currentP.name.startsWith("Bot ", ignoreCase = true) || currentP.name.startsWith("ai_", ignoreCase = true)
                if (!isActualAi) break
            } else {
                if (currentState.isHumanTurn) break
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
                val countToDraw = if (currentState.accumulatedPenalty > 0) currentState.accumulatedPenalty else 1
                val isPenalty = currentState.accumulatedPenalty > 0

                var stateAfterDraw = currentState
                for (i in 1..countToDraw) {
                    val state = _uiState.value
                    stateAfterDraw = GameEngine.drawSingleCard(state, isPenalty = isPenalty)
                    updateState(stateAfterDraw)
                    if (i < countToDraw) {
                        delay(300L)
                    }
                }

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
