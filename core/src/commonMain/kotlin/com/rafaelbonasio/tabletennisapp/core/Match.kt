package com.rafaelbonasio.tabletennisapp.core

import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.time.Clock.System.now
import kotlin.time.Instant
import kotlin.uuid.Uuid

class Match(val id: Uuid, val player1: Player, val player2: Player, val rules: MatchRules, val isPlayer2ServingFirst: Boolean) { // ITTF Statutes 2026 2.12: Match

    val start: Instant = now()

    //val end: Instant

    val events = mutableListOf<MatchEvent>()

    fun calculateScoreboard(): Scoreboard {
        val scoreboard = Scoreboard()

        scoreboard.isPlayer2Serving = isPlayer2ServingFirst

        fun calculatePointsDifference(): Int {
            return abs(scoreboard.player2PointScore - scoreboard.player1PointScore)
        }

        fun checkForFinish() {
            val gamesToWin = ceil(rules.matchSize / 2f).toInt()

            if (scoreboard.player1GameScore == gamesToWin || scoreboard.player2GameScore == gamesToWin) {
                scoreboard.isFinished = true
            }
        }

        fun checkForPlayer1Win() {
            if (scoreboard.player1PointScore >= rules.gameSize && calculatePointsDifference() >= 2) {
                scoreboard.player1PointScore = 0
                scoreboard.player2PointScore = 0

                scoreboard.player1GameScore++

                checkForFinish()
            }
        }

        fun checkForPlayer2Win() {
            if (scoreboard.player2PointScore >= rules.gameSize && calculatePointsDifference() >= 2)
            {
                scoreboard.player1PointScore = 0
                scoreboard.player2PointScore = 0

                scoreboard.player2GameScore++

                checkForFinish()
            }
        }

        fun checkServer() {
            if ((scoreboard.player1PointScore + scoreboard.player2PointScore) % 2 == 0) {
                scoreboard.isPlayer2Serving = !scoreboard.isPlayer2Serving
            }
        }

        for (event in events) {
            when (event) {
                MatchEvent.Player1Scored -> {
                    scoreboard.player1PointScore++
                    checkForPlayer1Win()
                    checkServer()
                }

                MatchEvent.Player2Scored -> {
                    scoreboard.player2PointScore++
                    checkForPlayer2Win()
                    checkServer()
                }

                MatchEvent.Net -> {
                    // No change to the score.
                }

                MatchEvent.Undo -> TODO()
                MatchEvent.Redo -> TODO()

                MatchEvent.Canceled -> {
                    scoreboard.isFinished = true
                }
            }
        }

        return scoreboard
    }
}

data class Scoreboard (
    var player1PointScore: Int = 0,
    var player2PointScore: Int = 0,

    var player1GameScore: Int = 0,
    var player2GameScore: Int = 0,

    var isPlayer2Serving: Boolean = false,

    var isFinished: Boolean = false
)

@Serializable
data class MatchRules(val gameSize: Int, val matchSize: Int, val serveCount: Int)

@Serializable
enum class MatchEvent { // ITTF Statutes 2026 2.11: Game
    Player1Scored,
    Player2Scored,
    Net,
    Undo,
    Redo,
    Canceled
}