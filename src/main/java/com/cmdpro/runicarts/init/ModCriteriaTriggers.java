package com.cmdpro.runicarts.init;

import net.minecraft.advancements.CriteriaTriggers;

public class ModCriteriaTriggers {
    public static final KillSoulKeeperTrigger KILLSOULKEEPER = new KillSoulKeeperTrigger();
    public static final SpawnSoulKeeperTrigger SPAWNSOULKEEPER = new SpawnSoulKeeperTrigger();
    public static void register() {
        CriteriaTriggers.register(KILLSOULKEEPER);
        CriteriaTriggers.register(SPAWNSOULKEEPER);
    }
}
