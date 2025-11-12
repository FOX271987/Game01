package com.utng.game01.presentation.component


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.utng.game01.data.model.Enemy
import com.utng.game01.data.model.Coin

@Composable
fun EnemyComponent(
    enemy: Enemy,
    cameraOffset: Float,
    modifier: Modifier = Modifier
) {
    if (!enemy.isAlive) return

    val screenX = enemy.position.x - cameraOffset
    val screenY = enemy.position.y

    val enemyColor = Color(0xFF8B0000)

    Box(
        modifier = modifier
            .offset(x = screenX.dp, y = screenY.dp)
            .size(width = enemy.size.width.dp, height = enemy.size.height.dp)
            .background(
                color = enemyColor,
                shape = CircleShape
            )
    )
}

@Composable
fun CoinComponent(
    coin: Coin,
    cameraOffset: Float,
    modifier: Modifier = Modifier
) {
    if (coin.isCollected) return

    val screenX = coin.position.x - cameraOffset
    val screenY = coin.position.y

    val coinColor = Color(0xFFFFD700)

    Box(
        modifier = modifier
            .offset(x = screenX.dp, y = screenY.dp)
            .size(width = coin.size.width.dp, height = coin.size.height.dp)
            .background(
                color = coinColor,
                shape = CircleShape
            )
    )
}