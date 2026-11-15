package com.example.matchmishap.engine

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class AudioAnnouncer(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setSpeechRate(0.85f)
                isInitialized = true
            }
        }
    }

    fun speakForHuman(text: String, humanName: String = "You") {
        if (!isInitialized || text.isBlank()) return

        val lower = text.lowercase()
        val involvesHuman = lower.contains(humanName.lowercase()) || lower.contains("you")
        val isImportant = lower.contains("draw") ||
                lower.contains("penalty") ||
                lower.contains("skip") ||
                lower.contains("pause") ||
                lower.contains("u-turn") ||
                lower.contains("wild") ||
                lower.contains("chaos") ||
                lower.contains("wins") ||
                lower.contains("game over")

        if (involvesHuman && isImportant) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
    }
}
