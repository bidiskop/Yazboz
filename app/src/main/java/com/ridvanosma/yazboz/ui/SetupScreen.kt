package com.ridvanosma.yazboz.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ridvanosma.yazboz.data.GameMode
import com.ridvanosma.yazboz.data.GameState
import com.ridvanosma.yazboz.data.newGame

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    onStartGame: (GameState) -> Unit
) {
    var modeName by rememberSaveable {
        mutableStateOf(GameMode.TEAMS.name)
    }

    var team1 by rememberSaveable { mutableStateOf("Biz") }
    var team2 by rememberSaveable { mutableStateOf("Siz") }

    var player1 by rememberSaveable { mutableStateOf("Oyuncu 1") }
    var player2 by rememberSaveable { mutableStateOf("Oyuncu 2") }
    var player3 by rememberSaveable { mutableStateOf("Oyuncu 3") }
    var player4 by rememberSaveable { mutableStateOf("Oyuncu 4") }

    val mode = GameMode.valueOf(modeName)

    // Eşli oyunda da puanlar dört oyuncu için ayrı ayrı tutulur.
    val currentNames = listOf(player1, player2, player3, player4)
    val currentTeamNames = if (mode == GameMode.TEAMS) {
        listOf(team1, team2)
    } else {
        emptyList()
    }

    val canStart =
        currentNames.all { it.isNotBlank() } &&
                (mode != GameMode.TEAMS || currentTeamNames.all { it.isNotBlank() })

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Yazboz",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Okey masanı hazırla",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = if (mode == GameMode.TEAMS) {
                    "Eşli oyunda dört oyuncunun sayısını ve cezasını ayrı girersin; ekip toplamlarını Yazboz otomatik hesaplar."
                } else {
                    "4 oyuncu ayrı oynar. Oyun 11 elle başlar; istediğin zaman yeni el ve ceza ekleyebilirsin."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Oyun düzeni",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            modifier = Modifier.weight(1f),
                            selected = mode == GameMode.TEAMS,
                            onClick = {
                                modeName = GameMode.TEAMS.name
                            },
                            label = {
                                Text("2 Ekip")
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Groups,
                                    contentDescription = null
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors()
                        )

                        FilterChip(
                            modifier = Modifier.weight(1f),
                            selected = mode == GameMode.PLAYERS,
                            onClick = {
                                modeName = GameMode.PLAYERS.name
                            },
                            label = {
                                Text("4 Oyuncu")
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = null
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors()
                        )
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (mode == GameMode.TEAMS) {
                            "Ekipler ve oyuncular"
                        } else {
                            "Oyuncu isimleri"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (mode == GameMode.TEAMS) {
                        Text(
                            text = "1. Ekip",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = team1,
                            onValueChange = { team1 = it },
                            singleLine = true,
                            label = { Text("1. ekip adı") }
                        )

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = player1,
                            onValueChange = { player1 = it },
                            singleLine = true,
                            label = { Text("1. ekip • 1. oyuncu") }
                        )

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = player2,
                            onValueChange = { player2 = it },
                            singleLine = true,
                            label = { Text("1. ekip • 2. oyuncu") }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Text(
                            text = "2. Ekip",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = team2,
                            onValueChange = { team2 = it },
                            singleLine = true,
                            label = { Text("2. ekip adı") }
                        )

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = player3,
                            onValueChange = { player3 = it },
                            singleLine = true,
                            label = { Text("2. ekip • 1. oyuncu") }
                        )

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = player4,
                            onValueChange = { player4 = it },
                            singleLine = true,
                            label = { Text("2. ekip • 2. oyuncu") }
                        )
                    } else {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = player1,
                            onValueChange = { player1 = it },
                            singleLine = true,
                            label = { Text("1. oyuncu") }
                        )

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = player2,
                            onValueChange = { player2 = it },
                            singleLine = true,
                            label = { Text("2. oyuncu") }
                        )

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = player3,
                            onValueChange = { player3 = it },
                            singleLine = true,
                            label = { Text("3. oyuncu") }
                        )

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = player4,
                            onValueChange = { player4 = it },
                            singleLine = true,
                            label = { Text("4. oyuncu") }
                        )
                    }
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = canStart,
                onClick = {
                    onStartGame(
                        newGame(
                            mode = mode,
                            names = currentNames,
                            handCount = 7,
                            teamNames = currentTeamNames
                        )
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = null
                )
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text("Oyna")
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}
