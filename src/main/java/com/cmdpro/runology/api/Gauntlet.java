package com.cmdpro.runology.api;

import net.minecraft.world.item.Item;

public class Gauntlet extends Item {
    public int magicLevel;
    public Gauntlet(Properties properties, int magicLevel) {
        super(properties);
        this.magicLevel = magicLevel;
    }
}
