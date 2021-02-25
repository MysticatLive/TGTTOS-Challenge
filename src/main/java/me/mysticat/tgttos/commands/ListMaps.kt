package me.mysticat.tgttos.commands

import me.mysticat.tgttos.Main
import me.mysticat.tgttos.utils.Utils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ListMaps(private var plugin: Main) : CommandExecutor {

  private val pluginPrefix = "&f[&d&lTGTTOS&f]"
  private val invalidMessage = "$pluginPrefix &7&oInvalid map name! Use /set help"

  // Provide sender with a list of registered maps
  @ExperimentalStdlibApi
  override fun onCommand(
      sender: CommandSender,
      cmd: Command,
      label: String,
      args: Array<String>
  ): Boolean {
    when {
      args.isEmpty() -> {
        sender.sendMessage(Utils.color("&f-------[&d&lTGTTOS&f]-------"))
        for (map in plugin.config.getConfigurationSection("tgttos.game")?.getKeys(false)!!) {
          sender.sendMessage(Utils.color("&d$map"))
        }
        sender.sendMessage(Utils.color("&f----------------------"))
      }
      args.isNotEmpty() -> sender.sendMessage(Utils.color(invalidMessage))
      else -> sender.sendMessage(Utils.color(invalidMessage))
    }
    return true
  }
}
