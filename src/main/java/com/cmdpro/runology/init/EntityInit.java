package com.cmdpro.runology.init;

import com.cmdpro.runology.Runology;
import com.cmdpro.runology.entity.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.spongepowered.asm.mixin.Shadow;

public class EntityInit {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Runology.MOD_ID);
    public static final RegistryObject<EntityType<RunicConstruct>> RUNICCONSTRUCT = ENTITY_TYPES.register("runicconstruct", () -> EntityType.Builder.of((EntityType.EntityFactory<RunicConstruct>) RunicConstruct::new, MobCategory.MONSTER).sized(1.4f, 2.7f).build(Runology.MOD_ID + ":" + "runicconstruct"));
    public static final RegistryObject<EntityType<RunicScout>> RUNICSCOUT = ENTITY_TYPES.register("runicscout", () -> EntityType.Builder.of((EntityType.EntityFactory<RunicScout>) RunicScout::new, MobCategory.MONSTER).sized(0.625f, 0.75f).build(Runology.MOD_ID + ":" + "runicscout"));
    public static final RegistryObject<EntityType<VoidBomb>> VOIDBOMB = ENTITY_TYPES.register("voidbomb", () -> EntityType.Builder.of((EntityType.EntityFactory<VoidBomb>) VoidBomb::new, MobCategory.MISC).sized(0.5f, 0.5f).build(Runology.MOD_ID + ":" + "voidbomb"));
    public static final RegistryObject<EntityType<RunicOverseer>> RUNICOVERSEER = ENTITY_TYPES.register("runicoverseer", () -> EntityType.Builder.of((EntityType.EntityFactory<RunicOverseer>) RunicOverseer::new, MobCategory.MONSTER).sized(0.6F, 2F).build(Runology.MOD_ID + ":" + "runicoverseer"));
}
