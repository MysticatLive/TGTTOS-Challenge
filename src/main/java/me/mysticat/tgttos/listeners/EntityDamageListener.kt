package me.mysticat.tgttos.listeners

import me.mysticat.tgttos.utils.GameManagement
import me.mysticat.tgttos.utils.GamePhase
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Chicken
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class EntityDamageListener : Listener {

  // Manage PvP & Chicken Hitting
  @EventHandler
  fun onEntityDamage(event: EntityDamageByEntityEvent) {
    val victim = event.entity
    val attacker = event.damager
    if (GameManagement.gameIsRunning == GamePhase.PLAYING &&
        event.entityType == EntityType.CHICKEN &&
        attacker is Player) {
      GameManagement.playersInGame.remove(attacker.uniqueId)
      (victim as Chicken).health = 0.0
      attacker.gameMode = GameMode.SPECTATOR
    } else if (GameManagement.gameIsRunning == GamePhase.PLAYING &&
        event.entityType == EntityType.PLAYER &&
        attacker is Player)
        (victim as Player).health =
            victim.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.defaultValue!!
    else if (GameManagement.gameIsRunning != GamePhase.PLAYING &&
        event.entityType == EntityType.PLAYER &&
        attacker is Player)
        event.isCancelled = true
  }
}
