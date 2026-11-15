package com.example.matchmishap.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.matchmishap.model.CardColor
import com.example.matchmishap.ui.theme.CardColorUtils

@Composable
fun ColorChaosPickerDialog(
    onColorSelected: (CardColor) -> Unit,
    onDismissRequest: () -> Unit = {}
) {
    val selectableColors = listOf(
        CardColor.TEAL,
        CardColor.PURPLE,
        CardColor.YELLOW,
        CardColor.GREEN,
        CardColor.RED
    )

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F172A)
            ),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD700)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier
                .width(340.dp)
                .padding(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFD700))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = "Color Chaos",
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "COLOR CHAOS!",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD700),
                        fontSize = 20.sp
                    )
                )

                Text(
                    text = "Select the new active color to continue play:",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Color Selection Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    selectableColors.forEach { color ->
                        ColorOptionButton(
                            cardColor = color,
                            onClick = { onColorSelected(color) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorOptionButton(
    cardColor: CardColor,
    onClick: () -> Unit
) {
    val mainColor = CardColorUtils.getMainColor(cardColor)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(mainColor)
                .border(2.5.dp, Color.White, CircleShape)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = cardColor.displayName,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 11.sp
            )
        )
    }
}
