package com.example.matchmishap.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchmishap.R
import com.example.matchmishap.model.Card
import com.example.matchmishap.model.CardColor
import com.example.matchmishap.model.CardType
import com.example.matchmishap.ui.theme.MatchMishapTheme

private data class CardThemeColors(
    val baseColor: Color,
    val interiorColor: Color,
    val contourColor: Color,
    val starburstOuter: Color,
    val starburstInner: Color,
    val pillColor: Color,
    val sparkleColor: Color
)

private fun getCardColorTheme(color: CardColor): CardThemeColors = when (color) {
    CardColor.TEAL -> CardThemeColors(
        baseColor = Color(0xFF0D1B2A),
        interiorColor = Color(0xFF1E3A8A),
        contourColor = Color(0xFF38BDF8),
        starburstOuter = Color(0xFF3B82F6),
        starburstInner = Color(0xFF93C5FD),
        pillColor = Color(0xFF07101E),
        sparkleColor = Color(0xFF60A5FA)
    )
    CardColor.PURPLE -> CardThemeColors(
        baseColor = Color(0xFF22123B),
        interiorColor = Color(0xFF3A2369),
        contourColor = Color(0xFFAB47BC),
        starburstOuter = Color(0xFFFF6F43),
        starburstInner = Color(0xFFFF8D54),
        pillColor = Color(0xFF1C0F35),
        sparkleColor = Color(0xFF4D3285)
    )
    CardColor.YELLOW -> CardThemeColors(
        baseColor = Color(0xFF3E2B00),
        interiorColor = Color(0xFFD87D00),
        contourColor = Color(0xFFFFD54F),
        starburstOuter = Color(0xFFFFB300),
        starburstInner = Color(0xFFFFF176),
        pillColor = Color(0xFF261B00),
        sparkleColor = Color(0xFFFFE082)
    )
    CardColor.GREEN -> CardThemeColors(
        baseColor = Color(0xFF0B2E0E),
        interiorColor = Color(0xFF1B5E20),
        contourColor = Color(0xFF4CAF50),
        starburstOuter = Color(0xFF66BB6A),
        starburstInner = Color(0xFFA5D6A7),
        pillColor = Color(0xFF061A08),
        sparkleColor = Color(0xFF81C784)
    )
    CardColor.RED -> CardThemeColors(
        baseColor = Color(0xFF380808),
        interiorColor = Color(0xFFB71C1C),
        contourColor = Color(0xFFEF5350),
        starburstOuter = Color(0xFFFF7043),
        starburstInner = Color(0xFFFFCC80),
        pillColor = Color(0xFF210404),
        sparkleColor = Color(0xFFE57373)
    )
    CardColor.WILD -> CardThemeColors(
        baseColor = Color(0xFF1A0E2E),
        interiorColor = Color(0xFF28114A),
        contourColor = Color(0xFFFFD700),
        starburstOuter = Color(0xFFE53935),
        starburstInner = Color(0xFFFFB300),
        pillColor = Color(0xFF1C0F35),
        sparkleColor = Color(0xFFFFD700)
    )
}

@Composable
fun CardView(
    card: Card,
    modifier: Modifier = Modifier,
    isPlayable: Boolean = false,
    isSelected: Boolean = false,
    width: Dp = 76.dp,
    height: Dp = 114.dp,
    overrideColor: CardColor? = null,
    onClick: (() -> Unit)? = null
) {
    val playableTransition = rememberInfiniteTransition(label = "PlayableGlow")
    val playableGlowAlpha by playableTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PlayableGlowAlpha"
    )

    val shadowElevation = when {
        isSelected -> 12.dp
        isPlayable -> 8.dp
        else -> 4.dp
    }

    val yOffset = when {
        isSelected -> (-14).dp
        isPlayable -> (-6).dp
        else -> 0.dp
    }

    val effectiveColor = if (card.color == CardColor.WILD && overrideColor != null && !overrideColor.isWild) {
        overrideColor
    } else {
        card.color
    }

    val borderColor = when {
        isSelected -> Color(0xFFFFD700)
        isPlayable -> Color(0xFFFFD700).copy(alpha = playableGlowAlpha)
        effectiveColor == CardColor.WILD -> Color(0xFFFFD700)
        else -> Color.White.copy(alpha = 0.9f)
    }

    val borderWidth = when {
        isSelected -> 3.dp
        isPlayable -> 2.5.dp
        else -> 1.5.dp
    }

    val theme = getCardColorTheme(effectiveColor)

    Box(
        modifier = modifier
            .offset(y = yOffset)
            .width(width)
            .height(height)
            .shadow(elevation = shadowElevation, shape = RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .border(width = borderWidth, color = borderColor, shape = RoundedCornerShape(10.dp))
            .then(
                if (onClick != null) {
                    Modifier.clickable(enabled = isPlayable, onClick = onClick)
                } else Modifier
            )
    ) {
        CardContent(
            card = card,
            theme = theme,
            cardWidth = width
        )
    }
}

