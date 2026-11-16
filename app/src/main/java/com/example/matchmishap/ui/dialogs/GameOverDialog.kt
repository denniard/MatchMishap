package com.example.matchmishap.ui.dialogs

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.matchmishap.model.Player

@Composable
fun GameOverDialog(
    winner: Player?,
    winningTeam: String?,
    isHumanWinner: Boolean,
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit,
    onReturnToLobby: (() -> Unit)? = null
) {
    Dialog(onDismissRequest = {}) {
        Box(
            modifier = Modifier
                .width(420.dp)
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            FireworksParticleOverlay()

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0F172A).copy(alpha = 0.95f)
                ),
                border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFFFFD700)),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                modifier = Modifier
                    .width(380.dp)
                    .padding(8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD700))
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = "Victory Trophy",
                            tint = Color.Black,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isHumanWinner) "🎉 VICTORY! 🎉" else "GAME OVER",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = if (isHumanWinner) Color(0xFFFFD700) else Color.White,
                            fontSize = 22.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = when {
                            winningTeam != null -> "🏆 Winner: $winningTeam"
                            winner != null -> "🏆 Winner: ${winner.name}"
                            else -> "Game Finished!"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF40C4FF),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onMainMenu,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.2.dp, Color.White.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Home,
                                    contentDescription = "Main Menu",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "MENU",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            Button(
                                onClick = onPlayAgain,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFD700),
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Replay,
                                    contentDescription = "Play Again",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "REPLAY",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        if (onReturnToLobby != null) {
                            Button(
                                onClick = onReturnToLobby,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE91E63),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Groups,
                                    contentDescription = "Lobby",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "RETURN TO LOBBY",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FireworksParticleOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "FireworksAnimation")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Progress"
    )

    val particleColors = listOf(
        Color(0xFFFFD700),
        Color(0xFF00E5FF),
        Color(0xFFFF4081),
        Color(0xFF76FF03),
        Color(0xFFE040FB),
        Color(0xFFFF9100)
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val centers = listOf(
            Offset(w * 0.2f, h * 0.25f),
            Offset(w * 0.8f, h * 0.25f),
            Offset(w * 0.5f, h * 0.15f),
            Offset(w * 0.15f, h * 0.7f),
            Offset(w * 0.85f, h * 0.7f)
        )

        val sparksPerBurst = 18
        centers.forEachIndexed { centerIndex, center ->
            for (i in 0 until sparksPerBurst) {
                val angle = (i.toDouble() / sparksPerBurst) * 2.0 * Math.PI
                val maxRadius = 100.dp.toPx()
                val currentRadius = maxRadius * progress
                val x = center.x + kotlin.math.cos(angle).toFloat() * currentRadius
                val y = center.y + kotlin.math.sin(angle).toFloat() * currentRadius + (progress * progress * 30.dp.toPx())

                val alpha = (1f - progress).coerceIn(0f, 1f)
                val color = particleColors[(centerIndex + i) % particleColors.size].copy(alpha = alpha)

                drawCircle(
                    color = color,
                    radius = (4.5.dp.toPx() * (1f - progress * 0.5f)),
                    center = Offset(x, y)
                )
            }
        }
    }
}
