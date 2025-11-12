package com.utng.game01.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.utng.game01.data.model.Player

@Composable
fun PlayerComponent(
    player: Player,
    cameraOffset: Float,
    modifier: Modifier = Modifier
) {
    val screenX = player.position.x - cameraOffset
    val screenY = player.position.y

    val playerColor = Color(0xFFE74C3C)

    val alpha = if (player.isInvulnerable) {
        if ((player.invulnerabilityTimer / 100) % 2 == 0L) 0.3f else 1f
    } else {
        1f
    }

    Box(
        modifier = modifier
            .offset(x = screenX.dp, y = screenY.dp)
            .size(width = player.size.width.dp, height = player.size.height.dp)
            .alpha(alpha)
            .background(
                color = playerColor,
                shape = RoundedCornerShape(8.dp)
            )
    )
}