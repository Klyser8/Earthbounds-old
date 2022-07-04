package com.github.klyser8.earthbounds.client.sound;

import com.github.klyser8.earthbounds.entity.mob.RubroEntity;
import com.github.klyser8.earthbounds.registry.EarthboundSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(value= EnvType.CLIENT)
public class PoweredInsideSoundInstance extends MovingSoundInstance {

    private final ClientPlayerEntity player;
    private float distance = 0.0f;

    public PoweredInsideSoundInstance(ClientPlayerEntity entity) {
        super(EarthboundSounds.ENTITY_ACTIVE_INSIDE, SoundCategory.AMBIENT, Random.create());
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.75f;
        this.pitch = 0.8f;
        this.player = entity;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }

    @Override
    public boolean canPlay() {
        return !player.isSilent();
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    @Override
    public void tick() {
        if (player.isRemoved()) {
            setDone();
            return;
        }
        this.volume = player.getHungerManager().getSaturationLevel() / 60;
        if (player.getHungerManager().getSaturationLevel() <= 8) {
            setDone();
        }
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
        this.distance = MathHelper.clamp(this.distance + 0.0025f, 0.0f, 1.0f);
    }

    public static void playSound(ClientPlayerEntity player) {
        PoweredInsideSoundInstance soundInstance = new PoweredInsideSoundInstance(player);
        MinecraftClient.getInstance().getSoundManager().play(soundInstance);
    }
}
