package com.ridvanosma.yazboz.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ridvanosma.yazboz.data.GameState
import com.ridvanosma.yazboz.data.HandEntry
import com.ridvanosma.yazboz.data.PenaltyEntry
import com.ridvanosma.yazboz.data.isPairedTeamGame
import com.ridvanosma.yazboz.data.teamTotals
import com.ridvanosma.yazboz.data.totals
import com.ridvanosma.yazboz.ui.theme.handPalette

private data class ScoreEditTarget(
    val handIndex: Int,
    val penaltyIndex: Int?,
    val participantIndex: Int,
    val initialValue: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreboardScreen(
    game: GameState,
    onGameChange: (GameState) -> Unit,
    onNewGame: () -> Unit
) {
    var showNewGameDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var editTarget by remember {
        mutableStateOf<ScoreEditTarget?>(null)
    }

    val pairedTeamMode = game.isPairedTeamGame()
    val playerTotals = game.totals()
    val resultTotals = if (pairedTeamMode) {
        game.teamTotals()
    } else {
        playerTotals
    }
    val leaderTotal = resultTotals.minOrNull() ?: 0L
    val differences = resultTotals.map { total -> total - leaderTotal }

    val horizontalScrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Yazboz",
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = if (pairedTeamMode) {
                                game.teamNames.joinToString("  •  ")
                            } else {
                                game.names.joinToString("  •  ")
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            showNewGameDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Yeni oyun"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Button(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    onClick = {
                        val nextHandNumber =
                            (game.hands.maxOfOrNull { it.handNumber } ?: 0) + 1

                        val updated = game.copy(
                            hands = game.hands + HandEntry(
                                handNumber = nextHandNumber,
                                scores = List(game.participantCount) { "" }
                            )
                        )

                        onGameChange(updated)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("El Ekle")
                }
            }
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val firstColumnWidth = 78.dp
            val scoreColumnWidth = if (game.participantCount == 2) {
                ((maxWidth - firstColumnWidth) / 2f).coerceAtLeast(116.dp)
            } else {
                78.dp
            }

            val minimumTableWidth =
                firstColumnWidth + (scoreColumnWidth * game.participantCount)

            val tableWidth = if (minimumTableWidth > maxWidth) {
                minimumTableWidth
            } else {
                maxWidth
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(horizontalScrollState)
            ) {
                Column(
                    modifier = Modifier
                        .requiredWidth(tableWidth)
                        .fillMaxHeight()
                ) {
                    TableHeader(
                        game = game,
                        firstColumnWidth = firstColumnWidth,
                        scoreColumnWidth = scoreColumnWidth
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        itemsIndexed(
                            items = game.hands,
                            key = { _, hand -> hand.handNumber }
                        ) { handIndex, hand ->
                            HandBlock(
                                hand = hand,
                                handIndex = handIndex,
                                scoreColumnWidth = scoreColumnWidth,
                                firstColumnWidth = firstColumnWidth,
                                onEditHandScore = { participantIndex, value ->
                                    editTarget = ScoreEditTarget(
                                        handIndex = handIndex,
                                        penaltyIndex = null,
                                        participantIndex = participantIndex,
                                        initialValue = value
                                    )
                                },
                                onEditPenaltyScore = { penaltyIndex, participantIndex, value ->
                                    editTarget = ScoreEditTarget(
                                        handIndex = handIndex,
                                        penaltyIndex = penaltyIndex,
                                        participantIndex = participantIndex,
                                        initialValue = value
                                    )
                                },
                                onAddPenalty = {
                                    val updatedHands = game.hands.toMutableList()
                                    val currentHand = updatedHands[handIndex]

                                    val newPenalty = PenaltyEntry(
                                        id = System.nanoTime(),
                                        scores = List(game.participantCount) { "" }
                                    )

                                    updatedHands[handIndex] = currentHand.copy(
                                        penalties = currentHand.penalties + newPenalty
                                    )

                                    onGameChange(
                                        game.copy(hands = updatedHands)
                                    )
                                },
                                onDeletePenalty = { penaltyIndex ->
                                    val updatedHands = game.hands.toMutableList()
                                    val currentHand = updatedHands[handIndex]
                                    val updatedPenalties =
                                        currentHand.penalties.toMutableList().apply {
                                            removeAt(penaltyIndex)
                                        }

                                    updatedHands[handIndex] = currentHand.copy(
                                        penalties = updatedPenalties
                                    )

                                    onGameChange(
                                        game.copy(hands = updatedHands)
                                    )
                                }
                            )
                        }

                        if (pairedTeamMode) {
                            item(key = "player-totals") {
                                PlayerTotalRow(
                                    totals = playerTotals,
                                    firstColumnWidth = firstColumnWidth,
                                    scoreColumnWidth = scoreColumnWidth
                                )
                            }

                            item(key = "team-totals") {
                                TeamTotalRow(
                                    totals = resultTotals,
                                    firstColumnWidth = firstColumnWidth,
                                    scoreColumnWidth = scoreColumnWidth
                                )
                            }

                            item(key = "differences") {
                                TeamDifferenceRow(
                                    differences = differences,
                                    firstColumnWidth = firstColumnWidth,
                                    scoreColumnWidth = scoreColumnWidth
                                )
                            }
                        } else {
                            item(key = "totals") {
                                TotalRow(
                                    totals = playerTotals,
                                    firstColumnWidth = firstColumnWidth,
                                    scoreColumnWidth = scoreColumnWidth
                                )
                            }

                            item(key = "differences") {
                                DifferenceRow(
                                    differences = differences,
                                    firstColumnWidth = firstColumnWidth,
                                    scoreColumnWidth = scoreColumnWidth
                                )
                            }
                        }

                        item(key = "bottom-space") {
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }

    if (showNewGameDialog) {
        AlertDialog(
            onDismissRequest = {
                showNewGameDialog = false
            },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.WarningAmber,
                    contentDescription = null
                )
            },
            title = {
                Text("Yeni oyun başlatılsın mı?")
            },
            text = {
                Text(
                    "Mevcut tablodaki puanlar silinecek ve bilgi giriş ekranına dönülecek."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showNewGameDialog = false
                        onNewGame()
                    }
                ) {
                    Text("Yeni Oyun")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showNewGameDialog = false
                    }
                ) {
                    Text("Vazgeç")
                }
            }
        )
    }

    editTarget?.let { target ->
        ScoreInputDialog(
            initialValue = target.initialValue,
            onDismiss = {
                editTarget = null
            },
            onSave = { newValue ->
                val updatedHands = game.hands.toMutableList()
                val hand = updatedHands[target.handIndex]

                if (target.penaltyIndex == null) {
                    val updatedScores = hand.scores.toMutableList()
                    updatedScores[target.participantIndex] = newValue

                    updatedHands[target.handIndex] = hand.copy(
                        scores = updatedScores
                    )
                } else {
                    val updatedPenalties = hand.penalties.toMutableList()
                    val penalty = updatedPenalties[target.penaltyIndex]
                    val updatedScores = penalty.scores.toMutableList()

                    updatedScores[target.participantIndex] = newValue

                    updatedPenalties[target.penaltyIndex] = penalty.copy(
                        scores = updatedScores
                    )

                    updatedHands[target.handIndex] = hand.copy(
                        penalties = updatedPenalties
                    )
                }

                onGameChange(
                    game.copy(hands = updatedHands)
                )

                editTarget = null
            }
        )
    }
}

