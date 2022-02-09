package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.item.enchantment.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EarthboundEnchantments {

    public static final Enchantment CRUMBLE = new CrumbleEnchantment();
    public static final ForceEnchantment FORCE = new ForceEnchantment();
    public static final PrecisionEnchantment PRECISION = new PrecisionEnchantment();
    public static final Enchantment AUTOMATION = new AutomationEnchantment();
    public static final Enchantment VERSATILITY = new VersatilityEnchantment();

    public static void register() {
        Registry.register(Registry.ENCHANTMENT, new Identifier(Earthbounds.MOD_ID, "crumble"), CRUMBLE);
        Registry.register(Registry.ENCHANTMENT, new Identifier(Earthbounds.MOD_ID, "force"), FORCE);
        Registry.register(Registry.ENCHANTMENT, new Identifier(Earthbounds.MOD_ID, "precision"), PRECISION);
        Registry.register(Registry.ENCHANTMENT, new Identifier(Earthbounds.MOD_ID, "automation"), AUTOMATION);
        Registry.register(Registry.ENCHANTMENT, new Identifier(Earthbounds.MOD_ID, "versatility"), VERSATILITY);
    }

}
