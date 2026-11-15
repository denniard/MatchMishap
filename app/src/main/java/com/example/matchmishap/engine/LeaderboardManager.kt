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
    private val localEntries = mutableListOf(
        LeaderboardEntry("1", "MischiefMaster", 42, 1250),
        LeaderboardEntry("2", "CardShark", 35, 980),
        LeaderboardEntry("3", "BluffKing", 28, 810)
    )

    private val db: FirebaseFirestore? = try {
        FirebaseFirestore.getInstance()
    } catch (_: Exception) {
        null
    }

    suspend fun submitScore(playerId: String, playerName: String, wonGame: Boolean, scoreDelta: Int) {
        try {
            if (db != null) {
                val docRef = db.collection("leaderboard").document(playerId)
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(docRef)
                    val currentWins = if (snapshot.exists()) (snapshot.getLong("wins") ?: 0L).toInt() else 0
                    val currentPoints = if (snapshot.exists()) (snapshot.getLong("points") ?: 0L).toInt() else 0

                    val newWins = currentWins + if (wonGame) 1 else 0
                    val newPoints = currentPoints + scoreDelta

                    val entry = LeaderboardEntry(playerId, playerName, newWins, newPoints)
                    transaction.set(docRef, entry)
                }.await()
                return
            }
        } catch (_: Exception) {}

        val existing = localEntries.find { it.playerId == playerId }
        if (existing != null) {
            localEntries.remove(existing)
            localEntries.add(existing.copy(playerName = playerName, wins = existing.wins + if (wonGame) 1 else 0, points = existing.points + scoreDelta))
        } else {
            localEntries.add(LeaderboardEntry(playerId, playerName, if (wonGame) 1 else 0, scoreDelta))
        }
        localEntries.sortByDescending { it.wins }
    }

    fun getTopLeaderboard(): Flow<List<LeaderboardEntry>> = callbackFlow {
        trySend(localEntries.sortedByDescending { it.wins })
        var listener: com.google.firebase.firestore.ListenerRegistration? = null
        try {
            if (db != null) {
                listener = db.collection("leaderboard").orderBy("wins", Query.Direction.DESCENDING)
                    .orderBy("points", Query.Direction.DESCENDING)
                    .limit(10)
                    .addSnapshotListener { snapshot, error ->
                        if (error == null && snapshot != null) {
                            val entries = snapshot.documents.mapNotNull { it.toObject(LeaderboardEntry::class.java) }
                            if (entries.isNotEmpty()) {
                                trySend(entries)
                            }
                        }
                    }
            }
        } catch (_: Exception) {}

        awaitClose {
            listener?.remove()
        }
    }
}
