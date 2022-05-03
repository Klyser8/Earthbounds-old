package com.github.klyser8.earthbounds.client;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.entity.renderer.GlowGreaseDropEntityRenderer;
import com.github.klyser8.earthbounds.item.flingshot.FlingshotItem;
import com.github.klyser8.earthbounds.registry.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.impl.client.rendering.ColorProviderRegistryImpl;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Identifier;

public class EarthboundsClient implements ClientModInitializer {

    public static final Identifier packetID = new Identifier(Earthbounds.MOD_ID, "spawn_packet");

    /**
     * Registers our Entity's renderer, which provides a model and texture for the entity.
     *
     * Entity Renderers can also manipulate the model before it renders based on
     * entity context (EndermanEntityRenderer#render).
     */
    @Override
    public void onInitializeClient() {
        EarthboundEntities.registerRenderers();
        EarthboundParticles.registerFactories();
        ColorProviderRegistryImpl.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 :
                PotionUtil.getColor(stack), EarthboundItems.FLINGING_POTION);
        BlockRenderLayerMap.INSTANCE.putBlock(EarthboundBlocks.GLOW_GREASE_SPLAT, RenderLayer.getTranslucent());

        EntityRendererRegistry.register(EarthboundEntities.GLOW_GREASE, GlowGreaseDropEntityRenderer::new);
        EntityRendererRegistry.register(EarthboundEntities.FLINGING_POTION, FlyingItemEntityRenderer::new);
        registerModelPredicates();
    }

    private static void registerModelPredicates() {
        Identifier pullingIdentifier = new Identifier(Earthbounds.MOD_ID, "pull");
        ModelPredicateProviderRegistry.register(EarthboundItems.FLINGSHOT, pullingIdentifier, (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0;
            }
            if (entity.getActiveItem() != stack) {
                return 0;
            }
            if (EnchantmentHelper.get(stack).containsKey(EarthboundEnchantments.AUTOMATION)) {
                return (float) (stack.getMaxUseTime() - entity.getItemUseTimeLeft())
                        % FlingshotItem.CHARGE_TIME / FlingshotItem.CHARGE_TIME;
            } else {
                return (float) (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / FlingshotItem.CHARGE_TIME;
            }
        });
    }
}
