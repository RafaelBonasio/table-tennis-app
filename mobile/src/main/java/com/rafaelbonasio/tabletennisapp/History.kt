package com.rafaelbonasio.tabletennisapp

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rafaelbonasio.tabletennisapp.core.Game
import com.rafaelbonasio.tabletennisapp.core.GameRules
import com.rafaelbonasio.tabletennisapp.core.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun HistoryTab(gameViewModel: GameViewModel, historyViewModel: HistoryViewModel, onStartGame: () -> Unit, paddingValues: PaddingValues) {
    var showDialog by remember { mutableStateOf(false) }

    val games by historyViewModel.games.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Novo Jogo")
            }
        },
        modifier = Modifier.fillMaxSize().padding(paddingValues)
    ) { paddingValues ->
        LazyColumn(modifier =
            Modifier.fillMaxSize().padding(paddingValues)) {
            items(games) { game ->
                val scoreboard = game.calculateScoreboard()

                OutlinedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("${game.player1.name} vs. ${game.player2.name}")
                    Text("${scoreboard.player1Score} - ${scoreboard.player2Score}")
                    Text("S: ${scoreboard.player1SetScore} - ${scoreboard.player2SetScore}")
                }
            }
        }

        if (showDialog) {
            GameSettingsDialog({ showDialog = false}, { pointCount, setCount ->
                val player1 = Player(Uuid.random(), "Jogador Um")
                val player2 = Player(Uuid.random(), "Jogador Dois")

                val rules = GameRules(pointCount, setCount, 2)

                gameViewModel.newGame(player1, player2, rules)

                onStartGame()
            })
        }
    }
}

class HistoryViewModel : ViewModel() {
    var _games = MutableStateFlow<List<Game>>(emptyList())
    val games = _games.asStateFlow()

    fun addGame(game: Game) {
        _games.update { list -> list + game }
    }
}
