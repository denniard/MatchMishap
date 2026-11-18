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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchmishap.R
import com.example.matchmishap.model.Card
import com.example.matchmishap.model.CardColor
import com.example.matchmishap.model.CardType
import com.example.matchmishap.ui.components.CardView
import com.example.matchmishap.ui.theme.MatchMishapTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = FastOutSlowInEasing)
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
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_splash_screen),
            contentDescription = "Match & Mishap Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                SplashCardPairGraphic()

                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_match_mishap_logo),
                    contentDescription = "MATCH & MISHAP Logo",
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(180.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                StripedProgressBar(
                    progress = progress.value,
                    stripeOffset = stripeOffset,
                    modifier = Modifier
                        .width(260.dp)
                        .height(18.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "DEALING CARDS...",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00E5FF),
                        fontSize = 13.sp,
                        letterSpacing = 3.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }

            Text(
                text = "© MISCHIEF GAMES",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    letterSpacing = 2.sp
                ),
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
    }
}

@Composable
private fun SplashCardPairGraphic() {
    Box(
        modifier = Modifier.size(width = 180.dp, height = 120.dp),
        contentAlignment = Alignment.Center
    ) {
        CardView(
            card = Card("splash_teal7", CardColor.TEAL, CardType.NUMBER, 7),
            width = 72.dp,
            height = 108.dp,
            modifier = Modifier
                .offset(x = (-30).dp, y = 4.dp)
                .rotate(-14f)
                .shadow(10.dp, RoundedCornerShape(10.dp))
        )

        CardView(
            card = Card("splash_orange_uturn", CardColor.YELLOW, CardType.U_TURN),
            width = 72.dp,
            height = 108.dp,
            modifier = Modifier
                .offset(x = 30.dp, y = 4.dp)
                .rotate(14f)
                .shadow(10.dp, RoundedCornerShape(10.dp))
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
                drawRect(
                    color = darkTrackBg,
                    size = Size(filledWidth, height)
                )

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

                    drawPath(
                        path = stripePath,
                        color = color
                    )

                    startX += stripeWidth
                }
            }
        }
    }
}

@Preview(widthDp = 800, heightDp = 400)
@Composable
fun SplashScreenPreview() {
    MatchMishapTheme {
        SplashScreen()
    }
}
