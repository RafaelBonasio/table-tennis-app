package com.rafaelbonasio.tabletennisapp.data.database

import androidx.room3.Dao
import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.Relation
import androidx.room3.Upsert
import com.rafaelbonasio.tabletennisapp.core.Match
import com.rafaelbonasio.tabletennisapp.core.MatchEvent
import com.rafaelbonasio.tabletennisapp.core.MatchRules
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Entity("matches")
data class MatchEntity(
    @PrimaryKey val id: Uuid,
    val player1Id: Uuid,
    val player2Id: Uuid,
    val rules: MatchRules,
    val events: List<MatchEvent>,
    val isPlayer2ServingFirst: Boolean
)

@Dao
interface MatchDao {
    @Upsert
    suspend fun upsertMatch(match: MatchEntity)

    @Query("SELECT * FROM matches WHERE id = :id")
    fun getMatch(id: Uuid): Flow<MatchWithPlayers>

    @Query("SELECT * FROM matches")
    fun getAllMatches(): Flow<List<MatchWithPlayers>>
}

data class MatchWithPlayers(
    @Embedded
    val match: MatchEntity,

    @Relation(parentColumns = ["player1Id"], entityColumns = ["id"])
    val player1: PlayerEntity,

    @Relation(parentColumns = ["player2Id"], entityColumns = ["id"])
    val player2: PlayerEntity
)

fun MatchWithPlayers.toMatch(): Match {
    val domainMatch = Match(match.id, player1.toPlayer(), player2.toPlayer(), match.rules, match.isPlayer2ServingFirst)
    domainMatch.events.addAll(match.events)

    return domainMatch
}

fun Match.toEntity(): MatchEntity {
    return MatchEntity(id, player1.id, player2.id, rules, events, isPlayer2ServingFirst)
}