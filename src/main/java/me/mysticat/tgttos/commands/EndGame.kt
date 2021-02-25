package me.mysticat.tgttos.commands

import me.mysticat.tgttos.Main
import me.mysticat.tgttos.utils.GameManagement
import me.mysticat.tgttos.utils.GamePhase
import me.mysticat.tgttos.utils.Utils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class EndGame(private val plugin: Main) : CommandExecutor {

  private val pluginPrefix = "&f[&d&lTGTTOS&f]"
  private val invalidMessage = "$pluginPrefix &7&oInvalid map name! Use /set help"
  private val gameEnded = "$pluginPrefix &7&oGame ended!"
  private val noGameRunning = "$pluginPrefix &7&oThere is no game running!"

  // End a running game
  @ExperimentalStdlibApi
  override fun onCommand(
      sender: CommandSender,
      cmd: Command,
      label: String,
      args: Array<String>
  ): Boolean {
    when {
      args.isEmpty() -> sender.sendMessage(Utils.color(invalidMessage))
      (plugin.config.contains("tgttos.game." + args[0])) -> {
        if (GameManagement.gameIsRunning != GamePhase.PLAYING) {
          sender.sendMessage(Utils.color(noGameRunning))
          return true
        }
        GameManagement.postGameTimer(plugin, args[0].lowercase())
        sender.sendMessage(Utils.color(gameEnded))
      }
      else -> sender.sendMessage(Utils.color(invalidMessage))
    }
    return true
  }
}
