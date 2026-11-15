package com.example.matchmishap.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchmishap.model.Card
import com.example.matchmishap.model.CardColor
import com.example.matchmishap.model.CardType
import com.example.matchmishap.ui.components.CardView
import com.example.matchmishap.ui.theme.MatchMishapTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
        )
        delay(150)
        onSplashFinished()
    }

    val stripeTransition = rememberInfiniteTransition(label = "StripeShift")
    val stripeOffset by stripeTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "StripeShift"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF280F3B))
    ) {
        // Dark purple card-pattern background
        SplashBackgroundPattern(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Centered Logo & Cards (Teal 7 & Orange U-Turn)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // Card Pair Graphic
                SplashCardPairGraphic()

                Spacer(modifier = Modifier.height(24.dp))

                // "MATCH & MISHAP" Title Banner Logo
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF1B0A2E).copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFFFFD700)),
                    shadowElevation = 16.dp
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "MATCH & MISHAP",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFFD700),
                                    fontSize = 28.sp,
                                    letterSpacing = 1.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(44.dp))

                // Styled Striped Progress Bar Loading Indicator
                StripedProgressBar(
                    progress = progress.value,
                    stripeOffset = stripeOffset,
                    modifier = Modifier
                        .width(280.dp)
                        .height(20.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // "DEALING CARDS..." Text
                Text(
                    text = "DEALING CARDS...",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00E5FF),
                        fontSize = 15.sp,
                        letterSpacing = 3.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }

            // Bottom Footer Text: "© MISCHIEF GAMES"
            Text(
                text = "© MISCHIEF GAMES",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA58BB8),
                    fontSize = 12.sp,
                    letterSpacing = 2.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun SplashBackgroundPattern(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Radial dark purple gradient
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF4A1E6D).copy(alpha = 0.5f),
                    Color(0xFF280F3B).copy(alpha = 0.95f),
                    Color(0xFF1B0A2E)
                ),
                center = Offset(w / 2f, h / 2f),
                radius = Math.max(w, h) * 0.75f
            )
        )

        // Subtle repeating diamond / card patterns across canvas
        val diamondSize = 36.dp.toPx()
        val spacingX = 84.dp.toPx()
        val spacingY = 84.dp.toPx()

        val cols = (w / spacingX).toInt() + 2
        val rows = (h / spacingY).toInt() + 2

        for (col in -1..cols) {
            for (row in -1..rows) {
                val cx = col * spacingX + (if (row % 2 == 0) spacingX / 2f else 0f)
                val cy = row * spacingY

                val path = Path().apply {
                    moveTo(cx, cy - diamondSize / 2f)
                    lineTo(cx + diamondSize / 2f, cy)
                    lineTo(cx, cy + diamondSize / 2f)
                    lineTo(cx - diamondSize / 2f, cy)
                    close()
                }

                drawPath(
                    path = path,
                    color = Color(0xFF4D2075).copy(alpha = 0.16f)
                )

                drawPath(
                    path = path,
                    color = Color(0xFF6B2A99).copy(alpha = 0.12f),
                    style = Stroke(width = 1.2.dp.toPx())
                )
            }
        }
    }
}

@Composable
private fun SplashCardPairGraphic() {
    Box(
        modifier = Modifier.size(width = 220.dp, height = 150.dp),
        contentAlignment = Alignment.Center
    ) {
        // Teal 7 Card (Tilted Left ~ -14°)
        CardView(
            card = Card("splash_teal7", CardColor.TEAL, CardType.NUMBER, 7),
            width = 92.dp,
            height = 138.dp,
            modifier = Modifier
                .offset(x = (-36).dp, y = 6.dp)
                .rotate(-14f)
                .shadow(12.dp, RoundedCornerShape(10.dp))
        )

        // Orange U-Turn Card (Tilted Right ~ +14°)
        CardView(
            card = Card("splash_orange_uturn", CardColor.YELLOW, CardType.U_TURN),
            width = 92.dp,
            height = 138.dp,
            modifier = Modifier
                .offset(x = 36.dp, y = 6.dp)
                .rotate(14f)
                .shadow(12.dp, RoundedCornerShape(10.dp))
        )
    }
}

@Composable
private fun StripedProgressBar(
    progress: Float,
    stripeOffset: Float,
    modifier: Modifier = Modifier
) {
    val tealColor = Color(0xFF00B4D8)
    val orangeColor = Color(0xFFFF8F00)
    val darkTrackBg = Color(0xFF170826)
    val borderCol = Color(0xFF5C288D)

    Box(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(darkTrackBg)
            .border(1.5.dp, borderCol, RoundedCornerShape(10.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val filledWidth = width * progress.coerceIn(0f, 1f)

            if (filledWidth > 0f) {
                // Background fill of current progress area
                drawRect(
                    color = darkTrackBg,
                    size = Size(filledWidth, height)
                )

                // Draw diagonal stripes (Teal & Orange alternating)
                val stripeWidth = 16.dp.toPx()
                var startX = -stripeWidth + (stripeOffset % (stripeWidth * 2))
                var colorToggle = false

                while (startX < filledWidth) {
                    val stripePath = Path().apply {
                        moveTo(startX, height)
                        lineTo(startX + stripeWidth, 0f)
                        lineTo(startX + stripeWidth * 2, 0f)
                        lineTo(startX + stripeWidth, height)
                        close()
                    }

                    val color = if (colorToggle) tealColor else orangeColor
                    colorToggle = !colorToggle

                    // Draw striped segment clipped to current filled width
                    drawPath(
                        path = stripePath,
                        color = color
                    )

                    startX += stripeWidth
                }

                // Glossy top accent line on progress bar
                drawLine(
                    color = Color.White.copy(alpha = 0.35f),
                    start = Offset(0f, 1.dp.toPx()),
                    end = Offset(filledWidth, 1.dp.toPx()),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
        }
    }
}

@Preview(widthDp = 400, heightDp = 800)
@Composable
fun SplashScreenPreview() {
    MatchMishapTheme {
        SplashScreen(onSplashFinished = {})
    }
}
