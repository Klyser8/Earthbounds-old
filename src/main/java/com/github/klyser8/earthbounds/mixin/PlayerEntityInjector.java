package com.github.klyser8.earthbounds.mixin;

import com.github.klyser8.earthbounds.registry.EarthboundParticles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityInjector extends LivingEntity {

    @Shadow public abstract HungerManager getHungerManager();

    protected PlayerEntityInjector(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void injectTick(CallbackInfo ci) {
        if (world.isClient && getHungerManager().getSaturationLevel() >= 8
                && age % (25 - ((int) getHungerManager().getSaturationLevel())) == 0) {
            for (int i = 0; i < 5; i++) {
                world.addParticle(EarthboundParticles.REDSTONE_CRACKLE,
                        getParticleX(0.5), getRandomBodyY(), getParticleZ(0.5),
                        0, 0, 0);
            }
        }
    }

}
