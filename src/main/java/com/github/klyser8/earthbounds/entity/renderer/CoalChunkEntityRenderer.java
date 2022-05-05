package com.github.klyser8.earthbounds.entity.renderer;

import com.github.klyser8.earthbounds.entity.misc.CoalChunkEntity;
import com.github.klyser8.earthbounds.entity.Conductive;
import com.github.klyser8.earthbounds.entity.model.CoalChunkEntityModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import java.util.Random;

public class CoalChunkEntityRenderer extends GeoEntityRenderer<CoalChunkEntity> {
    private static final int maxLightIn = 255;
    private float scale = 1;
    private final Random random = new Random();

    public CoalChunkEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new CoalChunkEntityModel());
    }

    @Override
    public void render(GeoModel model, CoalChunkEntity entity, float partialTicks, RenderLayer type,
                       MatrixStack matrixStack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder,
                       int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        int lightIn = calculateLightIn(entity);
        this.shadowRadius = 0.1f * entity.getScale();
        matrixStack.scale(entity.getScale(), entity.getScale(), entity.getScale());
        if (entity.age > CoalChunkEntity.MAX_AGE - 100) {
            alpha = (CoalChunkEntity.MAX_AGE - entity.age) / 100f;
        }
        if (model.getBone("root").isPresent()) {
            /*if (model.getBone("root").get().getScaleX() != scale) {
                model.getBone("root").get().setScaleX(scale);
                model.getBone("root").get().setScaleY(scale);
                model.getBone("root").get().setScaleZ(scale);
            }*/
            float rStep = 0.4f / (Conductive.MAX_HEAT);
            float gStep = 0.6f / (Conductive.MAX_HEAT);
            float bStep = 0.6f / (Conductive.MAX_HEAT);
            if (packedLightIn > maxLightIn) {
                packedLightIn = maxLightIn;
            }
            red = 1 - rStep * (Conductive.MAX_HEAT - entity.getCurrentHeat());
            green = gStep * (Conductive.MAX_HEAT - entity.getCurrentHeat());
            blue = bStep * (Conductive.MAX_HEAT - entity.getCurrentHeat());
        }
        super.render(model, entity, partialTicks, type, matrixStack, renderTypeBuffer, vertexBuilder,
                (int) Math.max(lightIn, entity.getBrightnessAtEyes() * 255),
                getPackedOverlay(entity, 0), red, green, blue, alpha);
    }

    private int calculateLightIn(CoalChunkEntity entity) {
        int lightIn = (int) ((maxLightIn / Conductive.MAX_HEAT) * entity.getCurrentHeat());
        if (lightIn > maxLightIn) {
            lightIn = maxLightIn;
        }
        if (lightIn < 0) {
            lightIn = 0;
        }
        return lightIn;
    }
}
