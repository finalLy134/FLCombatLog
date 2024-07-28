package com.missyu.dev.flcombatlog.traits;

import com.missyu.dev.flcombatlog.FLCombatLog;
import com.missyu.dev.flcombatlog.utils.Utils;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CombatLogTrait extends Trait implements Listener {
    private final FLCombatLog plugin = FLCombatLog.getPlugin(FLCombatLog.class);
    private int secondsLeft = plugin.getConfig().getInt("settings.npc-lifetime-seconds");
    private double currentAbsorption = 0.0;
    private Player parentPlayer;
    private BukkitRunnable countdownTask;
    private HologramTrait hologramTrait;

    public CombatLogTrait() {
        super("CombatLogTrait");
    }

    @Override
    public void onAttach() {
        startCountdown();
        setupHologram();
    }

    @Override
    public void onRemove() {
        cancelCountdown();
        clearHologram();
    }

    @EventHandler
    public void onNPCDamage(NPCDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        event.setCancelled(false);

        double damage = event.getDamage();
        double reducedDamage = calculateFinalDamage((Player) getNPC().getEntity(), damage);

        handleAbsorption(event, reducedDamage);
        resetCountdown((int) damage);
    }

    @EventHandler
    public void onNPCDeath(NPCDeathEvent event) {
        if (event.getNPC() != getNPC()) {
            return;
        }

        handleNPCDeath();
    }

    public void setParentPlayer(Player parentPlayer) {
        this.parentPlayer = parentPlayer;
    }

    public void setAbsorption(double absorption) {
        this.currentAbsorption = absorption / 2;
    }

    private void handleAbsorption(NPCDamageByEntityEvent event, double reducedDamage) {
        if (currentAbsorption > 0) {
            if (currentAbsorption > reducedDamage) {
                currentAbsorption -= reducedDamage;
                event.setDamage(0);
            } else {
                event.setDamage(reducedDamage - currentAbsorption);
                currentAbsorption = 0;
            }
        } else {
            currentAbsorption = 0;
        }
    }

    private void handleNPCDeath() {
        Player player = (Player) getNPC().getEntity();
        dropPlayerInventory(player);

        getNPC().despawn();
        getNPC().destroy();

        plugin.getCombatManager().setPlayerAsDead(parentPlayer.getUniqueId(), getKillerName(player), true);
    }

    private String getKillerName(Player player) {
        return player.getKiller() != null ? player.getKiller().getName() : "unknown";
    }

    private void dropPlayerInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }
    }

    private void startCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
        }

        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (secondsLeft <= 0) {
                    despawnNPC();
                    cancel();
                    return;
                }

                secondsLeft--;
                updateHologram();
            }
        };

        countdownTask.runTaskTimer(plugin, 20L, 20L);
    }

    private void cancelCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
    }

    private void resetCountdown(int damage) {
        secondsLeft += damage;
        updateHologram();
    }

    private void setupHologram() {
        hologramTrait = getNPC().getOrAddTrait(HologramTrait.class);
        updateHologram();
    }

    private void clearHologram() {
        if (hologramTrait != null) {
            hologramTrait.clear();
        }
    }

    private void updateHologram() {
        if (hologramTrait == null) {
            return;
        }

        Player player = (Player) getNPC().getEntity();
        if (player == null) {
            plugin.getLogger().warning("Entity is not a Player!");
            return;
        }

        hologramTrait.setLine(0, Utils.chat("&a" + Utils.formatTime(secondsLeft * 1000) + "."));
        hologramTrait.setLine(1, Utils.chat("&c&lDISCONNECT: &7" + getNPC().getName()));
        hologramTrait.setLine(2, getPrettyHearts(player.getHealth() / 2, 10));
    }

    private String getPrettyHearts(double health, double maxHealth) {
        StringBuilder heartsString = new StringBuilder();

        for (int heart = 0; heart < maxHealth; heart++) {
            if (heart < health) {
                heartsString.append("&c❤");
            } else {
                heartsString.append("&7❤");
            }
        }

        for (int i = 0; i < currentAbsorption; i++) {
            heartsString.append("&e❤");
        }

        return heartsString.toString();
    }

    private double calculateFinalDamage(Player player, double damage) {
        double armor = player.getAttribute(Attribute.GENERIC_ARMOR).getValue();
        double toughness = player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();

        return damage * (1 - (Math.min(20.0, Math.max(armor / 5.0, armor - damage / (2.0 + toughness / 4.0))) / 25.0));
    }

    private void despawnNPC() {
        getNPC().despawn();
        getNPC().destroy();
    }
}
