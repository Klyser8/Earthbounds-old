package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.item.enchantment.CrumbleEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EarthboundEnchantments {

    public static final Enchantment CRUMBLE = new CrumbleEnchantment();

    public static void register() {
        Registry.register(Registry.ENCHANTMENT, new Identifier(Earthbounds.MOD_ID, "crumble"), CRUMBLE);
    }

}
