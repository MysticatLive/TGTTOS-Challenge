package me.mysticat.tgttos.listeners

import me.mysticat.tgttos.Main
import me.mysticat.tgttos.utils.GameManagement
import me.mysticat.tgttos.utils.GamePhase
import me.mysticat.tgttos.utils.Utils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class BlockPlaceListener(private val plugin: Main) : Listener {

  // Prevent placing blocks that aren't wool mid-game
  @EventHandler
  fun onBlockPlace(event: BlockPlaceEvent) {
    if (GameManagement.gameIsRunning == GamePhase.PLAYING) {
      if (Utils.getItemNBTString(plugin, event.itemInHand, "placeable") != "true") {
        event.isCancelled = true
      } else {
        if (event.itemInHand == event.player.inventory.itemInMainHand)
            event.player.inventory.setItemInMainHand(event.itemInHand)
        else if (event.itemInHand == event.player.inventory.itemInOffHand)
            event.player.inventory.setItemInOffHand(event.itemInHand)
      }
    } else if (GameManagement.gameIsRunning == GamePhase.INTERMISSION) event.isCancelled = true
  }
}
