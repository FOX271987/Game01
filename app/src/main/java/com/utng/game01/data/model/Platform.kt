package com.utng.game01.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize

data class Platform(
    val position: Offset,
    val size: IntSize,
    val type: PlatformType = PlatformType.NORMAL,
    val isBreakable: Boolean = false,
    val isBroken: Boolean = false
) {
    fun getBoundingBox(): Rect {
        return Rect(
            left = position.x,
            top = position.y,
            right = position.x + size.width,
            bottom = position.y + size.height
        )
    }

    fun getTopSurface(): Rect {
        val surfaceThickness = 5f
        return Rect(
            left = position.x,
            top = position.y - surfaceThickness,
            right = position.x + size.width,
            bottom = position.y + surfaceThickness
        )
    }

    fun canSupport(): Boolean {
        return !isBroken
    }
}

enum class PlatformType {
    NORMAL,
    BREAKABLE,
    BOUNCY,
    MOVING,
    ICE,
    SPIKE
}