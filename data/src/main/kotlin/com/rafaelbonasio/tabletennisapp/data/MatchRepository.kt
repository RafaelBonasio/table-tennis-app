package com.rafaelbonasio.tabletennisapp.data

import com.rafaelbonasio.tabletennisapp.core.Match
import com.rafaelbonasio.tabletennisapp.data.database.MatchDao
import com.rafaelbonasio.tabletennisapp.data.database.PlayerDao
import com.rafaelbonasio.tabletennisapp.data.database.toEntity
import com.rafaelbonasio.tabletennisapp.data.database.toMatch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.Uuid

class MatchRepository(
    private val matchDao: MatchDao,
    private val playerDao: PlayerDao
) {
    val matches: Flow<List<Match>> = matchDao.getAllMatches().map {
        entities -> entities.map { it.toMatch() }
    }

    fun getMatch(id: Uuid): Flow<Match> {
        return matchDao.getMatch(id).map { it.toMatch() }
    }

    suspend fun saveMatch(match: Match) {
        playerDao.upsertPlayer(match.player1.toEntity())
        playerDao.upsertPlayer(match.player2.toEntity())

        matchDao.upsertMatch(match.toEntity())
    }
}