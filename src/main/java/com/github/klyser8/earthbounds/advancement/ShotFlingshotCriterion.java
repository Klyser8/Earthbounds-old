package com.github.klyser8.earthbounds.advancement;

import com.github.klyser8.earthbounds.Earthbounds;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.ShotCrossbowCriterion;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ShotFlingshotCriterion extends AbstractCriterion<ShotFlingshotCriterion.Conditions> {

    static final Identifier ID = new Identifier(Earthbounds.MOD_ID, "shot_flingshot");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended,
                                         AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(extended, itemPredicate);
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack) {
        this.trigger(player, (conditions) -> conditions.matches(stack));
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final ItemPredicate item;

        //Indicates what conditions this criteria will have
        public Conditions(EntityPredicate.Extended player, ItemPredicate item) {
            super(ShotFlingshotCriterion.ID, player);
            this.item = item;
        }

        public boolean matches(ItemStack stack) {
            return this.item.test(stack);
        }

        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("item", this.item.toJson());
            return jsonObject;
        }
    }

}
