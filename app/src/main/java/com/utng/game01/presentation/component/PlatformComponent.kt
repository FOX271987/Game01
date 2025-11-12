package com.utng.game01.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.utng.game01.data.model.Platform
import com.utng.game01.data.model.PlatformType

@Composable
fun PlatformComponent(
    platform: Platform,
    cameraOffset: Float,
    modifier: Modifier = Modifier
) {
    if (platform.isBroken) return

    val screenX = platform.position.x - cameraOffset
    val screenY = platform.position.y

    val platformColor = when (platform.type) {
        PlatformType.NORMAL -> Color(0xFF8B4513)
        PlatformType.BREAKABLE -> Color(0xFFDAA520)
        PlatformType.BOUNCY -> Color(0xFF00CED1)
        PlatformType.MOVING -> Color(0xFF9370DB)
        PlatformType.ICE -> Color(0xFFB0E0E6)
        PlatformType.SPIKE -> Color(0xFFDC143C)
    }

    Box(
        modifier = modifier
            .offset(x = screenX.dp, y = screenY.dp)
            .size(width = platform.size.width.dp, height = platform.size.height.dp)
            .background(
                color = platformColor,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 2.dp,
                color = platformColor.copy(alpha = 0.7f),
                shape = RoundedCornerShape(4.dp)
            )
    )
}