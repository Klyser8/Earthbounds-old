package com.github.klyser8.earthbounds.advancement;

import com.github.klyser8.earthbounds.Earthbounds;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class ShootGlowGreaseAgainstWallCriterion extends AbstractCriterion<ShootGlowGreaseAgainstWallCriterion.Conditions> {

    static final Identifier ID = new Identifier(Earthbounds.MOD_ID, "shoot_glow_grease_against_block");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject obj,
                                            EntityPredicate.Extended playerPredicate,
                                            AdvancementEntityPredicateDeserializer predicateDeserializer) {
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(obj.get("max_light_level"));
        DistancePredicate distancePredicate = DistancePredicate.fromJson(obj.get("landing_distance"));
        return new Conditions(playerPredicate, distancePredicate, intRange);
    }

    public void trigger(ServerPlayerEntity player, Vec3d start, Vec3d end, int maxLightLevel) {
        this.trigger(player, (conditions) -> conditions.matches(start, end, maxLightLevel));
    }

    public static class Conditions extends AbstractCriterionConditions {

        private final DistancePredicate distancePredicate;
        private final NumberRange.IntRange intRange;

        public Conditions(EntityPredicate.Extended playerPredicate, DistancePredicate distancePredicate, NumberRange.IntRange lightingLevel) {
            super(ID, playerPredicate);
            this.distancePredicate = distancePredicate;
            this.intRange = lightingLevel;
        }

        public boolean matches(Vec3d startPos, Vec3d endPos, int maxLightLevel) {
            return distancePredicate.test(startPos.x, startPos.y, startPos.z, endPos.x, endPos.y, endPos.z)
                    && intRange.test(maxLightLevel);
        }

    }

}
