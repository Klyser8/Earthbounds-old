package com.github.klyser8.earthbounds.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.ToIntFunction;

public class GlowGreaseSplatBlock extends AbstractLichenBlock {

    public GlowGreaseSplatBlock(Settings settings) {
        super(settings);
    }

    /**
     * {@return a function that receives a {@link BlockState} and returns the luminance for the state}
     * If the lichen has no visible sides, it supplies 0.
     *
     * @apiNote The return value is meant to be passed to
     * {@link AbstractBlock.Settings#luminance} builder method.
     *
     * @param luminance luminance supplied when the lichen has at least one visible side
     */
    public static ToIntFunction<BlockState> getLuminanceSupplier(int luminance) {
        return state -> GlowGreaseSplatBlock.hasAnyDirection(state) ? luminance : 0;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }
}
