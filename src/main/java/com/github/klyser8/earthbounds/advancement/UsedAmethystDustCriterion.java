package com.github.klyser8.earthbounds.advancement;

import com.github.klyser8.earthbounds.Earthbounds;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class UsedAmethystDustCriterion extends AbstractCriterion<UsedAmethystDustCriterion.Conditions> {

    static final Identifier ID = new Identifier(Earthbounds.MOD_ID, "used_amethyst_dust");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate,
                                            AdvancementEntityPredicateDeserializer predicateDeserializer) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(obj.get("item"));
        return new UsedAmethystDustCriterion.Conditions(playerPredicate, itemPredicate);
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack) {
        this.trigger(player, (conditions) -> conditions.matches(stack));
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static class Conditions
            extends AbstractCriterionConditions {
        private final ItemPredicate item;

        public Conditions(EntityPredicate.Extended player, ItemPredicate item) {
            super(ID, player);
            this.item = item;
        }

        public boolean matches(ItemStack stack) {
            return this.item.test(stack);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("item", this.item.toJson());
            return jsonObject;
        }
    }

}
