package com.github.klyser8.earthbounds.client;

import com.github.klyser8.earthbounds.client.sound.PoweredInsideSoundInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ClientCallbacks {

    public static void startPoweredSoundInstance(PlayerEntity player) {
        PoweredInsideSoundInstance soundInstance = new PoweredInsideSoundInstance((ClientPlayerEntity) player);
        MinecraftClient.getInstance().getSoundManager().play(soundInstance);
    }

}
