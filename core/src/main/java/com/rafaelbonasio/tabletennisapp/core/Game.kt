package com.rafaelbonasio.tabletennisapp.core

import kotlin.math.abs
import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class Game(val player1: Player, val player2: Player, val rules: GameRules) {

    @OptIn(ExperimentalTime::class)
    val start: Instant = now()

    //val end: Instant

    val events = mutableListOf<GameEvent>()

    fun calculateScoreboard(): Scoreboard {
        val scoreboard = Scoreboard()

        fun calculatePointsDifference(): Int {
            return abs(scoreboard.player2Score - scoreboard.player1Score)
        }

        fun checkForPlayer1Win() {
            if (scoreboard.player1Score >= rules.setSize && calculatePointsDifference() >= 2) {
                scoreboard.player1Score = 0
                scoreboard.player2Score = 0

                scoreboard.player1SetScore++
            }
        }

        fun checkForPlayer2Win() {
            if (scoreboard.player2Score >= rules.setSize && calculatePointsDifference() >= 2)
            {
                scoreboard.player1Score = 0
                scoreboard.player2Score = 0

                scoreboard.player2SetScore++
            }
        }

        fun checkServer() {
            if ((scoreboard.player1Score + scoreboard.player2Score) % 2 == 0) {
                scoreboard.isPlayer2Serving = !scoreboard.isPlayer2Serving
            }
        }

        for (event in events) {
            when (event) {
                GameEvent.Player1Scored -> {
                    scoreboard.player1Score++
                    checkForPlayer1Win()
                    checkServer()
                }

                GameEvent.Player2Scored -> {
                    scoreboard.player2Score++
                    checkForPlayer2Win()
                    checkServer()
                }

                GameEvent.Net -> {
                    // No change to the score.
                }

                GameEvent.Undo -> TODO()
                GameEvent.Redo -> TODO()
            }
        }

        return scoreboard
    }
}

data class Scoreboard (
    var player1Score: Int = 0,
    var player2Score: Int = 0,

    var player1SetScore: Int = 0,
    var player2SetScore: Int = 0,

    var isPlayer2Serving: Boolean = false
)

data class GameRules(val setSize: Int, val setCount: Int, val serveCount: Int)

enum class GameEvent {
    Player1Scored,
    Player2Scored,
    Net,
    Undo,
    Redo
}

data class Player @OptIn(ExperimentalUuidApi::class) constructor(val id: Uuid, var name: String)
