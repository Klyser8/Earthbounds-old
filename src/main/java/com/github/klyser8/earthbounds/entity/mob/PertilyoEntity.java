package com.github.klyser8.earthbounds.entity.mob;

import com.github.klyser8.earthbounds.client.sound.PertilyoFlyLoopSoundInstance;
import com.github.klyser8.earthbounds.entity.misc.GlowGreaseDropEntity;
import com.github.klyser8.earthbounds.entity.goal.EscapeAttackerGoal;
import com.github.klyser8.earthbounds.entity.goal.MoveToTargetBlockGoal;
import com.github.klyser8.earthbounds.registry.EarthboundItems;
import com.github.klyser8.earthbounds.registry.EarthboundSounds;
import com.github.klyser8.earthbounds.registry.EarthboundsAdvancementCriteria;
import com.github.klyser8.earthbounds.util.EarthMath;
import com.github.klyser8.earthbounds.util.EarthUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.TorchBlock;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
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

    //In ticks
    public static final int OXIDATION_STEP_0 = 0;
    public static final int OXIDATION_STEP_1 = 900;
    public static final int OXIDATION_STEP_2 = 1800;
    public static final int OXIDATION_STEP_3 = 2700;

    private static final byte SHOOT_STATE = 1;

    private static final int SHOOT_GLEAM_ANIMATION_DURATION = 9; // divided by 2!

    private static final TrackedData<Integer> ENERGY = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MAX_ENERGY = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> SECONDS_SINCE_DEOX = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_OXIDIZED = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_ROOSTING = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<BlockPos> CLOSEST_TORCH = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.BLOCK_POS);

    private int lastRoostTime = 0;

    private Vec3d leashedLocation;

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
        dataTracker.startTracking(IS_OXIDIZED, false);
        dataTracker.startTracking(IS_ROOSTING, false);
        dataTracker.startTracking(CLOSEST_TORCH, new BlockPos(0, 0, 0));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("TimeSinceDeox", getTimeSinceDeox());
        nbt.putInt("Energy", getEnergy());
        //Used for the pertilyo struck by lightning advancement.
        nbt.putBoolean("IsOxidized", isOxidized());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("TimeSinceDeox")) {
            setTimeSinceDeox(nbt.getInt("TimeSinceDeox"));
        }
        if (nbt.contains("Energy")) {
            setEnergy(nbt.getInt("Energy"));
        }
        if (nbt.contains("IsOxidized")) {
            setOxidized(nbt.getBoolean("IsOxidized"));
        }
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new MoveToTorchGoal(200));
        goalSelector.add(1, new PertilyoWanderARoundGoal());
        goalSelector.add(2, new PertilyoEscapeAttackerGoal(this, 1.25f));
        goalSelector.add(3, new PertilyoLookAroundGoal(this));
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this,
                "pose", 10, this::movementPredicate));
        animationData.addAnimationController(new AnimationController<>(this,
                "move", 10, this::posePredicate));
    }

    private <E extends IAnimatable> PlayState posePredicate(AnimationEvent<E> event) {
        if (isRoosting() && getAnimationState() == DEFAULT_STATE) {
            event.getController().transitionLengthTicks = 0;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("pose_roost", true));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {
        BlockPos belowBlock = new BlockPos(getPos().add(0, -0.2f, 0));
        if (shouldHibernate() && world.getBlockState(belowBlock).isSolidBlock(world, belowBlock) &&
                !isRoosting() && getAnimationState() != SHOOT_STATE) {
            event.getController().transitionLengthTicks = 10;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("hibernate", true));
            return PlayState.CONTINUE;
        }
        if (world.isThundering() && getTimeSinceDeox() > OXIDATION_STEP_1 && event.isMoving() &&
                !isRoosting() && getAnimationState() != SHOOT_STATE) {
            event.getController().transitionLengthTicks = 5;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("flap_loop_shake", true));
            return PlayState.CONTINUE;
        }
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
        return shouldHibernate();
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!world.isClient) {
            if (!Earthen.isDamagePickaxe(source)) {
                amount = Earthen.handleDamage(source, this, amount);
            }
        }
        return super.damage(source, amount);
    }

    @Override
    public void attachLeash(Entity entity, boolean sendPacket) {
        super.attachLeash(entity, sendPacket);
        leashedLocation = getPos();
    }

    @Override
    protected void updateLeash() {
        super.updateLeash();
        if (getHoldingEntity() instanceof ServerPlayerEntity player) {
            if (leashedLocation.getY() < -32 && getPos().getY() > world.getSeaLevel() && world.isSkyVisible(getBlockPos())) {
                EarthboundsAdvancementCriteria.ESCORT_PERTILYO.trigger(player);
            }
        }
    }

    /**
     * MIGHT BE readSpawnPacket() from 1.18
     *
     * Plays a moving sound constantly from the moment the mob spawns, until it dies.
     */
    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        PertilyoFlyLoopSoundInstance.playSound(this);
    }

    @Override
    public void tick() {
        super.tick();
        //keeps vertical speed to a limit. Looks wonky otherwise!
        if (upwardSpeed > 0.5) {
            setUpwardSpeed(0.5f);
        } else if (upwardSpeed < -0.5f && !shouldHibernate()) {
            setUpwardSpeed(-0.5f);
        }
        if (age % 20 == 0) {
            int deoxIncrease = 1;
            if (isTouchingWaterOrRain()) {
                deoxIncrease *= 2;
            }
            setTimeSinceDeox(getTimeSinceDeox() + deoxIncrease);
            if (!world.isClient) {
                if (world.isThundering() && world.isSkyVisible(getBlockPos()) && getTimeSinceDeox() > OXIDATION_STEP_1) {
                    if (random.nextFloat() < 0.02) {
                        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
                        if (lightning != null) {
                            lightning.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(getBlockPos()));
                            world.spawnEntity(lightning);
                        }
                    }
                }
            }
            if (getTimeSinceDeox() % OXIDATION_STEP_1 == 0
                    && getTimeSinceDeox() != 0 && getTimeSinceDeox() <= OXIDATION_STEP_3) {
                playSound(EarthboundSounds.PERTILYO_OXIDIZE, 0.5f, 1.0f + random.nextFloat() / 0.5f);
            }
        }
        if (this.isRoosting()) {
            this.setVelocity(Vec3d.ZERO);
        }
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
    protected void mobTick() {
        super.mobTick();
        if (shouldHibernate()) {
            if (!navigation.isFollowingPath()) {
                if (hasNoGravity()) {
                    setNoGravity(false);
                }
            }
            return;
        }
        if (!hasNoGravity()) {
            setNoGravity(true);
        }
        BlockPos blockPos = getBlockPos();
        BlockPos abovePos = blockPos.up();
        boolean isBlockAboveSolid = world.getBlockState(abovePos).isSolidBlock(world, blockPos);
        if (isRoosting()) {
            if (!isBlockAboveSolid || random.nextInt(getTimeSinceDeox() + 300) == 0
                    || getDamageTracker().wasRecentlyAttacked() || world.getLightLevel(getBlockPos()) > 7
                    || world.isThundering()) {
                lastRoostTime = age;
                setRoosting(false);
                playSound(EarthboundSounds.PERTILYO_PLOP, 1.0f, 1.5f);
            }
        } else {
            if (isBlockAboveSolid
                    && world.getLightLevel(getBlockPos()) < 6
                    && !EarthUtil.isOnCooldown(age, lastRoostTime, 300)
                    && !isRoosting()) {
                if (random.nextFloat() < 0.1) {
                    navigation.stop();
                    setRoosting(true);
                }
            }
            if (age % 100 == 0) {
                if (!world.isClient) {
                    BlockPos solidPosBelow = EarthMath.getClosestSolidBlockBelow(world, getBlockPos());
                    if (solidPosBelow == null || Math.sqrt(getBlockPos().getSquaredDistance(solidPosBelow)) < 3) {
                        return;
                    }
                    if (getEnergy() > 0) {
                        if (random.nextFloat() < 0.25) {
                            GlowGreaseDropEntity glowGrease = new GlowGreaseDropEntity(world, getX(), getY(), getZ(),
                                    EarthboundItems.GLOW_GREASE.getDefaultStack());
                            world.spawnEntity(glowGrease);
                            setEnergy(getEnergy() - 1);
                            playSound(EarthboundSounds.PERTILYO_PLOP, 1.0f, 1.0f);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
        if (getTimeSinceDeox() > OXIDATION_STEP_1) {
            if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                ItemEntity item = new ItemEntity(world, getX(), getY() + 0.5, getZ(),
                        EarthboundItems.PERTILYO_ROD.getDefaultStack());
                item.setToDefaultPickupDelay();
                world.spawnEntity(item);
                item.setOnFireFor(0); //Used to prevent the item from dying from fire
            }
        }
        if (getTimeSinceDeox() > 1) {
            setTimeSinceDeox(-1);
            playSound(EarthboundSounds.PERTILYO_DEOXIDIZE, 2.0f, 1.0f);
        }
        playElectricSparks(100);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isRoosting() ? EarthboundSounds.PERTILYO_AMBIENT_SLEEP : EarthboundSounds.PERTILYO_AMBIENT;
    }

    @Override
    public void playAmbientSound() {
        if (isRoosting()) {
            if (random.nextFloat() > 0.25f) {
                return;
            }
        }
        playSound(getAmbientSound(), isRoosting() ? getSoundVolume() / 2.0f : getSoundVolume(), this.getSoundPitch());
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return EarthboundSounds.PERTILYO_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return EarthboundSounds.PERTILYO_DEATH;
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        playHurtSound(source);
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    /**
     * Checks if the pertilyo is in the condition to spawn in a given location.
     *
     * @param type The type of the entity
     * @param world The world which the entity may spawn in
     * @param spawnReason the reason of the spawn
     * @param pos the spawn position
     * @return whether the mob is allowed to spawn at the location.
     */
    public static boolean checkMobSpawn(EntityType<? extends MobEntity> type, WorldAccess world,
                                        SpawnReason spawnReason, BlockPos pos, Random random) {
        if ((spawnReason == SpawnReason.NATURAL || spawnReason == SpawnReason.CHUNK_GENERATION)) {
            if (world.getLightLevel(pos) > 8 || pos.getY() > 0) {
                return false;
            }
            return canMobSpawn(type, world, spawnReason, pos, random);
        }
        return false;
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
    public int getTimeSinceDeox() {
        return dataTracker.get(SECONDS_SINCE_DEOX);
    }

    public void setTimeSinceDeox(int time) {
        dataTracker.set(SECONDS_SINCE_DEOX, time);
        //Oxidation time is set to -1 for the advancement related to Pertilyo to work.
        if (time >= OXIDATION_STEP_1 || time == -1) {
            if (!isOxidized()) {
                setOxidized(true);
            }
        } else {
            if (isOxidized()) {
                setOxidized(false);
            }
        }
    }

    public boolean shouldHibernate() {
        return world.isSkyVisible(getBlockPos()) && world.isThundering() && getTimeSinceDeox() > OXIDATION_STEP_1;
    }

    public boolean isOxidized() {
        return dataTracker.get(IS_OXIDIZED);
    }

    public void setOxidized(boolean isOxidized) {
        dataTracker.set(IS_OXIDIZED, isOxidized);
    }

    public Oxidizable.OxidationLevel getOxidizationLevel() {
        if (getTimeSinceDeox() < OXIDATION_STEP_1) {
            return Oxidizable.OxidationLevel.UNAFFECTED;
        } else if (getTimeSinceDeox() < OXIDATION_STEP_2) {
            return Oxidizable.OxidationLevel.EXPOSED;
        } else if (getTimeSinceDeox() < OXIDATION_STEP_3) {
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

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {

    }

    boolean isTorch(BlockPos pos) {
        return world.canSetBlock(pos) && world.getBlockState(pos).getBlock() instanceof TorchBlock;
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
                return /*!hasPositionTarget() MIGHT CAUSE WEIRD ISSUES! Disabled as pertilyo wouldn't destroy torches
                        && */shouldMoveToTorch()
                        && !isCloserThan(1)
                        && !shouldHibernate();
            }
            return false;
        }

        @Override
        public void start() {
            super.start();
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
                playSound(EarthboundSounds.PERTILYO_ANGRY, 1.0f, 1.0f);
                ((ServerWorld) world).spawnParticles(ParticleTypes.ANGRY_VILLAGER,
                        getEyePos().x, getEyePos().y, getEyePos().z,
                        5, 0.25,0.25,0.25, 0);
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
            return navigation.isIdle()
                    && random.nextInt(10) == 0
                    && !isRoosting()
                    && !shouldHibernate();
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
            return navigation.isFollowingPath() && !isRoosting();
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

    class PertilyoEscapeAttackerGoal extends EscapeAttackerGoal {

        public PertilyoEscapeAttackerGoal(PathAwareEntity mob, double speed) {
            super(mob, speed);
        }

        @Override
        public boolean canStart() {
            return super.canStart() && !shouldHibernate();
        }

        @Override
        public void start() {
            super.start();
            if (isRoosting()) {
                setRoosting(false);
            }
        }
    }

    class PertilyoLookAroundGoal extends LookAroundGoal {

        public PertilyoLookAroundGoal(MobEntity mob) {
            super(mob);
        }

        @Override
        public boolean canStart() {
            return super.canStart() && !shouldHibernate() && !isRoosting();
        }
    }

}
