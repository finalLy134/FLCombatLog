package com.missyu.dev.flcombatlog.managers;

import com.missyu.dev.flcombatlog.FLCombatLog;
import com.missyu.dev.flcombatlog.events.PlayerCombatStateChangedEvent;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

public class CombatManager {

    private final HashMap<UUID, Integer> combat = new HashMap<>();

    private final FLCombatLog plugin;

    private Connection connection;
    public CombatManager(FLCombatLog plugin) {
        this.plugin = plugin;
        this.setup();
    }

    private void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        try {
            File dbFile = new File(plugin.getDataFolder(), "deadplayers.db");
            if (!dbFile.exists()) {
                dbFile.createNewFile();
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            try (PreparedStatement stmt = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS dead_players (UUID TEXT PRIMARY KEY, KillerName TEXT)")) {
                stmt.executeUpdate();
            }
            plugin.getLogger().info("Database setup completed successfully.");
        } catch (Exception e) {
            plugin.getLogger().severe("Database setup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setPlayerAsDead(UUID playerUUID, String killerName, boolean isDead) {
        if (isDead) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT OR REPLACE INTO dead_players (UUID, KillerName) VALUES (?, ?)")) {
                    stmt.setString(1, playerUUID.toString());
                    stmt.setString(2, killerName);
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } else {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM dead_players WHERE UUID = ?")) {
                    stmt.setString(1, playerUUID.toString());
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public boolean isPlayerDead(UUID playerUUID) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM dead_players WHERE UUID = ?")) {
            stmt.setString(1, playerUUID.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getKillerName(UUID playerUUID) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT KillerName FROM dead_players WHERE UUID = ?")) {
            stmt.setString(1, playerUUID.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("KillerName");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addPlayer(Player player) {
        if (combat.containsKey(player.getUniqueId())) {
            this.resetTimeRemain(player);
            return;
        }

        combat.put(player.getUniqueId(), plugin.getConfig().getInt("settings.combat-tag-duration"));
        plugin.getServer().getPluginManager().callEvent(new PlayerCombatStateChangedEvent(player, true));
    }

    public void removePlayer(Player player) {
        if (!combat.containsKey(player.getUniqueId()))
            return;

        combat.remove(player.getUniqueId());
        plugin.getServer().getPluginManager().callEvent(new PlayerCombatStateChangedEvent(player, false));
    }

    public boolean isInCombat(Player player) {
        if (player == null)
            return false;

        return combat.containsKey(player.getUniqueId());
    }

    public int getTimeRemain(Player player) {
        if (player == null)
            return -1;

        return combat.get(player.getUniqueId());
    }

    public void resetTimeRemain(Player player) {
        if (player == null)
            return;

        combat.replace(player.getUniqueId(), plugin.getConfig().getInt("settings.combat-tag-duration"));
        plugin.getServer().getPluginManager().callEvent(new PlayerCombatStateChangedEvent(player, true));
    }

    public void decreaseTimeRemain(Player player) {
        int seconds = combat.get(player.getUniqueId());
        combat.replace(player.getUniqueId(), seconds - 1);
    }

    public void setTimeRemain(Player player, int time) {
        combat.replace(player.getUniqueId(), time);
    }
}