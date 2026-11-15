package com.example.matchmishap.engine

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val localRooms = mutableMapOf<String, MutableStateFlow<GameRoom?>>()

    private fun getFirestore(): FirebaseFirestore? {
        return try {
            FirebaseFirestore.getInstance()
        } catch (_: Exception) {
            null
        }
    }

    suspend fun createRoom(playerName: String, playerId: String): String {
        val roomId = (1..4).map { ('A'..'Z').random() }.joinToString("")
        val hostPlayer = RoomPlayer(id = playerId, name = playerName, isHost = true, isReady = true)
        val room = GameRoom(roomId = roomId, hostId = playerId, players = listOf(hostPlayer), isStarted = false)

        val flow = MutableStateFlow<GameRoom?>(room)
        localRooms[roomId] = flow

        try {
            val db = getFirestore()
            if (db != null) {
                val roomMap = mapOf(
                    "roomId" to roomId,
                    "hostId" to playerId,
                    "players" to listOf(
                        mapOf(
                            "id" to playerId,
                            "name" to playerName,
                            "isHost" to true,
                            "isReady" to true
                        )
                    ),
                    "isStarted" to false,
                    "gameStateJson" to ""
                )
                db.collection("game_rooms").document(roomId).set(roomMap).await()
            }
        } catch (_: Exception) {}

        return roomId
    }

    suspend fun joinRoom(roomId: String, playerName: String, playerId: String): Boolean {
        try {
            val db = getFirestore()
            if (db != null) {
                val docRef = db.collection("game_rooms").document(roomId)
                val snapshot = docRef.get().await()
                if (snapshot.exists()) {
                    val isStarted = snapshot.getBoolean("isStarted") ?: false
                    if (!isStarted) {
                        @Suppress("UNCHECKED_CAST")
                        val rawPlayers = snapshot.get("players") as? List<Map<String, Any>> ?: emptyList()
                        val playersList = rawPlayers.map { p ->
                            RoomPlayer(
                                id = p["id"]?.toString() ?: "",
                                name = p["name"]?.toString() ?: "",
                                isHost = p["isHost"] as? Boolean ?: false,
                                isReady = p["isReady"] as? Boolean ?: false
                            )
                        }.toMutableList()

                        if (playersList.none { it.id == playerId } && playersList.size < 4) {
                            playersList.add(RoomPlayer(id = playerId, name = playerName, isHost = false, isReady = true))
                            val updatedMaps = playersList.map { p ->
                                mapOf("id" to p.id, "name" to p.name, "isHost" to p.isHost, "isReady" to p.isReady)
                            }
                            docRef.update("players", updatedMaps).await()
                        }

                        val updatedRoom = GameRoom(roomId = roomId, hostId = snapshot.getString("hostId") ?: "", players = playersList, isStarted = isStarted)
                        val flow = localRooms.getOrPut(roomId) { MutableStateFlow(updatedRoom) }
                        flow.value = updatedRoom
                        return true
                    }
                }
            }
        } catch (_: Exception) {}

        val flow = localRooms[roomId] ?: run {
            val mockRoom = GameRoom(roomId = roomId, hostId = "host_1", players = listOf(RoomPlayer("host_1", "Host", true, true)), isStarted = false)
            val newFlow = MutableStateFlow<GameRoom?>(mockRoom)
            localRooms[roomId] = newFlow
            newFlow
        }

        val current = flow.value ?: return false
        if (current.isStarted) return false

        val existing = current.players.toMutableList()
        if (existing.none { it.id == playerId } && existing.size < 4) {
            existing.add(RoomPlayer(id = playerId, name = playerName, isHost = false, isReady = true))
            flow.value = current.copy(players = existing)
            return true
        } else if (existing.any { it.id == playerId }) {
            return true
        }
        return false
    }

    fun observeRoom(roomId: String): Flow<GameRoom?> = callbackFlow {
        val localFlow = localRooms[roomId]
        if (localFlow != null) {
            trySend(localFlow.value)
        }

        var listener: com.google.firebase.firestore.ListenerRegistration? = null
        try {
            val db = getFirestore()
            if (db != null) {
                listener = db.collection("game_rooms").document(roomId).addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null && snapshot.exists()) {
                        @Suppress("UNCHECKED_CAST")
                        val rawPlayers = snapshot.get("players") as? List<Map<String, Any>> ?: emptyList()
                        val playersList = rawPlayers.map { p ->
                            RoomPlayer(
                                id = p["id"]?.toString() ?: "",
                                name = p["name"]?.toString() ?: "",
                                isHost = p["isHost"] as? Boolean ?: false,
                                isReady = p["isReady"] as? Boolean ?: false
                            )
                        }
                        val room = GameRoom(
                            roomId = roomId,
                            hostId = snapshot.getString("hostId") ?: "",
                            players = playersList,
                            isStarted = snapshot.getBoolean("isStarted") ?: false
                        )
                        val flow = localRooms.getOrPut(roomId) { MutableStateFlow(room) }
                        flow.value = room
                        trySend(room)
                    }
                }
            }
        } catch (_: Exception) {}

        awaitClose { listener?.remove() }
    }

    suspend fun startGameWithAiFill(roomId: String) {
        try {
            val db = getFirestore()
            if (db != null) {
                val docRef = db.collection("game_rooms").document(roomId)
                val snapshot = docRef.get().await()
                if (snapshot.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    val rawPlayers = snapshot.get("players") as? List<Map<String, Any>> ?: emptyList()
                    val currentPlayers = rawPlayers.map { p ->
                        RoomPlayer(
                            id = p["id"]?.toString() ?: "",
                            name = p["name"]?.toString() ?: "",
                            isHost = p["isHost"] as? Boolean ?: false,
                            isReady = p["isReady"] as? Boolean ?: false
                        )
                    }.toMutableList()

                    while (currentPlayers.size < 4) {
                        val aiIndex = currentPlayers.size + 1
                        currentPlayers.add(RoomPlayer("ai_$aiIndex", "Bot $aiIndex", false, true))
                    }

                    val updatedMaps = currentPlayers.map { p ->
                        mapOf("id" to p.id, "name" to p.name, "isHost" to p.isHost, "isReady" to p.isReady)
                    }

                    docRef.update(mapOf("players" to updatedMaps, "isStarted" to true)).await()

                    val room = GameRoom(roomId = roomId, hostId = snapshot.getString("hostId") ?: "", players = currentPlayers, isStarted = true)
                    val flow = localRooms.getOrPut(roomId) { MutableStateFlow(room) }
                    flow.value = room
                    return
                }
            }
        } catch (_: Exception) {}

        val flow = localRooms[roomId]
        val current = flow?.value
        if (current != null) {
            val currentPlayers = current.players.toMutableList()
            while (currentPlayers.size < 4) {
                val aiIndex = currentPlayers.size + 1
                currentPlayers.add(RoomPlayer("ai_$aiIndex", "Bot $aiIndex", false, true))
            }
            flow.value = current.copy(players = currentPlayers, isStarted = true)
        }
    }
}
