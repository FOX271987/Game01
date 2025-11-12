package com.utng.game01.domain.physics

import androidx.compose.ui.geometry.Offset
import com.utng.game01.data.model.Player
import com.utng.game01.data.model.Platform
import com.utng.game01.data.model.Enemy
import com.utng.game01.data.model.Direction
import com.utng.game01.util.Constants
import kotlin.math.abs

class PhysicsEngine(
    private val collisionDetector: CollisionDetector = CollisionDetector()
) {

    /**
     * VERSIÓN FUNCIONAL Y PROBADA
     * Actualiza la física del jugador
     */
    fun updatePlayer(
        player: Player,
        platforms: List<Platform>,
        deltaTime: Float = 1f
    ): Player {
        // 1. Aplicar gravedad
        val newVelocityY = player.velocity.y + Constants.GRAVITY

        // 2. Calcular nueva posición
        val newPositionX = player.position.x + player.velocity.x
        val newPositionY = player.position.y + newVelocityY

        // 3. Crear player con nueva posición
        var updatedPlayer = player.copy(
            position = Offset(newPositionX, newPositionY),
            velocity = Offset(player.velocity.x, newVelocityY)
        )

        // 4. Detectar colisiones con plataformas
        var landedOnPlatform = false
        for (platform in platforms) {
            if (!platform.canSupport()) continue

            val collision = collisionDetector.checkPlayerPlatformCollision(
                updatedPlayer,
                platform
            )

            if (collision != null) {
                when (collision.side) {
                    CollisionSide.TOP -> {
                        updatedPlayer = updatedPlayer.copy(
                            position = Offset(
                                updatedPlayer.position.x,
                                platform.position.y - updatedPlayer.size.height
                            ),
                            velocity = Offset(updatedPlayer.velocity.x, 0f)
                        )
                        landedOnPlatform = true
                    }
                    CollisionSide.BOTTOM -> {
                        updatedPlayer = updatedPlayer.copy(
                            position = Offset(
                                updatedPlayer.position.x,
                                platform.position.y + platform.size.height
                            ),
                            velocity = Offset(updatedPlayer.velocity.x, 0f)
                        )
                    }
                    CollisionSide.LEFT, CollisionSide.RIGHT -> {
                        updatedPlayer = updatedPlayer.copy(
                            velocity = Offset(0f, updatedPlayer.velocity.y)
                        )
                    }
                    CollisionSide.NONE -> {}
                }
            }
        }

        // 5. Verificar límite del suelo
        val groundY = Constants.GROUND_LEVEL - updatedPlayer.size.height
        val isOnGround = updatedPlayer.position.y >= groundY

        if (isOnGround) {
            updatedPlayer = updatedPlayer.copy(
                position = Offset(updatedPlayer.position.x, groundY),
                velocity = Offset(updatedPlayer.velocity.x, 0f)
            )
        }

        // 6. Actualizar isJumping
        val grounded = isOnGround || landedOnPlatform
        updatedPlayer = updatedPlayer.copy(
            isJumping = !grounded
        )

        // 7. Limitar posición horizontal
        val clampedX = updatedPlayer.position.x.coerceIn(
            0f,
            Constants.WORLD_WIDTH - updatedPlayer.size.width
        )

        updatedPlayer = updatedPlayer.copy(
            position = Offset(clampedX, updatedPlayer.position.y)
        )

        // 8. Actualizar estado visual
        return updatedPlayer.updateState()
    }

    fun applyJump(player: Player): Player {
        return if (!player.isJumping) {
            player.copy(
                velocity = player.velocity.copy(y = Constants.JUMP_FORCE),
                isJumping = true
            )
        } else {
            player
        }
    }

    fun applyHorizontalMovement(player: Player, direction: Float): Player {
        val newVelocityX = (Constants.MOVE_SPEED * direction).coerceIn(
            -Constants.MAX_HORIZONTAL_SPEED,
            Constants.MAX_HORIZONTAL_SPEED
        )

        val newFacingRight = when {
            direction > 0 -> true
            direction < 0 -> false
            else -> player.isFacingRight
        }

        return player.copy(
            velocity = player.velocity.copy(x = newVelocityX),
            isFacingRight = newFacingRight
        )
    }

    fun stopHorizontalMovement(player: Player): Player {
        return player.copy(
            velocity = player.velocity.copy(x = 0f)
        )
    }

    fun updateEnemy(enemy: Enemy, platforms: List<Platform>): Enemy {
        if (!enemy.isAlive) return enemy

        val newX = enemy.position.x + enemy.velocity.x

        val shouldTurn = platforms.any { platform ->
            val enemyBottom = enemy.position.y + enemy.size.height
            val onPlatform = abs(enemyBottom - platform.position.y) < 5f

            if (onPlatform) {
                val enemyRight = newX + enemy.size.width
                val enemyLeft = newX

                when (enemy.direction) {
                    Direction.RIGHT -> enemyRight >= platform.position.x + platform.size.width
                    Direction.LEFT -> enemyLeft <= platform.position.x
                }
            } else {
                false
            }
        }

        val newDirection = if (shouldTurn) {
            when (enemy.direction) {
                Direction.RIGHT -> Direction.LEFT
                Direction.LEFT -> Direction.RIGHT
            }
        } else {
            enemy.direction
        }

        val newVelocityX = when (newDirection) {
            Direction.RIGHT -> Constants.ENEMY_SPEED
            Direction.LEFT -> -Constants.ENEMY_SPEED
        }

        return enemy.copy(
            position = Offset(newX, enemy.position.y),
            velocity = enemy.velocity.copy(x = newVelocityX),
            direction = newDirection
        )
    }
}