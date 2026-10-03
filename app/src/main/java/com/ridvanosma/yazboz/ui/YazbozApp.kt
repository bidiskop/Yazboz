package com.ridvanosma.yazboz.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.ridvanosma.yazboz.data.GameState
import com.ridvanosma.yazboz.data.GameStorage

@Composable
fun YazbozApp() {
    val context = LocalContext.current
    val storage = remember(context) {
        GameStorage(context.applicationContext)
    }

    var activeGame by remember {
        mutableStateOf<GameState?>(storage.loadGame())
    }

    val game = activeGame

    if (game == null) {
        SetupScreen(
            onStartGame = { newGame ->
                storage.saveGame(newGame)
                activeGame = newGame
            }
        )
    } else {
        ScoreboardScreen(
            game = game,
            onGameChange = { updatedGame ->
                storage.saveGame(updatedGame)
                activeGame = updatedGame
            },
            onNewGame = {
                storage.clearGame()
                activeGame = null
            }
        )
    }
}
