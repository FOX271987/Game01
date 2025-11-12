package com.utng.game01.domain.physics

import androidx.compose.ui.geometry.Rect
import com.utng.game01.data.model.Player
import com.utng.game01.data.model.Platform
import com.utng.game01.data.model.Enemy
import com.utng.game01.data.model.Coin
import kotlin.math.abs

class CollisionDetector {

    fun checkAABB(rect1: Rect, rect2: Rect): Boolean {
        return rect1.left < rect2.right &&
                rect1.right > rect2.left &&
                rect1.top < rect2.bottom &&
                rect1.bottom > rect2.top
    }

    fun checkPlayerPlatformCollision(
        player: Player,
        platform: Platform
    ): CollisionInfo? {
        val playerBox = player.getBoundingBox()
        val platformBox = platform.getBoundingBox()

        if (!checkAABB(playerBox, platformBox)) {
            return null
        }

        val side = determineCollisionSide(playerBox, platformBox, player.velocity)

        return CollisionInfo(
            collided = true,
            side = side,
            penetrationDepth = calculatePenetration(playerBox, platformBox, side)
        )
    }

    private fun determineCollisionSide(
        playerBox: Rect,
        platformBox: Rect,
        playerVelocity: androidx.compose.ui.geometry.Offset
    ): CollisionSide {
        val overlapLeft = playerBox.right - platformBox.left
        val overlapRight = platformBox.right - playerBox.left
        val overlapTop = playerBox.bottom - platformBox.top
        val overlapBottom = platformBox.bottom - playerBox.top

        val minOverlap = minOf(overlapLeft, overlapRight, overlapTop, overlapBottom)

        return when {
            minOverlap == overlapTop && playerVelocity.y > 0 -> CollisionSide.TOP
            minOverlap == overlapBottom && playerVelocity.y < 0 -> CollisionSide.BOTTOM
            minOverlap == overlapLeft && playerVelocity.x > 0 -> CollisionSide.LEFT
            minOverlap == overlapRight && playerVelocity.x < 0 -> CollisionSide.RIGHT
            else -> CollisionSide.NONE
        }
    }

    private fun calculatePenetration(
        playerBox: Rect,
        platformBox: Rect,
        side: CollisionSide
    ): Float {
        return when (side) {
            CollisionSide.TOP -> playerBox.bottom - platformBox.top
            CollisionSide.BOTTOM -> platformBox.bottom - playerBox.top
            CollisionSide.LEFT -> playerBox.right - platformBox.left
            CollisionSide.RIGHT -> platformBox.right - playerBox.left
            CollisionSide.NONE -> 0f
        }
    }

    fun checkPlayerEnemyCollision(
        player: Player,
        enemy: Enemy
    ): EnemyCollisionInfo? {
        if (!enemy.isAlive) return null

        val playerBox = player.getBoundingBox()
        val enemyBox = enemy.getBoundingBox()

        if (!checkAABB(playerBox, enemyBox)) {
            return null
        }

        val playerFeet = player.getFeetBoundingBox()
        val enemyTop = enemy.getTopHitbox()
        val jumpedOnEnemy = checkAABB(playerFeet, enemyTop) && player.velocity.y > 0

        return EnemyCollisionInfo(
            collided = true,
            jumpedOnTop = jumpedOnEnemy
        )
    }

    fun checkPlayerCoinCollision(
        player: Player,
        coin: Coin
    ): Boolean {
        if (coin.isCollected) return false

        val playerBox = player.getBoundingBox()
        val coinBox = coin.getBoundingBox()

        return checkAABB(playerBox, coinBox)
    }
}

data class CollisionInfo(
    val collided: Boolean,
    val side: CollisionSide,
    val penetrationDepth: Float
)

enum class CollisionSide {
    TOP,
    BOTTOM,
    LEFT,
    RIGHT,
    NONE
}

data class EnemyCollisionInfo(
    val collided: Boolean,
    val jumpedOnTop: Boolean
)