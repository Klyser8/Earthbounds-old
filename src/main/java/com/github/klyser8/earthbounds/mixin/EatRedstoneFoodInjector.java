package com.github.klyser8.earthbounds.mixin;

import com.github.klyser8.earthbounds.client.sound.PoweredOutsideSoundInstance;
import com.github.klyser8.earthbounds.item.RedstoneFoodItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class EatRedstoneFoodInjector {

    private PlayerEntity earthPlayer = null;

    @Shadow private int foodLevel;

    @Shadow private float saturationLevel;

    @Inject(method = "eat", at = @At("HEAD"), cancellable = true)
    public void eatInject(Item item, ItemStack stack, CallbackInfo ci) {
        if (item instanceof RedstoneFoodItem) {
            FoodComponent foodComponent = item.getFoodComponent();
            if (foodComponent == null) {
                return;
            }
            if (foodLevel == 20) {
                saturationLevel = Math.min(foodComponent.getSaturationModifier() + saturationLevel, (float) this.foodLevel);
            }
            foodLevel = Math.min(foodComponent.getHunger() + foodLevel, 20);
            ci.cancel();
        }
    }
}
