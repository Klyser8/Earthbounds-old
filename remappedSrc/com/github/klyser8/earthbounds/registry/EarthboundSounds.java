package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.Earthbounds;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.github.klyser8.earthbounds.Earthbounds.MOD_ID;

public class EarthboundSounds {

    public static final SoundEvent CARBORANEA_AMBIENT = new SoundEvent(
            new Identifier(MOD_ID, "entity.carboranea.ambient.cold"));
    public static final SoundEvent CARBORANEA_SHAKE = new SoundEvent(
            new Identifier(MOD_ID, "entity.carboranea.shake"));
    public static final SoundEvent CARBORANEA_SHAKE_SHORT = new SoundEvent(
            new Identifier(MOD_ID, "entity.carboranea.shake_short"));
    public static final SoundEvent CARBORANEA_HURT_WEAK = new SoundEvent(
            new Identifier(MOD_ID, "entity.carboranea.hurt_weak"));
    public static final SoundEvent CARBORANEA_HURT = new SoundEvent(
            new Identifier(MOD_ID, "entity.carboranea.hurt"));
    public static final SoundEvent CARBORANEA_DEATH = new SoundEvent(
            new Identifier(MOD_ID, "entity.carboranea.death"));

    public static void register() {
        Registry.register(Registry.SOUND_EVENT, CARBORANEA_AMBIENT.getId(), CARBORANEA_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, CARBORANEA_SHAKE.getId(), CARBORANEA_SHAKE);
        Registry.register(Registry.SOUND_EVENT, CARBORANEA_SHAKE_SHORT.getId(), CARBORANEA_SHAKE_SHORT);
        Registry.register(Registry.SOUND_EVENT, CARBORANEA_HURT.getId(), CARBORANEA_HURT);
        Registry.register(Registry.SOUND_EVENT, CARBORANEA_HURT_WEAK.getId(),
                CARBORANEA_HURT_WEAK);
        Registry.register(Registry.SOUND_EVENT, CARBORANEA_DEATH.getId(), CARBORANEA_DEATH);
    }
}
