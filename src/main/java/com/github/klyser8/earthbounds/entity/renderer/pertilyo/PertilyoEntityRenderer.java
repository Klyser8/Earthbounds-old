package com.github.klyser8.earthbounds.entity.renderer.pertilyo;

import com.github.klyser8.earthbounds.entity.PertilyoEntity;
import com.github.klyser8.earthbounds.entity.RubroEntity;
import com.github.klyser8.earthbounds.entity.model.PertilyoEntityModel;
import com.github.klyser8.earthbounds.entity.model.RubroEntityModel;
import com.github.klyser8.earthbounds.entity.renderer.EarthenEntityRenderer;
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
        addLayer(new PertilyoLightLayer(this));
    }

    @Override
    public RenderLayer getRenderType(PertilyoEntity animatable, float partialTicks, MatrixStack stack,
                                     VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder,
                                     int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }
}