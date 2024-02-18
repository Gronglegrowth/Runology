package com.cmdpro.runology.renderers;

import com.cmdpro.runology.Runology;
import com.cmdpro.runology.entity.BillboardProjectile;
import com.cmdpro.runology.entity.IceShard;
import com.cmdpro.runology.entity.ShatterAttack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;

import java.awt.*;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ShatterAttackRenderer extends EntityRenderer<ShatterAttack> {

    public ShatterAttackRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }
    @Override
    public ResourceLocation getTextureLocation(ShatterAttack pEntity) {
        return new ResourceLocation(Runology.MOD_ID, "textures/entity/shatterattack.png");
    }
    public VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld();
    @Override
    public void render(ShatterAttack pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(-pEntity.position().x, -pEntity.position().y, -pEntity.position().z);
        Vector3f vector3f = pEntity.getEntityData().get(ShatterAttack.VICTIMPOS);
        Vec3 pos = new Vec3(vector3f.x, vector3f.y, vector3f.z);
        VertexConsumer consumer = RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderTypeRegistry.TRANSPARENT_TEXTURE.applyWithModifierAndCache(getTextureLocation(pEntity), b -> b.setCullState(LodestoneRenderTypeRegistry.NO_CULL)));
        double length = pEntity.position().distanceTo(pos);
        Vec3 lastPos = pEntity.position();
        for (Map.Entry<Float, Vec2> i : pEntity.offsets.entrySet()) {
            double distance = i.getKey().doubleValue();
            Vec3 pos2 = pEntity.position().lerp(pos, distance/length);
            Vec2 rotVec = calculateRotationVector(lastPos.subtract(pos2));
            pos2 = pos2.add(calculateViewVector(i.getValue().x, rotVec.y).multiply(i.getValue().y, i.getValue().y, i.getValue().y));
            builder.setPosColorTexLightmapDefaultFormat().setAlpha(1f - ((float) pEntity.time / 20f)).setColor(Color.MAGENTA).renderBeam(consumer, pPoseStack.last().pose(), lastPos, pos2, 0.1f);
            lastPos = pos2;
        }
        builder.setPosColorTexLightmapDefaultFormat().setAlpha(1f - ((float) pEntity.time / 20f)).setColor(Color.MAGENTA).renderBeam(consumer, pPoseStack.last().pose(), lastPos, pos, 0.1f);
        pPoseStack.translate(pEntity.position().x, pEntity.position().y, pEntity.position().z);
        pPoseStack.popPose();
    }

    public Vec3 calculateViewVector(float pXRot, float pYRot) {
        float f = pXRot * ((float)Math.PI / 180F);
        float f1 = -pYRot * ((float)Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
    }
    public Vec2 calculateRotationVector(Vec3 pVec) {
        double d0 = pVec.x;
        double d1 = pVec.y;
        double d2 = pVec.z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        return new Vec2(
            Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI)))),
            Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F)
        );
    }

    @Override
    public boolean shouldRender(ShatterAttack pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return true;
    }
}