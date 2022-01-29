package com.github.klyser8.earthbounds.mixin;

import com.github.klyser8.earthbounds.entity.Earthen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.PickaxeItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
abstract class StopAttackSoundAgainstEarthenMixin extends LivingEntity {

    private Entity target;

    protected StopAttackSoundAgainstEarthenMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At(value = "HEAD"))
    private void storeTargetEntity(Entity target, CallbackInfo ci) {
        this.target = target;
    }

    @Redirect(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    private void preventAttackSound(World instance, PlayerEntity player, double posX, double posY, double posZ,
                                    SoundEvent soundEvent, SoundCategory soundCategory, float volume, float pitch) {
        handleSound(instance, posX, posY, posZ, soundEvent, soundCategory, volume, pitch);
    }

    private void handleSound(World instance, double posX, double posY, double posZ,
                             SoundEvent soundEvent, SoundCategory soundCategory, float volume, float pitch) {
        if (target instanceof Earthen) {
            if (!(getStackInHand(Hand.MAIN_HAND).getItem() instanceof PickaxeItem)) {
                soundEvent = SoundEvents.ENTITY_IRON_GOLEM_HURT;
                volume = 0.5f;
                pitch = 2.0f;
            }
        }
        instance.playSound(null, posX, posY, posZ, soundEvent, soundCategory, volume, pitch);
    }
}
