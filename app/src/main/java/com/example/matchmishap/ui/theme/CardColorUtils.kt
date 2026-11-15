package com.example.matchmishap.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.matchmishap.model.CardColor

object CardColorUtils {

    fun getMainColor(color: CardColor): Color = when (color) {
        CardColor.TEAL -> Color(0xFF1E88E5) // Vibrant Blue
        CardColor.PURPLE -> Color(0xFF8E24AA)
        CardColor.YELLOW -> Color(0xFFF57F17)
        CardColor.GREEN -> Color(0xFF388E3C)
        CardColor.RED -> Color(0xFFD32F2F)
        CardColor.WILD -> Color(0xFF1E1E2C)
    }

    fun getBackgroundBrush(color: CardColor): Brush = when (color) {
        CardColor.TEAL -> Brush.linearGradient(
            colors = listOf(Color(0xFF42A5F5), Color(0xFF1E88E5), Color(0xFF0D47A1))
        )
        CardColor.PURPLE -> Brush.linearGradient(
            colors = listOf(Color(0xFFAB47BC), Color(0xFF8E24AA), Color(0xFF4A148C))
        )
        CardColor.YELLOW -> Brush.linearGradient(
            colors = listOf(Color(0xFFFFD54F), Color(0xFFFFB300), Color(0xFFE65100))
        )
        CardColor.GREEN -> Brush.linearGradient(
            colors = listOf(Color(0xFF66BB6A), Color(0xFF43A047), Color(0xFF1B5E20))
        )
        CardColor.RED -> Brush.linearGradient(
            colors = listOf(Color(0xFFEF5350), Color(0xFFE53935), Color(0xFFB71C1C))
        )
        CardColor.WILD -> Brush.sweepGradient(
            colors = listOf(
                Color(0xFF1E88E5),
                Color(0xFF8E24AA),
                Color(0xFFE53935),
                Color(0xFFFFB300),
                Color(0xFF43A047),
                Color(0xFF1E88E5)
            )
        )
    }
}