@Composable
private fun TableHeader(
    game: GameState,
    firstColumnWidth: Dp,
    scoreColumnWidth: Dp
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        if (game.isPairedTeamGame()) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, bottom = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.width(firstColumnWidth),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "EL",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    game.teamNames.forEach { teamName ->
                        Box(
                            modifier = Modifier.width(scoreColumnWidth * 2f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = teamName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(firstColumnWidth))

                    game.names.forEach { name ->
                        Box(
                            modifier = Modifier
                                .width(scoreColumnWidth)
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.width(firstColumnWidth),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "EL",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                game.names.forEach { name ->
                    Box(
                        modifier = Modifier
                            .width(scoreColumnWidth)
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HandBlock(
    hand: HandEntry,
    handIndex: Int,
    firstColumnWidth: Dp,
    scoreColumnWidth: Dp,
    onEditHandScore: (participantIndex: Int, value: String) -> Unit,
    onEditPenaltyScore: (
        penaltyIndex: Int,
        participantIndex: Int,
        value: String
    ) -> Unit,
    onAddPenalty: () -> Unit,
    onDeletePenalty: (penaltyIndex: Int) -> Unit
) {
    val palette = handPalette(handIndex)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .width(firstColumnWidth)
                    .fillMaxHeight(),
                color = palette.hand,
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 6.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${hand.handNumber}. EL",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(
                        onClick = onAddPenalty
                    ) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = "Ceza",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            hand.scores.forEachIndexed { participantIndex, value ->
                ScoreCell(
                    modifier = Modifier.width(scoreColumnWidth),
                    value = value,
                    backgroundColor = palette.hand,
                    onClick = {
                        onEditHandScore(participantIndex, value)
                    }
                )
            }
        }

        hand.penalties.forEachIndexed { penaltyIndex, penalty ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .padding(horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .width(firstColumnWidth)
                        .fillMaxHeight(),
                    color = palette.penalty,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "CEZA",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            modifier = Modifier.size(28.dp),
                            onClick = {
                                onDeletePenalty(penaltyIndex)
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(17.dp),
                                imageVector = Icons.Rounded.DeleteOutline,
                                contentDescription = "Cezayı sil"
                            )
                        }
                    }
                }

                penalty.scores.forEachIndexed { participantIndex, value ->
                    ScoreCell(
                        modifier = Modifier.width(scoreColumnWidth),
                        value = value,
                        backgroundColor = palette.penalty,
                        isPenalty = true,
                        onClick = {
                            onEditPenaltyScore(
                                penaltyIndex,
                                participantIndex,
                                value
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreCell(
    modifier: Modifier,
    value: String,
    backgroundColor: Color,
    isPenalty: Boolean = false,
    onClick: () -> Unit
) {
    val isNegative = value.toLongOrNull()?.let { it < 0 } == true

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 3.dp, vertical = 5.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(8.dp),
            color = backgroundColor,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
            )
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value.ifBlank { "—" },
                    style = if (isPenalty) {
                        MaterialTheme.typography.titleSmall
                    } else {
                        MaterialTheme.typography.titleMedium
                    },
                    fontWeight = FontWeight.SemiBold,
                    color = if (isNegative) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun TotalRow(
    totals: List<Long>,
    firstColumnWidth: Dp,
    scoreColumnWidth: Dp
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.width(firstColumnWidth),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TOPLAM",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            totals.forEach { total ->
                Box(
                    modifier = Modifier.width(scoreColumnWidth),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = total.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerTotalRow(
    totals: List<Long>,
    firstColumnWidth: Dp,
    scoreColumnWidth: Dp
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.width(firstColumnWidth),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "OYUNCU\nTOP.",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            totals.forEach { total ->
                Box(
                    modifier = Modifier.width(scoreColumnWidth),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = total.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamTotalRow(
    totals: List<Long>,
    firstColumnWidth: Dp,
    scoreColumnWidth: Dp
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.width(firstColumnWidth),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "EKİP\nTOPLAM",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            totals.forEach { total ->
                Box(
                    modifier = Modifier.width(scoreColumnWidth * 2f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = total.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamDifferenceRow(
    differences: List<Long>,
    firstColumnWidth: Dp,
    scoreColumnWidth: Dp
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.width(firstColumnWidth),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "1.'YE\nFARK",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            differences.forEach { difference ->
                Box(
                    modifier = Modifier.width(scoreColumnWidth * 2f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (difference == 0L) {
                            "0"
                        } else {
                            "+$difference"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (difference == 0L) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun DifferenceRow(
    differences: List<Long>,
    firstColumnWidth: Dp,
    scoreColumnWidth: Dp
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.width(firstColumnWidth),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "1.'YE\nFARK",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            differences.forEach { difference ->
                Box(
                    modifier = Modifier.width(scoreColumnWidth),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (difference == 0L) {
                            "0"
                        } else {
                            "+$difference"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (difference == 0L) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreInputDialog(
    initialValue: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var rawDigits by rememberSaveable(initialValue) {
        mutableStateOf(
            initialValue
                .removePrefix("-")
                .filter(Char::isDigit)
        )
    }

    var isNegative by rememberSaveable(initialValue) {
        mutableStateOf(initialValue.startsWith("-"))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Sayı gir")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = rawDigits,
                    onValueChange = { entered ->
                        rawDigits = entered
                            .filter(Char::isDigit)
                            .take(9)
                    },
                    label = {
                        Text("Puan")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    prefix = {
                        if (isNegative && rawDigits.isNotBlank()) {
                            Text("-")
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (rawDigits.isNotBlank() && rawDigits != "0") {
                                isNegative = !isNegative
                            }
                        }
                    ) {
                        Text("± İşaret")
                    }

                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            rawDigits = ""
                            isNegative = false
                        }
                    ) {
                        Text("Temizle")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val normalized = rawDigits.trimStart('0')
                        .ifEmpty {
                            if (rawDigits.isBlank()) "" else "0"
                        }

                    val finalValue = when {
                        normalized.isBlank() -> ""
                        normalized == "0" -> "0"
                        isNegative -> "-$normalized"
                        else -> normalized
                    }

                    onSave(finalValue)
                }
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgeç")
            }
        }
    )
}
