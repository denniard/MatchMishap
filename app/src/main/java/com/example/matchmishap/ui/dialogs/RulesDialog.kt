package com.example.matchmishap.ui.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.RotateLeft
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun RulesDialog(
    onDismissRequest: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00B4D8)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier
                .width(420.dp)
                .height(340.dp)
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                        contentDescription = "Rules",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MATCH & MISHAP RULES",
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

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Rules content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    RuleSection(
                        title = "🎯 Objective",
                        description = "Be the first player (or team in 2v2) to play all cards from your hand!"
                    )

                    RuleSection(
                        title = "🎨 Card Matching",
                        description = "Match the active card by color (Teal, Purple, Yellow, Green, Red) or matching number / action type."
                    )

                    RuleItem(
                        icon = Icons.Rounded.HourglassEmpty,
                        iconTint = Color(0xFF00B4D8),
                        title = "PAUSE Card",
                        description = "Skips the next player's turn immediately."
                    )

                    RuleItem(
                        icon = Icons.AutoMirrored.Rounded.RotateLeft,
                        iconTint = Color(0xFF8E24AA),
                        title = "U-TURN Card",
                        description = "Reverses direction of play between Clockwise and Counter-Clockwise."
                    )

                    RuleItem(
                        icon = Icons.Rounded.AutoAwesome,
                        iconTint = Color(0xFFFFB300),
                        title = "DOUBLE UP (+2) Card",
                        description = "Forces next player to draw +2 cards! Can be stacked with another DOUBLE UP card to pass +4, +6, etc."
                    )

                    RuleItem(
                        icon = Icons.Rounded.AutoAwesome,
                        iconTint = Color(0xFFFFD700),
                        title = "COLOR CHAOS Wild Card",
                        description = "Can be played on any turn. Choose any color for the next player."
                    )

                    RuleSection(
                        title = "🤝 2v2 Partner Mode",
                        description = "You and Partner 1 play as Team 1 against Opponents A & B. Win as soon as either partner clears their hand!"
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismissRequest,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B4D8)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                ) {
                    Text(
                        text = "GOT IT!",
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

@Composable
private fun RuleSection(title: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700),
                fontSize = 13.sp
            )
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.LightGray,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
private fun RuleItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 12.sp
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.LightGray,
                    fontSize = 11.sp
                )
            )
        }
    }
}
