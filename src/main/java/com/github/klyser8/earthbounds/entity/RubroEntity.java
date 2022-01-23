package com.github.klyser8.earthbounds.entity;

import com.github.klyser8.earthbounds.entity.goal.EscapeAttackerGoal;
import com.github.klyser8.earthbounds.entity.goal.EscapeTargetGoal;
import com.github.klyser8.earthbounds.registry.EarthboundBlocks;
import com.github.klyser8.earthbounds.registry.EarthboundEntities;
import com.github.klyser8.earthbounds.registry.EarthboundParticles;
import com.github.klyser8.earthbounds.registry.EarthboundSounds;
import com.github.klyser8.earthbounds.client.sound.RubroActiveSoundInstance;
import com.github.klyser8.earthbounds.util.AdvancedBlockPos;
import com.github.klyser8.earthbounds.util.EarthMath;
import com.github.klyser8.earthbounds.util.EarthUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.ParticleKeyFrameEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.*;

import static com.github.klyser8.earthbounds.util.EarthMath.dirBetweenVecs;

public class RubroEntity extends PathAwareEntity implements IAnimatable, Earthen {

    public static final int POWER_LIMIT = 200;
    private static final int TAIL_SPIN_ANIMATION_DURATION = 36; //in ticks

    private static final UUID POWER_DAMAGE_BOOST_ID = UUID.fromString("e09618ac-395e-47b7-b6a1-b47aa59c92b9");
    private static final UUID POWER_SPEED_BOOST_ID = UUID.fromString("8391a50c-b8d5-44ee-bf77-84e519dbf5b2");
    private static final UUID POWER_FOLLOW_RANGE_ID = UUID.fromString("e31be277-eefe-4a00-b333-5ff68773f0a3");

