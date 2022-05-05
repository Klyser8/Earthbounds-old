package com.github.klyser8.earthbounds.registry;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.github.klyser8.earthbounds.Earthbounds.MOD_ID;

public class EarthboundSounds {


    public static final SoundEvent EARTHEN_HURT_WEAK = new SoundEvent(
            new Identifier(MOD_ID, "entity.earthen_hurt_weak"));
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
    public static final SoundEvent RUBRO_POUNCE = new SoundEvent(
            new Identifier(MOD_ID, "entity.rubro.pounce"));
    public static final SoundEvent RUBRO_POUNCE_STRIKE = new SoundEvent(
            new Identifier(MOD_ID, "entity.rubro.pounce_strike"));
    public static final SoundEvent RUBRO_JUMP = new SoundEvent(
            new Identifier(MOD_ID, "entity.rubro.jump"));
    public static final SoundEvent RUBRO_HURT = new SoundEvent(
            new Identifier(MOD_ID, "entity.rubro.hurt"));
    public static final SoundEvent RUBRO_TARGET = new SoundEvent(
            new Identifier(MOD_ID, "entity.rubro.target"));
    public static final SoundEvent RUBRO_DEATH = new SoundEvent(
            new Identifier(MOD_ID, "entity.rubro.death"));

    public static final SoundEvent PERTILYO_FLY_LOOP = new SoundEvent(
            new Identifier(MOD_ID, "entity.pertilyo.fly_loop"));
    public static final SoundEvent PERTILYO_AMBIENT = new SoundEvent(
            new Identifier(MOD_ID, "entity.pertilyo.ambient"));
    public static final SoundEvent PERTILYO_AMBIENT_SLEEP = new SoundEvent(
            new Identifier(MOD_ID, "entity.pertilyo.ambient_sleep"));
    public static final SoundEvent PERTILYO_ANGRY = new SoundEvent(
            new Identifier(MOD_ID, "entity.pertilyo.angry"));
    public static final SoundEvent PERTILYO_PLOP = new SoundEvent(
            new Identifier(MOD_ID, "entity.pertilyo.plop"));
    public static final SoundEvent PERTILYO_OXIDIZE = new SoundEvent(
            new Identifier(MOD_ID, "entity.pertilyo.oxidize"));
    public static final SoundEvent PERTILYO_DEOXIDIZE = new SoundEvent(
            new Identifier(MOD_ID, "entity.pertilyo.deoxidize"));
    public static final SoundEvent PERTILYO_DEATH = new SoundEvent(
            new Identifier(MOD_ID, "entity.pertilyo.death"));
    public static final SoundEvent PERTILYO_HURT = new SoundEvent(
            new Identifier(MOD_ID, "entity.pertilyo.hurt"));

    public static final SoundEvent FLINGSHOT_SHOOT = new SoundEvent(
            new Identifier(MOD_ID, "item.flingshot.shoot"));
    public static final SoundEvent MADDER_BUCK_LAND = new SoundEvent(
            new Identifier(MOD_ID, "entity.madder_buck.land"));

    public static final SoundEvent GLOW_GREASE_PLACE = new SoundEvent(
            new Identifier(MOD_ID, "block.glow_grease.place"));
    public static final SoundEvent GLOW_GREASE_BREAK = new SoundEvent(
            new Identifier(MOD_ID, "block.glow_grease.break"));
    public static final SoundEvent GLOW_GREASE_STEP = new SoundEvent(
            new Identifier(MOD_ID, "block.glow_grease.step"));
    public static final SoundEvent REDSTONE_FOSSIL_CREAK = new SoundEvent(
            new Identifier(MOD_ID, "block.redstone_fossil.creak"));

    public static void register() {
        //Entities
        Registry.register(Registry.SOUND_EVENT, CARBORANEA_AMBIENT.getId(), CARBORANEA_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, EARTHEN_HURT_WEAK.getId(), EARTHEN_HURT_WEAK);
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
        Registry.register(Registry.SOUND_EVENT, RUBRO_POUNCE.getId(), RUBRO_POUNCE);
        Registry.register(Registry.SOUND_EVENT, RUBRO_POUNCE_STRIKE.getId(), RUBRO_POUNCE_STRIKE);
        Registry.register(Registry.SOUND_EVENT, RUBRO_JUMP.getId(), RUBRO_JUMP);
        Registry.register(Registry.SOUND_EVENT, RUBRO_HURT.getId(), RUBRO_HURT);
        Registry.register(Registry.SOUND_EVENT, RUBRO_TARGET.getId(), RUBRO_TARGET);
        Registry.register(Registry.SOUND_EVENT, RUBRO_DEATH.getId(), RUBRO_DEATH);

        Registry.register(Registry.SOUND_EVENT, PERTILYO_FLY_LOOP.getId(), PERTILYO_FLY_LOOP);
        Registry.register(Registry.SOUND_EVENT, PERTILYO_AMBIENT.getId(), PERTILYO_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, PERTILYO_AMBIENT_SLEEP.getId(), PERTILYO_AMBIENT_SLEEP);
        Registry.register(Registry.SOUND_EVENT, PERTILYO_ANGRY.getId(), PERTILYO_ANGRY);
        Registry.register(Registry.SOUND_EVENT, PERTILYO_PLOP.getId(), PERTILYO_PLOP);
        Registry.register(Registry.SOUND_EVENT, PERTILYO_OXIDIZE.getId(), PERTILYO_OXIDIZE);
        Registry.register(Registry.SOUND_EVENT, PERTILYO_DEOXIDIZE.getId(), PERTILYO_DEOXIDIZE);
        Registry.register(Registry.SOUND_EVENT, PERTILYO_DEATH.getId(), PERTILYO_DEATH);
        Registry.register(Registry.SOUND_EVENT, PERTILYO_HURT.getId(), PERTILYO_HURT);

        //Items
        Registry.register(Registry.SOUND_EVENT, FLINGSHOT_SHOOT.getId(), FLINGSHOT_SHOOT);
        Registry.register(Registry.SOUND_EVENT, MADDER_BUCK_LAND.getId(), MADDER_BUCK_LAND);

        //Blocks
        Registry.register(Registry.SOUND_EVENT, GLOW_GREASE_PLACE.getId(), GLOW_GREASE_PLACE);
        Registry.register(Registry.SOUND_EVENT, GLOW_GREASE_BREAK.getId(), GLOW_GREASE_BREAK);
        Registry.register(Registry.SOUND_EVENT, GLOW_GREASE_STEP.getId(), GLOW_GREASE_STEP);
        Registry.register(Registry.SOUND_EVENT, REDSTONE_FOSSIL_CREAK.getId(), REDSTONE_FOSSIL_CREAK);
    }

    public class BlockSoundGroups {

        public static final BlockSoundGroup GLOW_GREASE = new BlockSoundGroup(1.0f, 1.25f, GLOW_GREASE_BREAK,
                GLOW_GREASE_STEP, GLOW_GREASE_PLACE, GLOW_GREASE_STEP, GLOW_GREASE_STEP);

    }
}
