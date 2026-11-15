package com.example.matchmishap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchmishap.engine.GameRoom
import com.example.matchmishap.engine.OnlineGameManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun OnlineLobbyScreen(
    onStartOnlineGame: (roomId: String, playerName: String, playerId: String) -> Unit,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("match_mishap_prefs", android.content.Context.MODE_PRIVATE) }
    var playerName by remember {
        mutableStateOf(prefs.getString("saved_player_name", null) ?: "Player ${(1..99).random()}")
    }

    androidx.compose.runtime.LaunchedEffect(playerName) {
        if (playerName.isNotBlank()) {
            prefs.edit().putString("saved_player_name", playerName).apply()
        }
    }

    var roomCodeInput by remember { mutableStateOf("") }
    var currentRoomId by remember { mutableStateOf<String?>(null) }
    var currentRoom by remember { mutableStateOf<GameRoom?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val playerId = remember { UUID.randomUUID().toString() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E3A8A),
                        Color(0xFF0F172A),
                        Color(0xFF070F18)
                    )
                )
            )
            .padding(16.dp)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackToMenu) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "ONLINE MULTIPLAYER LOBBY",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD700),
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (currentRoomId == null) {
                // Two-Column Landscape Setup (Left: Create Room, Right: Join Room)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Card: Create New Room
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00B4D8)),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "HOST NEW ROOM",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD700)
                                )
                            )

                            OutlinedTextField(
                                value = playerName,
                                onValueChange = { playerName = it },
                                label = { Text("Your Name") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFFFD700),
                                    unfocusedBorderColor = Color.Gray,
                                    focusedLabelColor = Color(0xFFFFD700),
                                    unfocusedLabelColor = Color.LightGray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    if (playerName.isBlank()) {
                                        errorMessage = "Please enter your name"
                                        return@Button
                                    }
                                    scope.launch {
                                        try {
                                            val id = OnlineGameManager.createRoom(playerName, playerId)
                                            currentRoomId = id
                                        } catch (e: Exception) {
                                            errorMessage = "Error creating room: ${e.localizedMessage}"
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B4D8)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                            ) {
                                Text(
                                    text = "CREATE ROOM",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black
                                    )
                                )
                            }
                        }
                    }

                    // Right Card: Join Existing Room
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFF9800)),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "JOIN EXISTING ROOM",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF9800)
                                )
                            )

                            OutlinedTextField(
                                value = roomCodeInput,
                                onValueChange = { roomCodeInput = it.uppercase() },
                                label = { Text("Enter 4-Letter Room Code") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFFFD700),
                                    unfocusedBorderColor = Color.Gray,
                                    focusedLabelColor = Color(0xFFFFD700),
                                    unfocusedLabelColor = Color.LightGray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    if (playerName.isBlank() || roomCodeInput.length < 4) {
                                        errorMessage = "Enter name and 4-letter room code"
                                        return@Button
                                    }
                                    scope.launch {
                                        val success = OnlineGameManager.joinRoom(roomCodeInput, playerName, playerId)
                                        if (success) {
                                            currentRoomId = roomCodeInput
                                        } else {
                                            errorMessage = "Room not found or already started!"
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                            ) {
                                Text(
                                    text = "JOIN ROOM",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black
                                    )
                                )
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFEF5350))
                    )
                }
            } else {
                // Inside Room Waiting Lobby
                val room = currentRoom
                val isHost = room?.hostId == playerId

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD700)),
                    modifier = Modifier.width(420.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "ROOM CODE: ${currentRoomId}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFD700)
                            )
                        )

                        Text(
                            text = "Connected Players (${room?.players?.size ?: 1}/4) - Min 2 to start (AI fills empty spots)",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            room?.players?.forEach { p ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF0F172A)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = p.name + if (p.isHost) " (Host)" else "",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = "READY",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFF4CAF50),
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        if (isHost) {
                            val canStart = (room?.players?.size ?: 0) >= 1
                            Button(
                                onClick = {
                                    scope.launch {
                                        OnlineGameManager.startGameWithAiFill(currentRoomId!!)
                                    }
                                },
                                enabled = canStart,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50),
                                    disabledContainerColor = Color.Gray
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                            ) {
                                Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "START GAME (FILL WITH AI)",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                )
                            }
                        } else {
                            Text(
                                text = "Waiting for host to start game...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                remember(currentRoomId) {
                    scope.launch {
                        OnlineGameManager.observeRoom(currentRoomId!!).collectLatest { updatedRoom ->
                            currentRoom = updatedRoom
                            if (updatedRoom?.isStarted == true) {
                                onStartOnlineGame(currentRoomId!!, playerName, playerId)
                            }
                        }
                    }
                }
            }
        }
    }
}
