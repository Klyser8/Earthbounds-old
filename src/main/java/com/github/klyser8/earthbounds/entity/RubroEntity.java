package com.github.klyser8.earthbounds.entity;

import com.github.klyser8.earthbounds.registry.EarthboundBlocks;
import com.github.klyser8.earthbounds.registry.EarthboundEntities;
import com.github.klyser8.earthbounds.util.AdvancedBlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.*;

public class RubroEntity extends PathAwareEntity implements IAnimatable, Earthen {

    public static final int POWER_LIMIT = 200;

    private final AnimationFactory factory;
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
    private static final TrackedData<Integer> CURRENT_ANIMATION = DataTracker.registerData(RubroEntity.class,
    TrackedDataHandlerRegistry.INTEGER);

    private static final int ANIMATION_NONE = 0;
    private static final int ANIMATION_DIG = 1;
    private static final int ANIMATION_HEADBUTT = 2;
    private static final int ANIMATION_TAIL_ATTACK = 3;

    private int maxPower;
    private int babyAge = 0;

    public RubroEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        factory = new AnimationFactory(this);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, -1.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
        maxPower = 100 + random.nextInt(10) * 10;
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
        setPower(startPower);
        setDeepslate(deepslate);
        if (isDeepslate()) {
            createDeepslateAttributes();
        }
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new MoveToRedstoneGoal());
//        goalSelector.add(1, new DigRedstoneGoal());
//        goalSelector.add(2, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return DefaultAttributeContainer.builder()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0D)
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
            setPower(nbt.getInt("power"));
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
        getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
        getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(15);
        getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).setBaseValue(5);
        getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.55);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(HAS_GOLD_SKULL, random.nextFloat() < 0.1f);
        dataTracker.startTracking(FROM_FOSSIL, false);
        dataTracker.startTracking(DEEPSLATE, random.nextBoolean());
        dataTracker.startTracking(POWER, 0);
        dataTracker.startTracking(CURRENT_ORE, ItemStack.EMPTY);
        dataTracker.startTracking(BLOCK_TARGET_POS, getBlockPos());
        dataTracker.startTracking(CURRENT_ANIMATION, 0);
    }

    private <E extends IAnimatable> PlayState posePredicate(AnimationEvent<E> event) {
        if (getPose() == EntityPose.CROUCHING) {
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("stand", true));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {
        if (event.isMoving()) {
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("walk", true));
            return PlayState.CONTINUE;
        }
        if (getCurrentAnimation() == ANIMATION_DIG) {
            event.getController().setAnimation(
                    new AnimationBuilder().addAnimation("dig", true));
            return PlayState.CONTINUE;
        }
        event.getController().setAnimation(
                new AnimationBuilder().addAnimation("idle", true));
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState attackPredicate(AnimationEvent<E> event) {
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this,
                "pose", 20, this::posePredicate));
        animationData.addAnimationController(new AnimationController<>(this,
                "move", 5, this::movementPredicate));
        animationData.addAnimationController(new AnimationController<>(this,
                "attack", 5, this::attackPredicate));
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        float height = super.getDimensions(pose).height;
        float width = super.getDimensions(pose).width;
        if (getPower() < 0) {
            height += getPower() / 1000.0f;
            width += getPower() / 700.0f;
        }
        return EntityDimensions.changing(width, height);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.75f;
    }

    @Override
    public void tick() {
        super.tick();
        if (age % 20 == 0) {
            calculateDimensions();
            if (getPower() > 0) {
                setPower(getPower() - 1);
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
        if (hand == Hand.MAIN_HAND && getPower() < maxPower) {
            int pow = 0;
            if (mainStack.isOf(Items.REDSTONE)) {
                pow = 1;
            } else if (mainStack.isOf(Items.REDSTONE_BLOCK)) {
                pow = 9;
            }
            if (pow > 0) {
                mainStack.decrement(1);
                setPower(getPower() + pow);
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

    public void setPower(int power) {
        dataTracker.set(POWER, power);
    }

    public BlockPos getBlockTargetPos() {
        return dataTracker.get(BLOCK_TARGET_POS);
    }

    public void setBlockTargetPos(BlockPos pos) {
        dataTracker.set(BLOCK_TARGET_POS, pos);
    }

    public int getCurrentAnimation() {
        return dataTracker.get(CURRENT_ANIMATION);
    }

    public void setCurrentAnimation(int id) {
        dataTracker.set(CURRENT_ANIMATION, id);
    }

    @Override
    protected void pushAway(Entity entity) {
        super.pushAway(entity);
    }

    class MoveToRedstoneGoal extends Goal {

        /**
         * Whether the goal can start or not. Any logic checking if a goal's conditions are met should
         * be written here.
         *
         * @return true if the goal can start
         */
        @Override
        public boolean canStart() {
            if (getRandom().nextInt(30) != 0
                    || getPower() < 0
                    || getNavigation().isFollowingPath()
                    || getEyePos().distanceTo(Vec3d.ofCenter(getBlockTargetPos())) < 1.5) {
                return false;
            }
            AdvancedBlockPos pos = findValidPoweringBlock();
            if (pos != null) {
                setBlockTargetPos(pos.getPos());
                return getFaceExposedToAir(getBlockTargetPos()) != null;
            } else {
                return false;
            }
        }

        /**
         * Any logic related to the goal's start should be written here.
         */
        @Override
        public void start() {
            System.out.println("got ya: " + getBlockTargetPos());
            BlockPos target = getFaceExposedToAir(getBlockTargetPos());
            getNavigation().startMovingTo(
                    target.getX(), target.getY(), target.getZ(), 1.25f);
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

        @Override
        public void stop() {
            super.stop();
        }

        /**
         * Selects a random solid block in a radius, and if any of its faces are exposed to air then the
         * block pos is returned. Otherwise, null is returned
         */
        private AdvancedBlockPos findValidPoweringBlock() {
            BlockPos origin = new BlockPos(getEyePos());
            Iterable<BlockPos> iterable = BlockPos.iterateOutwards(origin, 10, 10, 10);
            for (BlockPos pos : iterable) {
                if (world.getBlockState(pos).getBlock().equals(Blocks.REDSTONE_ORE) ||
                        world.getBlockState(pos).getBlock().equals(Blocks.DEEPSLATE_REDSTONE_ORE)) {
                    if (world.getEntitiesByType(EarthboundEntities.RUBRO, Box.of(Vec3d.ofCenter(pos),
                            2, 2, 2), rubroEntity -> getPower() > 0 &&
                            !rubroEntity.equals(RubroEntity.this)).size() == 0) {
                        if (getFaceExposedToAir(pos) != null) {
                            return new AdvancedBlockPos(pos);
                        }
                    }
                }
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
            if (world.getBlockState(pos.up()).isAir()) {
                return pos.north();
            } else if (world.getBlockState(pos.north()).isAir()) {
                return pos.east();
            } else if (world.getBlockState(pos.east()).isAir()) {
                return pos.south();
            } else if (world.getBlockState(pos.south()).isAir()) {
                return pos.west();
            } else if (world.getBlockState(pos.west()).isAir()) {
                return pos.up();
            } else if (world.getBlockState(pos.down()).isAir()) {
                return pos.down();
            } else {
                return null;
            }
        }
    }

    class DigRedstoneGoal extends Goal {

        protected int digTime;
        protected int cooldown;

        public DigRedstoneGoal() {
            this.digTime = 50;
        }

        @Override
        public boolean canStart() {
            if (cooldown > 0 ) {
                cooldown--;
            }
            return cooldown == 0
                    && isBlockPowering(world.getBlockState(getBlockTargetPos()).getBlock())
                    && getEyePos().distanceTo(Vec3d.ofCenter(getBlockTargetPos())) < 1.5;
        }

        @Override
        public void start() {
            setCurrentAnimation(ANIMATION_DIG);
        }

        @Override
        public void tick() {
            System.out.println("Dig: " + digTime);
            getLookControl().lookAt(getBlockTargetPos().getX(), getBlockTargetPos().getY(), getBlockTargetPos().getZ());
            if (digTime % 5 == 0) {
                if (!world.isClient) {
                    world.playSound(null, getBlockPos(), SoundEvents.BLOCK_LEVER_CLICK,
                            SoundCategory.NEUTRAL, 1.0f, 2.0f);
                }
                setPower(getPower() + 10);

            }
            digTime--;
        }

        @Override
        public boolean shouldContinue() {
            System.out.println("Power: " + getPower() + " vs MaxPower: " + maxPower);
            return getPower() < maxPower && digTime > 0;
        }

        @Override
        public void stop() {
            cooldown = random.nextInt(300) + 300;
            setCurrentAnimation(ANIMATION_NONE);
            digTime = 50;
        }
    }

}
