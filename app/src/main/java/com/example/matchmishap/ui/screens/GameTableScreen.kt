package com.example.matchmishap.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchmishap.R
import com.example.matchmishap.engine.GameEngine
import com.example.matchmishap.model.Card
import com.example.matchmishap.model.CardColor
import com.example.matchmishap.model.GameMode
import com.example.matchmishap.model.GameState
import com.example.matchmishap.ui.components.CardBackView
import com.example.matchmishap.ui.components.CardView
import com.example.matchmishap.ui.components.CentralTableView
import com.example.matchmishap.ui.components.LeftOpponentSeat
import com.example.matchmishap.ui.components.PlayerHandTray
import com.example.matchmishap.ui.components.RightOpponentSeat
import com.example.matchmishap.ui.components.TopPartnerSeat
import com.example.matchmishap.ui.dialogs.ColorChaosPickerDialog
import com.example.matchmishap.ui.dialogs.GameOverDialog
import com.example.matchmishap.ui.dialogs.RulesDialog
import com.example.matchmishap.ui.dialogs.SettingsDialog
import com.example.matchmishap.ui.theme.MatchMishapTheme

@Composable
fun GameTableScreen(
    gameState: GameState,
    onPlayCard: (Card) -> Unit,
    onDrawCard: () -> Unit,
    onSelectWildColor: (CardColor) -> Unit,
    onPassTurn: () -> Unit,
    onRestartGame: () -> Unit,
    onExitToMenu: () -> Unit,
    onReturnToLobby: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showRulesModal by remember { mutableStateOf(false) }
    var showSettingsModal by remember { mutableStateOf(false) }

    var rootCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var drawPileOffset by remember { mutableStateOf(Offset.Zero) }
    var discardPileOffset by remember { mutableStateOf(Offset.Zero) }
    val playerOffsets = remember { mutableStateMapOf<Int, Offset>() }
    val activeAnimations = remember { mutableStateListOf<FlyingCardAnimation>() }

    fun calculateCenter(coords: LayoutCoordinates): Offset {
        val root = rootCoords ?: return Offset.Zero
        val rootPosition = root.positionInRoot()
        val widgetPosition = coords.positionInRoot()
        return Offset(
            x = widgetPosition.x - rootPosition.x + coords.size.width / 2f,
            y = widgetPosition.y - rootPosition.y + coords.size.height / 2f
        )
    }

    var prevDiscardPileSize by remember { mutableStateOf(gameState.discardPile.size) }
    var prevTopCardId by remember { mutableStateOf(gameState.topCard.id) }
    var prevPlayerHandSizes by remember { mutableStateOf(gameState.players.map { it.hand.size }) }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(gameState) {
        if (!isInitialized) {
            isInitialized = true
            prevDiscardPileSize = gameState.discardPile.size
            prevTopCardId = gameState.topCard.id
            prevPlayerHandSizes = gameState.players.map { it.hand.size }
            return@LaunchedEffect
        }

        if (gameState.discardPile.size > prevDiscardPileSize || gameState.topCard.id != prevTopCardId) {
            val playingPlayerIndex = gameState.players.indices.minByOrNull { i ->
                gameState.players[i].hand.size - (prevPlayerHandSizes.getOrElse(i) { gameState.players[i].hand.size })
            } ?: gameState.getPlayerAfter(gameState.currentPlayerIndex, -1).id

            val startOffset = playerOffsets[playingPlayerIndex] ?: discardPileOffset
            val endOffset = discardPileOffset
            val playedCard = gameState.topCard

            val animId = System.currentTimeMillis() + (0..1000).random().toLong()
            activeAnimations.add(
                FlyingCardAnimation(
                    id = animId,
                    card = playedCard,
                    isFaceUp = true,
                    startOffset = startOffset,
                    endOffset = endOffset,
                    onFinished = {
                        activeAnimations.removeAll { it.id == animId }
                    }
                )
            )
        }

        for (i in gameState.players.indices) {
            val currentHandSize = gameState.players[i].hand.size
            val prevHandSize = prevPlayerHandSizes.getOrElse(i) { currentHandSize }
            if (currentHandSize > prevHandSize) {
                val startOffset = drawPileOffset
                val endOffset = playerOffsets[i] ?: drawPileOffset

                val animId = System.currentTimeMillis() + (0..1000).random().toLong() + i
                activeAnimations.add(
                    FlyingCardAnimation(
                        id = animId,
                        card = null,
                        isFaceUp = false,
                        startOffset = startOffset,
                        endOffset = endOffset,
                        onFinished = {
                            activeAnimations.removeAll { it.id == animId }
                        }
                    )
                )
            }
        }

        prevDiscardPileSize = gameState.discardPile.size
        prevTopCardId = gameState.topCard.id
        prevPlayerHandSizes = gameState.players.map { it.hand.size }
    }

    val humanPlayer = gameState.humanPlayer
    val isHumanTurn = gameState.isHumanTurn
    val is2v2Mode = gameState.gameMode == GameMode.TWO_VS_TWO

    val opponentA = gameState.players.getOrNull(1) ?: humanPlayer
    val partner1 = gameState.players.getOrNull(2) ?: humanPlayer
    val opponentB = gameState.players.getOrNull(3) ?: humanPlayer

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { rootCoords = it }
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_wood_table),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Image(
            painter = painterResource(id = R.drawable.ic_snack_burrito),
            contentDescription = "Snack Burrito",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
                .size(48.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.ic_snack_bowl),
            contentDescription = "Snack Bowl",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .size(48.dp)
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            GameHeaderBar(
                gameMode = gameState.gameMode,
                onRulesClick = { showRulesModal = true },
                onSettingsClick = { showSettingsModal = true },
                onRestartClick = onRestartGame,
                onMenuClick = onExitToMenu,
                onReturnToLobby = onReturnToLobby
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                TopPartnerSeat(
                    partnerPlayer = partner1,
                    isCurrentTurn = gameState.currentPlayerIndex == 2,
                    is2v2Mode = is2v2Mode,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 2.dp)
                        .onGloballyPositioned { playerOffsets[2] = calculateCenter(it) }
                )

                LeftOpponentSeat(
                    opponentPlayer = opponentA,
                    isCurrentTurn = gameState.currentPlayerIndex == 1,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .onGloballyPositioned { playerOffsets[1] = calculateCenter(it) }
                )

                RightOpponentSeat(
                    opponentPlayer = opponentB,
                    isCurrentTurn = gameState.currentPlayerIndex == 3,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .onGloballyPositioned { playerOffsets[3] = calculateCenter(it) }
                )

                CentralTableView(
                    topCard = gameState.topCard,
                    activeColor = gameState.activeColor,
                    drawPileCount = gameState.drawPile.size,
                    playDirection = gameState.playDirection,
                    statusMessage = gameState.statusMessage,
                    accumulatedPenalty = gameState.accumulatedPenalty,
                    isHumanTurn = isHumanTurn,
                    hasDrawnCardThisTurn = gameState.drawnCardThisTurn != null,
                    onDrawCard = onDrawCard,
                    onPassTurn = onPassTurn,
                    onDrawPilePositioned = { drawPileOffset = calculateCenter(it) },
                    onDiscardPilePositioned = { discardPileOffset = calculateCenter(it) },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-28).dp)
                )

                PlayerHandTray(
                    humanPlayer = humanPlayer,
                    isHumanTurn = isHumanTurn,
                    topCard = gameState.topCard,
                    activeColor = gameState.activeColor,
                    accumulatedPenalty = gameState.accumulatedPenalty,
                    onPlayCard = onPlayCard,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(0.70f)
                        .padding(bottom = 2.dp)
                        .onGloballyPositioned { playerOffsets[0] = calculateCenter(it) }
                )
            }
        }

        activeAnimations.forEach { anim ->
            FlyingCardItem(animation = anim)
        }

        if (gameState.isWaitingForColorChoice && isHumanTurn) {
            ColorChaosPickerDialog(
                onColorSelected = onSelectWildColor
            )
        }

        if (gameState.isGameOver) {
            GameOverDialog(
                winner = gameState.winner,
                winningTeam = gameState.winningTeam,
                isHumanWinner = gameState.winner?.isHuman == true || (is2v2Mode && gameState.winner?.teamId == 0),
                onPlayAgain = onRestartGame,
                onMainMenu = onExitToMenu,
                onReturnToLobby = onReturnToLobby
            )
        }

        if (showRulesModal) {
            RulesDialog(onDismissRequest = { showRulesModal = false })
        }

        if (showSettingsModal) {
            SettingsDialog(onDismissRequest = { showSettingsModal = false })
        }
    }
}

