package me.mysticat.tgttos.commands

import me.mysticat.tgttos.Main
import me.mysticat.tgttos.utils.GameManagement
import me.mysticat.tgttos.utils.Utils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DeleteGameInfo(private val plugin: Main) : CommandExecutor {

  private val pluginPrefix = "&f[&d&lTGTTOS&f]"
  private val invalidMessage = "$pluginPrefix &7&oInvalid map name! Use /set help"

  // Delete a map
  @ExperimentalStdlibApi
  override fun onCommand(
      sender: CommandSender,
      cmd: Command,
      label: String,
      args: Array<String>
  ): Boolean {
    val mapName = args[0].lowercase()
    when {
      args.isEmpty() -> GameManagement.displayHelp(sender as Player)
      args.size > 1 -> sender.sendMessage(Utils.color(invalidMessage))
      plugin.config.contains("tgttos.game.$mapName") -> {
        plugin.config.set("tgttos.game.$mapName", null)
        sender.sendMessage(Utils.color("$pluginPrefix &7&oMap &d$mapName &7&odeleted!"))
      }
      else -> sender.sendMessage(Utils.color(invalidMessage))
    }
    return true
  }
}
