package com.github.klyser8.earthbounds.entity.renderer;

import com.github.klyser8.earthbounds.entity.CopperBuckEntity;
import com.github.klyser8.earthbounds.entity.ShimmerShellEntity;
import com.github.klyser8.earthbounds.entity.model.CopperBuckEntityModel;
import com.github.klyser8.earthbounds.entity.model.ShimmerShellEntityModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class CopperBuckEntityRenderer extends GeoProjectilesRenderer<CopperBuckEntity> {

    public CopperBuckEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new CopperBuckEntityModel());
    }

    @Override
    public RenderLayer getRenderType(CopperBuckEntity animatable, float partialTicks, MatrixStack stack,
                                     VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder,
                                     int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void render(CopperBuckEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn,
                       VertexConsumerProvider bufferIn, int packedLightIn) {
        matrixStackIn.push();
        int rotation = (int) (System.currentTimeMillis() % 360);
        matrixStackIn.translate(0, 0.1, 0);
        if (entityIn.getCollisionAge() == 0) {
            matrixStackIn.multiply(new Quaternion(rotation / 2f, 0, 0, true));
        }
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pop();
    }
}
