package com.github.klyser8.earthbounds.mixin;

import com.github.klyser8.earthbounds.item.RedstoneFoodItem;
import com.github.klyser8.earthbounds.registry.EarthboundSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntity.class)
public abstract class BurpSoundInjector extends LivingEntity {

    @Shadow protected HungerManager hungerManager;

    @Shadow public abstract HungerManager getHungerManager();

    protected BurpSoundInjector(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "eatFood", at = @At(value = "TAIL"))
    public void eatRedstoneInjector(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (stack.getItem() instanceof RedstoneFoodItem redstone && redstone.getFoodComponent() != null
                && redstone.getFoodComponent().getSaturationModifier() > 0) {
            world.playSound(null, getX(), getY(), getZ(), EarthboundSounds.ENTITY_CHARGE, SoundCategory.PLAYERS, 0.5f, world.random.nextFloat() * 0.1f + 0.9f);
        }
    }

    @ModifyArgs(method = "eatFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;" +
            "DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    public void eatRedstoneArgModifier(Args args, World world, ItemStack stack) {
        if (stack.getItem() instanceof RedstoneFoodItem) {
            args.set(4, EarthboundSounds.ENTITY_EAT_REDSTONE);
        }
    }

}
