package com.github.klyser8.earthbounds.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class EarthMath {

    private static final Random random = new Random();

    /**
     * Returns a direction between two vectors.
     *
     * @param start
     * @param end
     * @return
     */
    public static Vec3d dirBetweenVecs(Vec3d start, Vec3d end) {
        return end.subtract(start).normalize();
    }

    public static Vec3d calculateVelocity(Entity shooter, float pitch, float yaw,
                                          float roll, float speed, float divergence) {
        float f = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        float g = -MathHelper.sin((pitch + roll) * ((float)Math.PI / 180));
        float h = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        Vec3d velocity = calculateVelocityNoShooter(f, g, h, speed, divergence);
        Vec3d vec3d = shooter.getVelocity();
        return velocity.add(vec3d.x, shooter.isOnGround() ? 0.0 : vec3d.y, vec3d.z);
    }

    private static Vec3d calculateVelocityNoShooter(double x, double y, double z, float speed, float divergence) {
        return new Vec3d(x, y, z).normalize().add(random.nextGaussian() *
                (double)0.0075f * (double)divergence, random.nextGaussian() * (double)0.0075f *
                (double)divergence, random.nextGaussian() * (double)0.0075f * (double)divergence).multiply(speed);
    }

    /**
     * Iterates all blocks downwards until the world's low height limit, and returns the first solid block position it
     * finds.
     *
     * @param world the current world
     * @param pos the position to start the search from
     * @return the solid position, or null if none is found.
     */
    public static BlockPos getClosestSolidBlockBelow(World world, BlockPos pos) {
        for (int y = pos.getY(); y > world.getBottomY(); y--) {
            if (world.getBlockState(pos.withY(y)).isSolidBlock(world, pos.withY(y))) {
                return pos.withY(y);
            }
        }
        return null;
    }
}
