package com.github.klyser8.earthbounds;

import com.github.klyser8.earthbounds.block.GlowGreaseSplatBlock;
import com.github.klyser8.earthbounds.entity.mob.Earthen;
import com.github.klyser8.earthbounds.entity.mob.RubroEntity;
import com.github.klyser8.earthbounds.entity.renderer.rubro.RubroEntityRenderer;
import com.github.klyser8.earthbounds.registry.*;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.client.input.Input;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.Recipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

public class MixinCallbacks {

    public static void stopAttackSoundAgainstEarthens(ItemStack stackInHand, Entity target, World instance, double posX,
                                                      double posY, double posZ, SoundEvent soundEvent,
                                                      SoundCategory soundCategory, float volume, float pitch) {
        if (target instanceof Earthen) {
            if (!(stackInHand.getItem() instanceof PickaxeItem)) {
                soundEvent = EarthboundSounds.EARTHEN_HURT_WEAK;
                volume = 1f;
                pitch = 1.0f + target.getWorld().random.nextFloat() / 5.0f;
            }
        }
        instance.playSound(null, posX, posY, posZ, soundEvent, soundCategory, volume, pitch);
    }

    public static double calculatePosOffset(World world, Vec3d pos) {
        BlockPos blockPos = new BlockPos(pos);
        BlockState state = world.getBlockState(blockPos);
        VoxelShape collisionShape = state.getOutlineShape(world, blockPos);
        if (!world.isAir(blockPos)
                && !collisionShape.isEmpty()
                && collisionShape.getBoundingBox().maxY < 0.2) {
            return pos.y;
        }
        return pos.y - 0.2;
    }

    public static void calculateVelocityAffectingPos(Vec3d pos, Box box, World world,
                                                     CallbackInfoReturnable<BlockPos> cir) {
        if (world.getBlockState(new BlockPos(pos)).getBlock() instanceof GlowGreaseSplatBlock) {
            cir.setReturnValue(new BlockPos(pos));
        } else {
            cir.setReturnValue(new BlockPos(pos.x, box.minY - 0.50000001, pos.z));
        }
    }

    public static boolean canMine(PlayerEntity miner) {
        NbtList list = miner.getMainHandStack().getEnchantments();
        if (list.isEmpty()) return true;
        for (NbtElement element : list) {
            if (element.toString().contains("crumble") && miner.isCreative()) {
                return false;
            }
        }
        return true;
    }

    public static void applyFlingshotMovementBoost(ItemStack activeItem, Input input) {
        if (activeItem.isOf(EarthboundItems.FLINGSHOT)) {
            input.movementSideways *= 3;
            input.movementForward *= 3;
        }
    }

    public static float applyFlingshotFov(float f, boolean isUsingItem, ItemStack activeItem, int useTicks) {
        if (isUsingItem && activeItem.isOf(EarthboundItems.FLINGSHOT)) {
            float useSeconds = useTicks / 15.0f; //FIXME fov zooms in too much
            useSeconds = useSeconds > 1.0f ? 1.0f : useSeconds * useSeconds;
            f *= 1.0f - useSeconds * 0.15f;
        }
        return f;
    }

    public static void canEnchant(ItemStack stack, List<EnchantmentLevelEntry> list,  Enchantment enchantment) {
        if (enchantment == EarthboundEnchantments.CRUMBLE
                && !EarthboundEnchantments.CRUMBLE.isAcceptableItem(stack) && !list.isEmpty()) {
            list.remove(list.size() - 1);
        } else if (enchantment == EarthboundEnchantments.FORCE
                && !EarthboundEnchantments.FORCE.isAcceptableItem(stack) && !list.isEmpty()) {
            list.remove(list.size() - 1);
        } else if (enchantment == EarthboundEnchantments.PRECISION
                && !EarthboundEnchantments.PRECISION.isAcceptableItem(stack) && !list.isEmpty()) {
            list.remove(list.size() - 1);
        } else if (enchantment == EarthboundEnchantments.AUTOMATION
                && !EarthboundEnchantments.PRECISION.isAcceptableItem(stack) && !list.isEmpty()) {
            list.remove(list.size() - 1);
        } else if (enchantment == EarthboundEnchantments.VERSATILITY
                && !EarthboundEnchantments.PRECISION.isAcceptableItem(stack) && !list.isEmpty()) {
            list.remove(list.size() - 1);
        }
    }

    public static void insertRecipesInGroups(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir) {
        ItemStack stack = recipe.getOutput();
        ItemGroup itemGroup = stack.getItem().getGroup();
        if (itemGroup == EarthboundItemGroup.COMBAT) {
            cir.setReturnValue(RecipeBookGroup.CRAFTING_EQUIPMENT);
        } else if (itemGroup == EarthboundItemGroup.PLACEABLES) {
            cir.setReturnValue(RecipeBookGroup.CRAFTING_BUILDING_BLOCKS);
        }
    }

    public static void insertDispenserCustomBehaviors(CallbackInfo ci, BlockPointerImpl blockPointerImpl,
                                                      DispenserBlockEntity dispenserBlockEntity, int i, ItemStack itemStack) {
        // get custom behavior
        DispenserBehavior customBehavior = EarthboundDispenserBehaviors.getCustomDispenserBehavior(itemStack);
        // check if custom behavior exists
        if(customBehavior != null) {
            // run custom behavior
            dispenserBlockEntity.setStack(i, customBehavior.dispense(blockPointerImpl, itemStack));
            ci.cancel();
        }
    }

    public static float renderRubroShadow(Entity entity, float f) {
        if (entity instanceof RubroEntity rubro && rubro.isBaby()) {
            return Math.min(RubroEntityRenderer.MAX_SHADOW_RADIUS - (rubro.getPower() + 150f) / -1000f, RubroEntityRenderer.MAX_SHADOW_RADIUS) * 2;
        }
        return f;
    }

    public static boolean shouldRenderPowerOutline(PlayerEntity player) {
        if (player == null || player.isSpectator() || !FabricLoaderImpl.INSTANCE.isModLoaded("origins")) {
            return false;
        }
        return OriginsCallbacks.shouldRenderPowerOutline(player);
    }

    public static void calculatePickaxeDamage(Args args, ItemStack stack, LivingEntity target) {
        if (stack.getItem() instanceof PickaxeItem) {
            if (target instanceof Earthen) {
                args.set(0, 1);
            }
        }
    }

}
