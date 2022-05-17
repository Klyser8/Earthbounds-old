package com.github.klyser8.earthbounds.entity.mob;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.OriginsCallbacks;
import com.github.klyser8.earthbounds.entity.goal.EscapeAttackerGoal;
import com.github.klyser8.earthbounds.entity.goal.EscapeTargetGoal;
import com.github.klyser8.earthbounds.event.PlayerBlockBreakEventHandler;
import com.github.klyser8.earthbounds.registry.*;
import com.github.klyser8.earthbounds.client.sound.PoweredOutsideSoundInstance;
import com.github.klyser8.earthbounds.util.AdvancedBlockPos;
import com.github.klyser8.earthbounds.util.EarthMath;
import com.github.klyser8.earthbounds.util.EarthUtil;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.*;
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
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.*;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.util.*;

import static com.github.klyser8.earthbounds.util.EarthMath.dirBetweenVecs;

public class RubroEntity extends PathAwareEarthenEntity implements Tameable {

    public static final int POWER_LIMIT = 200;
    private static final int TAIL_SPIN_ANIMATION_DURATION = 36; //in ticks

    private static final UUID POWER_DAMAGE_BOOST_ID = UUID.fromString("e09618ac-395e-47b7-b6a1-b47aa59c92b9");
    private static final UUID POWER_SPEED_BOOST_ID = UUID.fromString("8391a50c-b8d5-44ee-bf77-84e519dbf5b2");
    private static final UUID POWER_FOLLOW_RANGE_ID = UUID.fromString("e31be277-eefe-4a00-b333-5ff68773f0a3");

