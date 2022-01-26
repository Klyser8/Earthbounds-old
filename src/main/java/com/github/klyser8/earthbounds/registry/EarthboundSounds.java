package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.Earthbounds;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.github.klyser8.earthbounds.Earthbounds.MOD_ID;

public class EarthboundSounds {

    public static final SoundEvent CARBORANEA_AMBIENT = new SoundEvent(
            new Identifier(MOD_ID, "entity.carboranea.ambient"));
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
    public static final SoundEvent CARBORANEA_BUCKET_EMPTY = new SoundEvent(
            new Identifier(MOD_ID, "entity.carboranea.bucket_empty"));
    public static final SoundEvent CARBORANEA_BUCKET_FILL = new SoundEvent(
            new Identifier(MOD_ID, "entity.carboranea.bucket_fill"));

    public static final SoundEvent RUBRO_AMBIENT = new SoundEvent(
            new Identifier(MOD_ID, "entity.rubro.ambient"));
    public static final SoundEvent RUBRO_CREAK = new SoundEvent(
            new Identifier(MOD_ID, "entity.rubro.creak"));
    public static final SoundEvent RUBRO_ACTIVE = new SoundEvent(
            new Identifier(MOD_ID, "entity.rubro.active"));
    public static final SoundEvent RUBRO_CHARGE = new SoundEvent(
            new Identifier(MOD_ID, "entity.rubro.charge"));
    public static final SoundEvent RUBRO_EAT = new SoundEvent(
            new Identifier(MOD_ID, "entity.rubro.eat"));
    public static final SoundEvent RUBRO_HURT = new SoundEvent(
            new Identifier(MOD_ID, "entity.rubro.hurt"));
    public static final SoundEvent RUBRO_DEATH = new SoundEvent(
            new Identifier(MOD_ID, "entity.rubro.death"));

    public static void register() {
        Registry.register(Registry.SOUND_EVENT, CARBORANEA_AMBIENT.getId(), CARBORANEA_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, CARBORANEA_SHAKE.getId(), CARBORANEA_SHAKE);
        Registry.register(Registry.SOUND_EVENT, CARBORANEA_SHAKE_SHORT.getId(), CARBORANEA_SHAKE_SHORT);
        Registry.register(Registry.SOUND_EVENT, CARBORANEA_HURT.getId(), CARBORANEA_HURT);
        Registry.register(Registry.SOUND_EVENT, CARBORANEA_HURT_WEAK.getId(),
                CARBORANEA_HURT_WEAK);
        Registry.register(Registry.SOUND_EVENT, CARBORANEA_DEATH.getId(), CARBORANEA_DEATH);
        Registry.register(Registry.SOUND_EVENT, CARBORANEA_BUCKET_FILL.getId(), CARBORANEA_BUCKET_FILL);
        Registry.register(Registry.SOUND_EVENT, CARBORANEA_BUCKET_EMPTY.getId(), CARBORANEA_BUCKET_EMPTY);

        Registry.register(Registry.SOUND_EVENT, RUBRO_AMBIENT.getId(), RUBRO_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, RUBRO_CREAK.getId(), RUBRO_CREAK);
        Registry.register(Registry.SOUND_EVENT, RUBRO_ACTIVE.getId(), RUBRO_ACTIVE);
        Registry.register(Registry.SOUND_EVENT, RUBRO_CHARGE.getId(), RUBRO_CHARGE);
        Registry.register(Registry.SOUND_EVENT, RUBRO_EAT.getId(), RUBRO_EAT);
        Registry.register(Registry.SOUND_EVENT, RUBRO_HURT.getId(), RUBRO_HURT);
        Registry.register(Registry.SOUND_EVENT, RUBRO_DEATH.getId(), RUBRO_DEATH);
    }
}
