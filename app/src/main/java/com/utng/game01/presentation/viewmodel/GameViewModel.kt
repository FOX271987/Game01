package com.utng.game01.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utng.game01.data.model.*
import com.utng.game01.domain.physics.PhysicsEngine
import com.utng.game01.domain.physics.CollisionDetector
import com.utng.game01.util.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

class GameViewModel : ViewModel() {

    private val physicsEngine = PhysicsEngine()
    private val collisionDetector = CollisionDetector()

    private val _gameState = MutableStateFlow(createInitialState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var gameLoopJob: Job? = null

    private var isMovingLeft = false
    private var isMovingRight = false

    init {
        startGame()
    }

    private fun createInitialState(): GameState {
        return GameState(
            player = Player(
                position = Offset(100f, 630f),
                size = IntSize(50, 70)
            ),
            platforms = createLevel1Platforms(),
            enemies = createLevel1Enemies(),
            coins = createLevel1Coins(),
            gameStatus = GameStatus.PLAYING,
            currentLevel = 1
        )
    }

    private fun createLevel1Platforms(): List<Platform> {
        return listOf(
            Platform(
                position = Offset(0f, 700f),
                size = IntSize(800, 50),
                type = PlatformType.NORMAL
            ),
            Platform(
                position = Offset(300f, 600f),
                size = IntSize(150, 30),
                type = PlatformType.NORMAL
            ),
            Platform(
                position = Offset(600f, 500f),
                size = IntSize(150, 30),
                type = PlatformType.NORMAL
            ),
            Platform(
                position = Offset(900f, 400f),
                size = IntSize(150, 30),
                type = PlatformType.NORMAL
            ),
            Platform(
                position = Offset(1200f, 500f),
                size = IntSize(150, 30),
                type = PlatformType.NORMAL
            ),
            Platform(
                position = Offset(1500f, 600f),
                size = IntSize(150, 30),
                type = PlatformType.BOUNCY
            ),
            Platform(
                position = Offset(1800f, 700f),
                size = IntSize(200, 50),
                type = PlatformType.NORMAL
            )
        )
    }

    private fun createLevel1Enemies(): List<Enemy> {
        return listOf(
            Enemy(position = Offset(400f, 570f), type = EnemyType.GOOMBA),
            Enemy(position = Offset(700f, 470f), type = EnemyType.GOOMBA),
            Enemy(position = Offset(1000f, 370f), type = EnemyType.GOOMBA)
        )
    }

    private fun createLevel1Coins(): List<Coin> {
        return listOf(
            Coin(position = Offset(350f, 550f)),
            Coin(position = Offset(650f, 450f)),
            Coin(position = Offset(950f, 350f)),
            Coin(position = Offset(1250f, 450f)),
            Coin(position = Offset(1550f, 550f))
        )
    }

    fun startGame() {
        gameLoopJob?.cancel()

        gameLoopJob = viewModelScope.launch {
            while (true) {
                val startTime = System.currentTimeMillis()

                if (_gameState.value.isActive()) {
                    updateGame()
                }

                val elapsedTime = System.currentTimeMillis() - startTime
                val delayTime = Constants.FRAME_TIME_MS - elapsedTime
                if (delayTime > 0) {
                    delay(delayTime)
                }
            }
        }
    }

    private fun updateGame() {
        _gameState.update { currentState ->
            var updatedPlayer = currentState.player

            // 1. Aplicar movimiento horizontal
            updatedPlayer = when {
                isMovingLeft -> physicsEngine.applyHorizontalMovement(updatedPlayer, -1f)
                isMovingRight -> physicsEngine.applyHorizontalMovement(updatedPlayer, 1f)
                else -> physicsEngine.stopHorizontalMovement(updatedPlayer)
            }

            // 2. Actualizar física
            updatedPlayer = physicsEngine.updatePlayer(
                updatedPlayer,
                currentState.getActivePlatforms()
            )

            // 3. Actualizar enemigos
            val updatedEnemies = currentState.enemies.map { enemy ->
                physicsEngine.updateEnemy(enemy, currentState.getActivePlatforms())
            }

            // 4. Detectar colisiones con enemigos
            var playerAfterEnemyCollisions = updatedPlayer
            var finalEnemies = updatedEnemies

            for ((index, enemy) in updatedEnemies.withIndex()) {
                val collision = collisionDetector.checkPlayerEnemyCollision(
                    playerAfterEnemyCollisions,
                    enemy
                )

                if (collision != null && collision.collided) {
                    if (collision.jumpedOnTop) {
                        finalEnemies = finalEnemies.toMutableList().apply {
                            this[index] = enemy.copy(isAlive = false)
                        }
                        playerAfterEnemyCollisions = playerAfterEnemyCollisions.copy(
                            velocity = playerAfterEnemyCollisions.velocity.copy(y = -10f),
                            score = playerAfterEnemyCollisions.score + Constants.ENEMY_DEFEAT_POINTS
                        )
                    } else if (!playerAfterEnemyCollisions.isInvulnerable) {
                        playerAfterEnemyCollisions = takeDamage(playerAfterEnemyCollisions)
                    }
                }
            }

            // 5. Detectar colisiones con monedas
            var updatedCoins = currentState.coins
            var finalScore = playerAfterEnemyCollisions.score

            currentState.coins.forEachIndexed { index, coin ->
                if (!coin.isCollected &&
                    collisionDetector.checkPlayerCoinCollision(playerAfterEnemyCollisions, coin)) {
                    updatedCoins = updatedCoins.toMutableList().apply {
                        this[index] = coin.copy(isCollected = true)
                    }
                    finalScore += coin.value
                }
            }

            playerAfterEnemyCollisions = playerAfterEnemyCollisions.copy(score = finalScore)

            // 6. Actualizar temporizador de invulnerabilidad
            if (playerAfterEnemyCollisions.isInvulnerable) {
                val newTimer = playerAfterEnemyCollisions.invulnerabilityTimer - Constants.FRAME_TIME_MS
                if (newTimer <= 0) {
                    playerAfterEnemyCollisions = playerAfterEnemyCollisions.copy(
                        isInvulnerable = false,
                        invulnerabilityTimer = 0
                    )
                } else {
                    playerAfterEnemyCollisions = playerAfterEnemyCollisions.copy(
                        invulnerabilityTimer = newTimer
                    )
                }
            }

            // 7. Actualizar cámara
            val newCameraOffset = calculateCameraOffset(playerAfterEnemyCollisions.position.x)

            // 8. Verificar condiciones de victoria/derrota
            val newGameStatus = when {
                playerAfterEnemyCollisions.lives <= 0 -> GameStatus.GAME_OVER
                playerAfterEnemyCollisions.position.x >= Constants.WORLD_WIDTH - 200 ->
                    GameStatus.LEVEL_COMPLETE
                else -> currentState.gameStatus
            }

            currentState.copy(
                player = playerAfterEnemyCollisions,
                enemies = finalEnemies,
                coins = updatedCoins,
                cameraOffset = newCameraOffset,
                gameStatus = newGameStatus
            )
        }
    }

    private fun takeDamage(player: Player): Player {
        val newLives = player.lives - 1
        return player.copy(
            lives = newLives,
            isInvulnerable = true,
            invulnerabilityTimer = Constants.INVULNERABILITY_TIME_MS,
            velocity = player.velocity.copy(x = -5f, y = -8f)
        )
    }

    private fun calculateCameraOffset(playerX: Float): Float {
        val screenCenter = Constants.SCREEN_WIDTH / 2
        return (playerX - screenCenter).coerceAtLeast(0f)
            .coerceAtMost(Constants.WORLD_WIDTH - Constants.SCREEN_WIDTH)
    }

    fun jump() {
        _gameState.update { currentState ->
            currentState.copy(
                player = physicsEngine.applyJump(currentState.player)
            )
        }
    }

    fun moveLeft() {
        isMovingLeft = true
    }

    fun moveRight() {
        isMovingRight = true
    }

    fun stopMoveLeft() {
        isMovingLeft = false
    }

    fun stopMoveRight() {
        isMovingRight = false
    }

    fun togglePause() {
        _gameState.update { it.copy(isPaused = !it.isPaused) }
    }

    fun restartGame() {
        _gameState.value = createInitialState()
        isMovingLeft = false
        isMovingRight = false
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
    }
}