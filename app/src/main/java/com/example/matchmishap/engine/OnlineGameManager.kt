package com.example.matchmishap.engine

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class RoomPlayer(
    val id: String = "",
    val name: String = "",
    val isHost: Boolean = false,
    val isReady: Boolean = false
)

data class GameRoom(
    val roomId: String = "",
    val hostId: String = "",
    val players: List<RoomPlayer> = emptyList(),
    val isStarted: Boolean = false,
    val gameStateJson: String = ""
)

object OnlineGameManager {
    private val localRooms = mutableMapOf<String, GameRoom>()

    private val db: FirebaseFirestore? = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        null
    }

    suspend fun createRoom(playerName: String, playerId: String): String {
        val roomId = (1..4).map { ('A'..'Z').random() }.joinToString("")
        val hostPlayer = RoomPlayer(id = playerId, name = playerName, isHost = true, isReady = true)
        val room = GameRoom(roomId = roomId, hostId = playerId, players = listOf(hostPlayer), isStarted = false)

        try {
            if (db != null) {
                db.collection("game_rooms").document(roomId).set(room).await()
            }
        } catch (_: Exception) {}

        localRooms[roomId] = room
        return roomId
    }

    suspend fun joinRoom(roomId: String, playerName: String, playerId: String): Boolean {
        try {
            if (db != null) {
                val docRef = db.collection("game_rooms").document(roomId)
                val snapshot = docRef.get().await()
                if (snapshot.exists()) {
                    val room = snapshot.toObject(GameRoom::class.java)
                    if (room != null && !room.isStarted) {
                        val existing = room.players.toMutableList()
                        if (existing.none { it.id == playerId } && existing.size < 4) {
                            existing.add(RoomPlayer(id = playerId, name = playerName, isHost = false, isReady = true))
                            docRef.update("players", existing).await()
                            localRooms[roomId] = room.copy(players = existing)
                            return true
                        } else if (existing.any { it.id == playerId }) {
                            return true
                        }
                    }
                }
            }
        } catch (_: Exception) {}

        val room = localRooms[roomId] ?: run {
            val mockRoom = GameRoom(roomId = roomId, hostId = "host_1", players = listOf(RoomPlayer("host_1", "Host Player", true, true)), isStarted = false)
            localRooms[roomId] = mockRoom
            mockRoom
        }
        if (room.isStarted) return false
        val existing = room.players.toMutableList()
        if (existing.none { it.id == playerId }) {
            existing.add(RoomPlayer(id = playerId, name = playerName, isHost = false, isReady = true))
        }
        localRooms[roomId] = room.copy(players = existing)
        return true
    }

    fun observeRoom(roomId: String): Flow<GameRoom?> = callbackFlow {
        trySend(localRooms[roomId])
        var listener: com.google.firebase.firestore.ListenerRegistration? = null
        try {
            if (db != null) {
                listener = db.collection("game_rooms").document(roomId).addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null && snapshot.exists()) {
                        val room = snapshot.toObject(GameRoom::class.java)
                        if (room != null) {
                            localRooms[roomId] = room
                            trySend(room)
                        }
                    }
                }
            }
        } catch (_: Exception) {}

        awaitClose {
            listener?.remove()
        }
    }

    suspend fun startGameWithAiFill(roomId: String) {
        try {
            if (db != null) {
                val docRef = db.collection("game_rooms").document(roomId)
                val snapshot = docRef.get().await()
                val room = snapshot.toObject(GameRoom::class.java)
                if (room != null) {
                    val currentPlayers = room.players.toMutableList()
                    while (currentPlayers.size < 4) {
                        val aiIndex = currentPlayers.size + 1
                        currentPlayers.add(RoomPlayer("ai_$aiIndex", "Bot $aiIndex", false, true))
                    }
                    docRef.update(mapOf("players" to currentPlayers, "isStarted" to true)).await()
                    localRooms[roomId] = room.copy(players = currentPlayers, isStarted = true)
                    return
                }
            }
        } catch (_: Exception) {}

        val room = localRooms[roomId]
        if (room != null) {
            val currentPlayers = room.players.toMutableList()
            while (currentPlayers.size < 4) {
                val aiIndex = currentPlayers.size + 1
                currentPlayers.add(RoomPlayer("ai_$aiIndex", "Bot $aiIndex", false, true))
            }
            localRooms[roomId] = room.copy(players = currentPlayers, isStarted = true)
        }
    }
}
