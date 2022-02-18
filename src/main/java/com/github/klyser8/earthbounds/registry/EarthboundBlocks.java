package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.block.GlowGreaseSplatBlock;
import com.github.klyser8.earthbounds.block.RedstoneFossilBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.ToIntFunction;

public class EarthboundBlocks {

    public static final Material ORGANIC = new Material.Builder(MapColor.ORANGE).replaceable().allowsMovement().build();

    public static final Block REDSTONE_FOSSIL_BLOCK = new RedstoneFossilBlock(FabricBlockSettings
            .copyOf(Blocks.REDSTONE_ORE));
    public static final Block GILDED_REDSTONE_FOSSIL_BLOCK = new RedstoneFossilBlock(FabricBlockSettings
            .copyOf(Blocks.REDSTONE_ORE));
    public static final Block DEEPSLATE_REDSTONE_FOSSIL_BLOCK = new RedstoneFossilBlock(FabricBlockSettings
            .copyOf(Blocks.DEEPSLATE_REDSTONE_ORE));
    public static final Block DEEPSLATE_GILDED_REDSTONE_FOSSIL_BLOCK = new RedstoneFossilBlock(FabricBlockSettings
            .copyOf(Blocks.DEEPSLATE_REDSTONE_ORE));
    public static final GlowGreaseSplatBlock GLOW_GREASE_SPLAT = new GlowGreaseSplatBlock(FabricBlockSettings
            .of(ORGANIC).noCollision().strength(0.2f).jumpVelocityMultiplier(0.8f)
            .luminance(GlowGreaseSplatBlock.getLuminanceSupplier(7))
            .sounds(BlockSoundGroup.HONEY)
            .slipperiness(1.0f));

    public static void register() {
        Registry.register(Registry.BLOCK, new Identifier(Earthbounds.MOD_ID, "redstone_fossil"),
                REDSTONE_FOSSIL_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Earthbounds.MOD_ID, "gilded_redstone_fossil"),
                GILDED_REDSTONE_FOSSIL_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Earthbounds.MOD_ID, "deepslate_redstone_fossil"),
                DEEPSLATE_REDSTONE_FOSSIL_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Earthbounds.MOD_ID, "deepslate_gilded_redstone_fossil"),
                DEEPSLATE_GILDED_REDSTONE_FOSSIL_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Earthbounds.MOD_ID, "glow_grease_splat"),
                GLOW_GREASE_SPLAT);
    }

    //Code kindly stolen from Mojang
    private static ToIntFunction<BlockState> createLightLevelFromLitBlockState(int litLevel) {
        return state -> state.get(Properties.LIT) ? litLevel : 0;
    }
}
