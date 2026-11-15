package com.example.matchmishap.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.matchmishap.engine.LeaderboardManager

@Composable
fun LeaderboardDialog(
    onDismissRequest: () -> Unit
) {
    val leaderboardEntries by LeaderboardManager.getTopLeaderboard().collectAsState(initial = emptyList())

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD700)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier
                .width(400.dp)
                .height(460.dp)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EmojiEvents,
                        contentDescription = "Leaderboard",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GLOBAL LEADERBOARD",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD700),
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Rank & Player", style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray, fontWeight = FontWeight.Bold))
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Text(text = "Wins", style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray, fontWeight = FontWeight.Bold))
                        Text(text = "Points", style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray, fontWeight = FontWeight.Bold))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (leaderboardEntries.isEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No leaderboard entries yet.\nPlay online matches to rank up!",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        itemsIndexed(leaderboardEntries) { index, entry ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = when (index) {
                                    0 -> Color(0xFFFFD700).copy(alpha = 0.2f)
                                    1 -> Color(0xFFC0C0C0).copy(alpha = 0.2f)
                                    2 -> Color(0xFFCD7F32).copy(alpha = 0.2f)
                                    else -> Color(0xFF1E293B)
                                },
                                border = if (index < 3) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700)) else null
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "#${index + 1}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                color = if (index == 0) Color(0xFFFFD700) else Color.White
                                            ),
                                            modifier = Modifier.width(32.dp)
                                        )
                                        Text(
                                            text = entry.playerName,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                                        Text(
                                            text = "${entry.wins}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF4CAF50)
                                            ),
                                            modifier = Modifier.width(20.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                                        )
                                        Text(
                                            text = "${entry.points}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFD700)
                                            ),
                                            modifier = Modifier.width(32.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismissRequest,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B4D8)),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Text(
                        text = "CLOSE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    )
                }
            }
        }
    }
}
