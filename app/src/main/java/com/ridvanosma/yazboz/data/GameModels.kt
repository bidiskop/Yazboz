package com.ridvanosma.yazboz.data

enum class GameMode {
    TEAMS,
    PLAYERS
}

data class PenaltyEntry(
    val id: Long,
    val scores: List<String>
)

data class HandEntry(
    val handNumber: Int,
    val scores: List<String>,
    val penalties: List<PenaltyEntry> = emptyList()
)

data class GameState(
    val mode: GameMode,
    val names: List<String>,
    val hands: List<HandEntry>,
    val teamNames: List<String> = emptyList()
) {
    val participantCount: Int
        get() = names.size
}

fun newGame(
    mode: GameMode,
    names: List<String>,
    handCount: Int = 7,
    teamNames: List<String> = emptyList()
): GameState {
    val cleanedNames = names.map { it.trim() }
    val cleanedTeamNames = teamNames.map { it.trim() }

    return GameState(
        mode = mode,
        names = cleanedNames,
        hands = (1..handCount).map { handNumber ->
            HandEntry(
                handNumber = handNumber,
                scores = List(cleanedNames.size) { "" }
            )
        },
        teamNames = cleanedTeamNames
    )
}

fun GameState.totals(): List<Long> {
    return List(participantCount) { participantIndex ->
        hands.sumOf { hand ->
            val handScore = hand.scores
                .getOrNull(participantIndex)
                .orEmpty()
                .toLongOrNull() ?: 0L

            val penalties = hand.penalties.sumOf { penalty ->
                penalty.scores
                    .getOrNull(participantIndex)
                    .orEmpty()
                    .toLongOrNull() ?: 0L
            }

            handScore + penalties
        }
    }
}

fun GameState.isPairedTeamGame(): Boolean {
    return mode == GameMode.TEAMS &&
            names.size == 4 &&
            teamNames.size == 2
}

fun GameState.teamTotals(): List<Long> {
    val playerTotals = totals()

    return if (isPairedTeamGame()) {
        listOf(
            playerTotals.getOrElse(0) { 0L } + playerTotals.getOrElse(1) { 0L },
            playerTotals.getOrElse(2) { 0L } + playerTotals.getOrElse(3) { 0L }
        )
    } else {
        playerTotals
    }
}
