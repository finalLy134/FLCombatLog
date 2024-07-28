package com.missyu.dev.flcombatlog.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerCombatLogEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    private Player player;
    public PlayerCombatLogEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

}