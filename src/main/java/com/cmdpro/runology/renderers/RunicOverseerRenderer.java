package com.cmdpro.runology.renderers;

import com.cmdpro.runology.Runology;
import com.cmdpro.runology.entity.RunicConstruct;
import com.cmdpro.runology.entity.RunicOverseer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class RunicOverseerRenderer extends GeoEntityRenderer<RunicOverseer> {
    public RunicOverseerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RunicOverseerModel());
        this.shadowRadius = 0.5f;
    }
    @Override
    public ResourceLocation getTextureLocation(RunicOverseer instance) {
        return new ResourceLocation(Runology.MOD_ID, "textures/entity/runicoverseer.png");
    }

    @Override
    public RenderType getRenderType(RunicOverseer animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
    public static class RunicOverseerModel extends GeoModel<RunicOverseer> {
        @Override
        public ResourceLocation getModelResource(RunicOverseer object) {
            return new ResourceLocation(Runology.MOD_ID, "geo/runicoverseer.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(RunicOverseer object) {
            return new ResourceLocation(Runology.MOD_ID, "textures/entity/runicoverseer.png");
        }

        @Override
        public ResourceLocation getAnimationResource(RunicOverseer animatable) {
            return new ResourceLocation(Runology.MOD_ID, "animations/runicoverseer.animation.json");
        }
    }
}