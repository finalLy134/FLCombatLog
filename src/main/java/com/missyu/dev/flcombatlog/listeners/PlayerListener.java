package com.missyu.dev.flcombatlog.listeners;

import com.missyu.dev.flcombatlog.FLCombatLog;
import com.missyu.dev.flcombatlog.utils.Utils;
import com.missyu.dev.flcombatlog.events.PlayerCombatLogEvent;
import com.missyu.dev.flcombatlog.events.PlayerCombatStateChangedEvent;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {

    private FLCombatLog plugin;
    public PlayerListener(FLCombatLog plugin) {
        this.plugin = plugin;
    }

    private Map<UUID, BukkitRunnable> countdownTasks = new HashMap<>();

    @EventHandler
    public void onAfterDeathJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getCombatManager().isPlayerDead(player.getUniqueId()))
            return;

        player.getInventory().clear();
        player.setHealth(0);
    }

    @EventHandler
    public void onCombatQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.getServer().getOnlinePlayers().isEmpty())
            return;

        if (!plugin.getCombatManager().isInCombat(player))
            return;

        plugin.getServer().getPluginManager().callEvent(new PlayerCombatLogEvent(player));
    }

    @EventHandler
    public void onCombatLoggerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getCombatLogManager().isCombatLogger(player.getUniqueId()))
            return;

        UUID npcUniqueId = plugin.getCombatLogManager().getPlayerNPC(player.getUniqueId());

        if (npcUniqueId != null) {
            NPC npc = plugin.getNPCManager().getNPC(npcUniqueId);

            if (npc != null) {
                Player npcPlayer = (Player) npc.getEntity();

                if (npcPlayer != null) {
                    player.setHealth(npcPlayer.getHealth());
                }
            }
        }

        plugin.getCombatLogManager().removePlayer(player.getUniqueId());

        if (countdownTasks.containsKey(player.getUniqueId()))
            plugin.getServer().getPluginManager().callEvent(new PlayerCombatStateChangedEvent(player, true));
    }

    @EventHandler
    public void onLoginDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!plugin.getCombatManager().isPlayerDead(player.getUniqueId()))
            return;

        String killerName = plugin.getCombatManager().getKillerName(player.getUniqueId());

        event.setDeathMessage(null);
        player.sendMessage(Utils.chat("&cYou were killed by &6" + killerName + "&c while logged out."));
        plugin.getCombatManager().setTimeRemain(player, 0);
        plugin.getCombatManager().setPlayerAsDead(player.getUniqueId(), null, false);
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if (!plugin.getCombatManager().isInCombat(player) || !plugin.getCombatManager().isInCombat(killer))
            return;

        plugin.getCombatManager().removePlayer(player);
        plugin.getCombatManager().removePlayer(killer);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        if (damager.getGameMode().equals(GameMode.CREATIVE)
                || player.getGameMode().equals(GameMode.CREATIVE) ||
                player.getGameMode().equals(GameMode.SPECTATOR))
            return;

        plugin.getCombatManager().addPlayer(player);
        plugin.getCombatManager().addPlayer(damager);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        if (!(projectile.getShooter() instanceof Player)
                || projectile instanceof Snowball || projectile instanceof Egg)
            return;

        Player player = (Player) projectile.getShooter();

        if (event.getHitEntity() == null)
            return;

        if (!(event.getHitEntity() instanceof Player))
            return;

        Player damaged = (Player) event.getHitEntity();

        if (player.getGameMode().equals(GameMode.CREATIVE)
                || damaged.getGameMode().equals(GameMode.CREATIVE) ||
                damaged.getGameMode().equals(GameMode.SPECTATOR))
            return;

        plugin.getCombatManager().addPlayer(player);
        plugin.getCombatManager().addPlayer(damaged);
    }

    @EventHandler
    public void onCombatStateChanged(PlayerCombatStateChangedEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
        boolean inCombat = event.isInCombat();

        if (player == null)
            return;

        if (!inCombat) {
            BukkitRunnable existingTask = this.countdownTasks.remove(player.getUniqueId());
            if (existingTask != null) {
                existingTask.cancel();
            }
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(plugin.getConfig().getString("messages.no-longer"))));
            return;
        }

        BukkitRunnable existingTask = this.countdownTasks.get(player.getUniqueId());
        if (existingTask != null) {
            existingTask.cancel();
            this.countdownTasks.remove(player.getUniqueId());
        }

        BukkitRunnable countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getCombatManager().isInCombat(player)) {
                    countdownTasks.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                int seconds = plugin.getCombatManager().getTimeRemain(player);

                plugin.getCombatManager().decreaseTimeRemain(player);
                if (seconds <= 0) {
                    plugin.getCombatManager().removePlayer(player);
                    countdownTasks.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                String message = plugin.getConfig().getString("messages.in-combat");

                if (message != null) {
                    message = message.replace("%seconds%", String.valueOf(seconds));
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(message)));
                }
            }
        };

        this.countdownTasks.put(player.getUniqueId(), countdownTask);
        countdownTask.runTaskTimer(plugin, 0, 20L);
    }

}
