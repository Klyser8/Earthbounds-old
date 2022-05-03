package com.github.klyser8.earthbounds.entity.renderer;

import com.github.klyser8.earthbounds.entity.misc.GlowGreaseDropEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class GlowGreaseDropEntityRenderer extends EntityRenderer<GlowGreaseDropEntity> {

    private final ItemRenderer itemRenderer;

    public GlowGreaseDropEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(GlowGreaseDropEntity glowGreaseEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(this.dispatcher.getRotation());
        if (glowGreaseEntity.wasShotAtAngle()) {
            matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(115.0f));
        }
        this.itemRenderer.renderItem(glowGreaseEntity.getStack(), ModelTransformation.Mode.GROUND, i,
                OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, glowGreaseEntity.getId());
        matrixStack.pop();
        super.render(glowGreaseEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(GlowGreaseDropEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
