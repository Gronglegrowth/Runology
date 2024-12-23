package com.cmdpro.runology.renderers.block;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.runology.RenderEvents;
import com.cmdpro.runology.block.transmission.ShatteredFocusBlockEntity;
import com.cmdpro.runology.block.world.ShatterBlockEntity;
import com.cmdpro.runology.shaders.RunologyRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;

public class ShatteredFocusRenderer implements BlockEntityRenderer<ShatteredFocusBlockEntity> {
    @Override
    public void render(ShatteredFocusBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        if (blockEntity.isPowered) {
            for (BlockPos i : blockEntity.connectedTo) {
                poseStack.pushPose();
                ClientDatabankUtils.rotateStackToPoint(poseStack, blockEntity.getBlockPos().getCenter(), i.getCenter());
                renderLine(Vec3.ZERO, new Vec3(0, i.getCenter().distanceTo(blockEntity.getBlockPos().getCenter()), 0), 0.1f, partialTick, poseStack, bufferSource.getBuffer(RunologyRenderTypes.SHATTER));
                renderLine(Vec3.ZERO, new Vec3(0, i.getCenter().distanceTo(blockEntity.getBlockPos().getCenter()), 0), 0.1f, partialTick, poseStack, RenderEvents.createShatterOutlineBufferSource().getBuffer(RunologyRenderTypes.SHATTER));
                poseStack.popPose();
            }
        }
        poseStack.popPose();
    }
    EntityRenderDispatcher renderDispatcher;
    public ShatteredFocusRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        renderDispatcher = rendererProvider.getEntityRenderer();
    }
    private void renderLine(Vec3 start, Vec3 end, float thickness, float partialTick, PoseStack poseStack, VertexConsumer vertexConsumer) {
        Vector3f diff = end.toVector3f().sub(start.toVector3f()).normalize().mul(thickness, thickness, thickness);
        Vector3f diffRotated = new Vector3f(diff).rotateX(Math.toRadians(90));
        for (int i = 0; i < 4; i++) {
            Vector3f offset = new Vector3f(diffRotated).rotateY(Math.toRadians(90*i));
            Vector3f offset2 = new Vector3f(diffRotated).rotateY(Math.toRadians(90*(i+1)));
            vertexConsumer.addVertex(poseStack.last(), end.toVector3f().add(offset2));
            vertexConsumer.addVertex(poseStack.last(), end.toVector3f().add(offset));
            vertexConsumer.addVertex(poseStack.last(), start.toVector3f().add(offset));
            vertexConsumer.addVertex(poseStack.last(), start.toVector3f().add(offset2));
        }
        Vector3f offset = new Vector3f(diffRotated);
        Vector3f offset2 = new Vector3f(diffRotated).rotateY(Math.toRadians(90));
        Vector3f offset3 = new Vector3f(diffRotated).rotateY(Math.toRadians(180));
        Vector3f offset4 = new Vector3f(diffRotated).rotateY(Math.toRadians(270));
        vertexConsumer.addVertex(poseStack.last(), start.toVector3f().add(offset4));
        vertexConsumer.addVertex(poseStack.last(), start.toVector3f().add(offset3));
        vertexConsumer.addVertex(poseStack.last(), start.toVector3f().add(offset2));
        vertexConsumer.addVertex(poseStack.last(), start.toVector3f().add(offset));

        vertexConsumer.addVertex(poseStack.last(), end.toVector3f().add(offset));
        vertexConsumer.addVertex(poseStack.last(), end.toVector3f().add(offset2));
        vertexConsumer.addVertex(poseStack.last(), end.toVector3f().add(offset3));
        vertexConsumer.addVertex(poseStack.last(), end.toVector3f().add(offset4));
    }

}