package com.cmdpro.runology.entity;

import com.cmdpro.runology.Runology;
import com.cmdpro.runology.init.ParticleInit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.apache.commons.lang3.RandomUtils;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FireballProjectile extends Projectile {
    public int time;
    public FireballProjectile(EntityType<FireballProjectile> entityType, Level world) {
        super(entityType, world);
    }
    protected FireballProjectile(EntityType<FireballProjectile> entityType, double x, double y, double z, Level world) {
        this(entityType, world);
        this.setPos(x, y, z);
    }
    public FireballProjectile(EntityType<FireballProjectile> entityType, LivingEntity shooter, Level world) {
        this(entityType, shooter.getX(), shooter.getEyeY() - (double)0.1F, shooter.getZ(), world);
        this.setOwner(shooter);
        this.setDeltaMovement(shooter.getLookAngle().multiply(0.5, 0.5, 0.5));
    }
    @Override
    protected void onHitBlock(BlockHitResult hit) {
        level().setBlockAndUpdate(hit.getBlockPos().relative(hit.getDirection()), Blocks.FIRE.defaultBlockState());
        remove(RemovalReason.KILLED);
        super.onHitBlock(hit);
    }
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("xd", (float)this.getDeltaMovement().x);
        tag.putFloat("yd", (float)this.getDeltaMovement().y);
        tag.putFloat("zd", (float)this.getDeltaMovement().z);
        tag.putInt("time", (int)this.time);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        float vx = tag.getFloat("xd");
        float vy = tag.getFloat("yd");
        float vz = tag.getFloat("zd");
        this.time = tag.getInt("time");
        setDeltaMovement(new Vec3(vx, vy, vz));
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 p_36758_, Vec3 p_36759_) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, p_36758_, p_36759_, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void tick() {
        if (!level().isClientSide) {
            time++;
            if (time >= 200) {
                remove(RemovalReason.KILLED);
            }
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
                this.onHit(hitresult);
            }

            while(!this.isRemoved()) {
                EntityHitResult entityhitresult = this.findHitEntity(this.position(), this.position().add(getDeltaMovement()));
                if (entityhitresult != null) {
                    hitresult = entityhitresult;
                }

                if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
                    Entity entity = ((EntityHitResult)hitresult).getEntity();
                    Entity entity1 = this.getOwner();
                    if ((entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity))) {
                        hitresult = null;
                        entityhitresult = null;
                    }
                }

                if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
                    this.onHit(hitresult);
                    this.hasImpulse = true;
                }

                if (entityhitresult == null) {
                    break;
                }

                hitresult = null;
            }

        } else {
            for (int i = 0; i < 3; i++) {
                Vec3 pos = position();
                pos = pos.add(0, getBoundingBox().getYsize() / 2f, 0);
                pos = pos.add(RandomUtils.nextFloat(0, 0.2f) - 0.1f, RandomUtils.nextFloat(0, 0.2f) - 0.1f, RandomUtils.nextFloat(0, 0.2f) - 0.1f);
                level().addAlwaysVisibleParticle(ParticleInit.FIRE.get(), pos.x, pos.y, pos.z, 0, 0, 0);
            }
        }
        this.setPos(this.getX() + getDeltaMovement().x, this.getY() + getDeltaMovement().y, this.getZ() + getDeltaMovement().z);
        super.tick();
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        Entity entity = this.getOwner();
        DamageSource damagesource;
        if (entity instanceof LivingEntity) {
            damagesource = entity.damageSources().indirectMagic(entity, this);
            ((LivingEntity)entity).setLastHurtMob(hit.getEntity());
        } else if (entity instanceof Player) {
            damagesource = entity.damageSources().indirectMagic(entity, this);
            ((LivingEntity)entity).setLastHurtMob(hit.getEntity());
        } else {
            damagesource = null;
        }

        if (damagesource != null) {
            if (hit.getEntity() instanceof LivingEntity ent && entity instanceof LivingEntity) {
                ent.hurt(damagesource, 5);
                ent.setRemainingFireTicks(75);
            }
        }
        remove(RemovalReason.KILLED);
    }
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}