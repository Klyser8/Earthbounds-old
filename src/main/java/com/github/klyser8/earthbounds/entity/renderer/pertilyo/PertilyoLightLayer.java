package com.github.klyser8.earthbounds.entity.renderer.pertilyo;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.entity.PertilyoEntity;
import com.github.klyser8.earthbounds.entity.RubroEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class PertilyoLightLayer extends GeoLayerRenderer<PertilyoEntity> {

    private final Identifier PERTILYO_MODEL;
    private final Identifier PERTILYO_LIGHT;

    public PertilyoLightLayer(IGeoRenderer<PertilyoEntity> entityRendererIn) {
        super(entityRendererIn);
        PERTILYO_MODEL = new Identifier(Earthbounds.MOD_ID, "geo/mob/pertilyo.geo.json");
        PERTILYO_LIGHT = new Identifier(Earthbounds.MOD_ID, "textures/entity/pertilyo/pertilyo_light.png");
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn,
                       PertilyoEntity pertilyo, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        if (!pertilyo.isInvisible()) {
            /*if (pertilyo.getEnergy() <= 0) {
                return;
            }*/
            this.getRenderer().render(getEntityModel().getModel(PERTILYO_MODEL), pertilyo, partialTicks,
                    RenderLayer.getEyes(PERTILYO_LIGHT), matrixStackIn, bufferIn,
                    bufferIn.getBuffer(RenderLayer.getEyes(PERTILYO_LIGHT)), packedLightIn,
                    OverlayTexture.packUv(0, OverlayTexture.getV(pertilyo.hurtTime > 0)),
                    1f, 1f, 1f, 1f);
        }
    }

    @Override
    public RenderLayer getRenderType(Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(PERTILYO_LIGHT);
    }
}
