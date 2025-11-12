package com.utng.game01.util

object Constants {
    // Física del movimiento
    const val GRAVITY = 0.8f
    const val JUMP_FORCE = -18f
    const val MOVE_SPEED = 6f
    const val MAX_FALL_SPEED = 20f
    const val MAX_HORIZONTAL_SPEED = 12f

    // Dimensiones del mundo
    const val GROUND_LEVEL = 700f
    const val WORLD_WIDTH = 3000f
    const val SCREEN_HEIGHT = 800f
    const val SCREEN_WIDTH = 400f

    // Game loop
    const val TARGET_FPS = 60
    const val FRAME_TIME_MS = 1000L / TARGET_FPS

    // Gameplay
    const val INVULNERABILITY_TIME_MS = 2000L
    const val ENEMY_DEFEAT_POINTS = 100
    const val COIN_POINTS = 10
    const val ENEMY_SPEED = 2f
}