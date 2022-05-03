package com.github.klyser8.earthbounds.advancement;

import com.github.klyser8.earthbounds.Earthbounds;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class KilledByRubroCriterion extends AbstractCriterion<KilledByRubroCriterion.Conditions> {

    static final Identifier ID = new Identifier(Earthbounds.MOD_ID, "killed_by_rubro");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj,
                                            EntityPredicate.Extended playerPredicate,
                                            AdvancementEntityPredicateDeserializer predicateDeserializer) {
        EntityPredicate entityPredicate = EntityPredicate.fromJson(obj.get("entity"));
        return new Conditions(playerPredicate, entityPredicate);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, Entity entity) {
        this.trigger(player, conditions -> conditions.matches(player, entity));
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final EntityPredicate victim;

        //Indicates what conditions this criteria will have
        public Conditions(EntityPredicate.Extended player, EntityPredicate victim) {
            super(ID, player);
            this.victim = victim;
        }

        public boolean matches(ServerPlayerEntity player, Entity entity) {
            return victim.test(player, entity);
        }

        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("entity", victim.toJson());
            return jsonObject;
        }
    }

}
