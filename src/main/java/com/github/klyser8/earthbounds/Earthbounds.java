package com.github.klyser8.earthbounds;

import com.github.klyser8.earthbounds.event.PlayerBlockBreakEventHandler;
import com.github.klyser8.earthbounds.mixin.BrewingRecipeRegistryAccessor;
import com.github.klyser8.earthbounds.registry.*;
import com.github.klyser8.earthbounds.registry.features.EarthboundConfiguredFeatures;
import com.github.klyser8.earthbounds.registry.features.EarthboundFeatures;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;


public class Earthbounds implements ModInitializer {

    public static final String MOD_ID = "earth";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        EarthboundFeatures.setupAndRegister();
        GeckoLib.initialize();
        EarthboundEntities.register();
        EarthboundItems.register();
        EarthboundSounds.register();
        EarthboundParticles.register();
        EarthboundBlocks.register();
        EarthboundEnchantments.register();

        EarthboundsAdvancementCriteria.register();
        EarthboundPotions.register();
        initEvents();
    }

    private void initEvents() {
        PlayerBlockBreakEventHandler.init();
    }
}
