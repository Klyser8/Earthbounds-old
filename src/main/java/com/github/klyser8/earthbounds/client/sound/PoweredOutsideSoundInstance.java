package com.github.klyser8.earthbounds.client.sound;

import com.github.klyser8.earthbounds.entity.mob.RubroEntity;
import com.github.klyser8.earthbounds.registry.EarthboundSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(value= EnvType.CLIENT)
public class PoweredOutsideSoundInstance extends MovingSoundInstance {

    private final LivingEntity entity;
    private float distance = 0.0f;

    public PoweredOutsideSoundInstance(LivingEntity entity) {
        super(EarthboundSounds.ENTITY_ACTIVE_OUTSIDE, SoundCategory.HOSTILE, Random.create());
        this.repeat = true;
        this.repeatDelay = 200;
        this.volume = 1.25f;
        this.pitch = 0.8f + field_38800.nextFloat() / 5;
        this.entity = entity;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }

    @Override
    public boolean canPlay() {
        return !entity.isSilent();
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    @Override
    public void tick() {
        if (entity.isRemoved()) {
            setDone();
            return;
        }
        if (entity instanceof RubroEntity rubro) {
            if (rubro.getPower() < rubro.getMaxPower() * 0.2) {
                this.volume = 0;
            }
            this.volume = (rubro.getPower() / ((float) rubro.getMaxPower())) / 4.0f;
        } else if (entity instanceof PlayerEntity player) {
            this.volume = player.getHungerManager().getSaturationLevel() / 20;
            if (player.getHungerManager().getSaturationLevel() == 0) {
                setDone();
            }
        }
//        repeatDelay = random.nextInt(100) + 100;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.distance = MathHelper.clamp(this.distance + 0.0025f, 0.0f, 1.0f);
    }

    public static void playSound(LivingEntity entity) {
        PoweredOutsideSoundInstance soundInstance = new PoweredOutsideSoundInstance(entity);
        MinecraftClient.getInstance().getSoundManager().play(soundInstance);
    }
}
