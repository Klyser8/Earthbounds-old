package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.block.RedstoneFossilBlock;
import com.github.klyser8.earthbounds.entity.RubroEntity;
import com.github.klyser8.earthbounds.item.RedstoneFossilBlockItem;
import com.github.klyser8.earthbounds.util.AdvancedBlockPos;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EarthboundItems {

    public static final Item DEBUG_ITEM = new Item(new FabricItemSettings().rarity(Rarity.EPIC)) {

        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            PlayerEntity player = context.getPlayer();
            if (player == null) {
                return ActionResult.FAIL;
            }
            World world = player.getWorld();
            ItemStack stack = player.getStackInHand(Hand.OFF_HAND);
            if (!world.isClient) {
                if (stack == null) return ActionResult.FAIL;
                if (stack.getItem() instanceof BlockItem blockItem) {
                    Block matchBlock = blockItem.getBlock();
                    List<BlockPos> matchList = new ArrayList<>();
                    for (int x = -16; x < 16; x++) {
                        for (int y = -16; y < 16; y++) {
                            for (int z = -16; z < 16; z++) {
                                BlockPos checkPos = context.getBlockPos().add(x, y, z);
                                if (world.getBlockState(checkPos).getBlock().equals(matchBlock)) {
                                    matchList.add(checkPos);
                                }
                            }
                        }
                    }
                    if (matchList.isEmpty()) {
                        System.out.println("Nothing found!");
                    } else {
                        for (BlockPos pos : matchList) {
                            System.out.println("Found at location: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
                        }
                    }

                }
            }
            System.out.println();
            return ActionResult.SUCCESS;
        }

        public BlockPos getFaceExposedToAir(World world, BlockPos pos) {
            if (world.getBlockState(pos.mutableCopy().up()).isAir()) {
                return pos.up();
            } else if (world.getBlockState(pos.mutableCopy().north()).isAir()) {
                return pos.north();
            } else if (world.getBlockState(pos.mutableCopy().east()).isAir()) {
                return pos.east();
            } else if (world.getBlockState(pos.mutableCopy().south()).isAir()) {
                return pos.south();
            } else if (world.getBlockState(pos.mutableCopy().west()).isAir()) {
                return pos.west();
            } else if (world.getBlockState(pos.mutableCopy().down()).isAir()) {
                return pos.down();
            } else {
                return null;
            }
        }

        @Override
        public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
            entity.damage(DamageSource.MAGIC, 1024);
            user.sendMessage(Text.of("Annihilation!"), true);
            user.playSound(SoundEvents.ENTITY_WITHER_BREAK_BLOCK, 0.5f, 2.0f);
            return super.useOnEntity(stack, user, entity, hand);
        }
    };
    public static final Item CARBORANEA_BUCKET = new EntityBucketItem(EarthboundEntities.CARBORANEA,
            Fluids.LAVA, EarthboundSounds.CARBORANEA_BUCKET_EMPTY, new Item.Settings().maxCount(1).group(ItemGroup.MISC));

    public static final Item CARBORANEA_SPAWN_EGG = new SpawnEggItem(EarthboundEntities.CARBORANEA, 4671303,
            13913600, new Item.Settings().group(ItemGroup.MISC));
    public static final Item RUBRO_SPAWN_EGG = new SpawnEggItem(EarthboundEntities.RUBRO, 3618630,
            14417920, new Item.Settings().group(ItemGroup.MISC));

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "debug_item"), DEBUG_ITEM);
        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "carboranea_bucket"), CARBORANEA_BUCKET);
        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "carboranea_spawn_egg"), CARBORANEA_SPAWN_EGG);
        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "rubro_spawn_egg"), RUBRO_SPAWN_EGG);


        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.UNCOMMON)/*.group(ItemGroup.BUILDING_BLOCKS)*/));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "gilded_redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.GILDED_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.RARE)/*.group(ItemGroup.BUILDING_BLOCKS)*/));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "deepslate_redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.DEEPSLATE_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.UNCOMMON)/*.group(ItemGroup.BUILDING_BLOCKS)*/));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "deepslate_gilded_redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.DEEPSLATE_GILDED_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.RARE)/*.group(ItemGroup.BUILDING_BLOCKS)*/));
    }
}