    private final AnimationFactory factory;
    private static final TrackedData<Integer> MAX_POWER = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    //Whether the Rubro has a golden skull or not
    private static final TrackedData<Boolean> HAS_GOLD_SKULL = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    //Whether the Rubro was born from a fossil or not
    private static final TrackedData<Boolean> FROM_FOSSIL = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    //Whether the Rubro is made of deepslate or not
    public static final TrackedData<Boolean> DEEPSLATE = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    //How much power the Rubro has left
    private static final TrackedData<Integer> POWER = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    //The current ore the Rubro is hunting for
    private static final TrackedData<ItemStack> CURRENT_ORE = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.ITEM_STACK);
    //The current target block pos of the entity
    private static final TrackedData<BlockPos> BLOCK_TARGET_POS = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.BLOCK_POS);
    //Holds the ID of the current animation.
    private static final TrackedData<Integer> CURRENT_ENTITY_STATE = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    //Whether the Rubro is standing or not.
    private static final TrackedData<Boolean> STANDING = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    private RubroActiveSoundInstance activeSoundInstance = null;

    private static final int STATE_NONE = 0;
    private static final int STATE_DIGGING = 1;
    private static final int STATE_POUNCING = 2;
    private static final int STATE_SPINNING = 3;

    private boolean collides = true;
    private final Map<BlockPos, Integer> ignoredBlocks = new HashMap<>();

    public RubroEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        factory = new AnimationFactory(this);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, -1.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
                                 @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (getY() > -60 && getY() < 15) {
            setDeepslate(true);
        }
        if (isDeepslate()) {
            createDeepslateAttributes();
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    /**
     * Initializes the Rubro.
     *
     * @param fromFossil whether the rubro is born from a fossil or not.
     * @param goldSkull whether the rubro has a gold skull or not.
     * @param startPower the amount of power the rubro has as a baby (any value lower than 0)
     */
    public void initializeFossil(boolean deepslate, boolean fromFossil, boolean goldSkull, int startPower) {
        setFromFossil(fromFossil);
        setGoldSkull(goldSkull);
        updatePower(startPower);
        setDeepslate(deepslate);
        if (isDeepslate()) {
            createDeepslateAttributes();
        }
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new MoveToRedstoneGoal(300, 100));
        goalSelector.add(1, new RubroAttackGoal());
        goalSelector.add(2, new RubroEscapeTargetGoal());
        goalSelector.add(3, new EscapeAttackerGoal(this, 1.2f));
        goalSelector.add(4, new WanderAroundGoal(this, 1.0f));
        goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
        goalSelector.add(6, new LookAroundGoal(this) {
            @Override
            public boolean canStart() {
                return super.canStart() && getEntityState() == STATE_NONE;
            }
        });
        targetSelector.add(0, new RevengeGoal(this, RubroEntity.class));
        targetSelector.add(1, new RedstoneTargetGoal());
        targetSelector.add(2, new ActiveTargetGoal<>(this, WitchEntity.class,
                false, true));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return DefaultAttributeContainer.builder()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0D)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)
                .add(EntityAttributes.GENERIC_ARMOR, 10)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.35);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("deepslate")) {
            setDeepslate(nbt.getBoolean("deepslate"));
        }
        if (nbt.contains("fromFossil")) {
            setFromFossil(nbt.getBoolean("fromFossil"));
        }
        if (nbt.contains("goldSkull")) {
            setGoldSkull(nbt.getBoolean("goldSkull"));
        }
        if (nbt.contains("power")) {
            updatePower(nbt.getInt("power"));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("deepslate", isDeepslate());
        nbt.putBoolean("fromFossil", isFromFossil());
        nbt.putBoolean("goldSkull", hasGoldSkull());
        nbt.putInt("power", getPower());
    }

    /**
     * replaces the default attributes with deepslate attributes.
     */
    @SuppressWarnings("ConstantConditions")
    private void createDeepslateAttributes() {
        getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(25);
        getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.23);
        getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(15);
        getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).setBaseValue(5);
        getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.55);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(MAX_POWER, 100 + random.nextInt(10) * 10);
        dataTracker.startTracking(HAS_GOLD_SKULL, random.nextFloat() < 0.1f);
        dataTracker.startTracking(FROM_FOSSIL, false);
        dataTracker.startTracking(DEEPSLATE, random.nextBoolean());
        dataTracker.startTracking(POWER, 0);
        dataTracker.startTracking(CURRENT_ORE, ItemStack.EMPTY);
        dataTracker.startTracking(BLOCK_TARGET_POS, getBlockPos());
        dataTracker.startTracking(CURRENT_ENTITY_STATE, 0);
        dataTracker.startTracking(STANDING, false);
    }

    private <E extends IAnimatable> PlayState statePredicate(AnimationEvent<E> event) {
        if (getEntityState() == STATE_POUNCING) {
            event.getController().transitionLengthTicks = 3;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("leap", true));
            return PlayState.CONTINUE;
        }
        if (getEntityState() == STATE_DIGGING) {
            event.getController().transitionLengthTicks = 5;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("dig", true));
            return PlayState.CONTINUE;
        }
        if (getEntityState() == STATE_SPINNING && isAlive()) {
            event.getController().transitionLengthTicks = 1;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("tail_spin", true));
            return PlayState.CONTINUE;
        }
        if (getEntityState() == STATE_NONE) {
            event.getController().transitionLengthTicks = 10;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("idle", true));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {
        if (isStanding()) {
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("stand", true));
            return PlayState.CONTINUE;
        }
        if (event.isMoving()) {
            if (isSprinting() || getMovementSpeed() > 0.3) {
                event.getController().setAnimation(
                        new AnimationBuilder().addAnimation("sprint", true));
            } else {
                event.getController().setAnimation(
                        new AnimationBuilder().addAnimation("walk", true));
            }
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this,
                "state", 10, this::statePredicate));
        animationData.addAnimationController(new AnimationController<>(this,
                "move", 10, this::movementPredicate));
    }

    private <T extends IAnimatable> void particleListener(ParticleKeyFrameEvent<T> event) {
//        world.addParticle(new DustParticleEffect(new Vec3f(0.5f, 0.5f, 0.5f), 1.0f));
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        float height = super.getDimensions(pose).height;
        float width = super.getDimensions(pose).width;
        if (getPower() < 0) {
            height += getPower() / 1000.0f;
            width += getPower() / 700.0f;
        }
        if (isStanding()) {
            return EntityDimensions.changing(width, height * 2);
        } else {
            return EntityDimensions.changing(width, height);
        }
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.75f;
    }

    @Override
    protected float getJumpVelocity() {
        return (0.42f + getPower() / 4000.0f) * this.getJumpVelocityMultiplier();
    }

    @Override
    protected int computeFallDamage(float fallDistance, float damageMultiplier) {
        return (int) (super.computeFallDamage(fallDistance, damageMultiplier) * 0.25);
    }

    @Override
    public void lookAtEntity(Entity targetEntity, float maxYawChange, float maxPitchChange) {
        if (isOnGround()) {
            super.lookAtEntity(targetEntity, maxYawChange, maxPitchChange);
        } else {
            super.lookAtEntity(targetEntity, maxYawChange * 0.25f, maxPitchChange * 0.25f);
        }
    }

    @Override
    public void lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
        super.lookAt(anchorPoint, target);
    }

    @Override
    public boolean collides() {
        return super.collides() && collides;
    }

    /**
     * Sets whether this entity can collide with other entities or not.
     */
    public void setCollides(boolean collides) {
        this.collides = collides;
    }

    protected void leap(Vec3d direction, float hMultiplier, float vMultiplier) {
        addVelocity(direction.x * hMultiplier, direction.y * vMultiplier, direction.z * hMultiplier);
    }

    /**
     * Plays a moving sound constantly from the moment the mob spawns, until it dies.
     */
    @Override
    public void readFromPacket(MobSpawnS2CPacket packet) {
        super.readFromPacket(packet);
        RubroActiveSoundInstance.playSound(this);
    }

    @Override
    public void tick() {
        super.tick();
        if (getTarget() != null) {
            setGlowing(EarthUtil.isEntityLookingAtEntity(getTarget(), this));
        }
        if (age % 20 == 0) {
            calculateDimensions();
            if (getPower() > 0) {
                updatePower(getPower() - 1);
            }
        }
        for (BlockPos pos : ignoredBlocks.keySet()) {
            //A rubro can dig up the same redstone ore block once every 5 minutes.
            if (age - ignoredBlocks.get(pos) > 6000) {
                ignoredBlocks.remove(pos);
                break;
            }
        }
    }

    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        System.out.println("Power: " + getPower());
        ActionResult result = super.interactAt(player, hitPos, hand);
        if (world.isClient) return result;
        ItemStack mainStack = player.getMainHandStack();
        ItemStack offStack = player.getOffHandStack();
        if (hand == Hand.MAIN_HAND && getPower() < getMaxPower()) {
            int pow = 0;
            if (mainStack.isOf(Items.REDSTONE)) {
                pow = 1;
            } else if (mainStack.isOf(Items.REDSTONE_BLOCK)) {
                pow = 9;
            }
            if (pow > 0) {
                if (!player.isCreative()) {
                    mainStack.decrement(1);
                }
                updatePower(getPower() + pow);
            }
            return result;
        }
        return result;
    }

    @Override
    public boolean canSpawn(WorldView world) {
        /*for (int x = -5; x < 5; x++) {
            for (int y = -5; y < 5; y++) {
                for (int z = -5; z < 5; z++) {
                    if (isBlockPowering(world.getBlockState(new BlockPos(
                            getX() + x, getY() + y, getZ() + z)).getBlock())) {
                        return super.canSpawn(world);
                    }
                }
            }
        }*/
//        if (getY() > -60 && getY() < 15) {
        return super.canSpawn(world);
//        }
//        return false;
    }

    @Override
    public int getSafeFallDistance() {
        return super.getSafeFallDistance() * 3;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource)
                || damageSource.equals(DamageSource.ON_FIRE)
                || damageSource.equals(DamageSource.IN_FIRE);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isBaby() ? null : EarthboundSounds.RUBRO_AMBIENT;
    }

    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, this.getSoundVolume(), isDeepslate() ? 0.85f : 1.0f);
        }
        /*if (getPower() > getMaxPower() * 0.4 && random.nextBoolean()) {
            this.playSound(EarthboundSounds.RUBRO_ACTIVE, 0.25f, 0.8f + random.nextFloat() / 5);
        }*/
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return EarthboundSounds.RUBRO_HURT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (random.nextDouble() < 0.1) {
            this.playSound(EarthboundSounds.RUBRO_CREAK, 0.5f, 0.8f + random.nextFloat() / 2.5f);
        }
        super.playStepSound(pos, state);
    }

    /**
     * Rubros lose 1 power upon being hit, and have a 10% chance of dropping 1 redstone.
     * When they drop 1 redstone, they lose 20 power.
     */
    @Override
    public boolean damage(DamageSource source, float amount) {
        updatePower(getPower() - 1);
        if (!world.isClient) {
            this.playSound(EarthboundSounds.RUBRO_CREAK, 0.5f, 0.8f + random.nextFloat() / 2.5f);
            if (getPower() > getMaxPower() * 0.4 && random.nextDouble() < 0.1) {
                System.out.println("YO");
                ItemEntity item = new ItemEntity(world, getX(), getY() + 0.5, getZ(), Items.REDSTONE.getDefaultStack());
                item.setStack(Items.REDSTONE.getDefaultStack());
                item.setToDefaultPickupDelay();
                updatePower(getPower() - 20);
                world.spawnEntity(item);
            }
        }
        return super.damage(source, amount);
    }

    public static boolean checkMobSpawn(EntityType<? extends MobEntity> type, WorldAccess world,
                                        SpawnReason spawnReason, BlockPos pos, Random random) {
//        System.out.println(pos.getY());
//        System.out.println("spawnReason = " + spawnReason);
        if (spawnReason == SpawnReason.NATURAL && pos.getY() > -60 && pos.getY() < 15) {
//            System.out.println("pos = " + pos);
            return canMobSpawn(type, world, spawnReason, pos, random);
        }
        return false;
    }

    @Override
    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
