package com.github.klyser8.earthbounds.entity.renderer;

import com.github.klyser8.earthbounds.entity.CarboraneaEntity;
import com.github.klyser8.earthbounds.entity.model.CarboraneaEntityModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class CarboraneaEntityRenderer extends GeoEntityRenderer<CarboraneaEntity> {

    private static final float num = 0.00392156863f;
    private static final int maxLightIn = 240;

    public CarboraneaEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new CarboraneaEntityModel());
    }

    @Override
    public void render(GeoModel model, CarboraneaEntity carboranea, float partialTicks, RenderLayer type,
                       MatrixStack matrixStackIn, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder,
                       int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        //Make carboranea visible in darkness if heat is high enough.
        int lightIn = calculateLightIn(carboranea);
        if (carboranea.isBaby()) {
            babify(model, matrixStackIn);
            red = 0.5f;
            green = 0.5f;
            blue = 0.3f;
        } else {
            if (this.shadowRadius != 0.3f) {
                this.shadowRadius = 0.3f;
            }
            if (model.getBone("shell").isPresent() && model.getBone("shell").get().isHidden()) {
                model.getBone("shell").ifPresent(shellBone -> shellBone.setHidden(false));
            }
            green = 1 - lightIn * num;
            blue = 1 - (lightIn * num);
        }
        super.render(model, carboranea, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder,
                (int) Math.max(lightIn, carboranea.getBrightnessAtEyes() * 255), packedOverlayIn,
                red, green, blue, alpha);
    }

    /**
     * Turns the carboranea into a baby.
     * Baby carboraneas should be smaller and have no outer shell.
     * Additionally, they should be colored slightly differently.
     * They also got big heads! :)
     */
    private void babify(GeoModel model, MatrixStack matrixStackIn) {
        model.getBone("shell").ifPresent(shellBone -> shellBone.setHidden(true));
        matrixStackIn.scale(0.5f, 0.5f, 0.5f);
        this.shadowRadius = 0.15f;
    }

    /**
     * Calculates what the carboranea's current lightIn should be.
     * This depends entirely on its current heat! The hotter it is, the brighter it looks
     * in the dark.
     *
     * @param entity carboranea
     * @return a number from 0 to 255, indicating the current brightness.
     */
    private int calculateLightIn(CarboraneaEntity entity) {
        int lightIn = (int) (entity.getCurrentHeat() / 100 * (maxLightIn / 3));
        if (lightIn > maxLightIn) {
            lightIn = maxLightIn;
        }
        if (lightIn < 0) {
            lightIn = 0;
        }
        return lightIn;
    }
}
