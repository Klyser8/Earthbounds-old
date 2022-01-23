package com.github.klyser8.earthbounds.util;

import net.minecraft.util.math.Vec3d;

public class EarthMath {

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

}
