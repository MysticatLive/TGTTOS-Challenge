package me.mysticat.tgttos.commands

import java.util.*
import kotlin.math.roundToInt
import me.mysticat.tgttos.Main
import me.mysticat.tgttos.utils.GameManagement
import me.mysticat.tgttos.utils.GamePhase
import me.mysticat.tgttos.utils.Utils
import me.mysticat.tgttos.utils.Utils.color
import me.mysticat.tgttos.utils.Utils.resetPlayer
import me.mysticat.tgttos.utils.Utils.sendMessageAsPlugin
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

@Suppress("NAME_SHADOWING")
class StartGame(private val plugin: Main) : CommandExecutor {

  private val seconds = 5
  private val time = seconds * 20
  private val playerQueue: HashMap<UUID, Int> = HashMap<UUID, Int>()

  private val pluginPrefix = "&f[&d&lTGTTOS&f]"
  private val gameRunning = "$pluginPrefix &7&oA game is already running!"
  private val mapDNE = "$pluginPrefix &7&oThat map is not set up! Use /set help"

  @ExperimentalStdlibApi
  override fun onCommand(
      sender: CommandSender,
      cmd: Command,
      label: String,
      args: Array<String>
  ): Boolean {
    if (sender !is Player) {
      sendMessageAsPlugin("Only players may run this command!")
    }
    if (GameManagement.gameIsRunning != GamePhase.NOT_PLAYING)
        sender.sendMessage(color(gameRunning))
    else if (args.isEmpty()) {
      GameManagement.displayHelp(sender as Player)
      return true
    }
    val mapName = args[0].lowercase()
    if (args.size == 1 &&
        plugin.config.contains("tgttos.game.$mapName.start") &&
        plugin.config.contains("tgttos.game.$mapName.finish") &&
        plugin.config.contains("tgttos.game.$mapName.seconds") &&
        plugin.config.contains("tgttos.game.$mapName.mode")) {
      if (GameManagement.getConfigString(plugin, "tgttos.game.$mapName.mode") == "build") {
        if (!plugin.config.contains("tgttos.game.$mapName.corner1") &&
            !plugin.config.contains("tgttos.game.$mapName.corner2")) {
          sender.sendMessage(color(mapDNE))
          return true
        }
      }
      gameInit(args)
    } else sender.sendMessage(color(mapDNE))
    return true
  }

