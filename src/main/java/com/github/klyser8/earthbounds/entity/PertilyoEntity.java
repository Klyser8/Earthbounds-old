package com.github.klyser8.earthbounds.entity;

import com.github.klyser8.earthbounds.entity.goal.MoveToTargetBlockGoal;
import com.github.klyser8.earthbounds.util.EarthMath;
import com.github.klyser8.earthbounds.util.EarthUtil;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.TorchBlock;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class PertilyoEntity extends PathAwareEarthenEntity implements Earthen {

    private static final byte SHOOT_STATE = 1;

    private static final int SHOOT_GLEAM_ANIMATION_DURATION = 9; // divided by 2!

    private static final TrackedData<Integer> ENERGY = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MAX_ENERGY = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> SECONDS_SINCE_DEOX = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_ROOSTING = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<BlockPos> CLOSEST_TORCH = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.BLOCK_POS);

    @Nullable
    private BlockPos hangPos;
    private int lastRoostTime = 0;

    private final Map<BlockPos, Integer> ignoredBlocks = new HashMap<>();

    public PertilyoEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        moveControl = new FlightMoveControl(this, 20, true);
        navigation = createNavigation(world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
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
//        setUpwardSpeed(1);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(ENERGY, 0);
        dataTracker.startTracking(MAX_ENERGY, 10);
        dataTracker.startTracking(SECONDS_SINCE_DEOX, 0);
        dataTracker.startTracking(IS_ROOSTING, false);
        dataTracker.startTracking(CLOSEST_TORCH, new BlockPos(0, 0, 0));
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new MoveToTorchGoal(200));
        goalSelector.add(1, new PertilyoWanderARoundGoal());
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this,
                "move", 10, this::constantPredicate));
        animationData.addAnimationController(new AnimationController<>(this,
                "pose", 10, this::optionalPredicate));
    }

    private <E extends IAnimatable> PlayState constantPredicate(AnimationEvent<E> event) {
        if (event.isMoving()) {
            event.getController().transitionLengthTicks = 10;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("pose_fly_move", true));
            return PlayState.CONTINUE;
        } else if (isRoosting()) {
            event.getController().transitionLengthTicks = 0;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("pose_roost", true));
            return PlayState.CONTINUE;
        } /*else {
            event.getController().transitionLengthTicks = 10;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("default", true));
            return PlayState.CONTINUE;
        }*/
        return PlayState.STOP;
        /*if (isOnGround() && !event.isMoving()) {
            event.getController().transitionLengthTicks = 10;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("idle_ground", true));
            return PlayState.CONTINUE;
        }*/
    }

    private <E extends IAnimatable> PlayState optionalPredicate(AnimationEvent<E> event) {
        if (!isRoosting() && getAnimationState() != SHOOT_STATE) {
            event.getController().transitionLengthTicks = 5;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("flap_loop", true));
            return PlayState.CONTINUE;
        }
        if (getAnimationState() == SHOOT_STATE) {
            event.getController().transitionLengthTicks = 3;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("shoot_gleam", false));
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
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
            int deoxIncrease = 1;
            if (isTouchingWaterOrRain()) {
                deoxIncrease *= 2;
            }
            setDeoxAmount(getDeoxAmount() + deoxIncrease);
            if (!world.isClient) {
                if (world.isThundering() && world.isSkyVisible(getBlockPos())) {
                    if (random.nextFloat() < 0.01) {
                        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
                        if (lightning != null) {
                            lightning.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(getBlockPos()));
                            world.spawnEntity(lightning);
                        }
                    }
                }
            }
        }
        if (this.isRoosting()) {
            this.setVelocity(Vec3d.ZERO);
            this.setPos(this.getX(),
                    (double) MathHelper.floor(this.getY()) + 1.0 - (double)this.getHeight(),
                    this.getZ());
        } /*else {
            this.setVelocity(this.getVelocity().multiply(1.0, 0.6, 1.0));
        }*/
        if (age % 100 == 0) {
            for (BlockPos pos : ignoredBlocks.keySet()) {
                //Pertilyo may try destroying a torch every 30 seconds
                if (age - ignoredBlocks.get(pos) > 600) {
                    ignoredBlocks.remove(pos);
                    break;
                }
            }
        }
    }

    @Override
    public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
        setDeoxAmount(0);
        playElectricSparks(100);
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        BlockPos blockPos = getBlockPos();
        BlockPos abovePos = blockPos.up();
        boolean isBlockAboveSolid = world.getBlockState(abovePos).isSolidBlock(world, blockPos);
        if (isRoosting()) {
            if (!isBlockAboveSolid) {
                setRoosting(false);
            }
            if (random.nextFloat() < 0.01 || getDamageTracker().wasRecentlyAttacked()) {
                setRoosting(false);
            }
        } else {
            if (isBlockAboveSolid
                    && world.getLightLevel(getBlockPos()) < 6
                    && EarthUtil.isOnCooldown(age, lastRoostTime, 300)) {
                if (random.nextFloat() < 0.1) {
                    navigation.stop();
                    setRoosting(true);
                }
            }
        }
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
    public int getDeoxAmount() {
        return dataTracker.get(SECONDS_SINCE_DEOX);
    }

    public void setDeoxAmount(int time) {
        dataTracker.set(SECONDS_SINCE_DEOX, time);
    }

    public Oxidizable.OxidationLevel getOxidizationLevel() {
        if (getDeoxAmount() < 300) {
            return Oxidizable.OxidationLevel.UNAFFECTED;
        } else if (getDeoxAmount() < 600) {
            return Oxidizable.OxidationLevel.EXPOSED;
        } else if (getDeoxAmount() < 900) {
            return Oxidizable.OxidationLevel.WEATHERED;
        } else {
            return Oxidizable.OxidationLevel.OXIDIZED;
        }
    }

    public boolean isRoosting() {
        return dataTracker.get(IS_ROOSTING);
    }

    public void setRoosting(boolean roosting) {
        dataTracker.set(IS_ROOSTING, roosting);
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

    private void playElectricSparks(int amount) {
        for (int i = 0; i < amount; i++) {
            ((ServerWorld) world).spawnParticles(ParticleTypes.ELECTRIC_SPARK, getParticleX(0.5),
                    getRandomBodyY(), getParticleZ(0.5), 1, 0, 0, 0, 0);
        }
    }

    class MoveToTorchGoal extends MoveToTargetBlockGoal {

        private int shootGleamTime = 0;
        int ticks;

        public MoveToTorchGoal(int cooldown) {
            super(PertilyoEntity.this, pos ->
                    PertilyoEntity.this.world.getBlockState(pos).getBlock() instanceof TorchBlock
                    && getNavigation().findPathTo(pos, 1, 16) != null
                    && !ignoredBlocks.containsKey(pos), cooldown);
        }

        /**
         * Goal can start if:
         * - The Pertilyo should move to a torch
         * - The Pertilyo is not next to the target torch
         */
        @Override
        public boolean canStart() {
            if (super.canStart()) {
                return !hasPositionTarget()
                        && shouldMoveToTorch()
                        && !isCloserThan(1);
            }
            return false;
        }

        @Override
        public void tick() {
            if (targetPos == null) {
                return;
            }
            if (isCloserThan(3) && getAnimationState() == DEFAULT_STATE) {
                //If the target torch is too high up/low down, it's invalid! Quit goal
                if (Math.abs(getY() - targetPos.getY()) > 2) {
                    ignoredBlocks.put(targetPos, age);
                    targetPos = null;
                    return;
                }
                lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, Vec3d.ofCenter(targetPos));
                if (getNavigation().isFollowingPath()) {
                    getNavigation().stop();
                }
                setAnimationState(SHOOT_STATE);
                shootGleamTime = SHOOT_GLEAM_ANIMATION_DURATION + 1;
            }
            if (getAnimationState() == SHOOT_STATE) {
                handleGleam();
                if (shootGleamTime > 0) {
                    shootGleamTime--;
                } else {
                    targetPos = null;
                }
            }
            ++ticks;
        }

        @Override
        public boolean shouldContinue() {
            if (getNavigation().isIdle() && getAnimationState() != SHOOT_STATE) {
                return false;
            }
            if (targetPos == null) {
                return false;
            }
            if (getAnimationState() == DEFAULT_STATE && !isTorch(targetPos)) {
                return false;
            }
            return ticks < this.getTickCount(600);
        }

        @Override
        public void stop() {
            super.stop();
            if (getAnimationState() != DEFAULT_STATE) {
                setAnimationState(DEFAULT_STATE);
            }
            shootGleamTime = 0;
            this.ticks = 0;
        }

        private void handleGleam() {
            if (shootGleamTime > 0) {
                if (shootGleamTime == SHOOT_GLEAM_ANIMATION_DURATION / 2 - 2) {
                    world.breakBlock(targetPos, false, PertilyoEntity.this);
                    playSound(SoundEvents.ENTITY_ILLUSIONER_PREPARE_MIRROR, 1.0f, 2.0f);
                    setEnergy(getEnergy() + 1);
                    playElectricSparks(20);
                    setVelocity(getVelocity().negate());
                    Vec3d dir = EarthMath.dirBetweenVecs(Vec3d.ofCenter(targetPos), getEyePos().add(0, -0.3, 0));
                    Vec3d dir2 = EarthMath.dirBetweenVecs(Vec3d.ofCenter(targetPos), getEyePos().add(0, -0.7, 0));
                    Vec3d targetPos3d = Vec3d.ofCenter(targetPos);
                    for (double d = 0; d < getEyePos().distanceTo(Vec3d.ofCenter(targetPos)); d += 0.1) {
                        Vec3d particlePos = targetPos3d.add(dir.multiply(d));
                        Vec3d particlePos2 = targetPos3d.add(dir2.multiply(d));
                        ((ServerWorld) world).spawnParticles(ParticleTypes.WAX_OFF,
                                particlePos.x, particlePos.y, particlePos.z,
                                1, 0, 0, 0, 0.25);
                        ((ServerWorld) world).spawnParticles(ParticleTypes.WAX_OFF,
                                particlePos2.x, particlePos2.y, particlePos2.z,
                                1, 0, 0, 0, 0.25);
                    }
                }
            }
        }

        /**
         * The Pertilyo should be less likely to move to a torch if energized. It shouldn't move to a torch at
         * all if it is fully energized.
         */
        private boolean shouldMoveToTorch() {
            return getEnergy() < getMaxEnergy();
        }
    }

    class PertilyoWanderARoundGoal extends Goal {

        PertilyoWanderARoundGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return navigation.isIdle() && random.nextInt(10) == 0 && !isRoosting();
        }

        @Override
        public void start() {
            Vec3d vec3d = this.getRandomLocation();
            if (vec3d != null) {
                navigation.startMovingAlong(navigation.findPathTo(new BlockPos(vec3d), 1), 1.0);
            }
        }

        @Override
        public boolean shouldContinue() {
            return navigation.isFollowingPath();
        }

        /**
         * Will return a random location for the pertilyo to roam to.
         * If it is weathered or oxidized/there is a storm, it will prioritize going above ground.
         *
         * @return an above ground location, or a random location nearby.
         */
        @Nullable
        private Vec3d getRandomLocation() {
            Vec3d vec = getRotationVec(0.0f);
            Vec3d vec3d3 = null;
            if (getOxidizationLevel() == Oxidizable.OxidationLevel.WEATHERED
                    || getOxidizationLevel() == Oxidizable.OxidationLevel.OXIDIZED
                    || world.isThundering()) {
                vec3d3 = AboveGroundTargeting.find(PertilyoEntity.this, 8, 7,
                        vec.x, vec.z, 1.5707964f, 3, 1);
            }
            return vec3d3 != null ? vec3d3 : FuzzyTargeting.find(
                    PertilyoEntity.this, 10, 5);
        }
    }
}
