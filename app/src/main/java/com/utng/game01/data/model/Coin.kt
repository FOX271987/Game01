package com.utng.game01.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize

data class Coin(
    val position: Offset,
    val size: IntSize = IntSize(20, 20),
    val isCollected: Boolean = false,
    val value: Int = 100,
    val coinType: CoinType = CoinType.NORMAL
) {
    fun getBoundingBox(): Rect {
        return Rect(
            left = position.x,
            top = position.y,
            right = position.x + size.width,
            bottom = position.y + size.height
        )
    }
}

enum class CoinType {
    NORMAL,
    RED,
    BLUE,
    RAINBOW
}