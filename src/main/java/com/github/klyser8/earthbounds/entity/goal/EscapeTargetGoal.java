package com.github.klyser8.earthbounds.entity.goal;

import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class EscapeTargetGoal extends Goal {

    protected final PathAwareEntity mob;
    protected final double speed;
    protected double targetX;
    protected double targetY;
    protected double targetZ;

    public EscapeTargetGoal(PathAwareEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (mob.getTarget() == null) {
            return false;
        }
        return findTargetPos();
    }

    @Override
    public void start() {
        mob.getNavigation().startMovingTo(targetX, targetY, targetZ, speed);
    }

    @Override
    public boolean shouldContinue() {
        return !mob.getNavigation().isIdle();
    }

    protected boolean findTargetPos() {
        Vec3d vec3d = NoPenaltyTargeting.find(mob, 5, 4);
        if (vec3d == null) {
            return false;
        }
        targetX = vec3d.x;
        targetY = vec3d.y;
        targetZ = vec3d.z;
        return true;
    }
}
