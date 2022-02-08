package com.github.klyser8.earthbounds.entity;

import com.github.klyser8.earthbounds.block.GlowGreaseSplatBlock;
import com.github.klyser8.earthbounds.client.EarthboundsClient;
import com.github.klyser8.earthbounds.network.EntitySpawnPacket;
import com.github.klyser8.earthbounds.registry.EarthboundBlocks;
import com.github.klyser8.earthbounds.registry.EarthboundEntities;
import com.github.klyser8.earthbounds.registry.EarthboundItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GlowLichenBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GlowGreaseDropEntity extends ThrownItemEntity implements FlyingItemEntity {

    private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(
            GlowGreaseDropEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Boolean> SHOT_AT_ANGLE = DataTracker.registerData(
            GlowGreaseDropEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Nullable
    private LivingEntity shooter;

    public GlowGreaseDropEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public GlowGreaseDropEntity(World world, double x, double y, double z, ItemStack stack) {
        super(EarthboundEntities.GLOW_GREASE, world);
        this.setPosition(x, y, z);
        if (!stack.isEmpty() && stack.hasNbt()) {
            this.dataTracker.set(ITEM, stack.copy());
        }
        this.setVelocity(this.random.nextGaussian() * 0.001, 0.05, this.random.nextGaussian() * 0.001);
    }

    public GlowGreaseDropEntity(World world, @Nullable Entity entity, double x, double y, double z, ItemStack stack) {
        this(world, x, y, z, stack);
        this.setOwner(entity);
    }

    public GlowGreaseDropEntity(World world, ItemStack stack, double x, double y, double z, boolean shotAtAngle) {
        this(world, x, y, z, stack);
        this.dataTracker.set(SHOT_AT_ANGLE, shotAtAngle);
    }

    public GlowGreaseDropEntity(World world, ItemStack stack, Entity entity, double x, double y, double z,
                                boolean shotAtAngle) {
        this(world, stack, x, y, z, shotAtAngle);
        this.setOwner(entity);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(SHOT_AT_ANGLE, false);
    }

    @Override
    public boolean hasNoGravity() {
        return false;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (world.isClient) {
            return;
        }
        entityHitResult.getEntity().damage(DamageSource.thrownProjectile(this, this), 0);
        playHitSound();
        remove(RemovalReason.KILLED);
    }

    private void playHitSound() {
        if (!world.isClient) {
            playSound(SoundEvents.BLOCK_HONEY_BLOCK_FALL, 1.0f, 1.5f);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        BlockPos blockPos = new BlockPos(blockHitResult.getBlockPos());
        world.getBlockState(blockPos).onEntityCollision(world, blockPos, this);
        if (!world.isClient()) {
            remove(RemovalReason.KILLED);
            GlowGreaseSplatBlock glowGreaseBlock = EarthboundBlocks.GLOW_GREASE_SPLAT;
            BlockState state = glowGreaseBlock.getPlacementState(new AutomaticItemPlacementContext(
                    world, blockPos.up(), blockHitResult.getSide(),
                    EarthboundItems.GLOW_GREASE.getDefaultStack(), blockHitResult.getSide()));
            if (state == null) return;
            world.setBlockState(blockPos.up(), state);
            playHitSound();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (isTouchingWater()) {
            remove(RemovalReason.KILLED);
            playHitSound();
        }
    }

    @Override
    public Packet createSpawnPacket() {
        return EntitySpawnPacket.create(this, EarthboundsClient.packetID);
    }

    @Override
    protected Item getDefaultItem() {
        return EarthboundItems.GLOW_GREASE;
    }

    @Override
    public ItemStack getStack() {
        ItemStack itemStack = this.dataTracker.get(ITEM);
        return itemStack.isEmpty() ? new ItemStack(EarthboundItems.GLOW_GREASE) : itemStack;
    }

    public boolean wasShotAtAngle() {
        return this.dataTracker.get(SHOT_AT_ANGLE);
    }

    @Override
    public boolean cannotBeSilenced() {
        return super.cannotBeSilenced();
    }

    @Override
    public boolean isAttackable() {
        return false;
    }
}
