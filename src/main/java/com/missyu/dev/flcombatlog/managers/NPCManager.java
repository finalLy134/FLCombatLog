package com.missyu.dev.flcombatlog.managers;

import com.missyu.dev.flcombatlog.FLCombatLog;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Inventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class NPCManager {

    private final FLCombatLog plugin;
    private final NPCRegistry npcRegistry;
    public NPCManager(FLCombatLog plugin) {
        this.plugin = plugin;
        this.npcRegistry = CitizensAPI.getNPCRegistry();
    }

    public NPC createNPC(Player player) {
        NPC npc = npcRegistry.createNPC(player.getType(), player.getName());

        npc.spawn(player.getLocation());
        npc.getNavigator().getDefaultParameters().baseSpeed(0.8f);

        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, player.getInventory().getHelmet());
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, player.getInventory().getChestplate());
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, player.getInventory().getLeggings());
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, player.getInventory().getBoots());
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, player.getInventory().getItemInMainHand());
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.OFF_HAND, player.getInventory().getItemInOffHand());

        ItemStack[] playerInventory = player.getInventory().getContents();
        for (int i = 0; i < playerInventory.length; i++) {
            ItemStack item = playerInventory[i];
            if (item != null) {
                npc.getTrait(Inventory.class).setItem(i, item);
            }
        }

        return npc;
    }

    public NPC getNPC(UUID npcUniqueId) {
        return CitizensAPI.getNPCRegistry().getByUniqueId(npcUniqueId);
    }

    public void removeNPC(NPC npc) {
        if (npc == null)
            return;

        npc.despawn();
        npc.destroy();
    }

    public void removeNPC(UUID npcUniqueId) {
        removeNPC(CitizensAPI.getNPCRegistry().getByUniqueId(npcUniqueId));
    }

}