@Composable
private fun CardContent(
    card: Card,
    theme: CardThemeColors,
    cardWidth: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.baseColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val path = Path().apply {
                moveTo(w * 0.3f, h * 0.03f)
                lineTo(w * 0.93f, h * 0.03f)
                cubicTo(w * 0.95f, h * 0.03f, w * 0.96f, h * 0.04f, w * 0.96f, h * 0.06f)
                lineTo(w * 0.96f, h * 0.76f)
                cubicTo(w * 0.96f, h * 0.79f, w * 0.92f, h * 0.81f, w * 0.87f, h * 0.81f)
                lineTo(w * 0.72f, h * 0.81f)
                cubicTo(w * 0.67f, h * 0.81f, w * 0.65f, h * 0.83f, w * 0.65f, h * 0.86f)
                lineTo(w * 0.65f, h * 0.97f)
                lineTo(w * 0.07f, h * 0.97f)
                cubicTo(w * 0.05f, h * 0.97f, w * 0.04f, h * 0.96f, w * 0.04f, h * 0.94f)
                lineTo(w * 0.04f, h * 0.24f)
                cubicTo(w * 0.04f, h * 0.21f, w * 0.08f, h * 0.19f, w * 0.13f, h * 0.19f)
                lineTo(w * 0.28f, h * 0.19f)
                cubicTo(w * 0.33f, h * 0.19f, w * 0.35f, h * 0.17f, w * 0.35f, h * 0.14f)
                lineTo(w * 0.35f, h * 0.05f)
                close()
            }

            drawPath(path = path, color = theme.interiorColor)
            drawPath(path = path, color = theme.contourColor, style = Stroke(width = 2.dp.toPx()))

            val cx = w * 0.5f
            val cy = h * 0.5f
            val outerR = w * 0.45f
            val innerR = w * 0.28f
            val points = 16

            val burstPath = Path()
            for (i in 0 until (points * 2)) {
                val r = if (i % 2 == 0) outerR else innerR
                val angle = (i * Math.PI / points).toFloat()
                val x = cx + r * kotlin.math.cos(angle)
                val y = cy + r * kotlin.math.sin(angle)
                if (i == 0) burstPath.moveTo(x, y) else burstPath.lineTo(x, y)
            }
            burstPath.close()

            drawPath(path = burstPath, color = theme.starburstOuter.copy(alpha = 0.85f))

            val innerBurstPath = Path()
            for (i in 0 until (points * 2)) {
                val r = if (i % 2 == 0) outerR * 0.72f else innerR * 0.62f
                val angle = (i * Math.PI / points + Math.PI / points / 2).toFloat()
                val x = cx + r * kotlin.math.cos(angle)
                val y = cy + r * kotlin.math.sin(angle)
                if (i == 0) innerBurstPath.moveTo(x, y) else innerBurstPath.lineTo(x, y)
            }
            innerBurstPath.close()
            drawPath(path = innerBurstPath, color = theme.starburstInner)
        }

        CornerBadgeContent(
            card = card,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 4.dp, start = 5.dp)
        )

        CornerBadgeContent(
            card = card,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .rotate(180f)
                .padding(bottom = 4.dp, end = 5.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 6.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            when (card.type) {
                CardType.NUMBER -> NumberCardCenter(
                    numberValue = card.numberValue ?: 0,
                    cardWidth = cardWidth
                )
                CardType.DOUBLE_UP -> DoubleUpCardCenter(
                    theme = theme,
                    cardWidth = cardWidth
                )
                CardType.PLUS_FOUR -> PlusFourCardCenter(
                    theme = theme,
                    cardWidth = cardWidth
                )
                CardType.PAUSE -> PauseCardCenter(
                    theme = theme,
                    cardWidth = cardWidth
                )
                CardType.U_TURN -> UTurnCardCenter(
                    theme = theme,
                    cardWidth = cardWidth
                )
                CardType.COLOR_CHAOS -> ColorChaosCardCenter(
                    theme = theme,
                    cardWidth = cardWidth
                )
            }
        }
    }
}

