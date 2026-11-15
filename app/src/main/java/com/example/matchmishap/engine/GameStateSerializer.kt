package com.example.matchmishap.engine

import com.example.matchmishap.model.GameState
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object GameStateSerializer {
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    fun toJson(state: GameState): String {
        return try {
            moshi.adapter(GameState::class.java).toJson(state)
        } catch (e: Exception) {
            ""
        }
    }

    fun fromJson(json: String): GameState? {
        if (json.isBlank()) return null
        return try {
            moshi.adapter(GameState::class.java).fromJson(json)
        } catch (e: Exception) {
            null
        }
    }
}
