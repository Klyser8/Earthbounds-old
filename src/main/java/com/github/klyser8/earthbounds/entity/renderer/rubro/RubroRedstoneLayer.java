package com.github.klyser8.earthbounds.entity.renderer.rubro;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.entity.RubroEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class RubroRedstoneLayer extends GeoLayerRenderer<RubroEntity> {

    private final Identifier RUBRO_MODEL;
    private final Identifier RUBRO_REDSTONE;

    public RubroRedstoneLayer(IGeoRenderer<RubroEntity> entityRendererIn) {
        super(entityRendererIn);
        RUBRO_MODEL = new Identifier(Earthbounds.MOD_ID, "geo/mob/rubro.geo.json");
        RUBRO_REDSTONE = new Identifier(Earthbounds.MOD_ID, "textures/entity/rubro/redstone.png");
    }


    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn,
                       RubroEntity rubro, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        if (!rubro.isInvisible() && !rubro.isBaby()) {
            float strength = (float) rubro.getPower() / RubroEntity.POWER_LIMIT / 2.0f;
            this.getRenderer().render(getEntityModel().getModel(RUBRO_MODEL), rubro, partialTicks,
                    RenderLayer.getEyes(RUBRO_REDSTONE), matrixStackIn, bufferIn,
                    bufferIn.getBuffer(RenderLayer.getEyes(RUBRO_REDSTONE)), packedLightIn,
                    OverlayTexture.packUv(0, OverlayTexture.getV(rubro.hurtTime > 0)),
                    strength, strength, strength, 1f);
        }
    }
}
