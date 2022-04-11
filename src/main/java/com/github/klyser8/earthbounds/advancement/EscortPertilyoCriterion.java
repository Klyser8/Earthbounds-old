package com.github.klyser8.earthbounds.advancement;

import com.github.klyser8.earthbounds.Earthbounds;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EscortPertilyoCriterion extends AbstractCriterion<EscortPertilyoCriterion.Conditions> {

    static final Identifier ID = new Identifier(Earthbounds.MOD_ID, "escort_pertilyo");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj,
                                            EntityPredicate.Extended playerPredicate,
                                            AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Conditions(playerPredicate);
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, conditions -> true);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static class Conditions extends AbstractCriterionConditions {

        public Conditions(EntityPredicate.Extended player) {
            super(ID, player);
        }
    }

}
