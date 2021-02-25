package me.mysticat.tgttos.utils

import me.mysticat.tgttos.Main
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

object Utils {
  private const val pluginPrefix = "&d&lTGTTOS"

  fun sendMessageAsPlugin(message: String) {
    Bukkit.getConsoleSender().sendMessage(color("$pluginPrefix > $message"))
  }

  fun color(message: String): String {
    return ChatColor.translateAlternateColorCodes('&', message)
  }

  fun resetPlayer(player: Player) {
    player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.defaultValue!!
    player.foodLevel = 20
    player.inventory.clear()
    player.inventory.setArmorContents(null)
    player.updateInventory()
  }

  fun fillBlocks(corner1: Location, corner2: Location, material: Material) {
    val lowerNorthWest = lowerCorner(corner1, corner2)
    val upperSouthEast = upperCorner(corner1, corner2)
    if (lowerNorthWest.world == null || lowerNorthWest.world != upperSouthEast.world) return
    for (x in lowerNorthWest.blockX..upperSouthEast.blockX) {
      for (y in lowerNorthWest.blockY..upperSouthEast.blockY) {
        for (z in lowerNorthWest.blockZ..upperSouthEast.blockZ) {
          lowerNorthWest.world.getBlockAt(x, y, z).type = material
        }
      }
    }
  }

  fun replaceBlocks(
      corner1: Location,
      corner2: Location,
      oldMaterial: Material,
      newMaterial: Material
  ) {
    val lowerNorthWest = lowerCorner(corner1, corner2)
    val upperSouthEast = upperCorner(corner1, corner2)
    if (lowerNorthWest.world == null || lowerNorthWest.world != upperSouthEast.world) return
    for (x in lowerNorthWest.blockX..upperSouthEast.blockX) {
      for (y in lowerNorthWest.blockY..upperSouthEast.blockY) {
        for (z in lowerNorthWest.blockZ..upperSouthEast.blockZ) {
          if (lowerNorthWest.world.getBlockAt(x, y, z).type == oldMaterial)
              lowerNorthWest.world.getBlockAt(x, y, z).type = newMaterial
        }
      }
    }
  }

  private fun lowerCorner(corner1: Location, corner2: Location): Location {
    return Location(
        corner1.world,
        minOf(corner1.blockX, corner2.blockX).toDouble(),
        minOf(corner1.blockY, corner2.blockY).toDouble(),
        minOf(corner1.blockZ, corner2.blockZ).toDouble())
  }

  private fun upperCorner(corner1: Location, corner2: Location): Location {
    return Location(
        corner1.world,
        maxOf(corner1.blockX, corner2.blockX).toDouble(),
        maxOf(corner1.blockY, corner2.blockY).toDouble(),
        maxOf(corner1.blockZ, corner2.blockZ).toDouble())
  }

  fun getItemNBTString(plugin: Main, item: ItemStack, key: String): String? {
    if (!item.hasItemMeta()) return null
    val namespacedKey = NamespacedKey(plugin, key)
    val meta = item.itemMeta
    val container = meta.persistentDataContainer
    if (container.has(namespacedKey, PersistentDataType.STRING))
        return container.get(namespacedKey, PersistentDataType.STRING)
    return null
  }

  fun setItemNBTString(plugin: Main, meta: ItemMeta, key: String, value: String) {
    val namespacedKey = NamespacedKey(plugin, key)
    meta.persistentDataContainer.set(namespacedKey, PersistentDataType.STRING, value)
  }
}