    private static final TrackedData<Integer> MAX_POWER = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MIN_POWER = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    //Whether the Rubro has a golden skull or not
    private static final TrackedData<Integer> MASK_TYPE_ID = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    //Whether the Rubro was born from a fossil or not
    private static final TrackedData<Boolean> FROM_FOSSIL = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    //Whether the Rubro is made of deepslate or not
    public static final TrackedData<Boolean> DEEPSLATE = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    //How much power the Rubro has left
    private static final TrackedData<Integer> POWER = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    //Whether the rubro is 85% filled with power or not. Used for advancements.
    private static final TrackedData<Boolean> IS_FULLY_CHARGED = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    //The current ore the Rubro is hunting for
    private static final TrackedData<ItemStack> CURRENT_ORE = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.ITEM_STACK);
    //The current target block pos of the entity
    private static final TrackedData<BlockPos> BLOCK_TARGET_POS = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.BLOCK_POS);
    //Whether the Rubro is standing or not.
    private static final TrackedData<Boolean> STANDING = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    //Whether the Rubro is active or not. Only fossilized Rubros have the option to be active or inactive.
    private static final TrackedData<Boolean> ACTIVE = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    //The UUID of the owner, if present.
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.OPTIONAL_UUID);

    private static final byte DIGGING_STATE = 1;
    private static final byte LEAPING_STATE = 2;
    private static final byte SPINNING_STATE = 3;

    private boolean collides = true;
    private final Map<BlockPos, Integer> ignoredBlocks = new HashMap<>();

    public RubroEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, -1.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
                                 @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (getY() < 0) {
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
     * @param maskType the type of mask the rubro has
     * @param startPower the amount of power the rubro has as a baby (any value lower than 0)
     */
    public void initializeFossil(boolean deepslate, RubroMaskType maskType, int startPower, PlayerEntity owner) {
        setFromFossil(true);
        setMaskType(maskType);
        setMinPower(startPower);
        updatePower(startPower, true);
        setDeepslate(deepslate);
        if (isDeepslate()) {
            createDeepslateAttributes();
        }
        setOwner(owner);
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new MoveToRedstoneGoal(60));
        goalSelector.add(1, new RubroAttackGoal());
        goalSelector.add(2, new RubroEscapeTargetGoal());
        //If a deactivated is attacked, it should activate and run away
        goalSelector.add(3, new EscapeAttackerGoal(this, 1.2f) {
            @Override
            public void start() {
                super.start();
                if (RubroEntity.this.isActive()) {
                    setActive(false);
                }
            }

            @Override
            public boolean shouldContinue() {
                return super.shouldContinue() && !RubroEntity.this.isActive();
            }
        });
        goalSelector.add(4, new RubroFollowOwnerGoal());
        goalSelector.add(5, new WanderAroundFarGoal(this, 1.0f) {
            @Override
            public boolean canStart() {
                return super.canStart() && !isActive();
            }

            @Override
            public boolean shouldContinue() {
                return super.shouldContinue() && !isActive();
            }
        });
        goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f) {
            @Override
            public boolean canStart() {
                return super.canStart() && !isActive();
            }

            @Override
            public boolean shouldContinue() {
                return super.shouldContinue() && !isActive();
            }
        });
        goalSelector.add(7, new LookAroundGoal(this) {
            @Override
            public boolean canStart() {
                return super.canStart() && getAnimationState() == DEFAULT_STATE && !isActive();
            }

            @Override
            public boolean shouldContinue() {
                return super.shouldContinue() && !isActive();
            }
        });
        targetSelector.add(0, new RevengeGoal(this, RubroEntity.class) {
            @Override
            public boolean canStart() {
                return super.canStart() && !isBaby() && !isActive();
            }

            @Override
            public boolean shouldContinue() {
                return super.shouldContinue() && !isActive();
            }
        });
        targetSelector.add(1, new RedstoneTargetGoal());
        targetSelector.add(2, new RubiaTargetGoal());
        targetSelector.add(3, new ActiveTargetGoal<>(this, RubroEntity.class, 0, false, true,
                entity -> entity instanceof RubroEntity rubro
                        && !this.isFromFossil()
                        && rubro.isFromFossil()) {
            @Override
            public boolean canStart() {
                return super.canStart() && !isActive() && !isBaby();
            }

            @Override
            public boolean shouldContinue() {
                return super.shouldContinue() && !isActive();
            }
        });
        targetSelector.add(4, new ActiveTargetGoal<>(this, WitchEntity.class, false, true) {
            @Override
            public boolean canStart() {
                return super.canStart() && !isActive() && !isBaby();
            }

            @Override
            public boolean shouldContinue() {
                return super.shouldContinue() && !isActive();
            }
        });
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
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
        if (nbt.contains("Deepslate")) {
            setDeepslate(nbt.getBoolean("Deepslate"));
        }
        if (nbt.contains("FromFossil")) {
            setFromFossil(nbt.getBoolean("FromFossil"));
        }
        if (nbt.contains("MaskType")) {
            setMaskType(RubroMaskType.getFromId(nbt.getInt("MaskType")));
        }
        if (nbt.contains("Power")) {
            updatePower(nbt.getInt("Power"), true);
        }
        if (nbt.contains("MaxPower")) {
            setMaxPower(nbt.getInt("MaxPower"));
        }
        if (nbt.contains("MinPower")) {
            setMinPower(nbt.getInt("MinPower"));
        }
        if (nbt.contains("IsFullyCharged")) {
            setFullyCharged(nbt.getBoolean("IsFullyCharged"));
        }
        if (nbt.contains("IsAsleep")) {
            setActive(nbt.getBoolean("IsAsleep"));
        }
        UUID ownerUuid;
        if (nbt.containsUuid("Owner")) {
            ownerUuid = nbt.getUuid("Owner");
        } else {
            String string = nbt.getString("Owner");
            ownerUuid = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
        }
        if (ownerUuid != null) {
            setOwnerUuid(ownerUuid);
        }
        if (nbt.contains("Asleep")) {
            this.setActive(nbt.getBoolean("Asleep"));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Deepslate", isDeepslate());
        nbt.putBoolean("FromFossil", isFromFossil());
        nbt.putInt("MaskType", getMaskType().getId());
        nbt.putInt("Power", getPower());
        nbt.putInt("MaxPower", getMaxPower());
        nbt.putInt("MinPower", getMinPower());
        nbt.putBoolean("IsFullyCharged", isFullyCharged());
        nbt.putBoolean("IsAsleep", isActive());
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
        nbt.putBoolean("Asleep", isActive());
    }

    /**
     * replaces the default attributes with deepslate attributes.
     */
    @SuppressWarnings("ConstantConditions")
    private void createDeepslateAttributes() {
        getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(4.5);
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
        dataTracker.startTracking(MIN_POWER, 0);
        dataTracker.startTracking(MASK_TYPE_ID, 0);
        dataTracker.startTracking(FROM_FOSSIL, false);
        dataTracker.startTracking(DEEPSLATE, false);
        dataTracker.startTracking(POWER, 0);
        dataTracker.startTracking(IS_FULLY_CHARGED, false);
        dataTracker.startTracking(CURRENT_ORE, ItemStack.EMPTY);
        dataTracker.startTracking(BLOCK_TARGET_POS, getBlockPos());
        dataTracker.startTracking(STANDING, false);
        this.dataTracker.startTracking(ACTIVE, false);
        this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
    }

    private <E extends IAnimatable> PlayState statePredicate(AnimationEvent<E> event) {
        if (getAnimationState() == LEAPING_STATE) {
            event.getController().transitionLengthTicks = 3;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("leap", true));
            return PlayState.CONTINUE;
        }
        if (getAnimationState() == DIGGING_STATE) {
            event.getController().transitionLengthTicks = 5;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("dig", true));
            return PlayState.CONTINUE;
        }
        if (getAnimationState() == SPINNING_STATE && isAlive()) {
            event.getController().transitionLengthTicks = 1;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("tail_spin", true));
            return PlayState.CONTINUE;
        }
        if (getAnimationState() == DEFAULT_STATE && !isActive()) {
            event.getController().transitionLengthTicks = 10;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("idle", true));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {
        if (isStanding() && !isActive()) {
            event.getController().transitionLengthTicks = 10;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("stand", true));
            return PlayState.CONTINUE;
        }
        if (isActive()) {
            event.getController().transitionLengthTicks = 10;
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("sleep", true));
            return PlayState.CONTINUE;
        }
        if (event.isMoving()) {
            event.getController().transitionLengthTicks = 3;
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

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        float height = super.getDimensions(pose).height;
        float width = super.getDimensions(pose).width;
        /*if (getPower() < 0) {
            height += getPower() / 1000.0f;
            width += getPower() / 1000.0f;
        }*/
        if (isStanding()) {
            return EntityDimensions.changing(width * 0.75f, height * 2);
        } else {
            return EntityDimensions.changing(width, height);
        }
    }

    @Override
    public boolean isBaby() {
        return getPower() < 0;
    }

    @Override
    public float getScaleFactor() {
        if (isBaby()) {
            return 1.0f + getPower() / 1000.0f;
        } else {
            return super.getScaleFactor();
        }
    }

    @Override
    public float getSoundPitch() {
        float pitch = 1.0f;
        if (isDeepslate()) {
            pitch -= 0.2f;
        }
        if (isBaby()) {
            pitch -= getPower() / 1000f;
        }
        return pitch;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.75f;
    }

    @Override
    protected float getJumpVelocity() {
        float materialMultiplier = isDeepslate() ? 0.0f : 0.05f;
        float powerBonus = isBaby() ? 0 : getPower() / 4000.0f;
        return (0.42f + powerBonus) * this.getJumpVelocityMultiplier() + materialMultiplier;
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
    public boolean collides() {
        return super.collides() && collides;
    }

    /**
     * Sets whether this entity can collide with other entities or not.
     */
    public void setCollides(boolean collides) {
        this.collides = collides;
    }

    /**
     * Plays a moving sound constantly from the moment the mob spawns, until it dies.
     */
    @Override
    public void readFromPacket(MobSpawnS2CPacket packet) {
        super.readFromPacket(packet);
        PoweredOutsideSoundInstance.playSound(this);
    }

    @Override
    public void tick() {
        super.tick();
        if (!world.isClient && age % 5 == 0 && getPower() > getMaxPower() * 0.4) {
            playCrackleParticles(random.nextInt(3) + 1);
        }
        if (age % 20 == 0) {
            calculateDimensions();
            if ((age % 100 == 0 || !isFromFossil()) && getPower() > 0) {
                updatePower(getPower() - 1, false);
            }
        }
        if (age % 100 == 0) {
            for (BlockPos pos : ignoredBlocks.keySet()) {
                //A rubro can dig up the same redstone ore block once every 30 seconds.
                if (age - ignoredBlocks.get(pos) > 600) {
                    ignoredBlocks.remove(pos);
                    break;
                }
            }
        }
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ActionResult result = super.interactMob(player, hand);
        boolean wasBaby = isBaby();
        if (result.isAccepted()) {
            return result;
        }
        ItemStack stack = player.getStackInHand(hand);
        boolean isStackRedstone = false;
        int powIncrease = 0;
        float healAmount = 0;
        if (stack.isOf(Items.REDSTONE)) {
            powIncrease = 1;
            isStackRedstone = true;
        } else if (stack.isOf(Items.REDSTONE_BLOCK)) {
            powIncrease = 9;
            isStackRedstone = true;
        } else if (stack.isOf(EarthboundItems.PRIMORDIAL_REDSTONE)) {
            powIncrease = 40;
            isStackRedstone = true;
            healAmount = 10;
        } else if (stack.isOf(EarthboundItems.COBBLED_PEBBLE) || stack.isOf(EarthboundItems.ANDESITE_PEBBLE)
                || stack.isOf(EarthboundItems.DIORITE_PEBBLE) || stack.isOf(EarthboundItems.GRANITE_PEBBLE)
                || stack.isOf(EarthboundItems.DEEPSLATE_PEBBLE)) {
            powIncrease = 5;
            isStackRedstone = true;
            healAmount = 2;
        } else if (stack.isOf(EarthboundItems.REDSTONE_PEBBLE)) {
            powIncrease = 25;
            isStackRedstone = true;
            healAmount = 4;
        } else if (stack.isOf(EarthboundItems.BLUSHED_FLINTS)
                || stack.isOf(EarthboundItems.CRIMSON_QUARTZ) || stack.isOf(EarthboundItems.RED_BRICK)) {
            powIncrease = 50;
            isStackRedstone = true;
            healAmount = 6;
        } else if (stack.isOf(EarthboundItems.POWERED_BEETROOT)) {
            powIncrease = 25;
            isStackRedstone = true;
            healAmount = 5;
        }
        //If health + heal > max health, heal until max health. Otherwise, heal by heal amount.
        healAmount = Math.min(healAmount, getMaxHealth() - getHealth());
        if (healAmount > 0 && getHealth() < getMaxHealth()) {
            heal(healAmount);
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
            return ActionResult.success(true);
        }
        if (world.isClient) {
            boolean boolResult = isOwner(player) || isFromFossil() || isStackRedstone && getPower() + powIncrease / 2 < getMaxPower();
            return boolResult ? ActionResult.CONSUME : ActionResult.PASS;
        }
        if (isStackRedstone) {
            if (getPower() + powIncrease / 2 < getMaxPower()) {
                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }
                updatePower(getPower() + powIncrease, false);
                //Advancement gets triggered if the rubro was a baby before being fed, and is now an adult.
                if (wasBaby && !isBaby() && player instanceof ServerPlayerEntity serverPlayer && player.equals(getOwner())) {
                    EarthboundsAdvancementCriteria.GROW_UP_RUBRO.trigger(serverPlayer);
                }
                playSound(EarthboundSounds.ENTITY_EAT_REDSTONE,0.35f + powIncrease / 50f, getSoundPitch() + random.nextFloat() / 5.0f);
                playSound(EarthboundSounds.ENTITY_CHARGE, 0.35f + powIncrease / 50f, getSoundPitch() + random.nextFloat() / 5.0f);
                playRedstoneParticles(powIncrease);
                return ActionResult.success(true);
            }
        }
        if (!result.isAccepted() && isFromFossil() && isOwner(player) && !isStackRedstone) {
            setActive(!isActive());
            return ActionResult.success(true);
        }
        return ActionResult.FAIL;
    }

    @Override
    public void heal(float amount) {
        super.heal(amount);
        for (int i = 0; i < amount; i++) {
            if (world.isClient && isFromFossil()) {
                world.addParticle(EarthboundParticles.SMALL_HEART,
                        getParticleX(0.5), getEyeY() + 0.1 + (random.nextFloat() - 0.5f) / 5,
                        getParticleZ(0.5), 255, 0, 0);
            } else if (!world.isClient && !isFromFossil()) {
                ((ServerWorld) world).spawnParticles(EarthboundParticles.SMALL_HEART, getParticleX(0.5),
                        getEyeY() + 0.1 + (random.nextFloat() - 0.5f) / 5, getParticleZ(0.5),
                        0, 122, 0, 0, 1);
            }
        }
    }

    @Override
    public int getSafeFallDistance() {
        return super.getSafeFallDistance() * 3;
    }

    @Override
    public boolean cannotDespawn() {
        return getOwner() != null;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return getOwner() == null;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return EarthboundSounds.RUBRO_AMBIENT;
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        super.playSound(sound, volume, pitch);
    }

    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, this.getSoundVolume(), isDeepslate() ? getSoundPitch() - 0.15f : getSoundPitch());
        }
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return EarthboundSounds.RUBRO_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return EarthboundSounds.RUBRO_DEATH;
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
        int powerLost = 1;
        if (!world.isClient) {
            if (!Earthen.isDamagePickaxe(source)) {
                amount = Earthen.handleDamage(source, this, amount);
            }
            this.setActive(isActive());
        }
        if (!world.isClient) {
            if (getPower() > getMaxPower() * 0.4 && random.nextDouble() < amount / 30.0) {
                powerLost += 20;
                playRedstoneParticles(30);
                if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                    ItemEntity item = new ItemEntity(world, getX(), getY() + 0.5, getZ(), Items.REDSTONE.getDefaultStack());
                    item.setStack(Items.REDSTONE.getDefaultStack());
                    item.setToDefaultPickupDelay();
                    world.spawnEntity(item);
                }
            }
            this.playSound(EarthboundSounds.RUBRO_CREAK, 0.5f, 0.8f + random.nextFloat() / 2.5f);
            playRedstoneParticles(5);
        }
        updatePower(getPower() - powerLost, false);
        return super.damage(source, amount);
    }

    /**
     * Checks if the rubro is in the condition to spawn in a given location.
     *
     * @param type The type of the entity
     * @param world The world which the entity may spawn in
     * @param spawnReason the reason of the spawn
     * @param pos the spawn position
     * @return whether the mob is allowed to spawn at the location.
     */
    public static boolean checkMobSpawn(EntityType<? extends MobEntity> type, WorldAccess world,
                                        SpawnReason spawnReason, BlockPos pos, Random random) {
        if ((spawnReason == SpawnReason.NATURAL || spawnReason == SpawnReason.CHUNK_GENERATION)
                && pos.getY() > -60 && pos.getY() < 25) {
            if (world.getLightLevel(pos) > 8) {
                return false;
            }
            for (int x = -10; x < 10; x++) {
                for (int y = -5; y < 5; y++) {
                    for (int z = -10; z < 10; z++) {
                        AdvancedBlockPos redstonePos = new AdvancedBlockPos(
                                new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z));
                        //Block is required to be both powering and have one face exposed to air.
                        if (isBlockPowering(world.getBlockState(redstonePos.getPos()).getBlock())) {
                            boolean isExposedToAir = false;
                            for (BlockPos loopPos : redstonePos.getAllFaces()) {
                                //Block exposed to air may not be below.
                                if (loopPos.equals(redstonePos.down())) {
                                    continue;
                                }
                                if (world.getBlockState(loopPos).isAir()) {
                                    isExposedToAir = true;
                                    break;
                                }
                            }
                            return RubroEntity.canMobSpawn(type, world, spawnReason, pos, random) && isExposedToAir;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the current block can power up a Rubro.
     *
     * @param block the block to check
     * @return if the block is powering
     */
    private static boolean isBlockPowering(Block block) {
        return block.equals(Blocks.REDSTONE_ORE) || block.equals(Blocks.DEEPSLATE_REDSTONE_ORE) ||
                block.equals(EarthboundBlocks.REDSTONE_FOSSIL_BLOCK) ||
                block.equals(EarthboundBlocks.GILDED_REDSTONE_FOSSIL_BLOCK) ||
                block.equals(EarthboundBlocks.DEEPSLATE_REDSTONE_FOSSIL_BLOCK) ||
                block.equals(EarthboundBlocks.DEEPSLATE_GILDED_REDSTONE_FOSSIL_BLOCK);
    }

    private void playRedstoneParticles(int amount) {
        for (int i = 0; i < amount; i++) {
            ((ServerWorld) world).spawnParticles(DustParticleEffect.DEFAULT, getParticleX(0.5),
                    getRandomBodyY(), getParticleZ(0.5), 1, 0, 0, 0, 0);
        }
    }

    private void playCrackleParticles(int amount) {
        for (int i = 0; i < amount; i++) {
            ((ServerWorld) world).spawnParticles(EarthboundParticles.REDSTONE_CRACKLE, getParticleX(0.5),
                    getRandomBodyY(), getParticleZ(0.5), 1, 0, 0, 0, 0);
        }
    }

    public int getMaxPower() {
        return dataTracker.get(MAX_POWER);
    }

    private void setMaxPower(int maxPower) {
        dataTracker.set(MAX_POWER, maxPower);
    }

    public int getMinPower() {
        return dataTracker.get(MIN_POWER);
    }

    private void setMinPower(int minPower) {
        dataTracker.set(MIN_POWER, minPower);
    }

    public boolean isFromFossil() {
        return dataTracker.get(FROM_FOSSIL);
    }

    private void setFromFossil(boolean fromFossil) {
        dataTracker.set(FROM_FOSSIL, fromFossil);
    }

    public boolean isDeepslate() {
        return dataTracker.get(DEEPSLATE);
    }

    private void setDeepslate(boolean deepslate) {
        dataTracker.set(DEEPSLATE, deepslate);
    }

    public RubroMaskType getMaskType() {
        return RubroMaskType.getFromId(dataTracker.get(MASK_TYPE_ID));
    }

    public void setMaskType(RubroMaskType maskType) {
        dataTracker.set(MASK_TYPE_ID, maskType.getId());
    }

    public int getPower() {
        return dataTracker.get(POWER);
    }

    /**
     * Updates this Rubro's, setting it to the provided amount.
     * Additionally, it updates the current attribute modifiers with new ones.
     *
     * @param newPower the new power
     * @param overrides whether updating the power should override all safety checks or not. USE WITH CAUTION!
     */
    @SuppressWarnings("ConstantConditions")
    public void updatePower(int newPower, boolean overrides) {
        if (overrides) {
            dataTracker.set(POWER, newPower);
        } else {
            if (getPower() >= 0 && age > 20) {
                dataTracker.set(POWER, Math.max(newPower, 0));
            } else {
                dataTracker.set(POWER, Math.max(newPower, getMinPower()));
            }
        }
        if (getPower() >= getMaxPower() * 0.85 && !isFullyCharged()) {
            setFullyCharged(true);
        } else if (isFullyCharged() && getPower() < getMaxPower()) {
            setFullyCharged(false);
        }
        if (isBaby()) {
            return;
        }
        EntityAttributeInstance instance = this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        instance.removeModifier(POWER_DAMAGE_BOOST_ID);
        instance.addTemporaryModifier(
                new EntityAttributeModifier(POWER_DAMAGE_BOOST_ID, "power_damage_boost",
                        Math.max(1, (1 + getPower() / 150.0)), EntityAttributeModifier.Operation.MULTIPLY_BASE));

        instance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        instance.removeModifier(POWER_SPEED_BOOST_ID);
        instance.addTemporaryModifier(
                new EntityAttributeModifier(POWER_SPEED_BOOST_ID, "power_speed_boost",
                        getPower() / 1500.0, EntityAttributeModifier.Operation.ADDITION));

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

    public boolean isStanding() {
        return dataTracker.get(STANDING);
    }

    /**
     * Sets whether the Rubro should be standing up (to dig redstone).
     * @param standing if the rubro should stand up
     */
    public void setStanding(boolean standing) {
        dataTracker.set(STANDING, standing);
    }

    public boolean isFullyCharged() {
        return dataTracker.get(IS_FULLY_CHARGED);
    }

    public void setFullyCharged(boolean isMaxPower) {
        dataTracker.set(IS_FULLY_CHARGED, isMaxPower);
    }

    @Override
    public UUID getOwnerUuid() {
        return dataTracker.get(OWNER_UUID).orElse(null);
    }

    @Override
    @Nullable
    public PlayerEntity getOwner() {
        try {
            UUID uUID = this.getOwnerUuid();
            return uUID == null ? null : this.world.getPlayerByUuid(uUID);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    /**
     * Sets the UUID of the owner of the Rubro.
     * Use {@link #setOwner(PlayerEntity)} instead usually
     *
     * @param uuid owner's uuid. May be null.
     */
    public void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    /**
     * Sets the owner of the Rubro.
     *
     * @param player the player who owns the rubro.
     */
    public void setOwner(PlayerEntity player) {
        this.setOwnerUuid(player.getUuid());
    }

    public boolean isOwner(LivingEntity entity) {
        return entity == this.getOwner();
    }

    public AbstractTeam getScoreboardTeam() {
        if (this.isFromFossil()) {
            LivingEntity livingEntity = this.getOwner();
            if (livingEntity != null) {
                return livingEntity.getScoreboardTeam();
            }
        }

        return super.getScoreboardTeam();
    }

    public boolean isTeammate(Entity other) {
        if (this.isFromFossil()) {
            LivingEntity livingEntity = this.getOwner();
            if (other == livingEntity) {
                return true;
            }
            if (livingEntity != null) {
                return livingEntity.isTeammate(other);
            }
        }
        return super.isTeammate(other);
    }

    public void onDeath(DamageSource source) {
        if (!this.world.isClient && this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES)
                && this.getOwner() instanceof ServerPlayerEntity) {
            this.getOwner().sendSystemMessage(this.getDamageTracker().getDeathMessage(), Util.NIL_UUID);
        }
        super.onDeath(source);
    }

    /**
     * Whether the rubro should be curled up or not.
     * A rubro that's curled up won't move (like a dog sitting)
     *
     * @param active if the Rubro should be curled up
     */
    public void setActive(boolean active) {
        dataTracker.set(ACTIVE, active);
        setStanding(active);
    }

    public boolean isActive() {
        return dataTracker.get(ACTIVE);
    }

    /**
     * Method overridden to avoid possible confusion with the
     * {@link #isActive()} method. Avoid using this however!
     *
     * @return {@link #isActive()}
     */
    @Override
    @Deprecated
    public boolean isSleeping() {
        return isActive();
    }

    @Override
    protected void dash(Vec3d direction, float hMultiplier, float vMultiplier) {
        super.dash(direction, hMultiplier, vMultiplier);
    }

    /**
     * Baby rubros should spawn half as many sprinting particles
     */
    @Override
    protected void spawnSprintingParticles() {
        if (isBaby()) {
            if (random.nextBoolean()) super.spawnSprintingParticles();
        } else {
            super.spawnSprintingParticles();
        }
    }

    class MoveToRedstoneGoal extends Goal {

        private BlockPos pathDestination;
        private long lastAttemptTime;
        private final int startDigTicks;
        private int digTicks;
        private final int cooldown;

        public MoveToRedstoneGoal(int cooldown) {
            setControls(EnumSet.of(Control.MOVE, Control.LOOK));
            this.cooldown = cooldown;
            this.digTicks = 100 + random.nextInt(60);
            this.startDigTicks = digTicks;
            pathDestination = null;
            lastAttemptTime = world.getTime() - cooldown + 10;
        }

        /**
         * Whether the goal can start or not.
         * For the goal to start:
         * - The entity's state must be {@link #DEFAULT_STATE}
         * - If the rubro is healthy:
         *   1. Goal must not be on cooldown
         *   2. Rubro must not be already on a path
         *   3. Rubro must be below 40% power OR succeed a random check which is
         *      more likely to fail the more power is stored
         * - If unhealthy, it may skip the 3 checks above.
         * - The rubro must find a valid powering block
         * - The rubro must not have dug the block before
         * @return true if the goal can start
         */
        @Override
        public boolean canStart() {
            boolean isHealthy = getHealth() > getMaxHealth() / 3;
            boolean canStart = true;
            if (getAnimationState() != DEFAULT_STATE) {
                return false;
            }
            if (isFromFossil() || isActive()) {
                return false;
            }
            //Cooldown, current goal and current power are ignored when low on HP.
            if (isHealthy && EarthUtil.isOnCooldown(world.getTime(), lastAttemptTime, cooldown)) {
                return false;
            }
            lastAttemptTime = world.getTime();
            if (isHealthy) {
                if (getNavigation().isFollowingPath()) {
                    return false;
                }
                if (getPower() > getMaxPower() * 0.4) {
                    canStart = random.nextInt(getPower() / 5) == 0;
                }
            }
            if (!canStart) {
                return false;
            }
            AdvancedBlockPos advPos = findValidPoweringBlock();
            if (advPos == null) {
                return false;
            }
            if (ignoredBlocks.containsKey(advPos.getPos())) {
                return false;
            }
            pathDestination = advPos.getPos();
            return true;
        }

        /**
         * Any logic related to the goal's start should be written here.
         * P.S.: Spawning a marker entity and having the rubro path to it is a temporary issue to
         * MC-245865 (https://bugs.mojang.com/browse/MC-245865)
         */
        @Override
        public void start() {
            BlockPos destination = pathDestination;
            if (destination != null) {
                double x = destination.getX() + 0.5;
                double y = destination.getY() + 2;
                double z = destination.getZ() + 0.5;
                getNavigation().startMovingTo(x, y, z, 1.00f);
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
                        dash(vec, 0.5f, 0.25f);
                    }
                    pathDestination = null;
                    if (getEyePos().getY() < getBlockTargetPos().getY() &&
                            Math.abs(getEyePos().getY() - getBlockTargetPos().getY()) > 0.13) {
                        setStanding(true);
                    }
                    setAnimationState(DIGGING_STATE);
                }
            }
            if (getAnimationState() == DIGGING_STATE) {
                dig();
            }
        }

        /**
         * Whether the goal should continue or not. If the Rubro has been recently attacked,
         * the goal should stop right away.
         *
         * Null check required as of this issue: https://github.com/Klyser8/Earthbounds/issues/2.
         *
         * @return true if it should continue
         */
        @Override
        public boolean shouldContinue() {
            if (getDamageTracker().wasRecentlyAttacked() && getHealth() > getMaxHealth() / 1.5) {
                return false;
            }
            if (getPower() < getMaxPower() && digTicks > 0) {
                if (getAnimationState() == DEFAULT_STATE && !getNavigation().isIdle()) {
                    return true;
                }
                if (getBlockTargetPos() == null) {
                    Earthbounds.LOGGER.log(Level.ERROR,
                            "Rubro Entity: " + getUuidAsString() + " attempted scraping a redstone ore" +
                                    " which was null! Happened at location: " + getPos());
                } else {
                    if (getFaceExposedToAir(getBlockTargetPos()) == null) {
                        Earthbounds.LOGGER.log(Level.ERROR,
                                "Rubro Entity: " + getUuidAsString() + " attempted looking for a block's face " +
                                        " exposed to air, and it returned null! Happened at location: " + getPos());
                        return false;
                    }
                    if (getAnimationState() == DIGGING_STATE
                            && getEyePos().distanceTo(Vec3d.ofCenter(getFaceExposedToAir(getBlockTargetPos()))) < 2.0) {
                        return true;
                    }
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
            setAnimationState(DEFAULT_STATE);
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
            //Lights up redstone ore when being dug
            if (world.getBlockState(getBlockTargetPos()).getBlock() instanceof RedstoneOreBlock redstone) {
                redstone.onSteppedOn(world, getBlockTargetPos(),
                        world.getBlockState(getBlockTargetPos()), RubroEntity.this);
            }
            setBodyYaw(getHeadYaw());
            if (digTicks % 4 - Math.round(getPower() / 100.0) == 0) {
                playSound(isDeepslate() ? SoundEvents.BLOCK_DEEPSLATE_BREAK : SoundEvents.BLOCK_STONE_BREAK, 0.5f, 1.5f);
                playDigParticles();
            }
            //Every 10 ticks, increases the rubro's power and heals it if damaged. Also plays particles
            if (digTicks % 10 == 0) {
                updatePower(getPower() + 10, false);
                if (getHealth() < getMaxHealth()) {
                    heal(2);
                }
            }
            if (digTicks % 15 == 0) {
                playSound(EarthboundSounds.ENTITY_CHARGE,0.25f, 0.9f + random.nextFloat() / 5.0f);
                playRedstoneParticles(10);
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
            double xMult = 0.6;
            double yMult = -0.6;
            double zMult = 0.6;
            if (!isStanding()) {
                yMult = getY() >= 0 ? 3.5 : -0.6;
            }
            particleLoc = particleLoc.add(dir.multiply(xMult, yMult, zMult));
            ((ServerWorld)world).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK,
                            world.getBlockState(getBlockTargetPos())),
                    particleLoc.x,
                    particleLoc.y,
                    particleLoc.z,
                    10, 0, 0, 0, 0);
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
        private final int spinCooldown;
        //the pounce's cooldown
        private final int pounceCooldown;
        //how many ticks the entity can be idle before the goal is stopped
        private final int maxIdleTime = 30;
        //how many ticks are left till the spin is over
        private int tailSpinTime = 0;
        //how many ticks the entity has been idle for
        private int idleTime = 0;
        //the default minimum leap distance
        private final double defaultMinLeapDistance = 3.0;
        //the default maximum leap distance
        private final double defaultMaxLeapDistance;

        public RubroAttackGoal() {
            setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
            lastPounceTime = world.getTime() - calculatePounceCooldown() + 30;
            lastPounceTime = world.getTime();
            if (isDeepslate()) {
                this.spinCooldown = 70;
                this.pounceCooldown = 70;
                this.defaultMaxLeapDistance = 4.0;
            } else {
                this.spinCooldown = 50;
                this.pounceCooldown = 50;
                this.defaultMaxLeapDistance = 6.0;
            }
        }

        /**
         * - This goal cant start if the rubro is a baby
         * - This goal can only be attempted once every 20 ticks.
         * - If the Rubro's power is lower than 40% of the max power,
         *   returns false.
         * - It checks that the rubro has a target, and it is not dead.
         * - Then, it looks for a path to get to the target entity. If it is not null, it returns true...
         * - Otherwise, it checks if the target is at leaping distance. If it is, returns true.
         */
        @Override
        public boolean canStart() {
            if (isBaby() || isActive()) {
                return false;
            }
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
            if (getAnimationState() != DEFAULT_STATE) {
                return false;
            }
            if (EarthUtil.isOnCooldown(world.getTime(), lastPounceTime, pounceCooldown)
                    || EarthUtil.isOnCooldown(world.getTime(), lastSpinTime, spinCooldown) ) {
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
         * - The Rubro's deactivated
         * - The rubro's power is below 25%
         * - The target is not null
         * - The target is not dead
         * - The target is in walk range
         * - The target is not in spectator or creative mode
         * - The rubro has not been idle for longer than {@link #maxIdleTime} and its state is not leaping or spinning
         * - The rubro's spin attack is on cooldown
         */
        @Override
        public boolean shouldContinue() {
            LivingEntity target = getTarget();
            if (isActive()) {
                return false;
            }
            if (getPower() < getMaxPower() * 0.25) {
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
            if (idleTime >= maxIdleTime
                    && getAnimationState() != LEAPING_STATE
                    && getAnimationState() != SPINNING_STATE) {
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
            hasAirStruck = false;
            setAnimationState(DEFAULT_STATE);
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
            setSprinting(eyeToFootDistance > calculateMaxLeapDistance() && !getNavigation().isIdle());
            if (target == null) {
                return;
            }
            if (isOnGround()) {
                lookAtEntity(getTarget(), 30.0f, 30.0f);
            }
            //If the pounce is on cooldown, the rubro is on the ground and has a leaping state... It is done leaping.
            //Logic required to be before the pounce in order to work. Weird.
            if (EarthUtil.isOnCooldown(world.getTime(), lastPounceTime, pounceCooldown)
                    && isOnGround() && getAnimationState() == LEAPING_STATE) {
                setAnimationState(DEFAULT_STATE);
            }
            //Pounces the target if rubro's not doing anything, is at leap distance and is not on cooldown.
            //Additionally, it may only pounce if the spin attack is not on cooldown too.
            if (getAnimationState() == DEFAULT_STATE
                    && eyeToFootDistance <= calculateMaxLeapDistance()
                    && eyeToFootDistance >= defaultMinLeapDistance
                    && EarthUtil.canEntityFullySee(RubroEntity.this, target, 32)
                    && !EarthUtil.isOnCooldown(world.getTime(), lastPounceTime, pounceCooldown)
                    && !EarthUtil.isOnCooldown(world.getTime(), lastSpinTime, spinCooldown)
                    && EarthUtil.isEntityLookingAtEntity(RubroEntity.this, getTarget())) {
                pounce();
            }
            //If the rubro is leaping, strikes the target when close.
            if (getAnimationState() == LEAPING_STATE) {
                playRedstoneParticles(3);
                if (getEyePos().distanceTo(target.getEyePos()) <= 1.5 && !hasAirStruck) {
                    airStrike();
                }
            }
            //if the rubro has a null state, and has air struck + is on ground OR
            //is closer than X to player and is not on cooldown, try tail spinning.
            if (getAnimationState() == DEFAULT_STATE
                    && ((hasAirStruck && isOnGround()) || (eyeToFootDistance <= defaultMinLeapDistance && !EarthUtil.isOnCooldown(world.getTime(), lastSpinTime, spinCooldown)))) {
                //Will attempt the tail spin if it is close enough to the target. Otherwise, it will get closer to it.
                if (eyeToFootDistance <= defaultMinLeapDistance) {
                    setAnimationState(SPINNING_STATE);
                    tailSpinTime = TAIL_SPIN_ANIMATION_DURATION + 1;
                } else {
                    getNavigation().startMovingTo(getTarget(), 1.0);
                }
            }
            if (getAnimationState() == SPINNING_STATE) {
                if (tailSpinTime > 0) {
                    tailSpinTime--;
                }
                attemptTailSpin();
            }
        }

        /**
         * Makes the rubro pounce at the target entity.
         * Will aim either at the eyes or the feet, based on whether the target is currently on the
         * ground or not.
         */
        protected void pounce() {
            setAnimationState(LEAPING_STATE);
            Vec3d targetPos = getTarget().getEyePos().add(0, 1, 0);
            double hyp = getEyePos().distanceTo(targetPos);
            double opp = targetPos.y - getEyeY();
            float sin = (float) Math.sin(opp / hyp);
            float forceMultiplier = 1.22407623745f - sin;
            //Deepslate rubros leap slower
            float materialMultiplier = isDeepslate() ? 1.0f : 1.15f;
            dash(dirBetweenVecs(getPos(), targetPos),
                    (1.3f + getPower() / 500.0f) * materialMultiplier,
                    ((1.3f + getPower() / 500.0f) * forceMultiplier) * materialMultiplier);
            setCollides(false);
            lastPounceTime = world.getTime();
            getNavigation().stop();
            setBodyYaw(getHeadYaw());
            playSound(EarthboundSounds.RUBRO_POUNCE,0.5f, 1.4f + random.nextFloat() / 5.0f);
/*            if (!world.isClient) {
                world.playSound(null, getBlockPos(), EarthboundSounds.RUBRO_POUNCE,
                        SoundCategory.HOSTILE, 0.5f, 1.4f + random.nextFloat() / 5.0f);
            }*/
        }

        @SuppressWarnings("ConstantConditions")
        protected void airStrike() {
            LivingEntity target = getTarget();
            DamageSource source = new EntityDamageSource("rubro", /*getOwner() == null ? */RubroEntity.this/* : getOwner()*/) {
                @Override
                public Text getDeathMessage(LivingEntity entity) {
                    String string = "death.attack." + this.name + ".pounce";
                    return new TranslatableText(string, entity.getDisplayName(), this.source.getDisplayName());
                }
            };
            float damage = (float) getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            //If the target is a player, deal default damage. Otherwise, double it!
            damageEntity(target, damage, source);
//            target.setAttacker(getOwner() == null ? RubroEntity.this : getOwner());
            hasAirStruck = true;
            //Apply knockback to the target hit
            if (target.isOnGround()) {
                //Y velocity needs to be between 0.2 and 0.5. Otherwise, the target is barely knocked back
                double yVel = getVelocity().getY();
                if (yVel <= 0.2 || yVel > 0.5) {
                    yVel = 0.3;
                }
                target.setVelocity(getVelocity().add(0.2, yVel, 0.2).multiply(1.5, 1, 1.5)
                        .multiply(1.0 - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)));
            } else {
                target.setVelocity(getVelocity()
                        .multiply(1.0 - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)));
            }
            //If the entity blocked the attack, bounce back the rubro
            if (target.blockedByShield(source)) {
                setVelocity(getVelocity().negate());
            } else {
                if (!world.isClient) {
                    world.playSound(null, new BlockPos(getEyePos()), EarthboundSounds.RUBRO_POUNCE_STRIKE,
                            SoundCategory.HOSTILE, 0.5f, 1.4f + random.nextFloat() / 5.0f);
                }
            }
        }

        protected void attemptTailSpin() {
            if (tailSpinTime > 0) {
                //Different times at which damage should be dealt (done to match the animation)
                int firstSpinHalfway = (int) (TAIL_SPIN_ANIMATION_DURATION / 3.4);
                int secondSpinHalfway = (int) (TAIL_SPIN_ANIMATION_DURATION / 1.3);
                //Times at which the two spins begin. Used in case the rubro needs to jump before spinning
                int firstSpinBegin = TAIL_SPIN_ANIMATION_DURATION;
                int secondSpinBegin = TAIL_SPIN_ANIMATION_DURATION / 2;
                boolean isOutOfReach = Math.abs(getY() - getTarget().getY()) > 0.5;
                Vec3d dir = dirBetweenVecs(getPos(), getTarget().getPos());
                //If the rubro is on the ground and the target entity is 1+ block above it, then it should jump before
                //doing the tail spin attack
                if (tailSpinTime == firstSpinBegin || tailSpinTime == secondSpinBegin) {
                    if (isOnGround()
                            && (Math.abs(getY() - getTarget().getY()) > EarthUtil.calculateJumpHeight(getJumpVelocity())
                            || isOutOfReach)
                            && getTarget().getY() > getY()) {
                        dash(dir, 1.5f, 0);
                        jump();
                    }
                } else if (tailSpinTime == firstSpinBegin - 3 || tailSpinTime == secondSpinBegin - 3) {
                    if (!world.isClient) {
                        world.playSound(null, new BlockPos(getEyePos()), SoundEvents.ENTITY_HORSE_GALLOP,
                                SoundCategory.HOSTILE, 0.25f, 1.7f + random.nextFloat() / 5.0f);
                    }
                } else if (tailSpinTime == firstSpinHalfway || tailSpinTime == secondSpinHalfway) {
                    if (isOnGround()) {
                        dash(dir, 1f, 0);
                    }
                    //Damage entities in a box around the rubro (except for itself)
                    for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, Box.of(getPos(),
                                    defaultMinLeapDistance, 1, defaultMinLeapDistance),
                            e -> !e.equals(RubroEntity.this))) {
                        //Create a new damage source which cannot be blocked by a shield.
                        DamageSource source = new EntityDamageSource("rubro", RubroEntity.this) {
                            @Override
                            public Text getDeathMessage(LivingEntity entity) {
                                String string = "death.attack." + this.name + ".tail_spin";
                                return new TranslatableText(string, entity.getDisplayName(), this.source.getDisplayName());
                            }
                        };
                        float damage = (float) (getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 0.5f);
                        //If the target is a player, deal default damage. Otherwise, double it.
                        damageEntity(entity, damage, source);
                        //Applies knockback to the target hit.
                        dir = dirBetweenVecs(getPos(), entity.getPos());
                        entity.setVelocity(dir.multiply(0.5).add(0, 0.15, 0).multiply(1.0 - entity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)));
                    }
                }
            } else {
                setAnimationState(DEFAULT_STATE);
                lastSpinTime = world.getTime();
            }
        }

        private void damageEntity(LivingEntity entity, float amount, DamageSource source) {
            /*if (getOwner() != null) {
                entity.damage(DamageSource.player(getOwner()), 0);
            }*/
            entity.damage(source, amount);
            if (getOwner() instanceof ServerPlayerEntity owner && entity.isDead()) {
                EarthboundsAdvancementCriteria.KILLED_BY_RUBRO.trigger(owner, entity);
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
    }

    class RubroEscapeTargetGoal extends EscapeTargetGoal {

        public RubroEscapeTargetGoal() {
            super(RubroEntity.this, 1.0f);
        }

        @Override
        public boolean canStart() {
            if (isBaby() || isActive()) {
                return false;
            }
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

    class RubroFollowOwnerGoal extends Goal {

        private final double speed;
        private int updateCountdownTicks;
        private final float maxDistance;
        private final float minDistance;
        private final boolean leavesAllowed;

        public RubroFollowOwnerGoal() {
            this.speed = 1;
            this.minDistance = 10;
            this.maxDistance = 2;
            this.leavesAllowed = true;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (!isFromFossil()) {
                return false;
            }
            if (getOwner() == null) {
                return false;
            }
            if (getOwner().isSpectator()) {
                return false;
            }
            if (isActive()) {
                return false;
            }
            if (squaredDistanceTo(getOwner()) < (double)(this.minDistance * this.minDistance)) {
                return false;
            }
            return true;
        }

        @Override
        public boolean shouldContinue() {
            if (getOwner() == null) {
                return false;
            }
            if (isActive()) {
                return false;
            }
            if (getNavigation().isIdle()) {
                return false;
            }
            return !(squaredDistanceTo(getOwner()) <= (double)(this.maxDistance * this.maxDistance));
        }

        @Override
        public void start() {
            this.updateCountdownTicks = 0;
        }

        @Override
        public void stop() {
            getNavigation().stop();
            if (isSprinting()) {
                setSprinting(false);
            }
        }

        @Override
        public void tick() {
            if (getOwner() == null) {
                return;
            }
            getLookControl().lookAt(getOwner(), 10.0f, getMaxLookPitchChange());
            if (--this.updateCountdownTicks > 0) {
                return;
            }
            this.updateCountdownTicks = this.getTickCount(10);
            if (isLeashed() || hasVehicle()) {
                return;
            }
            if (squaredDistanceTo(getOwner()) >= 24 * 24) {
                tryTeleport();
            } else {
                getNavigation().startMovingTo(getOwner(), this.speed);
                if (squaredDistanceTo(getOwner()) >= 8 * 8) {
                    if (!isSprinting()) {
                        setSprinting(true);
                    }
                } else {
                    if (isSprinting()) {
                        setSprinting(false);
                    }
                }
            }
        }

        private void tryTeleport() {
            if (getOwner() == null) {
                return;
            }
            BlockPos blockPos = getOwner().getBlockPos();
            for (int i = 0; i < 10; ++i) {
                int j = this.getRandomInt(-3, 3);
                int k = this.getRandomInt(-1, 1);
                int l = this.getRandomInt(-3, 3);
                boolean hasTeleportFailed = this.tryTeleportTo(
                        blockPos.getX() + j, blockPos.getY() + k, blockPos.getZ() + l);
                if (hasTeleportFailed) continue;
                return;
            }
        }

        private boolean tryTeleportTo(int x, int y, int z) {
            if (getOwner() == null) {
                return false;
            }
            if (Math.abs(x - getOwner().getX()) < 2.0 && Math.abs(z - getOwner().getZ()) < 2.0) {
                return false;
            }
            if (!this.canTeleportTo(new BlockPos(x, y, z))) {
                return false;
            }
            refreshPositionAndAngles(x + 0.5, y, z + 0.5, getYaw(), getPitch());
            getNavigation().stop();
            return true;
        }

        private boolean canTeleportTo(BlockPos pos) {
            PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(world, pos.mutableCopy());
            if (pathNodeType != PathNodeType.WALKABLE) {
                return false;
            }
            BlockState blockState = world.getBlockState(pos.down());
            if (!this.leavesAllowed && blockState.getBlock() instanceof LeavesBlock) {
                return false;
            }
            BlockPos blockPos = pos.subtract(getBlockPos());
            return world.isSpaceEmpty(RubroEntity.this, getBoundingBox().offset(blockPos));
        }

        private int getRandomInt(int min, int max) {
            return random.nextInt(max - min + 1) + min;
        }
    }

    /**
     * Targets players which are not holding redstone in their hand, or that have broken a redstone ore block in the
     * recent 3 minutes.
     * This target goal is started only if the rubro's power is 40% or more of its max.
     */
    class RedstoneTargetGoal extends ActiveTargetGoal<PlayerEntity> {

        public RedstoneTargetGoal() {

            super(RubroEntity.this, PlayerEntity.class, 10, false, true,
                    entity -> (!entity.isHolding(Items.REDSTONE) && !entity.isHolding(Items.REDSTONE_BLOCK)
                            && !entity.isHolding(EarthboundItems.PRIMORDIAL_REDSTONE))
                            || (entity instanceof PlayerEntity player
                            && PlayerBlockBreakEventHandler.getRedstoneBreakTimes().containsKey(player)
                            && world.getTime() - PlayerBlockBreakEventHandler.getRedstoneBreakTimes().get(player)<3600));
        }

        @Override
        public boolean canStart() {
            if (isBaby() || isActive() || isFromFossil()) {
                return false;
            }
            boolean canStart = super.canStart() && getPower() > getMaxPower() * 0.4;
            if (targetEntity instanceof PlayerEntity player) {
                if (FabricLoaderImpl.INSTANCE.isModLoaded("origins")) {
                    if (OriginsCallbacks.isPlayerRubian(player)) {
                        return false;
                    }
                }
            }
            return canStart;
        }
    }

    class RubiaTargetGoal extends ActiveTargetGoal<LivingEntity> {

        public RubiaTargetGoal() {
            super(RubroEntity.this, LivingEntity.class, 0, false, true,
                    entity -> entity.hasStatusEffect(EarthboundStatusEffects.RUBIA) && !entity.equals(getOwner()) &&
                            !((entity instanceof RubroEntity rubro) && rubro.isFromFossil()) &&
                            !((world.getServer() != null && !world.getServer().isPvpEnabled() && entity instanceof PlayerEntity)));
        }

        @Override
        public boolean canStart() {
            if (isActive()) {
                return false;
            }
            if (isBaby()) {
                return false;
            }
            return getPower() > getMaxPower() * 0.3 && super.canStart();
        }

        @Override
        public void start() {
            playSound(EarthboundSounds.RUBRO_TARGET, 0.7f, 1.0f);
            super.start();
        }

        @Override
        public boolean shouldContinue() {
            if (isActive()) {
                return false;
            }
            if (getPower() < getMaxPower() * 0.2) {
                return false;
            }
            boolean canContinue = super.shouldContinue();
            return canContinue;
        }

        @Override
        public void tick() {
            if (target == null) {
                return;
            }
            if (!target.getActiveStatusEffects().containsKey(EarthboundStatusEffects.RUBIA)) {
                target = null;
            }
            super.tick();
        }
    }
}
