package com.utng.game01.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utng.game01.data.model.GameStatus
import com.utng.game01.presentation.component.*
import com.utng.game01.presentation.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel()
) {
    val gameState by viewModel.gameState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF87CEEB))
    ) {
        // Capa del juego
        Box(modifier = Modifier.fillMaxSize()) {
            // Plataformas
            gameState.platforms.forEach { platform ->
                PlatformComponent(
                    platform = platform,
                    cameraOffset = gameState.cameraOffset
                )
            }

            // Monedas
            gameState.coins.forEach { coin ->
                CoinComponent(
                    coin = coin,
                    cameraOffset = gameState.cameraOffset
                )
            }

            // Enemigos
            gameState.enemies.forEach { enemy ->
                EnemyComponent(
                    enemy = enemy,
                    cameraOffset = gameState.cameraOffset
                )
            }

            // Jugador
            PlayerComponent(
                player = gameState.player,
                cameraOffset = gameState.cameraOffset
            )
        }

        // HUD
        GameHUD(
            player = gameState.player,
            timeRemaining = gameState.timeRemaining,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Controles
        GameControls(
            onMoveLeft = { viewModel.moveLeft() },
            onMoveRight = { viewModel.moveRight() },
            onStopMoveLeft = { viewModel.stopMoveLeft() },
            onStopMoveRight = { viewModel.stopMoveRight() },
            onJump = { viewModel.jump() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )

        // Game Over
        if (gameState.gameStatus == GameStatus.GAME_OVER) {
            GameOverScreen(
                score = gameState.player.score,
                onRestart = { viewModel.restartGame() }
            )
        }

        // Level Complete
        if (gameState.gameStatus == GameStatus.LEVEL_COMPLETE) {
            LevelCompleteScreen(
                score = gameState.player.score,
                onNextLevel = { viewModel.restartGame() }
            )
        }
    }
}

@Composable
fun LevelCompleteScreen(
    score: Int,
    onNextLevel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "LEVEL COMPLETE!",
                color = Color.Green,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Score: $score",
                color = Color.White,
                fontSize = 32.sp
            )

            Button(onClick = onNextLevel) {
                Text("Next Level")
            }
        }
    }
}