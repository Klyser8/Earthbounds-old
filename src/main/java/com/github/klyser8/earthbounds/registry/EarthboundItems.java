package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.entity.RubroEntity;
import com.github.klyser8.earthbounds.util.AdvancedBlockPos;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
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

public class EarthboundItems {

    public static final Item DEBUG_ITEM = new Item(new FabricItemSettings().rarity(Rarity.EPIC)) {
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            if (context.getWorld().getBlockState(context.getBlockPos()).getBlock().equals(Blocks.REDSTONE_ORE) ||
                    context.getWorld().getBlockState(context.getBlockPos()).getBlock().equals(Blocks.DEEPSLATE_REDSTONE_ORE)) {
                if (context.getWorld().getEntitiesByType(EarthboundEntities.RUBRO, Box.of(Vec3d.ofCenter(context.getBlockPos()),
                        2, 2, 2), rubroEntity -> rubroEntity.getPower() >= 0 &&
                        !rubroEntity.isDead()).size() == 0) {
                    //Makes sure the powering block is exposed to air, and does not have
                    //more than two air blocks below it.
                    if (getFaceExposedToAir(context.getWorld(), context.getBlockPos()) != null
                            && !context.getWorld().getBlockState(context.getBlockPos().mutableCopy().down()).isAir()) {
                        System.out.println("It fits!");
                    }
                }
            }
            return super.useOnBlock(context);
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
                new BlockItem(EarthboundBlocks.REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "gilded_redstone_fossil"),
                new BlockItem(EarthboundBlocks.GILDED_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "deepslate_redstone_fossil"),
                new BlockItem(EarthboundBlocks.DEEPSLATE_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "deepslate_gilded_redstone_fossil"),
                new BlockItem(EarthboundBlocks.DEEPSLATE_GILDED_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
    }
}
