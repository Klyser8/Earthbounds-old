package com.github.klyser8.earthbounds.entity.renderer;

import com.github.klyser8.earthbounds.entity.Earthen;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class EarthenEntityRenderer<T extends LivingEntity & IAnimatable & Earthen> extends GeoEntityRenderer<T> {

    public EarthenEntityRenderer(EntityRendererFactory.Context ctx, AnimatedGeoModel<T> modelProvider) {
        super(ctx, modelProvider);
    }

    @Override
    public void render(GeoModel model, T animatable, float partialTicks, RenderLayer type, MatrixStack matrixStackIn,
                       VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                       int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer,
                vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
