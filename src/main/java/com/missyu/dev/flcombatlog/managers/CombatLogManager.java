package com.missyu.dev.flcombatlog.managers;

import com.missyu.dev.flcombatlog.FLCombatLog;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatLogManager {

    private Map<UUID, UUID> combatLoggers = new HashMap<>();

    private FLCombatLog plugin;
    public CombatLogManager(FLCombatLog plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(UUID playerUniqueId, UUID npcUniqueId) {
        this.combatLoggers.put(playerUniqueId, npcUniqueId);
    }

    public void removePlayer(UUID playerUniqueId) {
        if (!this.isCombatLogger(playerUniqueId))
            return;

        plugin.getNPCManager().removeNPC(this.combatLoggers.get(playerUniqueId));
        this.combatLoggers.remove(playerUniqueId);
    }

    public boolean isCombatLogger(UUID playerUniqueId) {
        return this.combatLoggers.containsKey(playerUniqueId) && this.combatLoggers.get(playerUniqueId) != null;
    }

    public UUID getPlayerNPC(UUID playerUniqueId) {
        return this.combatLoggers.get(playerUniqueId);
    }

}
