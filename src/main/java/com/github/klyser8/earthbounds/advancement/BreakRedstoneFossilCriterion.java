package com.github.klyser8.earthbounds.advancement;

import com.github.klyser8.earthbounds.Earthbounds;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class BreakRedstoneFossilCriterion extends AbstractCriterion<BreakRedstoneFossilCriterion.Conditions> {

    static final Identifier ID = new Identifier(Earthbounds.MOD_ID, "break_redstone_fossil");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj,
                                            EntityPredicate.Extended playerPredicate,
                                            AdvancementEntityPredicateDeserializer predicateDeserializer) {
        Block block = getBlock(obj);
        return new Conditions(playerPredicate, block);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, BlockState state) {
        this.trigger(player, conditions -> conditions.matches(state));
    }

    @Nullable
    private static Block getBlock(JsonObject obj) {
        if (obj.has("block")) {
            Identifier identifier = new Identifier(JsonHelper.getString(obj, "block"));
            return Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() ->
                    new JsonSyntaxException("Unknown block type '" + identifier + "'"));
        } else {
            return null;
        }
    }

    public static class Conditions extends AbstractCriterionConditions {

        private final Block block;

        //Indicates what conditions this criteria will have
        public Conditions(EntityPredicate.Extended player, @Nullable Block block) {
            super(ID, player);
            this.block = block;
        }

        public boolean matches(BlockState state) {
            return block == null || state.isOf(block);
        }

        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            if (block != null) {
                jsonObject.addProperty("block", Registry.BLOCK.getId(block).toString());
            }
            return jsonObject;
        }
    }

}
