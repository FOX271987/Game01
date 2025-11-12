package com.utng.game01.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize

data class Player(
    val position: Offset = Offset(100f, 600f),
    val velocity: Offset = Offset.Zero,
    val size: IntSize = IntSize(50, 70),
    val isJumping: Boolean = false,
    val isFacingRight: Boolean = true,
    val lives: Int = 3,
    val score: Int = 0,
    val state: PlayerState = PlayerState.IDLE,
    val isInvulnerable: Boolean = false,
    val invulnerabilityTimer: Long = 0L
) {
    fun getBoundingBox(): Rect {
        return Rect(
            left = position.x,
            top = position.y,
            right = position.x + size.width,
            bottom = position.y + size.height
        )
    }

    fun getFeetBoundingBox(): Rect {
        val feetHeight = 5f
        return Rect(
            left = position.x + 5,
            top = position.y + size.height - feetHeight,
            right = position.x + size.width - 5,
            bottom = position.y + size.height
        )
    }

    fun isOnGround(groundLevel: Float): Boolean {
        val tolerance = 5f
        return (position.y + size.height) >= (groundLevel - tolerance)
    }

    fun updateState(): Player {
        val newState = when {
            state == PlayerState.DEAD -> PlayerState.DEAD
            isJumping && velocity.y < 0 -> PlayerState.JUMPING
            isJumping && velocity.y > 0 -> PlayerState.FALLING
            velocity.x != 0f -> PlayerState.RUNNING
            else -> PlayerState.IDLE
        }

        return this.copy(state = newState)
    }
}

sealed class PlayerState {
    object IDLE : PlayerState()
    object RUNNING : PlayerState()
    object JUMPING : PlayerState()
    object FALLING : PlayerState()
    object DEAD : PlayerState()
}