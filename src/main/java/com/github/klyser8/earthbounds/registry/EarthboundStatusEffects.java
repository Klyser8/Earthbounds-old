package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.statuseffect.RubiaStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EarthboundStatusEffects {

    public static final StatusEffect RUBIA = new RubiaStatusEffect();

    public static void register() {
        Registry.register(Registry.STATUS_EFFECT, new Identifier(Earthbounds.MOD_ID, "rubia"), RUBIA);
    }

}
