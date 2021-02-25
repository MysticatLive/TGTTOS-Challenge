package me.mysticat.tgttos.utils

import java.util.*
import me.mysticat.tgttos.Main
import org.bukkit.*
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object GameManagement {

  var gameIsRunning = GamePhase.NOT_PLAYING
  var playersInGame = mutableListOf<UUID>()
  private var secondsAfterGame = 5

  // Timer for the game's runtime
  fun gameTimer(plugin: Main, mapName: String) {
    gameIsRunning = GamePhase.PLAYING
    var ticks = getConfigInt(plugin, "tgttos.game.$mapName.seconds") * 20
    object : BukkitRunnable() {
          override fun run() {
            ticks--
            if (playersInGame.isEmpty()) ticks = 0
            if (ticks <= 0) {
              for (player in Bukkit.getOnlinePlayers()) {
                player.sendTitle(Utils.color("&dTime!"), Utils.color(""), 2, 10, 8)
              }
              postGameTimer(plugin, mapName)
              cancel()
            }
          }
        }
        .runTaskTimer(plugin, 0, 1)
  }

  // Timer for post-game, the stall before reset
  fun postGameTimer(plugin: Main, mapName: String) {
    var ticks = secondsAfterGame * 20
    val gameMode = getConfigString(plugin, "tgttos.game.$mapName.mode")
    val spawnLocation: Location? = getSavedLocation(plugin, "tgttos.spawn")
    val corner1Location: Location? = getSavedLocation(plugin, "tgttos.game.$mapName.corner1")
    val corner2Location: Location? = getSavedLocation(plugin, "tgttos.game.$mapName.corner2")
    gameIsRunning = GamePhase.INTERMISSION

    object : BukkitRunnable() {
          override fun run() {
            ticks--
            if (ticks <= 0) {
              if (gameMode == "build" && corner1Location != null && corner2Location != null) {
                Utils.replaceBlocks(
                    corner1Location, corner2Location, Material.PINK_WOOL, Material.AIR)
              }
              for (player in Bukkit.getOnlinePlayers()) {
                Utils.resetPlayer(player)
                player.gameMode = GameMode.ADVENTURE
                if (spawnLocation != null) {
                  player.teleport(spawnLocation)
                  player.setBedSpawnLocation(spawnLocation, true)
                }
              }
              // remove chickens
              for (chicken in spawnLocation?.world?.entities!!) {
                if (chicken.type == EntityType.CHICKEN) chicken.remove()
              }
              playersInGame.clear()
              corner1Location?.world?.setGameRule(GameRule.KEEP_INVENTORY, false)
              gameIsRunning = GamePhase.NOT_PLAYING
              cancel()
            }
          }
        }
        .runTaskTimer(plugin, 0, 1)
  }

  fun getSavedLocation(plugin: Main, configSection: String): Location? {
    val data: FileConfiguration = plugin.config
    if (plugin.config.contains(configSection)) {
      return Location(
          data.getString("$configSection.world")?.let { Bukkit.getWorld(it) },
          data.getDouble("$configSection.x"),
          data.getDouble("$configSection.y"),
          data.getDouble("$configSection.z"),
          data.getDouble("$configSection.yaw").toFloat(),
          data.getDouble("$configSection.pitch").toFloat())
    }
    return null
  }

  fun setSavedLocation(plugin: Main, player: Player, configSection: String) {
    val data: FileConfiguration = plugin.config
    data.set("$configSection.x", player.location.blockX)
    data.set("$configSection.y", player.location.blockY)
    data.set("$configSection.z", player.location.blockZ)
    data.set("$configSection.pitch", player.location.pitch)
    data.set("$configSection.yaw", player.location.yaw)
    data.set("$configSection.world", player.location.world.name)
    plugin.saveConfig()
  }

  fun getConfigInt(plugin: Main, configSection: String): Int {
    val data: FileConfiguration = plugin.config
    if (plugin.config.contains(configSection)) return data.getInt(configSection)
    return 0
  }

  fun setConfigInt(plugin: Main, configSection: String, key: String, value: Int) {
    val data: FileConfiguration = plugin.config
    data.set("$configSection.$key", value)
    plugin.saveConfig()
  }

  fun getConfigString(plugin: Main, configSection: String): String? {
    val data: FileConfiguration = plugin.config
    if (plugin.config.contains(configSection)) return data.getString(configSection)
    return null
  }

  fun setConfigString(plugin: Main, configSection: String, key: String, value: String) {
    val data: FileConfiguration = plugin.config
    data.set("$configSection.$key", value)
    plugin.saveConfig()
  }

  fun setConfigString(plugin: Main, configSection: String, key: String, value: Int) {
    val data: FileConfiguration = plugin.config
    data.set("$configSection.$key", value)
    plugin.saveConfig()
  }

  fun displayHelp(player: Player) {
    player.sendMessage(Utils.color("&f-------[&d&lTGTTOS&f]-------"))
    player.sendMessage(Utils.color("&d/start <map> &7&oBegin playing TGTTOS!"))
    player.sendMessage(Utils.color("&d/set spawn &7&oSet where players return to after the game."))
    player.sendMessage(Utils.color("&d/maps &7&oList all map names."))
    player.sendMessage(Utils.color("&d/end <map> &7&oEnd a game."))
    player.sendMessage(
        Utils.color("&d/set <map> start &7&oCreate a new map or set where one begins."))
    player.sendMessage(Utils.color("&d/set <map> finish &7&oSet where the map chickens spawn."))
    player.sendMessage(
        Utils.color("&d/set <map> glass1 &7&oSet the first corner of the starting glass."))
    player.sendMessage(
        Utils.color("&d/set <map> glass2 &7&oSet the second corner of the starting glass."))
    player.sendMessage(
        Utils.color(
            "&d/set <map> corner1 &7&oSet the first corner of the area to clean post-game."))
    player.sendMessage(
        Utils.color(
            "&d/set <map> corner2 &7&oSet the second corner of the area to clean post-game."))
    player.sendMessage(Utils.color("&d/remove <map> &7&oRemove a map."))
    player.sendMessage(Utils.color("&f----------------------"))
  }
}
