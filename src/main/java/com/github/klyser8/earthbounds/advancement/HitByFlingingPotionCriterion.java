package com.github.klyser8.earthbounds.advancement;

import com.github.klyser8.earthbounds.Earthbounds;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class HitByFlingingPotionCriterion extends AbstractCriterion<HitByFlingingPotionCriterion.Conditions> {

    static final Identifier ID = new Identifier(Earthbounds.MOD_ID, "hit_by_flinging_potion");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj,
                                            EntityPredicate.Extended playerPredicate,
                                            AdvancementEntityPredicateDeserializer predicateDeserializer) {
        EntityPredicate entityPredicate = EntityPredicate.fromJson(obj.get("hit_entity"));
        DistancePredicate distancePredicate = DistancePredicate.fromJson(obj.get("distance"));
        Potion potion = null;
        if (obj.has("potion")) {
            Identifier identifier = new Identifier(JsonHelper.getString(obj, "potion"));
            potion = Registry.POTION.getOrEmpty(identifier).orElseThrow(() ->
                    new JsonSyntaxException("Unknown potion '" + identifier + "'"));
        }
        return new Conditions(playerPredicate, entityPredicate, distancePredicate, potion);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, Entity entity, Vec3d start, Vec3d end, Potion potion) {
        this.trigger(player, conditions -> conditions.matches(player, entity, start, end, potion));
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final EntityPredicate hitEntity;
        private final DistancePredicate distancePredicate;
        private final Potion potion;

        //Indicates what conditions this criteria will have
        public Conditions(EntityPredicate.Extended player, EntityPredicate hitEntity,
                          DistancePredicate distancePredicate, @Nullable Potion potion) {
            super(ID, player);
            this.hitEntity = hitEntity;
            this.distancePredicate = distancePredicate;
            this.potion = potion;
        }

        public boolean matches(ServerPlayerEntity player, Entity entity, Vec3d start, Vec3d end, Potion potion) {
            return hitEntity.test(player, entity) && distancePredicate.test(start.x, start.y, start.z, end.x, end.y, end.z)
                    && (potion == null || this.potion == potion || this.potion == null);
        }

        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("hit_entity", hitEntity.toJson());
            jsonObject.add("distance", distancePredicate.toJson());
            if (this.potion != null) {
                jsonObject.addProperty("potion", Registry.POTION.getId(this.potion).toString());
            }
            return jsonObject;
        }
    }

}
