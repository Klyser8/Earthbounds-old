package com.github.klyser8.earthbounds.entity;

import com.github.klyser8.earthbounds.block.GlowGreaseSplatBlock;
import com.github.klyser8.earthbounds.client.EarthboundsClient;
import com.github.klyser8.earthbounds.item.GlowGreaseItem;
import com.github.klyser8.earthbounds.network.EntitySpawnPacket;
import com.github.klyser8.earthbounds.registry.EarthboundBlocks;
import com.github.klyser8.earthbounds.registry.EarthboundEntities;
import com.github.klyser8.earthbounds.registry.EarthboundItems;
import com.github.klyser8.earthbounds.registry.EarthboundParticles;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
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

    public GlowGreaseDropEntity(World world, ItemStack stack, double x, double y, double z, boolean shotAtAngle) {
        this(world, x, y, z, stack);
        this.dataTracker.set(SHOT_AT_ANGLE, shotAtAngle);
    }

    public GlowGreaseDropEntity(World world, ItemStack stack, Entity owner, double x, double y, double z,
                                boolean shotAtAngle) {
        this(world, stack, x, y, z, shotAtAngle);
        this.setOwner(owner);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(SHOT_AT_ANGLE, false);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        ItemStack itemStack = this.dataTracker.get(ITEM);
        if (!itemStack.isEmpty()) {
            nbt.put("GlowGreaseItem", itemStack.writeNbt(new NbtCompound()));
        }
        nbt.putBoolean("ShotAtAngle", this.dataTracker.get(SHOT_AT_ANGLE));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        ItemStack itemStack = ItemStack.fromNbt(nbt.getCompound("GlowGreaseItem"));
        if (!itemStack.isEmpty()) {
            this.dataTracker.set(ITEM, itemStack);
        }
        if (nbt.contains("ShotAtAngle")) {
            this.dataTracker.set(SHOT_AT_ANGLE, nbt.getBoolean("ShotAtAngle"));
        }
    }
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
    }

    private void playHitSound() {
        if (!world.isClient) {
            playSound(SoundEvents.BLOCK_HONEY_BLOCK_FALL, 1.0f, 1.5f);
        }
    }

    @Override //Absolem Jackdaw absolute legend
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (isRemoved() || isInLava() || isTouchingWater()) {
            return;
        }
        BlockPos blockPos = new BlockPos(blockHitResult.getBlockPos());
        BlockPos placePos = blockPos.offset(blockHitResult.getSide());
        BlockState oldPlaceState = world.getBlockState(placePos);
        if (!world.isClient()) {
            discard();
            GlowGreaseSplatBlock glowGreaseBlock = EarthboundBlocks.GLOW_GREASE_SPLAT;
            BlockState greaseState;
            BlockState currentState;
            if (world.getBlockState(placePos).getBlock() instanceof GlowGreaseSplatBlock) {
                currentState = world.getBlockState(placePos);
            } else {
                currentState = glowGreaseBlock.getDefaultState();
            }
            greaseState = glowGreaseBlock.withDirection(currentState, world, placePos, blockHitResult.getSide().getOpposite());
            if (greaseState == null || !oldPlaceState.getMaterial().isReplaceable()) {
                world.spawnEntity(new ItemEntity(world, getX(), getY(), getZ(), getStack()));
            } else {
                world.setBlockState(placePos, greaseState, Block.NOTIFY_ALL);
                playHitSound();
                spawnHitParticles();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (isTouchingWater() || isInLava()) {
            discard();
            playSound(isTouchingWater() ? SoundEvents.ENTITY_GENERIC_SPLASH : SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 2.0f);
            if (!world.isClient) {
                ((ServerWorld) world).spawnParticles(EarthboundParticles.GREASE_CHUNK, getParticleX(0.5),
                        getRandomBodyY(), getParticleZ(0.5), 40, 0, 0, 0, 0.1);
            }
        }
        if (world.isClient) {
            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(EarthboundParticles.GREASE_CHUNK,
                        this.getX() + getVelocity().x * (double)i / 2.0,
                        this.getY() + getVelocity().y * (double)i / 2.0,
                        this.getZ() + getVelocity().z * (double)i / 2.0,0,0,0);
            }
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
    public boolean shouldRender(double distance) {
        return distance < 4096.0;
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return super.shouldRender(cameraX, cameraY, cameraZ);
    }

    @Override
    public boolean cannotBeSilenced() {
        return super.cannotBeSilenced();
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    public static boolean canFallThrough(BlockState state) {
        Material material = state.getMaterial();
        return state.isAir() || state.isIn(BlockTags.FIRE) || !material.isSolid();
    }

    private void spawnHitParticles() {
        if (world.isClient) {
            return;
        }
        ((ServerWorld) world).spawnParticles(EarthboundParticles.GREASE_CHUNK, getParticleX(0.5),
                getRandomBodyY(), getParticleZ(0.5), 40, 0, 0, 0, 0.1);
    }
}
