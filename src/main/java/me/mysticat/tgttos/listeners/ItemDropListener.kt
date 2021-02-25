package me.mysticat.tgttos.listeners

import me.mysticat.tgttos.Main
import me.mysticat.tgttos.utils.GameManagement
import me.mysticat.tgttos.utils.GamePhase
import me.mysticat.tgttos.utils.Utils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent

class ItemDropListener(private val plugin: Main) : Listener {

  // Prevent removing necessary items
  @EventHandler
  fun onBlockDropItem(event: PlayerDropItemEvent) {
    if (GameManagement.gameIsRunning != GamePhase.NOT_PLAYING &&
        Utils.getItemNBTString(plugin, event.itemDrop.itemStack, "droppable") == "false")
        event.isCancelled = true
  }
}
