package com.example.matchmishap.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.matchmishap.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchmishap.model.Card
import com.example.matchmishap.model.CardColor
import com.example.matchmishap.model.Player

@Composable
fun PlayerAvatarBadge(
    player: Player,
    isCurrentTurn: Boolean,
    modifier: Modifier = Modifier,
    avatarIcon: ImageVector = Icons.Rounded.Person,
    avatarPainterRes: Int? = null,
    isPartner: Boolean = false
) {
    val pulseTransition = rememberInfiniteTransition(label = "TurnPulse")
    val pulseBorderAlpha by pulseTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    val avatarBg = when {
        isCurrentTurn -> Brush.radialGradient(
            colors = listOf(Color(0xFFFFD700), Color(0xFFFF8F00))
        )
        isPartner -> Brush.linearGradient(
            colors = listOf(Color(0xFF0288D1), Color(0xFF01579B))
        )
        player.isHuman -> Brush.linearGradient(
            colors = listOf(Color(0xFF00B4D8), Color(0xFF0077B6))
        )
        else -> Brush.linearGradient(
            colors = listOf(Color(0xFF673AB7), Color(0xFF311B92))
        )
    }

    val borderColor = if (isCurrentTurn) Color(0xFFFFD700).copy(alpha = pulseBorderAlpha) else Color.White.copy(alpha = 0.6f)
    val borderWidth = if (isCurrentTurn) 2.5.dp else 1.2.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        BadgedBox(
            badge = {
                Badge(
                    containerColor = if (isCurrentTurn) Color(0xFFFFD700) else Color(0xFF1E293B),
                    contentColor = if (isCurrentTurn) Color.Black else Color.White
                ) {
                    Text(
                        text = "${player.cardCount} cards",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    )
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(avatarBg)
                    .border(borderWidth, borderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (avatarPainterRes != null) {
                    Image(
                        painter = painterResource(id = avatarPainterRes),
                        contentDescription = player.name,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = avatarIcon,
                        contentDescription = player.name,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isCurrentTurn) Color(0xFFFFD700) else Color(0xFF0F172A).copy(alpha = 0.85f),
            shadowElevation = 2.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                if (isPartner) {
                    Icon(
                        imageVector = Icons.Rounded.Groups,
                        contentDescription = "Partner",
                        tint = if (isCurrentTurn) Color.Black else Color(0xFF29B6F6),
                        modifier = Modifier
                            .size(10.dp)
                            .padding(end = 2.dp)
                    )
                }
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isCurrentTurn) Color.Black else Color.White,
                        fontSize = 9.sp
                    )
                )
            }
        }
    }
}

@Composable
fun TopPartnerSeat(
    partnerPlayer: Player,
    isCurrentTurn: Boolean,
    is2v2Mode: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            PartnerHandFanView(
                hand = partnerPlayer.hand,
                is2v2Mode = is2v2Mode
            )
        }

        PlayerAvatarBadge(
            player = partnerPlayer,
            isCurrentTurn = isCurrentTurn,
            avatarPainterRes = R.drawable.ic_avatar_partner,
            isPartner = is2v2Mode,
            modifier = Modifier.padding(end = 12.dp)
        )
    }
}

@Composable
private fun PartnerHandFanView(
    hand: List<Card>,
    is2v2Mode: Boolean
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.height(76.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        if (is2v2Mode) {
            // Display teammate's face-up cards so you can see your partner's hand!
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy((-12).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                hand.forEach { card ->
                    CardView(
                        card = card,
                        isPlayable = false,
                        width = 48.dp,
                        height = 72.dp
                    )
                }
            }
        } else {
            val displayCount = hand.size.coerceAtMost(7)
            for (i in 0 until displayCount) {
                val rotationAngle = (i - displayCount / 2f) * 6f
                val xOffset = ((i - displayCount / 2f) * 12).dp

                CardBackView(
                    width = 24.dp,
                    height = 36.dp,
                    label = "",
                    modifier = Modifier
                        .offset(x = xOffset)
                        .rotate(rotationAngle)
                )
            }
        }
    }
}

@Composable
fun LeftOpponentSeat(
    opponentPlayer: Player,
    isCurrentTurn: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        PlayerAvatarBadge(
            player = opponentPlayer,
            isCurrentTurn = isCurrentTurn,
            avatarPainterRes = R.drawable.ic_avatar_opponent_a
        )
        Spacer(modifier = Modifier.height(2.dp))
        OpponentCardStack(cardCount = opponentPlayer.cardCount)
    }
}

@Composable
fun RightOpponentSeat(
    opponentPlayer: Player,
    isCurrentTurn: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        PlayerAvatarBadge(
            player = opponentPlayer,
            isCurrentTurn = isCurrentTurn,
            avatarPainterRes = R.drawable.ic_avatar_opponent_b
        )
        Spacer(modifier = Modifier.height(2.dp))
        OpponentCardStack(cardCount = opponentPlayer.cardCount)
    }
}

@Composable
private fun OpponentCardStack(cardCount: Int) {
    val stackSize = cardCount.coerceAtMost(4)
    Box(
        modifier = Modifier
            .width(32.dp)
            .height(42.dp),
        contentAlignment = Alignment.Center
    ) {
        for (i in 0 until stackSize) {
            CardBackView(
                width = 26.dp,
                height = 38.dp,
                label = "",
                modifier = Modifier.offset(x = (i * 2).dp, y = (i * 2).dp)
            )
        }
    }
}

@Composable
fun PlayerHandTray(
    humanPlayer: Player,
    isHumanTurn: Boolean,
    topCard: Card,
    activeColor: CardColor,
    accumulatedPenalty: Int,
    onPlayCard: (Card) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
    ) {
        PlayerAvatarBadge(
            player = humanPlayer,
            isCurrentTurn = isHumanTurn,
            avatarPainterRes = R.drawable.ic_avatar_player,
            modifier = Modifier.padding(start = 8.dp, bottom = 1.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Surface(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp, bottom = 0.dp),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            color = Color(0xFF0A1118).copy(alpha = 0.88f),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isHumanTurn) Color(0xFFFFD700).copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f)
            )
        ) {
            Column(
                modifier = Modifier.padding(top = 2.dp, bottom = 3.dp, start = 6.dp, end = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "YOUR HAND (${humanPlayer.hand.size} Cards)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isHumanTurn) Color(0xFFFFD700) else Color.LightGray,
                            fontSize = 9.sp
                        )
                    )

                    if (isHumanTurn) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFD700),
                            shadowElevation = 2.dp
                        ) {
                            Text(
                                text = if (accumulatedPenalty > 0) "PLAY +2 OR DRAW $accumulatedPenalty!" else "YOUR TURN!",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black,
                                    fontSize = 8.sp
                                ),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(1.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                        .padding(vertical = 1.dp),
                    horizontalArrangement = Arrangement.spacedBy((-18).dp)
                ) {
                    humanPlayer.hand.forEach { card ->
                        val isPlayable = isHumanTurn && card.canPlayOn(
                            topCard = topCard,
                            activeColor = activeColor,
                            currentPenalty = accumulatedPenalty
                        )

                        CardView(
                            card = card,
                            isPlayable = isPlayable,
                            width = 68.dp,
                            height = 102.dp,
                            onClick = {
                                if (isPlayable) {
                                    onPlayCard(card)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
