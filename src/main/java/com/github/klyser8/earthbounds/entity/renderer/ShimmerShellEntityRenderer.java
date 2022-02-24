package com.github.klyser8.earthbounds.entity.renderer;

import com.github.klyser8.earthbounds.entity.ShimmerShellEntity;
import com.github.klyser8.earthbounds.entity.model.ShimmerShellEntityModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class ShimmerShellEntityRenderer extends GeoProjectilesRenderer<ShimmerShellEntity> {

    public ShimmerShellEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ShimmerShellEntityModel());
    }

    @Override
    public RenderLayer getRenderType(ShimmerShellEntity animatable, float partialTicks, MatrixStack stack,
                                     VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder,
                                     int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void render(ShimmerShellEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn,
                       VertexConsumerProvider bufferIn, int packedLightIn) {
//        float rotation = (entityIn.age * 5f % 360f);
//        matrixStackIn.multiply(new Quaternion(0, rotation, 0, true));
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }
}
