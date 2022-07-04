package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.item.*;
import com.github.klyser8.earthbounds.item.flingshot.ShimmerShellItem;
import com.github.klyser8.earthbounds.item.flingshot.FlingingPotionItem;
import com.github.klyser8.earthbounds.item.flingshot.FlingshotItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.block.Block;
import net.minecraft.block.TorchBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

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
//            Earthbounds.LOGGER.log(Level.DEBUG, "Light level: " +
//                    context.getWorld().getLightLevel(context.getBlockPos().add(0, 1, 0)));
            World world = player.getWorld();
            ItemStack stack = player.getStackInHand(Hand.OFF_HAND);
            if (!world.isClient) {
                if (stack == null) return ActionResult.FAIL;
                if (stack.getItem() instanceof BlockItem blockItem) {
                    Block matchBlock = blockItem.getBlock();
                    List<BlockPos> matchList = new ArrayList<>();
                    for (int x = -64; x < 64; x++) {
                        for (int y = -32; y < 32; y++) {
                            for (int z = -64; z < 64; z++) {
                                BlockPos checkPos = context.getBlockPos().add(x, y, z);
                                if (world.getBlockState(checkPos).getBlock().equals(matchBlock)) {
                                    matchList.add(checkPos);
                                }
                            }
                        }
                    }
                    if (matchList.isEmpty()) {
                        Earthbounds.LOGGER.log(Level.DEBUG, "Nothing found!");
                    } else {
                        for (BlockPos pos : matchList) {
                            Earthbounds.LOGGER.log(Level.DEBUG, "Found at location: "
                                    + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
                            System.out.println("Found at location: "
                                    + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
                        }
                    }

                }
            }
            return ActionResult.SUCCESS;
        }

        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient) {
                BlockPos torchPos = BlockPos.findClosest(user.getBlockPos(),12, 12,
                        pos -> world.getBlockState(pos).getBlock() instanceof TorchBlock).orElse(null);
            }
            return super.use(world, user, hand);
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
            if (entity.isDead()) return ActionResult.FAIL;
            entity.damage(DamageSource.MAGIC, 1024);
            user.sendMessage(Text.of("Annihilation!"), true);
            user.playSound(SoundEvents.ENTITY_WITHER_BREAK_BLOCK, 0.5f, 2.0f);
            return ActionResult.SUCCESS;
        }
    };
    public static final Item EARTHBOUNDS_ICON = new Item(new FabricItemSettings().rarity(Rarity.EPIC));


    public static final Item PERTILYO_ROD = new EarthboundItem((new Item.Settings()).group(EarthboundItemGroup.MISC).rarity(Rarity.UNCOMMON).fireproof(), true);
    public static final Item CARBORANEA_BUCKET = new EntityBucketItem(EarthboundEntities.CARBORANEA,
            Fluids.LAVA, EarthboundSounds.CARBORANEA_BUCKET_EMPTY, new Item.Settings().maxCount(1).group(EarthboundItemGroup.MISC));
    public static final Item FLINGING_POTION = new FlingingPotionItem((new Item.Settings().maxCount(3).group(EarthboundItemGroup.BREWING)));
    public static final Item AMETHYST_DUST = new AmethystDust(new Item.Settings().group(EarthboundItemGroup.BREWING));
    public static final Item GLOW_GREASE = new GlowGreaseItem(EarthboundBlocks.GLOW_GREASE_SPLAT, new Item.Settings().group(EarthboundItemGroup.PLACEABLES));
    public static final Item FLINGSHOT = new FlingshotItem(EarthboundToolMaterials.COPPER,
            new Item.Settings().group(EarthboundItemGroup.COMBAT).rarity(Rarity.UNCOMMON).maxDamage(320));
    public static final Item SHIMMER_SHELL = new ShimmerShellItem(new Item.Settings().group(EarthboundItemGroup.COMBAT).maxCount(32));
    public static final Item COPPER_BUCK = new BuckItem(new Item.Settings().group(EarthboundItemGroup.COMBAT).maxCount(32), EarthboundEntities.COPPER_BUCK);
    public static final Item MADDER_BUCK = new BuckItem(new Item.Settings().group(EarthboundItemGroup.COMBAT).maxCount(32), EarthboundEntities.MADDER_BUCK);
    public static final Item PRIMORDIAL_REDSTONE = new RedstoneFoodItem(new Item.Settings().group(EarthboundItemGroup.MISC).rarity(Rarity.COMMON).food(EarthboundFoodComponents.PRIMORDIAL_REDSTONE), 32);
    public static final Item POWERED_BEETROOT = new RedstoneFoodItem(new Item.Settings().group(EarthboundItemGroup.MISC).rarity(Rarity.COMMON).food(EarthboundFoodComponents.POWERED_BEETROOT), 32);
    public static Item COBBLED_PEBBLE;
    public static Item ANDESITE_PEBBLE;
    public static Item DIORITE_PEBBLE;
    public static Item GRANITE_PEBBLE;
    public static Item DEEPSLATE_PEBBLE;
    public static Item REDSTONE_PEBBLE;
    public static Item RED_BRICK;
    public static Item BLUSHED_FLINTS;
    public static Item CRIMSON_QUARTZ;

    public static final Item CARBORANEA_SPAWN_EGG = new SpawnEggItem(EarthboundEntities.CARBORANEA, 4671303,
            13913600, new Item.Settings().group(EarthboundItemGroup.MISC));
    public static final Item RUBRO_SPAWN_EGG = new SpawnEggItem(EarthboundEntities.RUBRO, 3618630,
            14417920, new Item.Settings().group(EarthboundItemGroup.MISC));
    public static final Item PERTILYO_SPAWN_EGG = new SpawnEggItem(EarthboundEntities.PERTILYO, 15105367,
            16565175, new Item.Settings().group(EarthboundItemGroup.MISC));

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "debug_item"), DEBUG_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "earthbounds_icon"), EARTHBOUNDS_ICON);
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "pertilyo_rod"), PERTILYO_ROD);
        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "carboranea_bucket"), CARBORANEA_BUCKET);
        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "flinging_potion"), FLINGING_POTION);
        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "amethyst_dust"), AMETHYST_DUST);
        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "glow_grease"), GLOW_GREASE);
        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "shimmer_shell"), SHIMMER_SHELL);
        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "copper_buck"), COPPER_BUCK);
        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "madder_buck"), MADDER_BUCK);
        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "flingshot"), FLINGSHOT);

        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "carboranea_spawn_egg"), CARBORANEA_SPAWN_EGG);
        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "rubro_spawn_egg"), RUBRO_SPAWN_EGG);
        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "pertilyo_spawn_egg"), PERTILYO_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "primordial_redstone"), PRIMORDIAL_REDSTONE);
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "powered_beetroot"), POWERED_BEETROOT);
        if (FabricLoaderImpl.INSTANCE.isModLoaded("origins")) {
            COBBLED_PEBBLE = new RedstoneFoodItem(new Item.Settings().group(EarthboundItemGroup.MISC).rarity(Rarity.COMMON)
                    .food(EarthboundFoodComponents.COBBLED_PEBBLE), 16);
            ANDESITE_PEBBLE = new RedstoneFoodItem(new Item.Settings().group(EarthboundItemGroup.MISC).rarity(Rarity.COMMON)
                    .food(EarthboundFoodComponents.ANDESITE_PEBBLE), 16);
            DIORITE_PEBBLE = new RedstoneFoodItem(new Item.Settings().group(EarthboundItemGroup.MISC).rarity(Rarity.COMMON)
                    .food(EarthboundFoodComponents.DIORITE_PEBBLE), 16);
            GRANITE_PEBBLE = new RedstoneFoodItem(new Item.Settings().group(EarthboundItemGroup.MISC).rarity(Rarity.COMMON)
                    .food(EarthboundFoodComponents.GRANITE_PEBBLE), 16);
            DEEPSLATE_PEBBLE = new RedstoneFoodItem(new Item.Settings().group(EarthboundItemGroup.MISC).rarity(Rarity.COMMON)
                    .food(EarthboundFoodComponents.DEEPSLATE_PEBBLE), 16);
            REDSTONE_PEBBLE = new RedstoneFoodItem(new Item.Settings().group(EarthboundItemGroup.MISC).rarity(Rarity.COMMON)
                    .food(EarthboundFoodComponents.REDSTONE_PEBBLE), 16);
            RED_BRICK = new RedstoneFoodItem(new Item.Settings().group(EarthboundItemGroup.MISC).rarity(Rarity.COMMON)
                    .food(EarthboundFoodComponents.RED_BRICK), 48);
            BLUSHED_FLINTS = new RedstoneFoodItem(new Item.Settings().group(EarthboundItemGroup.MISC).rarity(Rarity.COMMON)
                    .food(EarthboundFoodComponents.BLUSHED_FLINTS), 48);
            CRIMSON_QUARTZ = new RedstoneFoodItem(new Item.Settings().group(EarthboundItemGroup.MISC).rarity(Rarity.COMMON)
                    .food(EarthboundFoodComponents.CRIMSON_QUARTZ), 48);

            Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "cobbled_pebble"), COBBLED_PEBBLE);
            Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "andesite_pebble"), ANDESITE_PEBBLE);
            Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "diorite_pebble"), DIORITE_PEBBLE);
            Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "granite_pebble"), GRANITE_PEBBLE);
            Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "deepslate_pebble"), DEEPSLATE_PEBBLE);
            Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "redstone_pebble"), REDSTONE_PEBBLE);
            Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "crimson_quartz"), CRIMSON_QUARTZ);
            Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "blushed_flints"), BLUSHED_FLINTS);
            Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "red_brick"), RED_BRICK);
        }
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "primordial_redstone_block"),
                new BlockItem(EarthboundBlocks.PRIMORDIAL_REDSTONE_BLOCK,
                        new FabricItemSettings().rarity(Rarity.COMMON).group(EarthboundItemGroup.PLACEABLES)));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.UNCOMMON).group(EarthboundItemGroup.PLACEABLES)));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "gilded_redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.GILDED_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.RARE).group(EarthboundItemGroup.PLACEABLES)));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "crystalline_redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.CRYSTALLINE_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.RARE).group(EarthboundItemGroup.PLACEABLES)));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "verdant_redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.VERDANT_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.RARE).group(EarthboundItemGroup.PLACEABLES)));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "charred_redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.CHARRED_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.RARE).group(EarthboundItemGroup.PLACEABLES)));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "crimson_redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.CRIMSON_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.RARE).group(EarthboundItemGroup.PLACEABLES)));

        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "deepslate_redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.DEEPSLATE_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.UNCOMMON).group(EarthboundItemGroup.PLACEABLES)));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "deepslate_gilded_redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.DEEPSLATE_GILDED_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.RARE).group(EarthboundItemGroup.PLACEABLES)));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "deepslate_crystalline_redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.DEEPSLATE_CRYSTALLINE_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.RARE).group(EarthboundItemGroup.PLACEABLES)));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "deepslate_verdant_redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.DEEPSLATE_VERDANT_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.RARE).group(EarthboundItemGroup.PLACEABLES)));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "deepslate_charred_redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.DEEPSLATE_CHARRED_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.RARE).group(EarthboundItemGroup.PLACEABLES)));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "deepslate_crimson_redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.DEEPSLATE_CRIMSON_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.RARE).group(EarthboundItemGroup.PLACEABLES)));
    }

}