@Composable
private fun GameHeaderBar(
    gameMode: GameMode,
    onRulesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onRestartClick: () -> Unit,
    onMenuClick: () -> Unit,
    onReturnToLobby: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B).copy(alpha = 0.8f))
            ) {
                Icon(
                    imageVector = Icons.Rounded.Home,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            if (onReturnToLobby != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFE91E63),
                    onClick = onReturnToLobby,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Groups,
                            contentDescription = "Lobby",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LOBBY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "MATCH & MISHAP",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD700),
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (gameMode == GameMode.TWO_VS_TWO) Color(0xFF0288D1) else Color(0xFF7B1FA2),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = gameMode.displayName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onRestartClick,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B).copy(alpha = 0.8f))
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = "Restart Game",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onRulesClick,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B).copy(alpha = 0.8f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                    contentDescription = "Rules",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B).copy(alpha = 0.8f))
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(widthDp = 800, heightDp = 400)
@Composable
fun GameTableScreenPreview() {
    MatchMishapTheme {
        GameTableScreen(
            gameState = GameEngine.initGame(GameMode.FREE_FOR_ALL),
            onPlayCard = {},
            onDrawCard = {},
            onSelectWildColor = {},
            onPassTurn = {},
            onRestartGame = {},
            onExitToMenu = {}
        )
    }
}

data class FlyingCardAnimation(
    val id: Long,
    val card: Card?,
    val isFaceUp: Boolean,
    val startOffset: Offset,
    val endOffset: Offset,
    val onFinished: () -> Unit
)

@Composable
fun FlyingCardItem(
    animation: FlyingCardAnimation
) {
    val density = LocalDensity.current
    val cardWidthPx = with(density) { 70.dp.toPx() }
    val cardHeightPx = with(density) { 105.dp.toPx() }

    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(animation.id) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
        )
        animation.onFinished()
    }

    val progress = animProgress.value
    val currentX = lerp(animation.startOffset.x, animation.endOffset.x, progress)
    val currentY = lerp(animation.startOffset.y, animation.endOffset.y, progress)
    val currentScale = lerp(0.85f, 1f, progress)
    val currentRotation = lerp(0f, 12f * (if (progress > 0.5f) 1f - progress else progress), progress)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer(
                    translationX = currentX - (cardWidthPx / 2f),
                    translationY = currentY - (cardHeightPx / 2f),
                    scaleX = currentScale,
                    scaleY = currentScale,
                    rotationZ = currentRotation
                )
        ) {
            if (animation.isFaceUp && animation.card != null) {
                CardView(
                    card = animation.card,
                    width = 70.dp,
                    height = 105.dp
                )
            } else {
                CardBackView(
                    width = 70.dp,
                    height = 105.dp
                )
            }
        }
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}
