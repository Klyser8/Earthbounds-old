package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.item.EarthboundItem;
import com.github.klyser8.earthbounds.item.ShimmerShellItem;
import com.github.klyser8.earthbounds.item.flingshot.FlingingPotionItem;
import com.github.klyser8.earthbounds.item.flingshot.FlingshotItem;
import com.github.klyser8.earthbounds.item.GlowGreaseItem;
import com.github.klyser8.earthbounds.item.RedstoneFossilBlockItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.TorchBlock;
import net.minecraft.enchantment.EnchantmentHelper;
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
            Earthbounds.LOGGER.log(Level.DEBUG, "Light level: " +
                    context.getWorld().getLightLevel(context.getBlockPos().add(0, 1, 0)));
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
                        Earthbounds.LOGGER.log(Level.DEBUG, "Nothing found!");
                    } else {
                        for (BlockPos pos : matchList) {
                            Earthbounds.LOGGER.log(Level.DEBUG, "Found at location: "
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
                System.out.println(torchPos);
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
    public static final Item PERTILYO_ROD = new EarthboundItem((new Item.Settings()).group(EarthboundItemGroup.MISC).rarity(Rarity.UNCOMMON), true);
    public static final Item CARBORANEA_BUCKET = new EntityBucketItem(EarthboundEntities.CARBORANEA,
            Fluids.LAVA, EarthboundSounds.CARBORANEA_BUCKET_EMPTY, new Item.Settings().maxCount(1).group(EarthboundItemGroup.MISC));
    public static final Item FLINGING_POTION = new FlingingPotionItem((new Item.Settings().maxCount(1).group(EarthboundItemGroup.BREWING)));
    public static final Item AMETHYST_DUST = new Item(new Item.Settings().group(EarthboundItemGroup.BREWING));
    public static final Item GLOW_GREASE = new GlowGreaseItem(EarthboundBlocks.GLOW_GREASE_SPLAT, new Item.Settings().group(EarthboundItemGroup.PLACEABLES));
    public static final Item FLINGSHOT = new FlingshotItem(new Item.Settings().group(EarthboundItemGroup.COMBAT).rarity(Rarity.UNCOMMON).maxDamage(320));
    public static final Item SHIMMER_SHELL = new ShimmerShellItem(new Item.Settings().group(EarthboundItemGroup.COMBAT).maxCount(16));

    public static final Item CARBORANEA_SPAWN_EGG = new SpawnEggItem(EarthboundEntities.CARBORANEA, 4671303,
            13913600, new Item.Settings().group(EarthboundItemGroup.MISC));
    public static final Item RUBRO_SPAWN_EGG = new SpawnEggItem(EarthboundEntities.RUBRO, 3618630,
            14417920, new Item.Settings().group(EarthboundItemGroup.MISC));
    public static final Item PERTILYO_SPAWN_EGG = new SpawnEggItem(EarthboundEntities.PERTILYO, 15105367,
            16565175, new Item.Settings().group(EarthboundItemGroup.MISC));

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "debug_item"), DEBUG_ITEM);
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
                new Identifier(Earthbounds.MOD_ID, "flingshot"), FLINGSHOT);

        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "carboranea_spawn_egg"), CARBORANEA_SPAWN_EGG);
        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "rubro_spawn_egg"), RUBRO_SPAWN_EGG);
        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "pertilyo_spawn_egg"), PERTILYO_SPAWN_EGG);

        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.UNCOMMON).group(EarthboundItemGroup.PLACEABLES)));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "gilded_redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.GILDED_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.RARE).group(EarthboundItemGroup.PLACEABLES)));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "deepslate_redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.DEEPSLATE_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.UNCOMMON).group(EarthboundItemGroup.PLACEABLES)));
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "deepslate_gilded_redstone_fossil"),
                new RedstoneFossilBlockItem(EarthboundBlocks.DEEPSLATE_GILDED_REDSTONE_FOSSIL_BLOCK,
                        new FabricItemSettings().rarity(Rarity.RARE).group(EarthboundItemGroup.PLACEABLES)));
        registerModelPredicates();
    }

    private static void registerModelPredicates() {
        Identifier pullingIdentifier = new Identifier(Earthbounds.MOD_ID, "pull");
        FabricModelPredicateProviderRegistry.register(FLINGSHOT, pullingIdentifier, (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0;
            }
            if (entity.getActiveItem() != stack) {
                return 0;
            }
            if (EnchantmentHelper.get(stack).containsKey(EarthboundEnchantments.AUTOMATION)) {
                return (float) (stack.getMaxUseTime() - entity.getItemUseTimeLeft())
                        % FlingshotItem.CHARGE_TIME / FlingshotItem.CHARGE_TIME;
            } else {
                return (float) (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / FlingshotItem.CHARGE_TIME;
            }
        });
    }
}