//        System.out.println("pos = ");
        return super.canSpawn(world, spawnReason);
    }

    /**
     * Checks if the current block can power up a Rubro.
     *
     * @param block the block to check
     * @return if the block is powering
     */
    private boolean isBlockPowering(Block block) {
        return block.equals(Blocks.REDSTONE_ORE) || block.equals(Blocks.DEEPSLATE_REDSTONE_ORE) ||
                block.equals(EarthboundBlocks.REDSTONE_FOSSIL_BLOCK) ||
                block.equals(EarthboundBlocks.GILDED_REDSTONE_FOSSIL_BLOCK) ||
                block.equals(EarthboundBlocks.DEEPSLATE_REDSTONE_FOSSIL_BLOCK) ||
                block.equals(EarthboundBlocks.DEEPSLATE_GILDED_REDSTONE_FOSSIL_BLOCK);
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    public int getMaxPower() {
        return dataTracker.get(MAX_POWER);
    }

    private void setMaxPower(int maxPower) {
        dataTracker.set(MAX_POWER, maxPower);
    }

    public boolean isFromFossil() {
        return dataTracker.get(FROM_FOSSIL);
    }

    private void setFromFossil(boolean fromFossil) {
        dataTracker.set(FROM_FOSSIL, fromFossil);
    }

    public boolean hasGoldSkull() {
        return dataTracker.get(HAS_GOLD_SKULL);
    }

    private void setGoldSkull(boolean goldSkull) {
        dataTracker.set(HAS_GOLD_SKULL, goldSkull);
    }

    public boolean isDeepslate() {
        return dataTracker.get(DEEPSLATE);
    }

    private void setDeepslate(boolean deepslate) {
        dataTracker.set(DEEPSLATE, deepslate);
    }

    public int getPower() {
        return dataTracker.get(POWER);
    }

    /**
     * Updates this Rubro's, setting it to the provided amount.
     * Additionally, it updates the current attribute modifiers with new ones.
     *
     * @param power the new power
     */
    @SuppressWarnings("ConstantConditions")
    public void updatePower(int power) {
        dataTracker.set(POWER, Math.max(power, 0));
        EntityAttributeInstance instance = this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        instance.removeModifier(POWER_DAMAGE_BOOST_ID);
        instance.addTemporaryModifier(
                new EntityAttributeModifier(POWER_DAMAGE_BOOST_ID, "power_damage_boost",
                        1 + getPower() / 150.0, EntityAttributeModifier.Operation.MULTIPLY_BASE));

        instance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        instance.removeModifier(POWER_SPEED_BOOST_ID);
        instance.addTemporaryModifier(
                new EntityAttributeModifier(POWER_SPEED_BOOST_ID, "power_speed_boost",
                        getPower() / 1000.0, EntityAttributeModifier.Operation.ADDITION));

        instance = this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE);
        instance.removeModifier(POWER_FOLLOW_RANGE_ID);
        instance.addTemporaryModifier(
                new EntityAttributeModifier(POWER_FOLLOW_RANGE_ID, "power_follow_range",
                        getPower() / 80.0, EntityAttributeModifier.Operation.MULTIPLY_BASE));

    }

    public BlockPos getBlockTargetPos() {
        return dataTracker.get(BLOCK_TARGET_POS).mutableCopy();
    }

    public void setBlockTargetPos(BlockPos pos) {
        dataTracker.set(BLOCK_TARGET_POS, pos);
    }

    public int getEntityState() {
        return dataTracker.get(CURRENT_ENTITY_STATE);
    }

    public void setEntityState(int id) {
        dataTracker.set(CURRENT_ENTITY_STATE, id);
    }

    public boolean isStanding() {
        return dataTracker.get(STANDING);
    }

    public void setStanding(boolean standing) {
        dataTracker.set(STANDING, standing);
    }

    class MoveToRedstoneGoal extends Goal {

        private BlockPos pathDestination;
        private long lastAttemptTime;
        private final int startDigTicks;
        private int digTicks;
        private final int cooldown;

        public MoveToRedstoneGoal(int cooldown, int digTicks) {
            setControls(EnumSet.of(Control.MOVE, Control.LOOK));
            this.cooldown = cooldown;
            this.digTicks = digTicks;
            this.startDigTicks = digTicks;
            pathDestination = null;
            lastAttemptTime = world.getTime() - cooldown + 10;
        }

        /**
         * Whether the goal can start or not.
         * For the goal to start:
         * - At least {@link #cooldown} must have passed since the last attempt.
         * - The rubro must not be following a path
         * - The rubro's state must be: {@link #STATE_NONE}
         * - the Rubro looks for a valid powering block. If it's found, the goal starts.
         * - If the rubro's power is below 40% of its max, it will start the goal. Otherwise...
         * - It will perform a random check. The higher the power, the lower the chance of success
         * @return true if the goal can start
         */
        @Override
        public boolean canStart() {
            if (world.getTime() - lastAttemptTime < cooldown) {
                return false;
            }
            lastAttemptTime = world.getTime();
            if (getNavigation().isFollowingPath()) {
                return false;
            }
            if (getPower() > getMaxPower() * 0.4
                    && random.nextInt(getPower()) != 0
                    && getHealth() > getMaxHealth() / 3) {
                return false;
            }
            AdvancedBlockPos advPos = findValidPoweringBlock();
            if (advPos == null) {
                return false;
            }
            if (ignoredBlocks.containsKey(advPos.getPos())) {
                return false;
            }
            boolean start = false;
            if (getEntityState() == STATE_NONE) {
                if (getPower() < getMaxPower() * 0.4) {
                    start = true;
                } else {
                    start = random.nextInt(getPower()) == 0;
                }
            }
            if (start) {
                pathDestination = advPos.getPos();
                return true;
            }
            return false;
        }

        /**
         * Any logic related to the goal's start should be written here.
         * P.S.: Spawning a marker entity and having the rubro path to it is a temporary issue to
         * MC-245865 (https://bugs.mojang.com/browse/MC-245865)
         */
        @Override
        public void start() {
            BlockPos destination = pathDestination;
            Entity entity = EntityType.MARKER.create(world);
            if (entity != null) {
                Vec3d pos = new Vec3d(destination.getX() + 0.5, destination.getY() + 1, destination.getZ() + 0.5);
                entity.setPos(pos.x, pos.y, pos.z);
                world.spawnEntity(entity);
                getNavigation().startMovingTo(entity, 1.00f);
            }
        }

        @Override
        public void tick() {
            super.tick();
            BlockPos face = getFaceExposedToAir(getBlockTargetPos());
            if (face == null) {
                return;
            }
            double distance = getEyePos().distanceTo(Vec3d.ofCenter(face));
            if (digTicks == startDigTicks) {
                if (distance < 2.0) {
                    getLookControl().lookAt(getBlockTargetPos().getX() + 0.5,
                            getBlockTargetPos().getY(), getBlockTargetPos().getZ() + 0.5);
                    if (distance > 1.0) {
                        Vec3d vec = EarthMath.dirBetweenVecs(getEyePos(), Vec3d.ofCenter(face).add(0, 1, 0));
                        leap(vec, 0.5f, 0.25f);
                    }
                    pathDestination = null;
                    if (getEyePos().getY() < getBlockTargetPos().getY() &&
                            Math.abs(getEyePos().getY() - getBlockTargetPos().getY()) > 0.13) {
                        setStanding(true);
                    }
                    setEntityState(STATE_DIGGING);
                }
            }
            if (getEntityState() == STATE_DIGGING) {
                dig();
            }
        }

        /**
         * Whether the goal should continue or not. If the Rubro has been recently attacked,
         * the goal should stop right away.
         *
         * @return true if it should continue
         */
        @Override
        public boolean shouldContinue() {
            if (getDamageTracker().wasRecentlyAttacked() && getHealth() > getMaxHealth() / 1.5) {
                return false;
            }
            if (getPower() < getMaxPower() && digTicks > 0) {
                if (getEntityState() == STATE_NONE && !getNavigation().isIdle()) {
                    return true;
                }
                if (getEntityState() == STATE_DIGGING
                        && getEyePos().distanceTo(Vec3d.ofCenter(getFaceExposedToAir(getBlockTargetPos()))) < 2.0) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        /**
         * Pushing the entity slightly forward is a work-around to the following bug:
         * MC-245865 (https://bugs.mojang.com/browse/MC-245865)
         */
        @Override
        public void stop() {
            if (isStanding()) {
                setStanding(false);
            }
            setEntityState(STATE_NONE);
            digTicks = startDigTicks;
            ignoredBlocks.putIfAbsent(getBlockTargetPos(), age);
            super.stop();
        }

        private void dig() {
            float yLevel = getBlockTargetPos().getY();
            if (isStanding()) {
                yLevel -= 10;
            }
            getLookControl().lookAt(getBlockTargetPos().getX() + 0.5, yLevel, getBlockTargetPos().getZ() + 0.5);
            setBodyYaw(getHeadYaw());
            if (digTicks % 4 - Math.round(getPower() / 100.0) == 0) {
                if (!world.isClient) {
                    world.playSound(null, getBlockPos(),
                            isDeepslate() ? SoundEvents.BLOCK_DEEPSLATE_BREAK : SoundEvents.BLOCK_STONE_BREAK,
                            SoundCategory.NEUTRAL, 0.5f, 1.5f);
                }
                playDigParticles();
            }
            //Every 10 ticks, increases the rubro's power and heals it if damaged. Also plays particles
            if (digTicks % 10 == 0) {
                updatePower(getPower() + 10);
                if (getHealth() < getMaxHealth()) {
                    heal(2);
                }
            }
            if (digTicks % 15 == 0) {
                if (!world.isClient) {
                    world.playSound(null, getBlockPos(), EarthboundSounds.RUBRO_CHARGE,
                            SoundCategory.NEUTRAL, 0.25f, 0.9f + random.nextFloat() / 5.0f);
                }
                playPowerupParticles();
            }
            digTicks--;
        }

        /**
         * Selects a random solid block in a radius, and if any of its faces are exposed to air then the
         * block pos is returned. Otherwise, null is returned
         */
        private AdvancedBlockPos findValidPoweringBlock() {
            BlockPos origin = new BlockPos(getEyePos());
            Iterable<BlockPos> iterable = BlockPos.iterateOutwards(origin, 10, 10, 10);
            for (BlockPos pos : iterable) {
                if (isBlockPowering(world.getBlockState(pos).getBlock()) && !ignoredBlocks.containsKey(pos)) {
                    if (world.getEntitiesByType(EarthboundEntities.RUBRO, Box.of(Vec3d.ofCenter(pos),
                            2, 2, 2), rubroEntity -> rubroEntity.getPower() >= 0 &&
                            !rubroEntity.equals(RubroEntity.this) && !rubroEntity.isDead()).size() == 0) {
                        AdvancedBlockPos bestPos = calculateBestTargetPos(pos);
                        if (bestPos != null) {
                            return bestPos;
                        }
                    }
                }
            }
            return null;
        }

        /**
         * Returns the best target location to reach a block. Checks that:
         * - The block has a face exposed to air
         * - The face exposed does not have air two blocks under
         * Then, if the face is not the top or bottom, it means that it is on either cardinal side:
         * - If one block beneath the cardinal face is air, the valid position is that one
         * - If not, the valid location is the cardinal face.
         */
        private AdvancedBlockPos calculateBestTargetPos(BlockPos pos) {
            if (getFaceExposedToAir(pos) != null
                    && !world.isAir(getFaceExposedToAir(pos).down(2))) {
                AdvancedBlockPos advPos = new AdvancedBlockPos(pos);
                if (!getFaceExposedToAir(pos).equals(pos.mutableCopy().down())
                        && !getFaceExposedToAir(pos).equals(pos.mutableCopy().up())) {
                    if (world.isAir(getFaceExposedToAir(pos).down())) {
                        advPos = new AdvancedBlockPos(getFaceExposedToAir(pos).down(2));
                    } else {
                        advPos = new AdvancedBlockPos(getFaceExposedToAir(pos).down());
                    }
                }
                setBlockTargetPos(pos);
                return advPos;
            }
            return null;
        }

        /**
         * Checks whether the given BlockPos has any face which is exposed to air.
         * Prioritizes the top first, following each cardinal direction and finally under.
         *
         * @param pos the position of the block to check
         * @return the air BlockPos adjacent to the given position
         */
        public BlockPos getFaceExposedToAir(BlockPos pos) {
            if (world.getBlockState(pos.mutableCopy().up()).isAir()) {
                return pos.mutableCopy().up();
            } else if (world.getBlockState(pos.mutableCopy().north()).isAir()) {
                return pos.mutableCopy().north();
            } else if (world.getBlockState(pos.mutableCopy().east()).isAir()) {
                return pos.mutableCopy().east();
            } else if (world.getBlockState(pos.mutableCopy().south()).isAir()) {
                return pos.mutableCopy().south();
            } else if (world.getBlockState(pos.mutableCopy().west()).isAir()) {
                return pos.west();
            } else if (world.getBlockState(pos.mutableCopy().down()).isAir()) {
                return pos.mutableCopy().down();
            } else {
                return null;
            }
        }

        /**
         * Plays the particles related to rubro's redstone digging.
         */
        private void playDigParticles() {
            Vec3d particleLoc = getEyePos();
            Vec3d dir = dirBetweenVecs(particleLoc,
                    Vec3d.ofCenter(getBlockTargetPos().mutableCopy().setY((int) getEyeY())));
            particleLoc = particleLoc.add(dir.multiply(0.8, -0.6, 0.8));
            ((ServerWorld)world).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK,
                            world.getBlockState(getBlockTargetPos())),
                    particleLoc.x,
                    particleLoc.y,
                    particleLoc.z,
                    10, 0, 0, 0, 0);
        }

        private void playPowerupParticles() {
            for (int i = 0; i < 10; i++) {
                ((ServerWorld) world).spawnParticles(/*DustParticleEffect.DEFAULT*/EarthboundParticles.RUNE, getParticleX(0.5),
                        getRandomBodyY(), getParticleZ(0.5), 1, 0, 0, 0, 0);
            }
        }
    }

    class RubroAttackGoal extends Goal {

        //the world time when the goal was last attempted
        private long lastAttemptTime;
        //the world time when the goal last pounced
        private long lastPounceTime;
        //the world time when the goal last spun
        private long lastSpinTime;
        //whether the rubro has airstruck yet or not during a pounce
        private boolean hasAirStruck = false;
        //the pounce's cooldown
        private final int spinCooldown = 100;
        //the pounce's cooldown
        private final int pounceCooldown = 120;
        //how many ticks the entity can be idle before the goal is stopped
        private final int maxIdleTime = 30;
        //how many ticks are left till the spin is over
        private int tailSpinTime = 0;
        //how many ticks the entity has been idle for
        private int idleTime = 0;
        //the default minimum leap distance
        private final double defaultMinLeapDistance = 3.0;
        //the default maximum leap distance
        private final double defaultMaxLeapDistance = 4.0;

        public RubroAttackGoal() {
            setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
            lastPounceTime = world.getTime() - calculatePounceCooldown() + 30;
            lastPounceTime = world.getTime();
        }

        /**
         * - This goal can only be attempted once every 100 ticks.
         * - If the Rubro's power is lower than 40% of the max power,
         *   returns false.
         * - It checks that the rubro has a target, and it is not dead.
         * - Then, it looks for a path to get to the target entity. If it is not null, it returns true...
         * - Otherwise, it checks if the target is at leaping distance. If it is, returns true.
         */
        @Override
        public boolean canStart() {
            if (world.getTime() - lastAttemptTime < 20) {
                return false;
            }
            lastAttemptTime = world.getTime();
            if (getPower() < getMaxPower() * 0.4) {
                return false;
            }
            if (getTarget() == null) {
                return false;
            }
            if (getTarget().isDead()) {
                 return false;
            }
            if (getEntityState() != STATE_NONE) {
                return false;
            }
            if (world.getTime() - lastPounceTime < pounceCooldown) {
                return false;
            }
            return true;
        }

        @Override
        public void start() {
            getNavigation().startMovingTo(getTarget(), 1);
            setAttacking(true);
        }

        /**
         * Returns false if:
         * - The rubro's power is below 40%
         * - The target is not null
         * - The target is not dead
         * - The target is in walk range
         * - The target is not in spectator or creative mode
         * - The rubro has not been idle for longer than {@link #maxIdleTime} and its state is not pouncing or spinning
         * - The rubro's spin attack is on cooldown
         */
        @Override
        public boolean shouldContinue() {
            LivingEntity target = getTarget();
            if (getPower() < getMaxPower() * 0.4) {
                return false;
            }
            if (target == null) {
                return false;
            }
            if (target.isDead()) {
                return false;
            }
            if (!isInWalkTargetRange(target.getBlockPos())) {
                return false;
            }
            if (target.isSpectator() || (target instanceof PlayerEntity && ((PlayerEntity)target).isCreative())) {
                return false;
            }
            if (idleTime >= maxIdleTime && getEntityState() != STATE_POUNCING && getEntityState() != STATE_SPINNING) {
                return false;
            }
            //If the spin attack is on cooldown, it means it has been executed at least once.
            if (EarthUtil.isOnCooldown(world.getTime(), lastSpinTime, spinCooldown)) {
                return false;
            }
            return true;
        }

        @Override
        public void stop() {
            LivingEntity target = getTarget();
            if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(target)) {
                setTarget(null);
            }
            if (isSprinting()) {
                setSprinting(false);
            }
            setAttacking(false);
            getNavigation().stop();
            setCollides(true);
            if (hasAirStruck) {
                hasAirStruck = false;
            }
            setEntityState(STATE_NONE);
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        /**
         * Makes the rubro look at the target entity. Then, it may pounce at it if:
         * - The target can be seen
         * - The target distance is between two values
         * - The target is not 3 blocks higher than the Rubro
         * - The Rubro has not leaped yet.
         *
         * Then, it may damage it if:
         * - The Rubro is not on the ground
         * - The Rubro is 2 blocks or less from the target
         * - The Rubro has not damaged the target yet
         * The target damaged this way gets the Rubro's velocity applied to them IF they dont block the damage.
         * If they do, the rubro is knocked back instead.
         */
        @Override
        public void tick() {
            LivingEntity target = getTarget();
            updateIdleTime();
            double eyeToFootDistance = getEyePos().distanceTo(getTarget().getPos());
            if (isPounceOnCooldown() && isOnGround() && getEntityState() == STATE_POUNCING) {
                setEntityState(STATE_NONE);
                hasAirStruck = false;
            }
            setSprinting(eyeToFootDistance > calculateMaxLeapDistance() && !getNavigation().isIdle());
            //If the rubro is spinning, the tail spin time should decrease by 1 each tick.
            if (target == null) {
                return;
            }
            if (isOnGround()) {
                lookAtEntity(getTarget(), 30.0f, 30.0f);
            }
            //Pounces the target if rubro's not doing anything, is at leap distance and is not on cooldown.
            if (getEntityState() == STATE_NONE
                    && eyeToFootDistance <= calculateMaxLeapDistance()
                    && eyeToFootDistance >= defaultMinLeapDistance
                    && canSee(target)
                    && !isPounceOnCooldown() &&
                    EarthUtil.isEntityLookingAtEntity(RubroEntity.this, getTarget())) {
                pounce();
            }
            //If the rubro is pouncing, strikes the target when close.
            if (getEntityState() == STATE_POUNCING) {
                if (getEyePos().distanceTo(target.getEyePos()) <= 1.5 && !hasAirStruck) {
                    System.out.println("STRIKE");
                    airStrike();
                }
            }
            if (getEntityState() == STATE_NONE || getEntityState() == STATE_SPINNING) {
                //Rubro may ignore the default cooldown if it just pounced its target!
                if ((EarthUtil.isOnCooldown(world.getTime(), lastSpinTime, spinCooldown) &&
                        (EarthUtil.isOnCooldown(world.getTime(), lastPounceTime, pounceCooldown))
                        || Math.abs(getTarget().getY() - getY()) > 2)) {
                    return;
                }
                //If the rubro is too far to spin, it should move to the target. Otherwise, spin away!
                if (eyeToFootDistance < defaultMinLeapDistance) {
                    //If the rubro's state is none, it means the spin has not begun yet Start it!
                    if (getEntityState() == STATE_NONE) {
                        setEntityState(STATE_SPINNING);
                        tailSpinTime = TAIL_SPIN_ANIMATION_DURATION + 1;
                    }
                    attemptTailSpin();
                } else {
                    getNavigation().startMovingTo(getTarget(), 1.0);
                }
            }
            System.out.println("Tail Spin: " + tailSpinTime);
        }

        /**
         * Makes the rubro pounce at the target entity.
         * Will aim either at the eyes or the feet, based on whether the target is currently on the
         * ground or not.
         */
        protected void pounce() {
            setEntityState(STATE_POUNCING);
            Vec3d targetPos = getTarget().isOnGround() ? getTarget().getEyePos() : getTarget().getPos();
            double hyp = getEyePos().distanceTo(targetPos);
            double opp = targetPos.y - getEyeY();
            float sin = (float) Math.sin(opp / hyp);
            float forceMultiplier = 1.22407623745f - sin;
            leap(dirBetweenVecs(getPos(), getTarget().isOnGround() ? getTarget().getEyePos() : getTarget().getPos()),
                    1.3f + getPower() / 500.0f, (1.3f + getPower() / 500.0f) * forceMultiplier);
            setCollides(false);
            lastPounceTime = world.getTime();
            getNavigation().stop();
            setBodyYaw(getHeadYaw());

        }

        @SuppressWarnings("ConstantConditions")
        protected void airStrike() {
            LivingEntity target = getTarget();
            DamageSource source = new EntityDamageSource("rubro", RubroEntity.this) {
                @Override
                public Text getDeathMessage(LivingEntity entity) {
                    String string = "death.attack." + this.name + ".pounce";
                    return new TranslatableText(string, entity.getDisplayName(), this.source.getDisplayName());
                }
            };
            float damage = (float) getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            //If the target is a player, deal default damage. Otherwise, double it!
            target.damage(source, target instanceof PlayerEntity ? damage : damage * 2);
            hasAirStruck = true;
            //Apply knockback to the target hit
            if (target.isOnGround()) {
                target.setVelocity(getVelocity().multiply(1.5, 1, 1.5).add(0, 0.1, 0));
            } else {
                target.setVelocity(getVelocity());
            }
            //If the entity blocked the attack, bounce back the rubro
            if (EarthUtil.hasBlockedMostRecentDamage(target)) {
                setVelocity(getVelocity().negate());
            }
        }

        protected void attemptTailSpin() {
            if (tailSpinTime > 0) {
                tailSpinTime--;
                //Different times at which damage should be dealt (done to match the animation)
                int firstSpinHalfway = (int) (TAIL_SPIN_ANIMATION_DURATION / 3.4);
                int secondSpinHalfway = (int) (TAIL_SPIN_ANIMATION_DURATION / 1.3);
                //Times at which the two spins begin. Used in case the rubro needs to jump before spinning
                int firstSpinBegin = TAIL_SPIN_ANIMATION_DURATION;
                int secondSpinBegin = TAIL_SPIN_ANIMATION_DURATION / 2;
                //If the rubro is on the ground and the target entity is 1+ block above it, then it shoudld jump before
                //doing the tail spin attack
                if (isOnGround() && Math.abs(getY() - getTarget().getY()) > 1 && getTarget().getY() > getY()) {
                    if (tailSpinTime == firstSpinBegin || tailSpinTime == secondSpinBegin) {
                        System.out.println("JUMP");
                        jump();
                    }
                }
                if (tailSpinTime == firstSpinHalfway || tailSpinTime == secondSpinHalfway) {
                    System.out.println("half");
                    Vec3d dir = dirBetweenVecs(getPos(), getTarget().getPos());
                    leap(dir, 0.75f, 0);
                    //Damage entities in a box around the rubro (except for itself)
                    for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, Box.of(getPos(),
                                    defaultMinLeapDistance, 1, defaultMinLeapDistance),
                            e -> !e.equals(RubroEntity.this))) {
                        //Create a new damage source which cannot be blocked by a shield.
                        DamageSource source = new DamageSource("mob") {
                            @Override
                            public Text getDeathMessage(LivingEntity entity) {
                                String string = "death.attack." + this.name;
                                return new TranslatableText(string, entity.getDisplayName(), getDisplayName());
                            }
                        };
                        float damage = (float) (getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 0.5f);
                        //If the target is a player, deal default damage. Otherwise, double it.
                        entity.damage(source, getTarget() instanceof PlayerEntity ? damage : damage * 2);
                        //Applies knockback to the target hit.
                        dir = dirBetweenVecs(getPos(), entity.getPos());
                        entity.setAttacker(RubroEntity.this);
                        entity.setVelocity(dir.multiply(0.5).add(0, 0.15, 0));
                    }
                }
            } else {
                setEntityState(STATE_NONE);
                lastSpinTime = world.getTime();
            }
        }

        /**
         * Returns the maximum leap distance, considering the entity's current power.
         */
        private double calculateMaxLeapDistance() {
            return defaultMaxLeapDistance + getPower() / 100.0;
        }

        private int calculatePounceCooldown() {
            return pounceCooldown + getPower() / 5;
        }

        /**
         * Updates the entity's idle time, if it's navigation is idle. Otherwise, resets it to 0.
         */
        private void updateIdleTime() {
            if (getNavigation().isIdle()) {
                idleTime++;
            } else {
                idleTime = 0;
            }
        }

        private boolean isPounceOnCooldown() {
            return world.getTime() - lastPounceTime < calculatePounceCooldown();
        }
    }

    class RubroEscapeTargetGoal extends EscapeTargetGoal {

        public RubroEscapeTargetGoal() {
            super(RubroEntity.this, 1.0f);
        }

        @Override
        public boolean canStart() {
            if (getTarget() == null) {
                return false;
            }
            if (distanceTo(getTarget()) > 12) {
                return false;
            }
            return super.canStart() && getPower() > getMaxPower() * 0.3;
        }

        @Override
        public void start() {
            setSprinting(true);
            super.start();
        }

        @Override
        public boolean shouldContinue() {
            if (getTarget() == null) {
                return false;
            }
            if (distanceTo(getTarget()) > 16) {
                return false;
            }
            return super.shouldContinue();
        }

        @Override
        public void stop() {
            setSprinting(false);
            super.stop();
        }
    }

    /**
     * Targets players which do not have redstone in their hotbar.
     * This target goal is executed only if the rubro's power is 40% or more of its max.
     */
    class RedstoneTargetGoal extends ActiveTargetGoal<PlayerEntity> {

        public RedstoneTargetGoal() {

            super(RubroEntity.this, PlayerEntity.class, 10, false, true,
                    entity -> entity instanceof PlayerEntity player &&
                    (!EarthUtil.playerHotbarContains(player, Set.of(
                            Items.REDSTONE.getDefaultStack(),
                            Items.REDSTONE_BLOCK.getDefaultStack()))));
        }

        @Override
        public boolean canStart() {
            return super.canStart() && getPower() > getMaxPower() * 0.4;
        }
    }

}
