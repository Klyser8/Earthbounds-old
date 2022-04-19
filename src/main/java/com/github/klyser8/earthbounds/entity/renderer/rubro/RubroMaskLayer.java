package com.github.klyser8.earthbounds.entity.renderer.rubro;

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
    private final Identifier RUBRO_MASK_GILDED;
    private final Identifier RUBRO_MASK_CRYSTALLINE;
    private final Identifier RUBRO_MASK_CHARRED;
    private final Identifier RUBRO_MASK_VERDANT;
    private final Identifier RUBRO_MASK_CRIMSON;

    public RubroMaskLayer(IGeoRenderer<RubroEntity> entityRendererIn) {
        super(entityRendererIn);
        RUBRO_MODEL = new Identifier(Earthbounds.MOD_ID, "geo/mob/rubro.geo.json");
        RUBRO_MASK = new Identifier(Earthbounds.MOD_ID, "textures/entity/rubro/mask.png");
        RUBRO_MASK_GILDED = new Identifier(Earthbounds.MOD_ID, "textures/entity/rubro/gilded_mask.png");
        RUBRO_MASK_CRYSTALLINE = new Identifier(Earthbounds.MOD_ID, "textures/entity/rubro/crystalline_mask.png");
        RUBRO_MASK_CHARRED = new Identifier(Earthbounds.MOD_ID, "textures/entity/rubro/charred_mask.png");
        RUBRO_MASK_VERDANT = new Identifier(Earthbounds.MOD_ID, "textures/entity/rubro/verdant_mask.png");
        RUBRO_MASK_CRIMSON = new Identifier(Earthbounds.MOD_ID, "textures/entity/rubro/crimson_mask.png");
    }


    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn,
                       RubroEntity entity, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        Identifier maskIdentifier;
        switch (entity.getMaskType()) {
            case GILDED -> maskIdentifier = RUBRO_MASK_GILDED;
            case CRYSTALLINE -> maskIdentifier = RUBRO_MASK_CRYSTALLINE;
            case CHARRED -> maskIdentifier = RUBRO_MASK_CHARRED;
            case VERDANT -> maskIdentifier = RUBRO_MASK_VERDANT;
            case CRIMSON -> maskIdentifier = RUBRO_MASK_CRIMSON;
            default -> maskIdentifier = RUBRO_MASK;
        }
        this.getRenderer().render(getEntityModel().getModel(RUBRO_MODEL), entity, partialTicks,
                RenderLayer.getArmorCutoutNoCull(maskIdentifier), matrixStackIn, bufferIn,
                bufferIn.getBuffer(RenderLayer.getEyes(maskIdentifier)), packedLightIn,
                OverlayTexture.packUv(0, OverlayTexture.getV(entity.hurtTime > 0)),
                1f, 1f, 1f, 1f);
    }


}
