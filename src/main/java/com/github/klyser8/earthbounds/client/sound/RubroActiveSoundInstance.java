package com.github.klyser8.earthbounds.client.sound;

import com.github.klyser8.earthbounds.entity.mob.RubroEntity;
import com.github.klyser8.earthbounds.registry.EarthboundSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

@Environment(value= EnvType.CLIENT)
public class RubroActiveSoundInstance extends MovingSoundInstance {

    private final RubroEntity rubro;
    private final Random random;
    private float distance = 0.0f;

    public RubroActiveSoundInstance(RubroEntity rubro) {
        super(EarthboundSounds.RUBRO_ACTIVE, SoundCategory.HOSTILE);
        this.random = new Random();
        this.repeat = true;
        this.repeatDelay = 200;
        this.volume = 1.25f;
        this.pitch = 0.8f + random.nextFloat() / 5;
        this.rubro = rubro;
        this.x = rubro.getX();
        this.y = rubro.getY();
        this.z = rubro.getZ();
    }

    @Override
    public boolean canPlay() {
        return !rubro.isSilent();
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    @Override
    public void tick() {
        if (rubro.isRemoved()) {
            setDone();
            return;
        }
        if (rubro.getPower() < rubro.getMaxPower() * 0.2) {
            this.volume = 0;
        }
//        repeatDelay = random.nextInt(100) + 100;
        this.x = rubro.getX();
        this.y = rubro.getY();
        this.z = rubro.getZ();
        this.distance = MathHelper.clamp(this.distance + 0.0025f, 0.0f, 1.0f);
        this.volume = (rubro.getPower() / ((float) rubro.getMaxPower())) / 4.0f;
    }

    public static void playSound(RubroEntity rubro) {
        RubroActiveSoundInstance soundInstance = new RubroActiveSoundInstance(rubro);
        MinecraftClient.getInstance().getSoundManager().play(soundInstance);
    }
}
