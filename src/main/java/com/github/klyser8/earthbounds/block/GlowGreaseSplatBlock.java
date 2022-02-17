package com.github.klyser8.earthbounds.block;

import com.github.klyser8.earthbounds.registry.EarthboundParticles;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.*;
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

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onSteppedOn(world, pos, state, entity);
        if (world.isClient &&
                (entity.getVelocity().getX() != 0 || entity.getVelocity().getZ() != 0)) {
            Vec3d vel = entity.getVelocity();
            if (world.getTime() % 5 == 0) {
                world.addParticle(EarthboundParticles.GREASE_POP,
                        entity.getParticleX(0.5), entity.getBodyY(0.05), entity.getParticleZ(0.5),
                        -vel.x / 4, 0.01, -vel.z / 4);
            }
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        for (Direction dir : getDirections(state)) {
            Vec3d particlePos = calculateParticlePos(dir, pos, random);
            if (particlePos == null) {
                continue;
            }
            world.addParticle(EarthboundParticles.GREASE_CHUNK,
                    particlePos.x,
                    particlePos.y,
                    particlePos.z,
                    0.0, 0.0, 0.0);
        }
    }

    private static boolean hasDirection(BlockState state, Direction direction) {
        BooleanProperty booleanProperty = AbstractLichenBlock.getProperty(direction);
        return state.contains(booleanProperty) && state.get(booleanProperty);
    }

    private List<Direction> getDirections(BlockState state) {
        List<Direction> dirs = new ArrayList<>();
        for (Direction dir : DIRECTIONS) {
            if (hasDirection(state, dir)) {
                dirs.add(dir);
            }
        }
        return dirs;
    }

    private Vec3d calculateParticlePos(Direction dir, BlockPos pos, Random random) {
        Vec3d particlePos;
        if (dir == Direction.UP) {
            particlePos = Vec3d.of(pos).add(
                    dir.getOffsetX() / 10.0 + random.nextDouble(),
                    dir.getOffsetY() / 10.0 + 1,
                    dir.getOffsetZ() / 10.0 + random.nextDouble());
        } else if (dir == Direction.NORTH) {
            particlePos = Vec3d.of(pos).add(
                    dir.getOffsetX() / 10.0 + random.nextDouble(),
                    dir.getOffsetY() / 10.0 + random.nextDouble(),
                    dir.getOffsetZ() + 1.05);
        } else if (dir == Direction.EAST) {
            particlePos = Vec3d.of(pos).add(
                    dir.getOffsetX() / 10.0 + 0.85,
                    dir.getOffsetY() / 10.0 + random.nextDouble(),
                    dir.getOffsetZ() / 10.0 + random.nextDouble());
        } else if (dir == Direction.SOUTH) {
            particlePos = Vec3d.of(pos).add(
                    dir.getOffsetX() / 10.0 + random.nextDouble(),
                    dir.getOffsetY() / 10.0 + random.nextDouble(),
                    dir.getOffsetZ() / 10.0 + 0.85);
        } else if (dir == Direction.WEST) {
            particlePos = Vec3d.of(pos).add(
                    dir.getOffsetX() / 10.0 + 0.15,
                    dir.getOffsetY() / 10.0 + random.nextDouble(),
                    dir.getOffsetZ() / 10.0 + random.nextDouble());
        } else {
            particlePos = null;
        }
        return particlePos;
    }
}