@Composable
private fun NumberCardCenter(
    numberValue: Int,
    cardWidth: Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$numberValue",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = if (cardWidth > 80.dp) 44.sp else 32.sp,
                color = Color.White,
                fontFamily = FontFamily.SansSerif
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DoubleUpCardCenter(
    theme: CardThemeColors,
    cardWidth: Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxHeight().padding(vertical = 4.dp)
    ) {
        Text(
            text = "+2",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = if (cardWidth > 80.dp) 20.sp else 15.sp,
                color = Color.White,
                fontFamily = FontFamily.SansSerif
            )
        )

        PillBanner(text = "DOUBLE UP", pillColor = theme.pillColor, cardWidth = cardWidth)

        Image(
            painter = painterResource(id = R.drawable.ic_character_mischief),
            contentDescription = null,
            modifier = Modifier.size(if (cardWidth > 80.dp) 32.dp else 22.dp)
        )

        PillBanner(text = "DOUBLE UP", pillColor = theme.pillColor, cardWidth = cardWidth)

        Text(
            text = "+2",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = if (cardWidth > 80.dp) 20.sp else 15.sp,
                color = Color.White,
                fontFamily = FontFamily.SansSerif
            )
        )
    }
}

@Composable
private fun PauseCardCenter(
    theme: CardThemeColors,
    cardWidth: Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxHeight().padding(vertical = 4.dp)
    ) {
        Text(
            text = "||",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = if (cardWidth > 80.dp) 18.sp else 14.sp,
                color = Color.White,
                fontFamily = FontFamily.SansSerif
            )
        )

        PillBanner(text = "PAUSE", pillColor = theme.pillColor, cardWidth = cardWidth)

        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(if (cardWidth > 80.dp) 8.dp else 6.dp)
                    .height(if (cardWidth > 80.dp) 24.dp else 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White)
            )
            Box(
                modifier = Modifier
                    .width(if (cardWidth > 80.dp) 8.dp else 6.dp)
                    .height(if (cardWidth > 80.dp) 24.dp else 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White)
            )
        }

        PillBanner(text = "PAUSE", pillColor = theme.pillColor, cardWidth = cardWidth)

        Text(
            text = "||",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = if (cardWidth > 80.dp) 18.sp else 14.sp,
                color = Color.White,
                fontFamily = FontFamily.SansSerif
            )
        )
    }
}

@Composable
private fun UTurnCardCenter(
    theme: CardThemeColors,
    cardWidth: Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxHeight().padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(if (cardWidth > 80.dp) 18.dp else 14.dp).rotate(180f)
        )

        PillBanner(text = "U-TURN", pillColor = theme.pillColor, cardWidth = cardWidth)

        Icon(
            imageVector = Icons.Rounded.Refresh,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(if (cardWidth > 80.dp) 32.dp else 22.dp)
        )

        PillBanner(text = "U-TURN", pillColor = theme.pillColor, cardWidth = cardWidth)

        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(if (cardWidth > 80.dp) 18.dp else 14.dp)
        )
    }
}

