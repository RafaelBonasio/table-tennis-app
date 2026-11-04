package com.rafaelbonasio.tabletennisapp.data.database

import androidx.room3.Dao
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.Upsert
import com.rafaelbonasio.tabletennisapp.core.Player
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Entity("players")
data class PlayerEntity(
    @PrimaryKey val id: Uuid,
    val name: String
)

@Dao
interface PlayerDao {
    @Upsert
    suspend fun upsertPlayer(player: PlayerEntity)

    @Query("SELECT * FROM players")
    fun getAllPlayers(): Flow<List<PlayerEntity>>
}

fun PlayerEntity.toPlayer(): Player {
    return Player(id, name)
}

fun Player.toEntity(): PlayerEntity {
    return PlayerEntity(id, name)
}
