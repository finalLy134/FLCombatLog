package com.missyu.dev.flcombatlog.managers;

import com.missyu.dev.flcombatlog.FLCombatLog;
import com.missyu.dev.flcombatlog.traits.CombatLogTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;

public class TraitManager {

    private final FLCombatLog plugin;
    public TraitManager(FLCombatLog plugin) {
        this.plugin = plugin;

        CitizensAPI.getTraitFactory().registerTrait(
                TraitInfo.create(CombatLogTrait.class).withName("CombatLogTrait")
        );
    }
}