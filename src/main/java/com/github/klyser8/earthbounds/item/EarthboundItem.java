package com.github.klyser8.earthbounds.item;

import net.minecraft.item.Item;

public class EarthboundItem extends Item {

    private final boolean lightningResistant;
    public EarthboundItem(Settings settings, boolean lightningResistant) {
        super(settings);
        this.lightningResistant = lightningResistant;
    }

    public boolean isLightningResistant() {
        return lightningResistant;
    }
}
