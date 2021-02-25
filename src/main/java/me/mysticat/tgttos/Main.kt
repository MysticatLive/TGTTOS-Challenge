package me.mysticat.tgttos

import me.mysticat.tgttos.commands.*
import me.mysticat.tgttos.listeners.*
import me.mysticat.tgttos.utils.Utils
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

  override fun onEnable() {
    this.config.options().copyDefaults(true)
    Utils.sendMessageAsPlugin(Utils.color("&aPlugin Enabled Successfully!"))
    getCommand("start")?.setExecutor(StartGame(this))
    getCommand("set")?.setExecutor(SetGameInfo(this))
    getCommand("delete")?.setExecutor(DeleteGameInfo(this))
    getCommand("maps")?.setExecutor(ListMaps(this))
    getCommand("end")?.setExecutor(EndGame(this))
    server.pluginManager.registerEvents(ItemDropListener(this), this)
    server.pluginManager.registerEvents(BlockPlaceListener(this), this)
    server.pluginManager.registerEvents(BlockBreakListener(this), this)
    server.pluginManager.registerEvents(EntityDamageListener(), this)
    server.pluginManager.registerEvents(EntityDeathListener(), this)
    server.pluginManager.registerEvents(PlayerMoveListener(), this)
  }

  override fun onDisable() {
    Utils.sendMessageAsPlugin(Utils.color("&cPlugin Disabled Successfully!"))
  }
}
