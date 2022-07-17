package com.github.klyser8.earthbounds.event;

import com.github.klyser8.earthbounds.registry.EarthboundItems;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.AlternativeLootCondition;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.LootFunctionManager;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.*;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.DesertPyramidFeature;
import net.minecraft.world.gen.feature.StructureFeature;

public class LootTableLoadingEventHandler {

    public static void init() {
        LootTableLoadingCallback.EVENT.register((resourceManager, manager, id, supplier, setter) -> {
            if (id.equals(LootTables.DESERT_PYRAMID_CHEST)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(EarthboundItems.PRIMORDIAL_REDSTONE))
                        .withFunction(SetCountLootFunction.builder(UniformLootNumberProvider.create(-1, 3)).build())
                        .withCondition(RandomChanceLootCondition.builder(0.5f).build());
                supplier.pool(poolBuilder);
            }
            if (id.equals(LootTables.JUNGLE_TEMPLE_CHEST)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(EarthboundItems.PRIMORDIAL_REDSTONE))
                        .withFunction(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 2)).build())
                        .withCondition(RandomChanceLootCondition.builder(0.25f).build());
                supplier.pool(poolBuilder);
            }
            if (id.equals(LootTables.ABANDONED_MINESHAFT_CHEST)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(EarthboundItems.PERTILYO_FRAGMENT))
                        .withFunction(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1)).build())
                        .withCondition(RandomChanceLootCondition.builder(0.2f).build());
                supplier.pool(poolBuilder);
            }
        });
    }

}
