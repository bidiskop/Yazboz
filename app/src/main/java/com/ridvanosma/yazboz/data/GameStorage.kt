package com.ridvanosma.yazboz.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class GameStorage(context: Context) {

    private val preferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun loadGame(): GameState? {
        // Eski sürümde kullanılan kayıt anahtarını artık yüklemiyoruz.
        // Böylece eski 2 sütunlu ekip oyunu yeni eşli oyun yapısını bozmaz.
        if (preferences.contains(KEY_ACTIVE_GAME_LEGACY)) {
            preferences.edit()
                .remove(KEY_ACTIVE_GAME_LEGACY)
                .apply()
        }

        val raw = preferences.getString(KEY_ACTIVE_GAME_V2, null) ?: return null

        val game = runCatching {
            decodeGame(JSONObject(raw))
        }.getOrNull()

        // Yeni eşli oyun yapısına uymayan kayıt varsa otomatik temizle.
        if (game == null || !isCompatible(game)) {
            clearGame()
            return null
        }

        return game
    }

    fun saveGame(game: GameState) {
        preferences.edit()
            .putString(KEY_ACTIVE_GAME_V2, encodeGame(game).toString())
            .remove(KEY_ACTIVE_GAME_LEGACY)
            .apply()
    }

    fun clearGame() {
        preferences.edit()
            .remove(KEY_ACTIVE_GAME_V2)
            .remove(KEY_ACTIVE_GAME_LEGACY)
            .apply()
    }

    private fun isCompatible(game: GameState): Boolean {
        return when (game.mode) {
            GameMode.TEAMS -> {
                game.names.size == 4 &&
                        game.teamNames.size == 2 &&
                        game.hands.all { hand ->
                            hand.scores.size == 4 &&
                                    hand.penalties.all { penalty ->
                                        penalty.scores.size == 4
                                    }
                        }
            }

            GameMode.PLAYERS -> {
                game.names.size == 4 &&
                        game.hands.all { hand ->
                            hand.scores.size == 4 &&
                                    hand.penalties.all { penalty ->
                                        penalty.scores.size == 4
                                    }
                        }
            }
        }
    }

    private fun encodeGame(game: GameState): JSONObject {
        return JSONObject().apply {
            put("mode", game.mode.name)
            put("names", JSONArray(game.names))
            put("teamNames", JSONArray(game.teamNames))

            val handsArray = JSONArray()

            game.hands.forEach { hand ->
                val penaltiesArray = JSONArray()

                hand.penalties.forEach { penalty ->
                    penaltiesArray.put(
                        JSONObject().apply {
                            put("id", penalty.id)
                            put("scores", JSONArray(penalty.scores))
                        }
                    )
                }

                handsArray.put(
                    JSONObject().apply {
                        put("handNumber", hand.handNumber)
                        put("scores", JSONArray(hand.scores))
                        put("penalties", penaltiesArray)
                    }
                )
            }

            put("hands", handsArray)
        }
    }

    private fun decodeGame(root: JSONObject): GameState {
        val mode = GameMode.valueOf(root.getString("mode"))

        val namesArray = root.getJSONArray("names")
        val names = List(namesArray.length()) { index ->
            namesArray.optString(index, "")
        }

        val teamNamesArray = root.optJSONArray("teamNames") ?: JSONArray()
        val teamNames = List(teamNamesArray.length()) { index ->
            teamNamesArray.optString(index, "")
        }

        val handsArray = root.getJSONArray("hands")
        val hands = List(handsArray.length()) { handIndex ->
            val handObject = handsArray.getJSONObject(handIndex)
            val scoresArray = handObject.getJSONArray("scores")
            val penaltiesArray =
                handObject.optJSONArray("penalties") ?: JSONArray()

            val penalties = List(penaltiesArray.length()) { penaltyIndex ->
                val penaltyObject =
                    penaltiesArray.getJSONObject(penaltyIndex)
                val penaltyScores =
                    penaltyObject.getJSONArray("scores")

                PenaltyEntry(
                    id = penaltyObject.optLong(
                        "id",
                        System.nanoTime() + penaltyIndex
                    ),
                    scores = List(names.size) { index ->
                        penaltyScores.optString(index, "")
                    }
                )
            }

            HandEntry(
                handNumber = handObject.optInt(
                    "handNumber",
                    handIndex + 1
                ),
                scores = List(names.size) { index ->
                    scoresArray.optString(index, "")
                },
                penalties = penalties
            )
        }

        return GameState(
            mode = mode,
            names = names,
            hands = hands,
            teamNames = teamNames
        )
    }

    private companion object {
        const val PREFERENCES_NAME = "yazboz_preferences"

        // Eski sürüm anahtarı
        const val KEY_ACTIVE_GAME_LEGACY = "active_game"

        // Yeni eşli oyun sürümü
        const val KEY_ACTIVE_GAME_V2 = "active_game_v2"
    }
}
