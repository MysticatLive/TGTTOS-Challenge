package me.mysticat.tgttos.listeners

import me.mysticat.tgttos.Main
import me.mysticat.tgttos.utils.GameManagement
import me.mysticat.tgttos.utils.GamePhase
import me.mysticat.tgttos.utils.Utils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class BlockBreakListener(private var plugin: Main) : Listener {

  // Prevent block breaking aside from shears on wool
  @EventHandler
  fun onBlockBreak(event: BlockBreakEvent) {
    if (event.block.type.toString() ==
        GameManagement.getConfigString(plugin, "tgttos.settings.block") &&
        Utils.getItemNBTString(plugin, event.player.inventory.itemInMainHand, "usable") == "true")
        return
    if (GameManagement.gameIsRunning != GamePhase.NOT_PLAYING) event.isCancelled = true
  }
}
