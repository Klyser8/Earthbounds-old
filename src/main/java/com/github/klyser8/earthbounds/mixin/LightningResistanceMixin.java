package com.github.klyser8.earthbounds.mixin;

import com.github.klyser8.earthbounds.item.EarthboundItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class)
public class LightningResistanceMixin {

    @ModifyArg(method =
            "onStruckByLightning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LightningEntity;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private float calculateDamage(float dmg) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof ItemEntity item) {
            if (item.getStack().getItem() instanceof EarthboundItem earthboundItem) {
                if (earthboundItem.isLightningResistant()) {
                    dmg = 0;
                }
            }
        }
        return dmg;
    }

}
