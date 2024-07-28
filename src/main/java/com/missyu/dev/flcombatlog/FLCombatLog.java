package com.missyu.dev.flcombatlog;

import com.missyu.dev.flcombatlog.commands.FLCLCommand;
import com.missyu.dev.flcombatlog.listeners.CombatLogListener;
import com.missyu.dev.flcombatlog.listeners.PlayerListener;
import com.missyu.dev.flcombatlog.managers.CombatLogManager;
import com.missyu.dev.flcombatlog.managers.CombatManager;
import com.missyu.dev.flcombatlog.managers.NPCManager;
import com.missyu.dev.flcombatlog.managers.TraitManager;
import com.missyu.dev.flcombatlog.utils.Utils;
import org.bukkit.plugin.java.JavaPlugin;

public final class FLCombatLog extends JavaPlugin {

    private CombatManager combatManager;
    private CombatLogManager combatLogManager;
    private TraitManager traitManager;
    private NPCManager npcManager;

    public CombatManager getCombatManager() {
        return this.combatManager;
    }
    public CombatLogManager getCombatLogManager() { return this.combatLogManager; }
    public TraitManager getTraitManager() {
        return this.traitManager;
    }
    public NPCManager getNPCManager() {
        return this.npcManager;
    }

    void init() {
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        this.getConfig().addDefault("messages.in-combat", "&cYou are now in combat &4&l>> &a%seconds% seconds.");
        this.getConfig().addDefault("messages.no-longer", "&cYou are no longer in combat.");
        this.getConfig().addDefault("settings.npc-lifetime-seconds", 30);
        this.getConfig().addDefault("settings.combat-tag-duration", 30);
        this.saveConfig();

        this.combatManager = new CombatManager(this);
        this.combatLogManager = new CombatLogManager(this);
        this.traitManager = new TraitManager(this);
        this.npcManager = new NPCManager(this);
    }

    void register() {
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new CombatLogListener(this), this);

        FLCLCommand flclCommand = new FLCLCommand(this);
        this.getCommand("flcombatlog").setExecutor(flclCommand);
        this.getCommand("flcombatlog").setTabCompleter(flclCommand);
    }
    void start() {
        this.getServer().getConsoleSender().sendMessage(Utils.chat("[FLCombatLog] &aLoaded FLCombatLog."));
    }
    void stop() {
        this.combatManager.close();
        this.getServer().getConsoleSender().sendMessage(Utils.chat("[FLCombatLog] &cStopped FLCombatLog."));
    }

    @Override
    public void onEnable() {
        this.init();
        this.register();
        this.start();
    }

    @Override
    public void onDisable() {
        this.stop();
    }
}
