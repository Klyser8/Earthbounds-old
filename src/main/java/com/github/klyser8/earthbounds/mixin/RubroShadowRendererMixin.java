package com.github.klyser8.earthbounds.mixin;

import com.github.klyser8.earthbounds.entity.RubroEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderDispatcher.class)
public class RubroShadowRendererMixin {

    @ModifyVariable(method = "renderShadow", at = @At(value = "STORE", ordinal = 0), ordinal = 3)
    private static float renderRubroShadow(float f, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                           Entity entity, float opacity, float tickDelta, WorldView world, float radius) {
        if (entity instanceof RubroEntity rubro && rubro.isBaby()) {
            System.out.println("LETSEGO");
            return 1 + rubro.getPower() / 1000f;
        }
        return f;
    }

}
