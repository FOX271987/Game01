package com.utng.game01.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize

data class Enemy(
    val position: Offset,
    val velocity: Offset = Offset(2f, 0f),
    val size: IntSize = IntSize(40, 40),
    val type: EnemyType = EnemyType.GOOMBA,
    val isAlive: Boolean = true,
    val direction: Direction = Direction.RIGHT
) {
    fun getBoundingBox(): Rect {
        return Rect(
            left = position.x,
            top = position.y,
            right = position.x + size.width,
            bottom = position.y + size.height
        )
    }

    fun getTopHitbox(): Rect {
        val topHeight = 10f
        return Rect(
            left = position.x,
            top = position.y,
            right = position.x + size.width,
            bottom = position.y + topHeight
        )
    }
}

enum class EnemyType {
    GOOMBA,
    KOOPA,
    PIRANHA
}

enum class Direction {
    LEFT,
    RIGHT
}