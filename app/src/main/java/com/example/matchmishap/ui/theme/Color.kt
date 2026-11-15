package com.example.matchmishap.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.matchmishap.model.CardColor

val PrimaryTeal = Color(0xFF1E88E5)
val OnPrimaryTeal = Color(0xFFFFFFFF)
val SecondaryPurple = Color(0xFF8E24AA)
val OnSecondaryPurple = Color(0xFFFFFFFF)
val DarkTableBackground = Color(0xFF0B192C)
val FeltGreen = Color(0xFF0F382C)
val FeltBorder = Color(0xFF1E5144)

// Card Specific Colors
val TealCardBg = Color(0xFF1E88E5)
val TealCardLight = Color(0xFF42A5F5)
val TealCardDark = Color(0xFF0D47A1)

val PurpleCardBg = Color(0xFF8E24AA)
val PurpleCardLight = Color(0xFFBA68C8)
val PurpleCardDark = Color(0xFF4A148C)

val YellowCardBg = Color(0xFFFFB300)
val YellowCardLight = Color(0xFFFFD54F)
val YellowCardDark = Color(0xFFFF6F00)

val GreenCardBg = Color(0xFF43A047)
val GreenCardLight = Color(0xFF81C784)
val GreenCardDark = Color(0xFF1B5E20)

val RedCardBg = Color(0xFFE53935)
val RedCardLight = Color(0xFFE57373)
val RedCardDark = Color(0xFFB71C1C)

val WildCardBg = Color(0xFF1E1E2C)
val WildBorderGold = Color(0xFFFFD700)

fun CardColor.toComposeColor(): Color = when (this) {
    CardColor.TEAL -> TealCardBg
    CardColor.PURPLE -> PurpleCardBg
    CardColor.YELLOW -> YellowCardBg
    CardColor.GREEN -> GreenCardBg
    CardColor.RED -> RedCardBg
    CardColor.WILD -> WildCardBg
}

fun CardColor.toLightComposeColor(): Color = when (this) {
    CardColor.TEAL -> TealCardLight
    CardColor.PURPLE -> PurpleCardLight
    CardColor.YELLOW -> YellowCardLight
    CardColor.GREEN -> GreenCardLight
    CardColor.RED -> RedCardLight
    CardColor.WILD -> WildBorderGold
}

fun CardColor.toDarkComposeColor(): Color = when (this) {
    CardColor.TEAL -> TealCardDark
    CardColor.PURPLE -> PurpleCardDark
    CardColor.YELLOW -> YellowCardDark
    CardColor.GREEN -> GreenCardDark
    CardColor.RED -> RedCardDark
    CardColor.WILD -> Color.Black
}
