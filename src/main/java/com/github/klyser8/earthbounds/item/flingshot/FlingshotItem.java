package com.github.klyser8.earthbounds.item.flingshot;

import com.github.klyser8.earthbounds.item.EarthboundItem;
import com.github.klyser8.earthbounds.item.enchantment.VersatilityEnchantment;
import com.github.klyser8.earthbounds.registry.EarthboundEnchantments;
import com.github.klyser8.earthbounds.registry.EarthboundItems;
import com.github.klyser8.earthbounds.util.EarthMath;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;
import java.util.function.Predicate;

public class FlingshotItem extends EarthboundItem implements Vanishable {

    public static final Predicate<ItemStack> FLINGSHOT_PROJECTILES = stack ->
            stack.getItem() instanceof Flingable;

    public static final int CHARGE_TIME = 15;

    public FlingshotItem(Settings settings) {
        super(settings, true);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return hasAutomation(stack) ? CHARGE_TIME : 72000;
    }

    @Override
    public int getEnchantability() {
        return 1;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack flingShot = user.getStackInHand(hand);
        boolean isHoldingProjectile = !getHeldProjectile(flingShot, user).isEmpty();
        if (isHoldingProjectile) {//TODO change speed of player when using flingshot
            user.setCurrentHand(hand);
            return TypedActionResult.consume(flingShot);
        } else {
            return TypedActionResult.fail(flingShot);
        }
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public void onStoppedUsing(ItemStack flingshot, World world, LivingEntity user, int remainingUseTicks) {
        if (hasAutomation(flingshot)) {
            return;
        }
        handleFlingshotLogic(flingshot, world, user,remainingUseTicks);
    }

    @Override
    public ItemStack finishUsing(ItemStack flingshot, World world, LivingEntity user) {
        handleFlingshotLogic(flingshot, world, user, 0);
        return super.finishUsing(flingshot, world, user);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    private void handleFlingshotLogic(ItemStack flingshot, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) {
            return;
        }
        boolean isCreative = player.getAbilities().creativeMode;
        ItemStack projStack = getHeldProjectile(flingshot, player);
        if (projStack.isEmpty()) {
            return;
        }
        int ticksPassed = this.getMaxUseTime(flingshot) - remainingUseTicks;
        if ((hasAutomation(flingshot) && ticksPassed % CHARGE_TIME != 0) || ticksPassed < CHARGE_TIME) {
            return;
        }
        if (!world.isClient) {
            float force = calculateForce(flingshot, projStack);
            float divergence = calculateDivergence(flingshot);
            if (projStack.getItem() instanceof Flingable flingable) {
                ProjectileEntity projEntity = flingable.createFlingableEntity(world, projStack, user);
                world.spawnEntity(projEntity);
                projEntity.setVelocity(player, player.getPitch(), player.getYaw(), 1.0f, force, divergence);
                projEntity.velocityModified = true;
                if (!isCreative) {
                    projStack.decrement(1);
                }
            } else {
                Vec3d velocity = EarthMath.calculateVelocity(player, player.getPitch(),
                        player.getYaw(), 1.0f, force, divergence);
                ItemEntity itemEntity = new ItemEntity(world,
                        player.getEyePos().x, player.getEyeY(), player.getEyePos().getZ(), projStack.copy(),
                        velocity.x, velocity.y, velocity.z);
                itemEntity.setPickupDelay(20);
                world.spawnEntity(itemEntity);
                projStack.setCount(0);
            }
            flingshot.damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
        }
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,
                1.0f, 1.3f + world.random.nextFloat() / 2.0f);
        player.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    private float calculateForce(ItemStack flingshot, ItemStack projectile) {
        float force = 1.0f;
        if (projectile.isOf(EarthboundItems.GLOW_GREASE)) {
            force -= 0.2f;
        }
        if (EnchantmentHelper.get(flingshot).get(EarthboundEnchantments.FORCE) != null) {
            force += EarthboundEnchantments.FORCE.getExtraForce(
                    EnchantmentHelper.get(flingshot).get(EarthboundEnchantments.FORCE));
        }
        return force;
    }

    private float calculateDivergence(ItemStack flingshot) {
        float divergence = 10.0f;
        if (EnchantmentHelper.get(flingshot).get(EarthboundEnchantments.PRECISION) != null) {
            divergence -= EarthboundEnchantments.PRECISION.getDivergenceModifier(
                    EnchantmentHelper.get(flingshot).get(EarthboundEnchantments.PRECISION));
        }
        return divergence;
    }

    private boolean hasAutomation(ItemStack flingshot) {
        return EnchantmentHelper.get(flingshot).get(EarthboundEnchantments.AUTOMATION) != null;
    }

    private static boolean hasVersatility(ItemStack flingshot) {
        return EnchantmentHelper.get(flingshot).get(EarthboundEnchantments.VERSATILITY) != null;
    }

    /**
     * Returns the projectile held in the hand which is not holding the flingshot.
     * In case the flingshot has the {@link VersatilityEnchantment}, any item can be flung.
     *
     * @param flingshot the flingshot item used
     * @param entity the entity to be checked
     * @return the itemstack if it is a valid projectile. An empty stack otherwise.
     */
    public static ItemStack getHeldProjectile(ItemStack flingshot, LivingEntity entity) {
        if (hasVersatility(flingshot)) {
            return hasVersatility(entity.getMainHandStack()) ? entity.getOffHandStack() : entity.getMainHandStack();
        }
        if (FLINGSHOT_PROJECTILES.test(entity.getStackInHand(Hand.OFF_HAND))) {
            return entity.getStackInHand(Hand.OFF_HAND);
        }
        if (FLINGSHOT_PROJECTILES.test(entity.getStackInHand(Hand.MAIN_HAND))) {
            return entity.getStackInHand(Hand.MAIN_HAND);
        }
        return ItemStack.EMPTY;
    }
}
