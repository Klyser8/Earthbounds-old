package com.github.klyser8.earthbounds;

import com.github.klyser8.earthbounds.block.GlowGreaseSplatBlock;
import com.github.klyser8.earthbounds.entity.Earthen;
import com.github.klyser8.earthbounds.registry.EarthboundEnchantments;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

public class MixinCallbacks {

    public static void stopAttackSoundAgainstEarthens(ItemStack stackInHand, Entity target, World instance, double posX,
                                                      double posY, double posZ, SoundEvent soundEvent,
                                                      SoundCategory soundCategory, float volume, float pitch) {
        if (target instanceof Earthen) {
            if (!(stackInHand.getItem() instanceof PickaxeItem)) {
                soundEvent = SoundEvents.ENTITY_IRON_GOLEM_HURT; //TODO replace this sound with a custom one
                volume = 0.5f;
                pitch = 2.0f;
            }
        }
        instance.playSound(null, posX, posY, posZ, soundEvent, soundCategory, volume, pitch);
    }

    public static int calculatePosOffset(World world, BlockPos blockPos, Vec3d pos) {
        BlockState state = world.getBlockState(blockPos);
        VoxelShape collisionShape = state.getOutlineShape(world, blockPos);
        if (world.isAir(blockPos)
                || collisionShape.isEmpty()
                || collisionShape.getBoundingBox().maxY > 0.2) {
            return MathHelper.floor(pos.y - (double)0.2f);
        } else {
            return (int) Math.floor(pos.y);
        }
    }

    public static void calculateVelocityAffectingPos(Vec3d pos, Box box, World world,
                                                     CallbackInfoReturnable<BlockPos> cir) {
        BlockPos blockPos = new BlockPos(pos.x, box.minY, pos.z);
        if (world.getBlockState(blockPos).getBlock() instanceof GlowGreaseSplatBlock) {
            cir.setReturnValue(blockPos);
        } else {
            cir.setReturnValue(blockPos.add(0, -0.5000001, 0));
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

    public static void canEnchant(ItemStack stack, List<EnchantmentLevelEntry> list,  Enchantment enchantment) {
        if (enchantment == EarthboundEnchantments.FORCE
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

}
