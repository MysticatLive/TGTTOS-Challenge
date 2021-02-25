package me.mysticat.tgttos.commands

import me.mysticat.tgttos.Main
import me.mysticat.tgttos.utils.GameManagement
import me.mysticat.tgttos.utils.Utils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetGameInfo(private val plugin: Main) : CommandExecutor {

  private val pluginPrefix = "&f[&d&lTGTTOS&f]"
  private val invalidMessage = "$pluginPrefix &7&oInvalid map name! Use /set help"
  private val availableModes = "$pluginPrefix &7&oGame mode options: &dBuild&7&o, &dTrident&7&o."

  // Manage the creation of a map
  @ExperimentalStdlibApi
  override fun onCommand(
      sender: CommandSender,
      cmd: Command,
      label: String,
      args: Array<String>
  ): Boolean {
    if (sender !is Player) {
      Utils.sendMessageAsPlugin("Only players may run this command!")
    }
    if (args.isEmpty()) GameManagement.displayHelp(sender as Player)
    else if (args.size == 1) {
      if (args[0] == "spawn") setPosition(sender, "spawn")
      else if (args[0] == "help") GameManagement.displayHelp(sender as Player)
    } else if (args.size == 2) {
      val mapName = args[0].lowercase()
      if (args[1].lowercase() == "start") setGamePosition(sender, mapName, "start")
      else if (!plugin.config.contains("tgttos.game." + args[0].lowercase())) {
        sender.sendMessage(Utils.color(invalidMessage))
        return true
      } else
          when (args[1].lowercase()) {
            "finish" -> setGamePosition(sender, mapName, "finish")
            "glass1" -> setGamePosition(sender, mapName, "glass1")
            "glass2" -> setGamePosition(sender, mapName, "glass2")
            "corner1" -> setGamePosition(sender, mapName, "corner1")
            "corner2" -> setGamePosition(sender, mapName, "corner2")
            "mode" -> sender.sendMessage(Utils.color(availableModes))
            else -> sender.sendMessage(Utils.color(invalidMessage))
          }
    } else if (args.size == 3) {
      if (!plugin.config.contains("tgttos.game." + args[0].lowercase())) {
        sender.sendMessage(Utils.color(invalidMessage))
        return true
      }
      val mapName = args[0].lowercase()
      when (args[1].lowercase()) {
        "mode" -> {
          when (args[2].lowercase()) {
            "build" -> setValue(sender, mapName, "mode", "build")
            "trident" -> setValue(sender, mapName, "mode", "trident")
            else -> sender.sendMessage(Utils.color(availableModes))
          }
        }
        "seconds" -> setValue(sender, mapName, "seconds", args[2].toInt())
        else -> sender.sendMessage(Utils.color(invalidMessage))
      }
    } else sender.sendMessage(Utils.color(invalidMessage))
    return true
  }

  private fun setGamePosition(sender: CommandSender, mapName: String, mapSection: String) {
    GameManagement.setSavedLocation(plugin, sender as Player, "tgttos.game.$mapName.$mapSection")
    val location = GameManagement.getSavedLocation(plugin, "tgttos.game.$mapName.$mapSection")
    if (location != null)
        sender.sendMessage(
            Utils.color(
                "$pluginPrefix &f&oLocation of &d$mapName&f&o's &d$mapSection &f&oset to &d" +
                    location.x +
                    " " +
                    location.y +
                    " " +
                    location.z +
                    " &f&oin &d" +
                    location.world.name))
  }

  private fun setPosition(sender: CommandSender, mapSection: String) {
    GameManagement.setSavedLocation(plugin, sender as Player, "tgttos.$mapSection")
    val location = GameManagement.getSavedLocation(plugin, "tgttos.$mapSection")
    if (location != null)
        sender.sendMessage(
            Utils.color(
                "$pluginPrefix &f&oLocation of &d$mapSection &f&oset to &d" +
                    location.x +
                    " " +
                    location.y +
                    " " +
                    location.z +
                    " &f&oin &d" +
                    location.world.name))
  }

  private fun setValue(sender: CommandSender, mapName: String, key: String, value: String) {
    GameManagement.setConfigString(plugin, "tgttos.game.$mapName", key, value)
    sender.sendMessage(Utils.color("$pluginPrefix &d$mapName&f&o's &d$key &f&owas set to &d$value"))
  }

  private fun setValue(sender: CommandSender, mapName: String, key: String, value: Int) {
    try {
      GameManagement.setConfigString(plugin, "tgttos.game.$mapName", key, value)
      sender.sendMessage(
          Utils.color("$pluginPrefix &d$mapName&f&o's &d$key &f&owas set to &d$value"))
    } catch (error: IllegalArgumentException) {
      sender.sendMessage(Utils.color(invalidMessage))
    }
  }
}
