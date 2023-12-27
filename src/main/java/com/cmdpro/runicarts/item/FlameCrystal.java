package com.cmdpro.runicarts.item;

import com.cmdpro.runicarts.RunicArts;
import com.cmdpro.runicarts.api.ISoulcastersCrystal;
import com.cmdpro.runicarts.api.RunicArtsUtil;
import net.minecraft.world.item.Item;

public class FlameCrystal extends Item implements ISoulcastersCrystal {

    public FlameCrystal(Properties properties) {
        super(properties);
        RunicArtsUtil.SOULCASTER_CRYSTALS.add(this);
    }
    @Override
    public String getId() {
        return RunicArts.MOD_ID + ":flame";
    }
}
