package com.github.klyser8.earthbounds.entity.renderer;

import com.github.klyser8.earthbounds.entity.PertilyoEntity;
import com.github.klyser8.earthbounds.entity.RubroEntity;
import com.github.klyser8.earthbounds.entity.model.PertilyoEntityModel;
import com.github.klyser8.earthbounds.entity.model.RubroEntityModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.geo.render.built.GeoModel;

public class PertilyoEntityRenderer extends EarthenEntityRenderer<PertilyoEntity> {

    public PertilyoEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PertilyoEntityModel());
    }

    @Override
    public RenderLayer getRenderType(PertilyoEntity animatable, float partialTicks, MatrixStack stack,
                                     VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder,
                                     int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }
}
