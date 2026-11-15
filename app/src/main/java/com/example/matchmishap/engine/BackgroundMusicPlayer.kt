package com.example.matchmishap.engine

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

class BackgroundMusicPlayer(context: Context) {
    private var audioTrack: AudioTrack? = null
    private var musicJob: Job? = null
    private var isPlaying = false

    fun start() {
        if (isPlaying) return
        isPlaying = true

        musicJob = CoroutineScope(Dispatchers.IO).launch {
            val sampleRate = 22050
            val channelConfig = AudioFormat.CHANNEL_OUT_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat)

            try {
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setEncoding(audioFormat)
                            .setChannelMask(channelConfig)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack?.play()
            } catch (_: Exception) {
                isPlaying = false
                return@launch
            }

            // Relaxing melodic pentatonic arpeggio chord notes (Hz)
            val notes = doubleArrayOf(261.63, 329.63, 392.00, 523.25, 440.00, 392.00, 329.63, 293.66)
            val durationMs = 350
            val numSamples = (sampleRate * durationMs) / 1000
            val samples = ShortArray(numSamples)

            var index = 0
            while (isActive && isPlaying) {
                val freq = notes[index % notes.size]
                index++

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val envelope = 1.0 - (i.toDouble() / numSamples)
                    val sample = (sin(2.0 * Math.PI * freq * t) * 6000.0 * envelope).toInt().toShort()
                    samples[i] = sample
                }

                try {
                    audioTrack?.write(samples, 0, numSamples)
                } catch (_: Exception) {
                    break
                }
                delay(150L)
            }
        }
    }

    fun stop() {
        isPlaying = false
        musicJob?.cancel()
        musicJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }
}
