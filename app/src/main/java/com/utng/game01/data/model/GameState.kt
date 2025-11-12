package com.utng.game01.data.model

import androidx.compose.ui.geometry.Offset

data class GameState(
    val player: Player = Player(),
    val platforms: List<Platform> = emptyList(),
    val enemies: List<Enemy> = emptyList(),
    val coins: List<Coin> = emptyList(),
    val gameStatus: GameStatus = GameStatus.PLAYING,
    val currentLevel: Int = 1,
    val timeRemaining: Int = 300,
    val cameraOffset: Float = 0f,
    val isPaused: Boolean = false
) {
    fun isActive(): Boolean {
        return gameStatus == GameStatus.PLAYING && !isPaused
    }

    fun getAliveEnemies(): List<Enemy> {
        return enemies.filter { it.isAlive }
    }

    fun getActivePlatforms(): List<Platform> {
        return platforms.filter { it.canSupport() }
    }
}

enum class GameStatus {
    PLAYING,
    PAUSED,
    GAME_OVER,
    LEVEL_COMPLETE
}