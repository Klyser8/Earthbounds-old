package com.github.klyser8.earthbounds.entity;

import com.github.klyser8.earthbounds.registry.EarthboundEntities;
import com.github.klyser8.earthbounds.registry.EarthboundSounds;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class CarboraneaEntity extends AnimalEntity implements Earthen, Conductive {

    private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.BLAZE_POWDER);

    private final int maxCoals; //How many coals this entity can have present at a time

    private static final TrackedData<Float> HEAT = DataTracker.registerData(CarboraneaEntity.class,
            TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> SHOULD_MATE_SITTING = DataTracker.registerData(CarboraneaEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> COAL_AMOUNT = DataTracker.registerData(CarboraneaEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ANIMATION_SHAKE_TIME = DataTracker.registerData(CarboraneaEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    private final List<CoalChunkEntity> coals;

    private AnimationFactory factory = new AnimationFactory(this);

    public CarboraneaEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        coals = new ArrayList<>();
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
        maxCoals = random.nextBoolean() ? 3 : random.nextBoolean() ? 6 : 9;

    }

    @Override
    protected void initGoals() {
//        this.goalSelector.add(0, new EscapeAttackerGoal(this, 1.5));
//        this.goalSelector.add(1, new CarboraneaMateGoal(this, 1.0));
//        this.goalSelector.add(2, new FollowParentGoal(this, 1.0));
        this.goalSelector.add(3, new MoveToHeatSourceGoal(50));
//        this.goalSelector.add(4, new ExcreteCoalsGoal());
//        this.goalSelector.add(5, new FollowPlayerGoal(1.0f, 5, 20));
//        this.goalSelector.add(6, new CarboraneaWanderAroundGoal(this, 1.0, 120));
//        this.goalSelector.add(7, new LookAtEntityGoal(this, CarboraneaEntity.class, 5.0f, 0.5f));
//        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 5.0f, 0.5f));
//        this.goalSelector.add(8, new LookAroundGoal(this));
//        this.targetSelector.add(0, new RevengeGoal(this, Earthen.class));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class,
                false, false));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, AnimalEntity.class,
                false, false));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(SHOULD_MATE_SITTING, false);
        dataTracker.startTracking(HEAT, 0.0f);
        dataTracker.startTracking(COAL_AMOUNT, 0);
        dataTracker.startTracking(ANIMATION_SHAKE_TIME, 0);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return EarthboundEntities.CARBORANEA.create(world);
    }

    @Override
    public void tick() {
        super.tick();
        int period = 10;
        if (age % period == 0) {
            updateHeat(period);
        }
        if (age < 40) {
            setMovementSpeed(0);
        } else if (age == 41) {
            setMovementSpeed((float) getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
        }
        coals.removeIf(CoalChunkEntity::isRemoved);
        dataTracker.set(COAL_AMOUNT, coals.size());
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return 0.0f;
    }

    @Override
    public double getHeightOffset() {
        return 0.0;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return BREEDING_INGREDIENT.test(stack);
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this,
                "default", 10, this::defaultPredicate));
        animationData.addAnimationController(new AnimationController<>(this,
                "shaking", 0, this::shakingPredicate));
    }

    private <E extends IAnimatable> PlayState defaultPredicate(AnimationEvent<E> event) {
        if (age < 40) {
            event.getController().transitionLengthTicks = 0;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("spawn", false));
            return PlayState.CONTINUE;
        }
        if (deathTime > 0) {
            event.getController().transitionLengthTicks = 0;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("death", false));
            return PlayState.CONTINUE;
        }
        if (dataTracker.get(SHOULD_MATE_SITTING)) {
            event.getController().transitionLengthTicks = 10;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("breed", true));
            return PlayState.CONTINUE;
        }
        if (isInLava() && event.isMoving()) {
            event.getController().transitionLengthTicks = 0;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("swim", true));
            return PlayState.CONTINUE;
        }
        if (isInLava()) {
            event.getController().transitionLengthTicks = 20;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("float", true));
            return PlayState.CONTINUE;
        }
        if (event.isMoving()) {
            event.getController().transitionLengthTicks = 0;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("walk", true));
            return PlayState.CONTINUE;
        }
        if (getCurrentHeat() < MAX_HEAT * 0.5) {
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("wave", false));
        } else {
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("bounce", false));
        }
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState shakingPredicate(AnimationEvent<E> event) {
        if (dataTracker.get(ANIMATION_SHAKE_TIME) > 0) {
            dataTracker.set(ANIMATION_SHAKE_TIME, dataTracker.get(ANIMATION_SHAKE_TIME) - 1);
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("shake", false));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return createMobAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 5)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.25);
    }

    @Override
    public float updateHeat(int period) {
        if (isBaby()) {
            return 0.0f;
        }
        float currentHeat = dataTracker.get(HEAT);
        float heatChange = Conductive.calculateHeatChangePerPeriod(world, getEyePos(), false);
        currentHeat += heatChange;
        if (currentHeat > MAX_HEAT) {
            currentHeat = MAX_HEAT;
        } else if (currentHeat < 0) {
            currentHeat = 0;
        }
        dataTracker.set(HEAT, currentHeat);
        return heatChange;
    }

    @Override
    public void breed(ServerWorld world, AnimalEntity other) {
        super.breed(world, other);
        setBreedingAge(72000);
        other.setBreedingAge(72000);
        dataTracker.set(ANIMATION_SHAKE_TIME, 20);
        playSound(EarthboundSounds.CARBORANEA_SHAKE_SHORT, 1.2f, 1.0f);
        ((CarboraneaEntity) other).dataTracker.set(ANIMATION_SHAKE_TIME, 20);
    }

    @Override
    public boolean canBreedWith(AnimalEntity other) {
        return super.canBreedWith(other);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        float height = super.getDimensions(pose).height;
        float width = super.getDimensions(pose).width;
        return isBaby() ? EntityDimensions.changing(height / 2, width / 2) : super.getDimensions(pose);
    }

    @Override
    public float getCurrentHeat() {
        return dataTracker.get(HEAT);
    }

    @Override
    public void setCurrentHeat(float heat) {
        dataTracker.set(HEAT, heat);
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean canWalkOnFluid(Fluid fluid) {
        return fluid.isIn(FluidTags.LAVA);
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return EarthboundSounds.CARBORANEA_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return EarthboundSounds.CARBORANEA_DEATH;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isBaby() ? null : EarthboundSounds.CARBORANEA_AMBIENT;
    }

    @Override
    public void playAmbientSound() {
        super.playAmbientSound();
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 100;
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (!sound.equals(EarthboundSounds.CARBORANEA_SHAKE_SHORT) && !sound.equals(EarthboundSounds.CARBORANEA_SHAKE)) {
            pitch -= getCurrentHeat() / 2000;
            super.playSound(sound, volume, pitch);
        }
    }

    class CarboraneaWanderAroundGoal extends WanderAroundGoal {

        public CarboraneaWanderAroundGoal(PathAwareEntity mob, double speed, int chance) {
            super(mob, speed, chance);
        }

        @Override
        public boolean canStart() {
            return super.canStart();
//                System.out.println("X: " + targetX + ", Y: " + targetY + ", Z: " + targetZ + ", Heat source: " + Conductive.isPosHeatSource(world, new BlockPos(targetX, targetY, targetZ)));
        }

        @Override
        public void stop() {
            super.stop();
        }

        @Override
        protected Vec3d getWanderTarget() {
            return NoPenaltyTargeting.find(CarboraneaEntity.this,
                    (int) (6 - (getCurrentHeat() / 100)), (int) (6 - (getCurrentHeat() / 100)));
        }
    }

    class MoveToHeatSourceGoal extends Goal {

        private double targetX;
        private double targetY;
        private double targetZ;

        private final int attempts;

        public MoveToHeatSourceGoal(int attempts) {
            this.targetX = 0;
            this.targetY = 0;
            this.targetZ = 0;
            this.attempts = attempts;
        }

        /**
         * Whether the goal can start or not. Any logic checking if a goal's conditions are met should
         * be written here.
         *
         * @return true if the goal can start
         */
        @Override
        public boolean canStart() {
            if (isBaby()
                    || getCurrentHeat() > MAX_HEAT * 0.75
                    || Conductive.calculateHeatChangePerPeriod(world, getEyePos(), true) > 2.0f
                    || getNavigation().isFollowingPath()) {
                return false;
            }
            boolean heatSourceFound = false;
            int i = 0;
            while (i < attempts) {
                if (searchHeatSource()) {
                    heatSourceFound = true;
                    break;
                }
                i++;
            }
            return heatSourceFound;
        }

        /**
         * Whether the goal should continue or not.
         *
         * @return true if it should continue
         */
        @Override
        public boolean shouldContinue() {
            return !getNavigation().isIdle();
        }

        /**
         * Any logic related to the goal's start should be written here.
         */
        @Override
        public void start() {
            getNavigation().startMovingTo(targetX, targetY, targetZ, 1.25f);
        }

        /**
         * if a valid 3d vector is found, the 3 target fields are updated with the
         * vector's values and the method returns true.
         */
        protected boolean searchHeatSource() {
            Vec3d vec3d = this.selectRandomConductingBlock();
            if (vec3d == null) {
                return false;
            }
            targetX = vec3d.x;
            targetY = vec3d.y;
            targetZ = vec3d.z;
            return true;
        }

        /**
         * Searches for a nearby heat source by picking a random block nearby and checking if
         *
         * @return the block's 3d vector if it is a heat source, null otherwise.
         */
        private Vec3d selectRandomConductingBlock() {
            BlockPos blockPos = getBlockPos().add(random.nextInt(40) - 20, random.nextInt(20) - 10,
                    random.nextInt(40) - 20);
            return Conductive.calculateHeatChangePerPeriod(world, Vec3d.ofBottomCenter(blockPos), true) >
                    Conductive.calculateHeatChangePerPeriod(world, getEyePos(), true)
                    ? Vec3d.ofBottomCenter(blockPos) : null;
        }

    }

    class ExcreteCoalsGoal extends Goal {

        private int cooldown;

        public ExcreteCoalsGoal() {
            cooldown = -1;
        }

        @Override
        public boolean canStart() {
            return !isInLava() && getCurrentHeat() > MAX_HEAT * 0.75
                    && coals.size() + 1 < maxCoals && !isInLove();
        }

        @Override
        public void start() {
            super.start();
        }

        @Override
        public void tick() {
            super.tick();
            if (cooldown == -1) {
                playSound(EarthboundSounds.CARBORANEA_SHAKE, 1.0f, 1.0f);
            }
            cooldown--;
            if (cooldown > 0) {
                return;
            }
            excreteCoalChunk(new Vec3d(
                    -0.1D + random.nextDouble() / 5,
                    random.nextDouble() / 4,
                    -0.1D + random.nextDouble() / 5));
            cooldown = 3;
            setCurrentHeat((float) (getCurrentHeat() - MAX_HEAT * 0.1875));
        }

        @Override
        public boolean shouldContinue() {
            return !isInLava() && getCurrentHeat() > MAX_HEAT * 0.3
                    && coals.size() < maxCoals && !isInLove();
        }

        @Override
        public void stop() {
            super.stop();
            cooldown = -1;
        }

        private void excreteCoalChunk(Vec3d velocity) {
            CoalChunkEntity coal = EarthboundEntities.COAL_CHUNK.create(world);
            if (coal == null) return;
            coal.setPos(getX() + random.nextFloat() / 2,
                    getY() + 0.25,
                    getZ() + random.nextFloat() / 2);
            world.spawnEntity(coal);
            coal.setVelocity(velocity);
            coals.add(coal);
            playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0f,
                    1.6f +  random.nextFloat() / 2.5f);
            dataTracker.set(ANIMATION_SHAKE_TIME, dataTracker.get(ANIMATION_SHAKE_TIME) + 15);
        }
    }

    class FollowPlayerGoal extends Goal {

        private final Random random;
        private LivingEntity followed;
        private final double speed;
        private int updateCountdownTicks;
        private final float maxDistance;
        private final float minDistance;
        private float oldWaterPathfindingPenalty;

        public FollowPlayerGoal(double speed, float minDistance, float maxDistance) {
            this.random = new Random();
            this.speed = speed;
            this.minDistance = minDistance;
            this.maxDistance = maxDistance;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        public boolean canStart() {
            if (getTarget() == null) return false;
            float chance = (getCurrentHeat() / MAX_HEAT) * 100;
            if (random.nextFloat() < chance) {
                return false;
            } else if (getTarget() == null) {
                return false;
            } else if (getTarget().isSpectator()) {
                return false;
            } else if (distanceTo(getTarget()) < this.minDistance) {
                return false;
            } else {
                followed = getTarget();
                return true;
            }
        }

        public boolean shouldContinue() {
            if (navigation.isIdle()) {
                return false;
            } else {
                return distanceTo(followed) >= this.maxDistance;
//                return !(carbon.squaredDistanceTo(player) <= (double)(this.maxDistance * this.maxDistance));
            }
        }

        public void start() {
            updateCountdownTicks = 0;
            oldWaterPathfindingPenalty = getPathfindingPenalty(PathNodeType.WATER);
            setPathfindingPenalty(PathNodeType.WATER, getPathfindingPenalty(PathNodeType.WATER));
        }

        public void stop() {
            followed = null;
            navigation.stop();
            setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
        }

        public void tick() {
            getLookControl().lookAt(followed, 10.0F, getMaxLookPitchChange());
            if (--updateCountdownTicks <= 0) {
                updateCountdownTicks = 10;
                navigation.startMovingTo(followed, speed);
            }
        }
    }
    class CarboraneaMateGoal extends AnimalMateGoal {

        protected CarboraneaEntity sittingMate = null;

        public CarboraneaMateGoal(CarboraneaEntity carboranea, double chance) {
            super(carboranea, chance);
        }

        @Override
        public boolean canStart() {
            return super.canStart() && getCurrentHeat() > MAX_HEAT * 0.7;
        }

        @Override
        public void start() {
            super.start();
            sittingMate = (CarboraneaEntity) (random.nextBoolean() ? animal : mate);
        }

        /**
         * Either one of the two mates should be sitting while breeding.
         */
        @Override
        public void tick() {
            super.tick();
            if (squaredDistanceTo(mate) < 3.0 && !sittingMate.dataTracker.get(SHOULD_MATE_SITTING)) {
                dataTracker.set(SHOULD_MATE_SITTING, true);
            }
        }

        @Override
        public void stop() {
            super.stop();
            dataTracker.set(SHOULD_MATE_SITTING, false);
            sittingMate.dataTracker.set(SHOULD_MATE_SITTING, false);
            sittingMate = null;
        }
    }

    /*class TossCoalsAtEntityGoal extends Goal {

        private int cooldown;

        public TossCoalsAtEntityGoal() {
            cooldown = 0;
        }

        @Override
        public boolean canStart() {
            return isInLava() && getCurrentHeat() > MAX_HEAT * 0.75 && getTarget() != null
                    && coals.size() < maxCoals && canSee(getTarget()) && distanceTo(getTarget()) <= 15;
        }

        @Override
        public void tick() {
            super.tick();
            cooldown--;
            if (getTarget() == null) {
                return;
            }
            lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, getTarget().getEyePos());
            if (cooldown > 0) {
                return;
            }
//            CoalChunkEntity coalChunk = EarthboundEntities.COAL_CHUNK.create(world);
            PersistentProjectileEntity coalChunk = ProjectileUtil.createArrowProjectile(CarboraneaEntity.this, Items.ARROW.getDefaultStack(), 1);
            if (coalChunk == null) {
                return;
            }
//            coals.add(coalChunk);
            coalChunk.setPos(getX() + 0.25, getY() + getHeight(), getZ() + 0.25);
            double distance = getPos().distanceTo(getTarget().getEyePos());
            System.out.println("Eye pos: " + getTarget().getEyePos());
            Vec3d dir = getTarget().getEyePos().subtract(getEyePos());
            System.out.println("Blob + vector = " + (getEyePos().add(dir)));
            double d = getTarget().getX() - getX();
            double e = getTarget().getBodyY(0.3333333333333333) - coalChunk.getY();
            double f = getTarget().getZ() - getZ();
            double g = Math.sqrt(d * d + f * f);
            coalChunk.setVelocity(d, e + g * (double)0.2f, f, 1.6f, 14 - world.getDifficulty().getId() * 4);
            world.spawnEntity(coalChunk);
            cooldown = 40 + random.nextInt(60);
        }

        @Override
        public boolean shouldContinue() {
            return isInLava() && getCurrentHeat() > MAX_HEAT * 0.75 && getTarget() != null
                    && dataTracker.get(COAL_AMOUNT) < maxCoals && canSee(getTarget()) && distanceTo(getTarget()) <= 15
                    && random.nextFloat() > 0.01;
        }
    }*/

}
