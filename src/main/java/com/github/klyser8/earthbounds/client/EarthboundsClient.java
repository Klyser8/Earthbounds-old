package com.github.klyser8.earthbounds.client;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.entity.GlowGreaseDropEntity;
import com.github.klyser8.earthbounds.entity.renderer.GlowGreaseDropEntityRenderer;
import com.github.klyser8.earthbounds.network.EntitySpawnPacket;
import com.github.klyser8.earthbounds.registry.EarthboundBlocks;
import com.github.klyser8.earthbounds.registry.EarthboundEntities;
import com.github.klyser8.earthbounds.registry.EarthboundItems;
import com.github.klyser8.earthbounds.registry.EarthboundParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.impl.client.rendering.ColorProviderRegistryImpl;
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

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
        receiveEntityPacket();
    }

    public void receiveEntityPacket() {
        ClientSidePacketRegistryImpl.INSTANCE.register(packetID, (ctx, byteBuf) -> {
            EntityType<?> et = Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
            UUID uuid = byteBuf.readUuid();
            int entityId = byteBuf.readVarInt();
            Vec3d pos = EntitySpawnPacket.PacketBufUtil.readVec3d(byteBuf);
            float pitch = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
            float yaw = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
            ctx.getTaskQueue().execute(() -> {
                if (MinecraftClient.getInstance().world == null)
                    throw new IllegalStateException("Tried to spawn entity in a null world!");
                Entity e = et.create(MinecraftClient.getInstance().world);
                if (e == null)
                    throw new IllegalStateException("Failed to create instance of entity \"" + Registry.ENTITY_TYPE.getId(et) + "\"!");
                e.updateTrackedPosition(pos);
                e.setPos(pos.x, pos.y, pos.z);
                e.setPitch(pitch);
                e.setYaw(yaw);
                e.setId(entityId);
                e.setUuid(uuid);
                MinecraftClient.getInstance().world.addEntity(entityId, e);
            });
        });
    }
}
