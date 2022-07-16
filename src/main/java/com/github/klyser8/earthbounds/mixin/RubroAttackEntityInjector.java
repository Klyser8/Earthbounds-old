package com.github.klyser8.earthbounds.mixin;

import com.github.klyser8.earthbounds.entity.mob.RubroEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class RubroAttackEntityInjector {

    @Shadow protected int playerHitTimer;

    @Shadow @Nullable protected PlayerEntity attackingPlayer;

    @Inject(method = "damage", at = @At(value = "TAIL"))
    private void injectRubroLogic(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = source.getAttacker();
        if (entity instanceof RubroEntity rubro && rubro.getOwner() != null) {
            playerHitTimer = 100;
            attackingPlayer = ((RubroEntity) entity).getOwner();
        }
    }


}
