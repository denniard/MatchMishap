package com.example.matchmishap

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.matchmishap.engine.LeaderboardManager
import com.example.matchmishap.ui.screens.GameTableScreen
import com.example.matchmishap.ui.screens.MainMenuScreen
import com.example.matchmishap.ui.screens.OnlineLobbyScreen
import com.example.matchmishap.ui.theme.MatchMishapTheme
import com.example.matchmishap.viewmodel.GameViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore


enum class AppScreen {
    MAIN_MENU,
    ONLINE_LOBBY,
    GAME_TABLE
}

class MainActivity : ComponentActivity() {

    val db = FirebaseFirestore.getInstance()
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        enableEdgeToEdge()

        setContent {
            MatchMishapTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    color = Color(0xFF070F18)
                ) {
                    val uiState by gameViewModel.uiState.collectAsStateWithLifecycle()
                    var currentScreen by remember { mutableStateOf(AppScreen.MAIN_MENU) }
                    var onlinePlayerName by remember { mutableStateOf("") }
                    var onlinePlayerId by remember { mutableStateOf("") }

                    remember(uiState.isGameOver) {
                        if (uiState.isGameOver && onlinePlayerId.isNotBlank()) {
                            val isHumanWinner = uiState.winner?.isHuman == true
                            CoroutineScope(Dispatchers.IO).launch {
                                LeaderboardManager.submitScore(
                                    playerId = onlinePlayerId,
                                    playerName = onlinePlayerName,
                                    wonGame = isHumanWinner,
                                    scoreDelta = if (isHumanWinner) 100 else 25
                                )
                            }
                        }
                    }

                    Crossfade(
                        targetState = currentScreen,
                        animationSpec = tween(400),
                        label = "ScreenCrossfade"
                    ) { screen ->
                        when (screen) {
                            AppScreen.MAIN_MENU -> {
                                MainMenuScreen(
                                    onStartGame = { selectedMode ->
                                        gameViewModel.restartGame(selectedMode)
                                        currentScreen = AppScreen.GAME_TABLE
                                    },
                                    onOpenOnline = {
                                        currentScreen = AppScreen.ONLINE_LOBBY
                                    },
                                    onOpenLeaderboard = {}
                                )
                            }
                            AppScreen.ONLINE_LOBBY -> {
                                OnlineLobbyScreen(
                                    onStartOnlineGame = { roomId, name, id ->
                                        onlinePlayerName = name
                                        onlinePlayerId = id
                                        gameViewModel.restartGame()
                                        currentScreen = AppScreen.GAME_TABLE
                                    },
                                    onBackToMenu = {
                                        currentScreen = AppScreen.MAIN_MENU
                                    }
                                )
                            }
                            AppScreen.GAME_TABLE -> {
                                GameTableScreen(
                                    gameState = uiState,
                                    onPlayCard = { card -> gameViewModel.playCard(card) },
                                    onDrawCard = { gameViewModel.drawCard() },
                                    onSelectWildColor = { color -> gameViewModel.selectWildColor(color) },
                                    onPassTurn = { gameViewModel.passDrawnCard() },
                                    onRestartGame = { gameViewModel.restartGame() },
                                    onExitToMenu = { currentScreen = AppScreen.MAIN_MENU }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
