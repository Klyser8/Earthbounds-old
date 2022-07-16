package com.github.klyser8.earthbounds.event;

import com.github.klyser8.earthbounds.registry.EarthboundItems;
import net.fabricmc.fabric.api.loot.v2.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.*;

public class LootTableLoadingEventHandler {

    public static void init() {

        LootTableEvents.MODIFY.register((resourceManager, manager, id, supplier, setter) -> {
            if (id.equals(LootTables.DESERT_PYRAMID_CHEST)) {
                LootPool poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(EarthboundItems.PRIMORDIAL_REDSTONE))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(-1, 3)).build())
                        .conditionally(RandomChanceLootCondition.builder(0.5f).build()).build();
                supplier.pool(poolBuilder);
            }
            if (id.equals(LootTables.JUNGLE_TEMPLE_CHEST)) {
                LootPool poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(EarthboundItems.PRIMORDIAL_REDSTONE))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 2)).build())
                        .conditionally(RandomChanceLootCondition.builder(0.25f).build()).build();
                supplier.pool(poolBuilder);
            }
            if (id.equals(LootTables.ABANDONED_MINESHAFT_CHEST)) {
                LootPool poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(EarthboundItems.PERTILYO_FRAGMENT))
                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1)).build())
                        .conditionally(RandomChanceLootCondition.builder(0.2f).build()).build();
                supplier.pool(poolBuilder);
            }
        });
    }

}
