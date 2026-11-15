package com.example.matchmishap.engine

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class LeaderboardEntry(
    val playerId: String = "",
    val playerName: String = "",
    val wins: Int = 0,
    val points: Int = 0
)

object LeaderboardManager {
    private val db = FirebaseFirestore.getInstance()
    private val lbRef = db.collection("leaderboard")

    suspend fun submitScore(playerId: String, playerName: String, wonGame: Boolean, scoreDelta: Int) {
        val docRef = lbRef.document(playerId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val currentWins = if (snapshot.exists()) (snapshot.getLong("wins") ?: 0L).toInt() else 0
            val currentPoints = if (snapshot.exists()) (snapshot.getLong("points") ?: 0L).toInt() else 0

            val newWins = currentWins + if (wonGame) 1 else 0
            val newPoints = currentPoints + scoreDelta

            val entry = LeaderboardEntry(
                playerId = playerId,
                playerName = playerName,
                wins = newWins,
                points = newPoints
            )
            transaction.set(docRef, entry)
        }.await()
    }

    fun getTopLeaderboard(): Flow<List<LeaderboardEntry>> = callbackFlow {
        val listener = lbRef.orderBy("wins", Query.Direction.DESCENDING)
            .orderBy("points", Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val entries = snapshot.documents.mapNotNull { doc -> doc.toObject(LeaderboardEntry::class.java) }
                trySend(entries)
            }
        awaitClose { listener.remove() }
    }
}
