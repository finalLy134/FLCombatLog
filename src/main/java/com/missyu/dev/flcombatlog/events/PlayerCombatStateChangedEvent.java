package com.missyu.dev.flcombatlog.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerCombatStateChangedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    private Player player;
    private boolean inCombat;
    public PlayerCombatStateChangedEvent(Player player, boolean inCombat) {
        this.player = player;
        this.inCombat = inCombat;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isInCombat() {
        return this.inCombat;
    }

}