package com.github.klyser8.earthbounds;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class OriginsCallbacks {

    public static boolean shouldRenderPowerOutline(PlayerEntity player) {
        PowerHolderComponent component = PowerHolderComponent.KEY.get(player);
        for (Power power : component.getPowers()) {
            if (power.getType().getIdentifier().equals(new Identifier(Earthbounds.MOD_ID, "power_food_replacement"))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPlayerRubian(PlayerEntity player) {
        for (Origin origin : ModComponents.ORIGIN.get(player).getOrigins().values()) {
            if (origin.getIdentifier().equals(new Identifier(Earthbounds.MOD_ID, "rubian"))) {
                return true;
            }
        }
        return false;
    }

    public static boolean doesPlayerHaveRedstoneFoodPower(PlayerEntity player) {
        PowerHolderComponent component = PowerHolderComponent.KEY.get(player);
        for (Power power : component.getPowers()) {
            if (power.getType().getIdentifier().equals(new Identifier(Earthbounds.MOD_ID, "only_redstone_food"))) {
                return true;
            }
        }
        return false;
    }

}
