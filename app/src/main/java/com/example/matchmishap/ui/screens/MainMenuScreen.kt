package com.example.matchmishap.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import com.example.matchmishap.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchmishap.model.Card
import com.example.matchmishap.model.CardColor
import com.example.matchmishap.model.CardType
import com.example.matchmishap.model.GameMode
import com.example.matchmishap.ui.components.CardView
import com.example.matchmishap.ui.dialogs.LeaderboardDialog
import com.example.matchmishap.ui.dialogs.RulesDialog
import com.example.matchmishap.ui.dialogs.SettingsDialog
import com.example.matchmishap.ui.theme.MatchMishapTheme

@Composable
fun MainMenuScreen(
    onStartGame: (GameMode) -> Unit,
    onOpenOnline: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showRulesModal by remember { mutableStateOf(false) }
    var showSettingsModal by remember { mutableStateOf(false) }
    var showLeaderboardModal by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_card_pattern),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawCircle(
                color = Color(0xFF8E24AA).copy(alpha = 0.15f),
                radius = w * 0.35f,
                center = androidx.compose.ui.geometry.Offset(w * 0.15f, h * 0.25f)
            )
            drawCircle(
                color = Color(0xFF00B4D8).copy(alpha = 0.12f),
                radius = w * 0.35f,
                center = androidx.compose.ui.geometry.Offset(w * 0.85f, h * 0.75f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HeroCardBanner(modifier = Modifier.weight(0.40f))

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(0.60f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_match_mishap_logo),
                        contentDescription = "MATCH & MISHAP Logo",
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(68.dp)
                            .padding(bottom = 6.dp)
                    )

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF120822).copy(alpha = 0.85f)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF8E24AA)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier.fillMaxWidth(0.95f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Row 1: Solo & 2v2
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = { onStartGame(GameMode.FREE_FOR_ALL) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .background(Brush.horizontalGradient(listOf(Color(0xFFFF9800), Color(0xFFF57C00))), RoundedCornerShape(12.dp))
                                        .border(1.2.dp, Color(0xFFFFD700), RoundedCornerShape(12.dp))
                                ) {
                                    Icon(imageVector = Icons.Rounded.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "SOLO", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color.White))
                                }

                                Button(
                                    onClick = { onStartGame(GameMode.TWO_VS_TWO) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .background(Brush.horizontalGradient(listOf(Color(0xFF00B4D8), Color(0xFF009688))), RoundedCornerShape(12.dp))
                                        .border(1.2.dp, Color(0xFFFFD700), RoundedCornerShape(12.dp))
                                ) {
                                    Icon(imageVector = Icons.Rounded.Groups, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "2V2", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color.White))
                                }
                            }

                            // Row 2: Online & Leaderboard
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = onOpenOnline,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .background(Brush.horizontalGradient(listOf(Color(0xFFE91E63), Color(0xFF9C27B0))), RoundedCornerShape(12.dp))
                                        .border(1.2.dp, Color(0xFFFFD700), RoundedCornerShape(12.dp))
                                ) {
                                    Icon(imageVector = Icons.Rounded.Public, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "ONLINE", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color.White))
                                }

                                Button(
                                    onClick = { showLeaderboardModal = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .background(Brush.horizontalGradient(listOf(Color(0xFFFFB300), Color(0xFFF57F17))), RoundedCornerShape(12.dp))
                                        .border(1.2.dp, Color.White, RoundedCornerShape(12.dp))
                                ) {
                                    Icon(imageVector = Icons.Rounded.EmojiEvents, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "RANKS", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color.Black))
                                }
                            }

                            // Row 3: Rules & Settings
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = { showRulesModal = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .background(Brush.horizontalGradient(listOf(Color(0xFF5C6BC0), Color(0xFF3F51B5))), RoundedCornerShape(12.dp))
                                        .border(1.2.dp, Color(0xFFFFD700).copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                                ) {
                                    Icon(imageVector = Icons.AutoMirrored.Rounded.MenuBook, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "RULES", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color.White))
                                }

                                Button(
                                    onClick = { showSettingsModal = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .background(Brush.horizontalGradient(listOf(Color(0xFF7986CB), Color(0xFF303F9F))), RoundedCornerShape(12.dp))
                                        .border(1.2.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                                ) {
                                    Icon(imageVector = Icons.Rounded.Settings, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "CONFIG", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color.White))
                                }
                            }
                        }
                    }
                }
            }

            Text(
                text = "© MISCHIEF GAMES",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp
                ),
                textAlign = TextAlign.Center
            )
        }

        if (showRulesModal) {
            RulesDialog(onDismissRequest = { showRulesModal = false })
        }

        if (showSettingsModal) {
            SettingsDialog(onDismissRequest = { showSettingsModal = false })
        }

        if (showLeaderboardModal) {
            LeaderboardDialog(onDismissRequest = { showLeaderboardModal = false })
        }
    }
}

@Composable
private fun HeroCardBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.height(150.dp),
        contentAlignment = Alignment.Center
    ) {
        CardView(
            card = Card("c1", CardColor.PURPLE, CardType.PAUSE),
            width = 58.dp,
            height = 87.dp,
            modifier = Modifier.offset(x = (-35).dp, y = 6.dp).rotate(-20f)
        )
        CardView(
            card = Card("c2", CardColor.YELLOW, CardType.U_TURN),
            width = 58.dp,
            height = 87.dp,
            modifier = Modifier.offset(x = (-18).dp, y = (-5).dp).rotate(-10f)
        )
        CardView(
            card = Card("c3", CardColor.WILD, CardType.COLOR_CHAOS),
            width = 66.dp,
            height = 99.dp,
            modifier = Modifier.offset(x = 0.dp, y = (-14).dp).rotate(0f)
        )
        CardView(
            card = Card("c4", CardColor.RED, CardType.DOUBLE_UP),
            width = 58.dp,
            height = 87.dp,
            modifier = Modifier.offset(x = 18.dp, y = (-5).dp).rotate(10f)
        )
        CardView(
            card = Card("c5", CardColor.TEAL, CardType.NUMBER, 7),
            width = 58.dp,
            height = 87.dp,
            modifier = Modifier.offset(x = 35.dp, y = 6.dp).rotate(20f)
        )

        Image(
            painter = painterResource(id = R.drawable.ic_character_mischief),
            contentDescription = "Mascot Mischief",
            modifier = Modifier
                .size(54.dp)
                .offset(y = (-38).dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .border(2.dp, Color(0xFFFFD700), CircleShape)
        )
    }
}

@Preview(widthDp = 800, heightDp = 400)
@Composable
fun MainMenuScreenPreview() {
    MatchMishapTheme {
        MainMenuScreen(onStartGame = {}, onOpenOnline = {}, onOpenLeaderboard = {})
    }
}