  @ExperimentalStdlibApi
  private fun gameInit(args: Array<String>) {
    val mapName = args[0].lowercase()
    val gameMode = GameManagement.getConfigString(plugin, "tgttos.game.$mapName.mode")
    var block: ItemStack? = null
    var tool: ItemStack? = null

    // set up map locations
    val startLocation: Location? =
        GameManagement.getSavedLocation(plugin, "tgttos.game.$mapName.start")
    val finishLocation: Location? =
        GameManagement.getSavedLocation(plugin, "tgttos.game.$mapName.finish")
    val glass1Location: Location? =
        GameManagement.getSavedLocation(plugin, "tgttos.game.$mapName.glass1")
    val glass2Location: Location? =
        GameManagement.getSavedLocation(plugin, "tgttos.game.$mapName.glass2")
    val corner1Location: Location? =
        GameManagement.getSavedLocation(plugin, "tgttos.game.$mapName.corner1")
    val corner2Location: Location? =
        GameManagement.getSavedLocation(plugin, "tgttos.game.$mapName.corner2")
    val glassMaterial =
        GameManagement.getConfigString(plugin, "tgttos.settings.glass")?.let {
          Material.matchMaterial(it)
        }
    val woolMaterial =
        GameManagement.getConfigString(plugin, "tgttos.settings.block")?.let {
          Material.matchMaterial(it)
        }
    if (glass1Location != null && glass2Location != null && glassMaterial != null) {
      Utils.fillBlocks(glass1Location, glass2Location, glassMaterial)
    }
    if (gameMode == "build" &&
        corner1Location != null &&
        corner2Location != null &&
        woolMaterial != null) {
      Utils.replaceBlocks(corner1Location, corner2Location, woolMaterial, Material.AIR)
    }

    // set up gamemode items
    if (gameMode == "build") {
      block = ItemStack(Material.PINK_WOOL, 1)
      val blockMeta = block.itemMeta
      Utils.setItemNBTString(plugin, blockMeta, "droppable", "false")
      Utils.setItemNBTString(plugin, blockMeta, "placeable", "true")
      block.itemMeta = blockMeta

      tool = ItemStack(Material.SHEARS, 1)
      val toolMeta = tool.itemMeta
      Utils.setItemNBTString(plugin, toolMeta, "droppable", "false")
      Utils.setItemNBTString(plugin, toolMeta, "usable", "true")
      tool.itemMeta = toolMeta
    } else if (gameMode == "trident") {
      tool = ItemStack(Material.TRIDENT, 1)
      val toolMeta = tool.itemMeta
      Utils.setItemNBTString(plugin, toolMeta, "droppable", "false")
      toolMeta.addEnchant(Enchantment.RIPTIDE, 3, true)
      tool.itemMeta = toolMeta
    }

    // begin game for each player
    GameManagement.gameIsRunning = GamePhase.INTERMISSION
    for (player in Bukkit.getOnlinePlayers()) {
      val uuid: UUID = player.uniqueId
      if (startLocation != null) {
        player.teleport(startLocation)
      }
      player.setBedSpawnLocation(startLocation, true)
      player.gameMode = GameMode.SURVIVAL
      resetPlayer(player)

      if (gameMode == "build") {
        player.inventory.setItemInOffHand(block)
        player.inventory.setItem(0, tool)
      } else if (gameMode == "trident") {
        player.inventory.setItem(0, tool)
      }

      // prepare for countdown timer
      playerQueue[uuid] = time
      GameManagement.playersInGame.add(uuid)

      // clear map
      for (item in startLocation?.world?.entities!!) {
        if (item is Item) item.remove()
      }

      // remove chickens
      for (chicken in startLocation.world?.entities!!) {
        if (chicken.type == EntityType.CHICKEN) chicken.remove()
      }

      // spawn chickens
      for (player in GameManagement.playersInGame) {
        finishLocation?.world?.spawnEntity(finishLocation, EntityType.CHICKEN)
      }

      // begin countdown timer
      object : BukkitRunnable() {
            override fun run() {
              if (playerQueue[uuid]!! % 20 == 0) {
                player.sendTitle(
                    color(
                        "&fGame begins in &d" + (playerQueue[uuid]!! / 20).toFloat().roundToInt()),
                    color("&dGet to the other side!"),
                    3,
                    14,
                    3)
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 1f)
              }
              playerQueue.replace(uuid, playerQueue[uuid]!! - 1)
              if (playerQueue[uuid]!! <= 0) {
                playerQueue.remove(uuid)
                player.sendTitle("", "", 0, 0, 0)
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 2f)

                // when all players are prepared
                if (playerQueue.isEmpty()) {
                  player.world.setGameRule(GameRule.KEEP_INVENTORY, true)
                  player.world.setGameRule(GameRule.DO_TILE_DROPS, false)
                  player.world.setGameRule(GameRule.FALL_DAMAGE, false)
                  player.world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
                  player.world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
                  player.world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
                  player.world.time = 1200
                  player.world.clearWeatherDuration = 100
                  if (glass1Location != null && glass2Location != null) {
                    Utils.replaceBlocks(
                        glass1Location, glass2Location, Material.PINK_STAINED_GLASS, Material.AIR)
                  }
                }
                GameManagement.gameTimer(plugin, mapName)
                cancel()
              }
            }
          }
          .runTaskTimer(plugin, 0, 1)
    }
  }
}
