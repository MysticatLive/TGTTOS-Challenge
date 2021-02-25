package me.mysticat.tgttos.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMoveListener : Listener {

  // Speed up respawning process
  @EventHandler
  fun onPlayerMove(event: PlayerMoveEvent) {
    if (event.to.y < 0) event.player.health = 0.0
  }
}
