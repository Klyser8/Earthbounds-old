package com.github.klyser8.earthbounds.entity.renderer;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.entity.RubroEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class RubroMaskLayer extends GeoLayerRenderer<RubroEntity> {

    private final Identifier RUBRO_MODEL;
    private final Identifier RUBRO_MASK;
    private final Identifier RUBRO_MASK_GOLD; //1/20 chance of a rubro cub spawning with a gold mask

    public RubroMaskLayer(IGeoRenderer<RubroEntity> entityRendererIn) {
        super(entityRendererIn);
        RUBRO_MODEL = new Identifier(Earthbounds.MOD_ID, "geo/mob/rubro.geo.json");
        RUBRO_MASK = new Identifier(Earthbounds.MOD_ID, "textures/entity/rubro/mask.png");
        RUBRO_MASK_GOLD = new Identifier(Earthbounds.MOD_ID, "textures/entity/rubro/mask_gold.png");
    }


    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn,
                       RubroEntity entity, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        Identifier maskIdentifier = /*entity.isFromFossil() && */entity.hasGoldSkull() ? RUBRO_MASK_GOLD : RUBRO_MASK;
        this.getRenderer().render(getEntityModel().getModel(RUBRO_MODEL), entity, partialTicks,
                RenderLayer.getArmorCutoutNoCull(maskIdentifier), matrixStackIn, bufferIn,
                bufferIn.getBuffer(RenderLayer.getEyes(maskIdentifier)), packedLightIn,
                OverlayTexture.packUv(0, OverlayTexture.getV(entity.hurtTime > 0)),
                1f, 1f, 1f, 1f);
    }
}
