package com.cmdpro.runology;

import com.cmdpro.runology.api.AnalyzeTaskSerializer;
import com.cmdpro.runology.api.InstabilityEvent;
import com.cmdpro.runology.api.RunologyUtil;
import com.cmdpro.runology.api.RunicEnergyType;
import com.cmdpro.runology.entity.RunicConstruct;
import com.cmdpro.runology.entity.RunicOverseer;
import com.cmdpro.runology.entity.RunicScout;
import com.cmdpro.runology.init.EntityInit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = Runology.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(EntityInit.RUNICCONSTRUCT.get(), RunicConstruct.setAttributes());
        event.put(EntityInit.RUNICSCOUT.get(), RunicScout.setAttributes());
        event.put(EntityInit.RUNICOVERSEER.get(), RunicOverseer.setAttributes());
    }
    @SubscribeEvent
    public static void registerStuff(RegisterEvent event) {

    }
    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        RunologyUtil.RUNIC_ENERGY_TYPES_REGISTRY = event.create(new RegistryBuilder<RunicEnergyType>()
                .setName(new ResourceLocation(Runology.MOD_ID, "runicenergytypes")));
        RunologyUtil.INSTABILITY_EVENTS_REGISTRY = event.create(new RegistryBuilder<InstabilityEvent>()
                .setName(new ResourceLocation(Runology.MOD_ID, "instabilityevents")));
        RunologyUtil.ANALYZE_TASKS_REGISTRY = event.create(new RegistryBuilder<AnalyzeTaskSerializer>()
                .setName(new ResourceLocation(Runology.MOD_ID, "analyzetasks")));
    }
    @SubscribeEvent
    public static void entitySpawnRestriction(SpawnPlacementRegisterEvent event) {

        event.register(EntityInit.RUNICSCOUT.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(EntityInit.RUNICCONSTRUCT.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
}
