package com.github.klyser8.earthbounds.client.sound;

import com.github.klyser8.earthbounds.entity.PertilyoEntity;
import com.github.klyser8.earthbounds.entity.RubroEntity;
import com.github.klyser8.earthbounds.registry.EarthboundSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

@Environment(value= EnvType.CLIENT)
public class PertilyoFlyLoopSoundInstance extends MovingSoundInstance {

    private final PertilyoEntity pertilyo;
    private final Random random;
    private float distance = 0.0f;

    public PertilyoFlyLoopSoundInstance(PertilyoEntity pertilyo) {
        super(EarthboundSounds.PERTILYO_FLY_LOOP, SoundCategory.NEUTRAL);
        this.random = new Random();
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.1f;
        this.pitch = 0.8f + random.nextFloat() / 5;
        this.pertilyo = pertilyo;
        this.x = pertilyo.getX();
        this.y = pertilyo.getY();
        this.z = pertilyo.getZ();
    }

    @Override
    public boolean canPlay() {
        return !pertilyo.isSilent() && !pertilyo.isRoosting();
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    @Override
    public void tick() {
        if (pertilyo.isDead() || pertilyo.isRoosting()) {
            setDone();
            return;
        }
        this.x = pertilyo.getX();
        this.y = pertilyo.getY();
        this.z = pertilyo.getZ();
        pitch = (float) (0.7 + pertilyo.getVelocity().length());
        this.distance = MathHelper.clamp(this.distance + 0.0025f, 0.0f, 1.0f);
    }

    public static void playSound(PertilyoEntity pertilyo) {
        PertilyoFlyLoopSoundInstance soundInstance = new PertilyoFlyLoopSoundInstance(pertilyo);
        MinecraftClient.getInstance().getSoundManager().play(soundInstance);
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }
}
