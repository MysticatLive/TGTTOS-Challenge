package me.mysticat.tgttos.listeners

import me.mysticat.tgttos.utils.GameManagement
import me.mysticat.tgttos.utils.GamePhase
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class EntityDeathListener : Listener {

  // Prevent chicken item drops
  @EventHandler
  fun onEntityDeath(event: EntityDeathEvent) {
    if (GameManagement.gameIsRunning == GamePhase.PLAYING && event.entityType == EntityType.CHICKEN)
        event.drops.clear()
  }
}
