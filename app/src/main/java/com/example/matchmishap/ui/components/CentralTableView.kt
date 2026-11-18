package com.example.matchmishap.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.matchmishap.model.Card
import com.example.matchmishap.model.CardColor
import com.example.matchmishap.model.PlayDirection
import com.example.matchmishap.ui.theme.CardColorUtils

@Composable
fun CentralTableView(
    topCard: Card,
    activeColor: CardColor,
    drawPileCount: Int,
    playDirection: PlayDirection,
    statusMessage: String,
    accumulatedPenalty: Int,
    isHumanTurn: Boolean,
    hasDrawnCardThisTurn: Boolean,
    onDrawCard: () -> Unit,
    onPassTurn: () -> Unit,
    onDrawPilePositioned: (LayoutCoordinates) -> Unit = {},
    onDiscardPilePositioned: (LayoutCoordinates) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "DirectionRotation")
    val directionAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (playDirection == PlayDirection.CLOCKWISE) 360f else -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DirectionAngle"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(2.dp)
            .offset(y = (-14).dp)
    ) {
        StatusBanner(statusMessage = statusMessage, isHumanTurn = isHumanTurn)

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(290.dp)
                .height(140.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val radius = Math.min(w, h) * 0.48f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            CardColorUtils.getMainColor(activeColor).copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    ),
                    radius = radius * 1.1f,
                    center = center
                )

                drawCircle(
                    color = Color(0xFFFF9800).copy(alpha = 0.5f),
                    radius = radius,
                    style = Stroke(width = 2.5.dp.toPx())
                )
            }

            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = playDirection.name,
                tint = Color(0xFFFF9800),
                modifier = Modifier
                    .size(230.dp)
                    .rotate(directionAngle)
                    .clip(CircleShape)
            )

            // Piles Container with Perfect Translucent Slot Outlines for Draw, Discard, and Color Wheel
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. DRAW PILE SLOT
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.onGloballyPositioned(onDrawPilePositioned)
                ) {
                    Box(
                        contentAlignment = Alignment.BottomCenter,
                        modifier = Modifier
                            .background(
                                color = Color.Black.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.35f)),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(3.dp)
                    ) {
                        CardBackView(
                            width = 64.dp,
                            height = 96.dp,
                            label = "DRAW",
                            onClick = if (isHumanTurn && !hasDrawnCardThisTurn) onDrawCard else null
                        )

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700)),
                            modifier = Modifier
                                .padding(bottom = 2.dp)
                                .shadow(2.dp, CircleShape)
                        ) {
                            Text(
                                text = "$drawPileCount",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    color = Color(0xFFFFD700)
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    if (hasDrawnCardThisTurn && isHumanTurn) {
                        Button(
                            onClick = onPassTurn,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFD700),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(26.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = "PASS TURN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    } else {
                        Button(
                            onClick = onDrawCard,
                            enabled = isHumanTurn && !hasDrawnCardThisTurn,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (accumulatedPenalty > 0) Color(0xFFE53935) else Color(0xFFFF9800),
                                disabledContainerColor = Color.Gray.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(24.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = if (accumulatedPenalty > 0) "DRAW +$accumulatedPenalty" else "DRAW",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }

                // 2. DISCARD PILE SLOT
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.onGloballyPositioned(onDiscardPilePositioned)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(
                                color = Color.Black.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.35f)),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(3.dp)
                    ) {
                        CardView(
                            card = topCard,
                            isPlayable = false,
                            width = 64.dp,
                            height = 96.dp,
                            overrideColor = activeColor
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "DISCARD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            color = Color.LightGray
                        )
                    )
                }

                // 3. ACTIVE COLOR INDICATOR WHEEL SLOT
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.25f),
                            shape = CircleShape
                        )
                        .border(
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.35f)),
                            shape = CircleShape
                        )
                        .padding(4.dp)
                ) {
                    ActiveColorIndicatorWheel(activeColor = activeColor)
                }
            }
        }

        if (isHumanTurn && hasDrawnCardThisTurn) {
            Spacer(modifier = Modifier.height(2.dp))
            OutlinedButton(
                onClick = onPassTurn,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFFFD700)
                ),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFFFD700)),
                modifier = Modifier.height(24.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Text(
                    text = "PASS TURN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun ActiveColorIndicatorWheel(activeColor: CardColor) {
    val activeColorCompose = CardColorUtils.getMainColor(activeColor)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFF00B4D8),
                            Color(0xFF8E24AA),
                            Color(0xFFE53935),
                            Color(0xFFFFB300),
                            Color(0xFF43A047),
                            Color(0xFF00B4D8)
                        )
                    )
                )
                .padding(3.dp)
                .clip(CircleShape)
                .background(activeColorCompose)
                .border(1.5.dp, Color.White, CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .size(15.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
            )
        }

        Spacer(modifier = Modifier.height(1.dp))

        Text(
            text = activeColor.displayName.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                fontSize = 8.sp,
                color = activeColorCompose
            )
        )
    }
}

@Composable
private fun StatusBanner(statusMessage: String, isHumanTurn: Boolean) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isHumanTurn) Color(0xFF0D47A1).copy(alpha = 0.9f) else Color(0xFF1E293B).copy(alpha = 0.9f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isHumanTurn) Color(0xFFFFD700) else Color.White.copy(alpha = 0.2f)
        ),
        shadowElevation = 2.dp
    ) {
        Text(
            text = statusMessage,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isHumanTurn) Color(0xFFFFD700) else Color.White,
                fontSize = 10.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
        )
    }
}