@Composable
private fun ColorChaosCardCenter(
    theme: CardThemeColors,
    cardWidth: Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxHeight().padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (cardWidth > 80.dp) 16.dp else 12.dp)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        listOf(
                            Color(0xFF00B4D8),
                            Color(0xFFAB47BC),
                            Color(0xFFE53935),
                            Color(0xFFFFB300),
                            Color(0xFF43A047),
                            Color(0xFF00B4D8)
                        )
                    )
                )
        )

        PillBanner(text = "COLOR CHAOS", pillColor = theme.pillColor, cardWidth = cardWidth)

        Box(
            modifier = Modifier
                .size(if (cardWidth > 80.dp) 32.dp else 22.dp)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        listOf(
                            Color(0xFF00B4D8),
                            Color(0xFF8E24AA),
                            Color(0xFFE53935),
                            Color(0xFFFFB300),
                            Color(0xFF43A047),
                            Color(0xFF00B4D8)
                        )
                    )
                )
                .border(1.5.dp, Color.White, CircleShape)
        )

        PillBanner(text = "COLOR CHAOS", pillColor = theme.pillColor, cardWidth = cardWidth)

        Box(
            modifier = Modifier
                .size(if (cardWidth > 80.dp) 16.dp else 12.dp)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        listOf(
                            Color(0xFF00B4D8),
                            Color(0xFFAB47BC),
                            Color(0xFFE53935),
                            Color(0xFFFFB300),
                            Color(0xFF43A047),
                            Color(0xFF00B4D8)
                        )
                    )
                )
        )
    }
}

@Composable
private fun PillBanner(
    text: String,
    pillColor: Color,
    cardWidth: Dp
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(pillColor)
            .padding(horizontal = if (cardWidth > 80.dp) 8.dp else 4.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                fontSize = if (cardWidth > 80.dp) 9.sp else 7.sp,
                color = Color.White
            )
        )
    }
}

@Composable
private fun PlusFourCardCenter(
    theme: CardThemeColors,
    cardWidth: Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxHeight().padding(vertical = 4.dp)
    ) {
        Text(
            text = "+4",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = if (cardWidth > 80.dp) 20.sp else 15.sp,
                color = Color.White,
                fontFamily = FontFamily.SansSerif
            )
        )

        PillBanner(text = "PLUS FOUR", pillColor = theme.pillColor, cardWidth = cardWidth)

        Image(
            painter = painterResource(id = R.drawable.ic_character_mischief),
            contentDescription = null,
            modifier = Modifier.size(if (cardWidth > 80.dp) 32.dp else 22.dp)
        )

        PillBanner(text = "PLUS FOUR", pillColor = theme.pillColor, cardWidth = cardWidth)

        Text(
            text = "+4",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = if (cardWidth > 80.dp) 20.sp else 15.sp,
                color = Color.White,
                fontFamily = FontFamily.SansSerif
            )
        )
    }
}

@Composable
private fun CornerBadgeContent(
    card: Card,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        val badgeText = when (card.type) {
            CardType.NUMBER -> "${card.numberValue ?: 0}"
            CardType.DOUBLE_UP -> "+2"
            CardType.PLUS_FOUR -> "+4"
            CardType.PAUSE -> "||"
            CardType.U_TURN -> "↺"
            CardType.COLOR_CHAOS -> "★"
        }
        Text(
            text = badgeText,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                fontSize = 9.sp,
                color = Color.White
            )
        )
    }
}

@Composable
fun CardBackView(
    modifier: Modifier = Modifier,
    width: Dp = 76.dp,
    height: Dp = 114.dp,
    label: String = "MATCH & MISHAP",
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .border(width = 1.5.dp, color = Color(0xFFFFD700), shape = RoundedCornerShape(10.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_card_back),
            contentDescription = "MATCH & MISHAP Card Back",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
fun CardViewPreviews() {
    MatchMishapTheme {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CardView(
                card = Card("1", CardColor.GREEN, CardType.NUMBER, 7),
                isPlayable = true
            )
            CardView(
                card = Card("2", CardColor.PURPLE, CardType.PAUSE)
            )
            CardView(
                card = Card("3", CardColor.YELLOW, CardType.U_TURN)
            )
            CardView(
                card = Card("4", CardColor.RED, CardType.DOUBLE_UP)
            )
            CardView(
                card = Card("5", CardColor.WILD, CardType.COLOR_CHAOS),
                overrideColor = CardColor.TEAL,
                isPlayable = true
            )
            CardBackView()
        }
    }
}
