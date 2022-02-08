package com.github.klyser8.earthbounds.mixin;

import com.github.klyser8.earthbounds.MixinCallbacks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
abstract class StopAttackSoundAgainstEarthenMixin {

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    private Entity target;

    @Inject(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At(value = "HEAD"))
    private void storeTargetEntity(Entity target, CallbackInfo ci) {
        this.target = target;
    }

    @Redirect(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    private void preventAttackSound(World instance, PlayerEntity player, double posX, double posY, double posZ,
                                    SoundEvent soundEvent, SoundCategory soundCategory, float volume, float pitch) {
        MixinCallbacks.stopAttackSoundAgainstEarthens(getEquippedStack(EquipmentSlot.MAINHAND), target, instance, posX,
                posY, posZ, soundEvent, soundCategory, volume, pitch);
    }

}
