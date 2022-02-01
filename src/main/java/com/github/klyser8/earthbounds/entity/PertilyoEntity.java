package com.github.klyser8.earthbounds.entity;

import net.minecraft.block.TorchBlock;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.NoWaterTargeting;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class PertilyoEntity extends PathAwareEarthenEntity implements Earthen {

    private static final byte DEFAULT_FLAG = 0;

    private static final TrackedData<Integer> ENERGY = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MAX_ENERGY = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> SECONDS_SINCE_DEOX = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Byte> PERTILYO_FLAG = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Boolean> IS_ROOSTING = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<BlockPos> CLOSEST_TORCH = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.BLOCK_POS);

    @Nullable
    private BlockPos hangPos;

    public PertilyoEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        moveControl = new FlightMoveControl(this, 20, true);
        navigation = createNavigation(world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return DefaultAttributeContainer.builder()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 1.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 10.0D)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)
                .add(EntityAttributes.GENERIC_ARMOR, 15)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5);
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
                                 @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        setUpwardSpeed(1);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(ENERGY, 0);
        dataTracker.startTracking(MAX_ENERGY, 10);
        dataTracker.startTracking(SECONDS_SINCE_DEOX, 0);
        dataTracker.startTracking(IS_ROOSTING, false);
        dataTracker.startTracking(PERTILYO_FLAG, (byte) 0);
        dataTracker.startTracking(CLOSEST_TORCH, new BlockPos(0, 0, 0));
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new MoveToTorchGoal());
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this,
                "move", 10, this::movementPredicate));
        animationData.addAnimationController(new AnimationController<>(this,
                "pose", 10, this::posePredicate));
    }

    private <E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {
        if (!isRoosting()) {
            event.getController().transitionLengthTicks = 3;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("flap_loop", true));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <E extends IAnimatable> PlayState posePredicate(AnimationEvent<E> event) {
        if (event.isMoving()) {
            event.getController().transitionLengthTicks = 3;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("pose_fly_move", true));
            return PlayState.CONTINUE;
        } else if (isRoosting()) {
            event.getController().transitionLengthTicks = 10;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("pose_roost", true));
            return PlayState.CONTINUE;
        } else {
            event.getController().transitionLengthTicks = 10;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("idle", true));
            return PlayState.CONTINUE;
        }
    }

    /**
     * Pertilyo likes pathfinding in the air, nowhere else.
     */
    @Override
    public float getPathfindingFavor(BlockPos pos) {
        if (world.getBlockState(pos).isAir()) {
            return 10.0f;
        }
        return 0.0f;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (age % 20 == 0) {
            setSecondsSinceDeox(getSecondsSinceDeox() + 1);
        }
        if (this.isRoosting()) {
            this.setVelocity(Vec3d.ZERO);
            this.setPos(this.getX(),
                    (double) MathHelper.floor(this.getY()) + 1.0 - (double)this.getHeight(),
                    this.getZ());
        } else {
            this.setVelocity(this.getVelocity().multiply(1.0, 0.6, 1.0));
        }
        int width = 15;
        for (int i = 0; i < 10; i++) {
            BlockPos randomPos = getBlockPos().add(new BlockPos(
                    random.nextInt(width) - width / 2,
                    random.nextInt(width) - width / 2,
                    random.nextInt(width) - width / 2));
            if (!world.isClient && world.getBlockState(randomPos).getBlock() instanceof TorchBlock
                    && FuzzyTargeting.findTo(this, width, width, Vec3d.ofCenter(randomPos)) != null) {
                if (getClosestTorch() == null ||
                        getBlockPos().getSquaredDistance(randomPos) < getBlockPos().getSquaredDistance(getClosestTorch())) {
                    System.out.println("found you");
                    setClosestTorch(randomPos);
                    System.out.println(getClosestTorch());
                }
            }
        }
    }



    @Override
    protected void mobTick() {
        super.mobTick();
        /*BlockPos blockPos = getBlockPos();
        BlockPos abovePos = blockPos.up();
        if (isRoosting()) {
            if (!world.getBlockState(abovePos).isSolidBlock(world, blockPos)) {
                setRoosting(false);
                if (!isSilent()) {
                    world.syncWorldEvent(null, WorldEvents.BAT_TAKES_OFF, blockPos, 0);
                }
            }
        } else {
            //If the hanging pos isnt null, and it isnt OR it's not air and it's not above bottom Y then set it to null
            if (!(hangingPos == null
                    || world.isAir(hangingPos) && hangingPos.getY() > world.getBottomY())) {
                hangingPos = null;
            }
            //If hanging pos is null Or random check is passed or hanging pos is within distance, then create new
            //hanging position.
            if (hangingPos == null
                    || random.nextInt(3000) == 0 || hangingPos.isWithinDistance(getPos(), 2.0)) {
                hangingPos = new BlockPos(
                        getX() + random.nextInt(7) - random.nextInt(7),
                        getY() + random.nextInt(6) - 2,
                        getZ() + random.nextInt(7) - random.nextInt(7));
            }
            //Applies velocity to Pertilyo
            double x = hangingPos.getX() + 0.5 - getX();
            double y = hangingPos.getY() + 0.1 - getY();
            double z = hangingPos.getZ() + 0.5 - getZ();
            Vec3d currentVel = getVelocity();
            Vec3d newVel = currentVel.add(
                    (Math.signum(x) * 0.5 - currentVel.x) * 0.1f,
                    (Math.signum(y) * 0.7f - currentVel.y) * 0.1f,
                    (Math.signum(z) * 0.5 - currentVel.z) * 0.1f);
            setVelocity(newVel);
            float deg = (float)(MathHelper.atan2(newVel.z, newVel.x) * 57.2957763671875) - 90.0f;
            float yaw = MathHelper.wrapDegrees(deg - getYaw());
            forwardSpeed = 0.5f;
            setYaw(getYaw() + yaw);
            System.out.println(abovePos);
            if (random.nextInt(100) == 0 && world.getBlockState(abovePos).isSolidBlock(world, abovePos)) {
                this.setRoosting(true);
            }
        }*/
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world){
            @Override
            public boolean isValidPosition(BlockPos pos) {
                return !this.world.getBlockState(pos.down()).isAir();
            }
        };
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(false);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }

    public int getEnergy() {
        return dataTracker.get(ENERGY);
    }

    public void setEnergy(int energy) {
        dataTracker.set(ENERGY, Math.max(0, energy));
    }

    public int getMaxEnergy() {
        return dataTracker.get(MAX_ENERGY);
    }

    /**
     * Seconds since last de-oxidation happened. This can only happen through being struck by lightning.
     */
    public int getSecondsSinceDeox() {
        return dataTracker.get(SECONDS_SINCE_DEOX);
    }

    public void setSecondsSinceDeox(int time) {
        dataTracker.set(SECONDS_SINCE_DEOX, time);
    }

    public boolean isRoosting() {
        return dataTracker.get(IS_ROOSTING);
    }

    public void setRoosting(boolean roosting) {
        dataTracker.set(IS_ROOSTING, roosting);
    }

    public BlockPos getClosestTorch() {
        return dataTracker.get(CLOSEST_TORCH);
    }

    public void setClosestTorch(BlockPos pos) {
        dataTracker.set(CLOSEST_TORCH, pos);
    }

    public boolean isInAir() {
        return !onGround;
    }

    boolean isTorch(BlockPos pos) {
        return world.canSetBlock(pos) && world.getBlockState(pos).getBlock() instanceof TorchBlock;
    }

    boolean isTooFar(BlockPos pos) {
        return !getBlockPos().isWithinDistance(pos, 32);
    }



    private class MoveToTorchGoal extends Goal {

        private static final int MAX_NAVIGATION_TICKS = 600;
        int ticks;
        Path path;

        MoveToTorchGoal() {
            ticks = world.random.nextInt(10);
            setControls(EnumSet.of(Goal.Control.MOVE));
        }

        /**
         * Goal can start if:
         * - The torch position is not null
         * - The Pertilyo should move to a torch
         * - The torch position is a torch
         * - The Pertilyo is not near the target torch already
         */
        @Override
        public boolean canStart() {
            if (getClosestTorch() == null) {
                return false;
            }
            boolean bl = !hasPositionTarget()
                    && shouldMoveToTorch()
                    && isTorch(getClosestTorch())
                    && !getBlockPos().isWithinDistance(getClosestTorch(), 1);
            path = getNavigation().findPathTo(getClosestTorch(), 1, 16);
            return path != null && bl;

        }

        @Override
        public void tick() {
            if (getClosestTorch() == null) {
                return;
            }
            if (getBlockPos().isWithinDistance(getClosestTorch(), 3)) {
                world.breakBlock(getClosestTorch(), false, PertilyoEntity.this);
            }
            ++ticks;
            if (ticks > this.getTickCount(600)) {
                return;
            }
            if (navigation.isFollowingPath()) {
                return;
            }
            getNavigation().startMovingAlong(path, 1.0f);
        }

        @Override
        public boolean shouldContinue() {
            return getClosestTorch() != null
                    && isTorch(getClosestTorch())
                    && !getBlockPos().isWithinDistance(getClosestTorch(), 1)
                    && ticks < this.getTickCount(600);
        }

        @Override
        public void stop() {
            if (!isTorch(getClosestTorch())) {
                setClosestTorch(null);
            }
            this.ticks = 0;
            navigation.stop();
            navigation.resetRangeMultiplier();
        }

        /**
         * The Pertilyo should be less likely to move to a torch if energized. It shouldn't move to a torch at
         * all if it is fully energized.
         */
        private boolean shouldMoveToTorch() {
            return getEnergy() < getMaxEnergy();
        }
    }
}
