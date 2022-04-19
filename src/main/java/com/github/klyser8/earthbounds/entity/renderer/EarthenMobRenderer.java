package com.github.klyser8.earthbounds.entity.renderer;

import com.github.klyser8.earthbounds.entity.Earthen;
import com.github.klyser8.earthbounds.registry.EarthboundDamageSource;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.PickaxeItem;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class EarthenMobRenderer<T extends MobEntity & IAnimatable & Earthen> extends GeoMobRenderer<T>{

    protected EarthenMobRenderer(EntityRendererFactory.Context ctx, AnimatedGeoModel<T> modelProvider) {
        super(ctx, modelProvider);
    }

    @Override
    public void render(GeoModel model, T animatable, float partialTicks, RenderLayer type, MatrixStack matrixStackIn,
                       VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                       int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer,
                vertexBuilder, packedLightIn, getPackedOverlay((Earthen) animatable, 0), red, green, blue, alpha);
    }

    /**
     * Earthen mobs should not flash red when hurt unless they are harmed by a pickaxe.
     */
    public static int getPackedOverlay(Earthen earthen, float uIn) {
        if (!(earthen instanceof LivingEntity entity)) {
            return 0;
        }
        boolean wasAttackValid = false;
        if (earthen.getLastDamager() != null) {
            if (earthen.getLastDamager() instanceof LivingEntity damager) {
                wasAttackValid = damager.getMainHandStack().getItem() instanceof PickaxeItem;
            }
        }
        String sourceName = earthen.getLastDamageSourceName();
        if (sourceName.equalsIgnoreCase(EarthboundDamageSource.SHIMMER_EXPLOSION_NAME)
                || sourceName.equalsIgnoreCase(EarthboundDamageSource.SHIMMER_EXPLOSION_PLAYER_NAME)
                || sourceName.equalsIgnoreCase(EarthboundDamageSource.COPPER_BUCK_NAME)
                || sourceName.equalsIgnoreCase(EarthboundDamageSource.COPPER_BUCK_PLAYER_NAME)) {
            wasAttackValid = true;
        }
        return OverlayTexture.getUv(OverlayTexture.getU(uIn),
                entity.hurtTime > 0 && wasAttackValid);
    }
}
