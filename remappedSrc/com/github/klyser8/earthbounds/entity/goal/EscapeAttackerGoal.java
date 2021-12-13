package com.github.klyser8.earthbounds.entity.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * Goal which has the entity flee a possible attacker.
 */
public class EscapeAttackerGoal extends Goal {

    protected final PathAwareEntity mob;
    //Speed multiplier! Changes the speed which the mob should execute the goal at, based on its speed attribute.
    //e.g. if speed attribute is 2.0 and below field is 2.0, the entity will move at speed 4.0
    protected final double speed;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected boolean active;

    public EscapeAttackerGoal(PathAwareEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    /**
     * Goal starts only if the mob has an attacker and if {@link #findTarget()} returns true.
     *
     * @return true if the above is satisfied
     */
    @Override
    public boolean canStart() {
        if (this.mob.getAttacker() == null) {
            return false;
        }
        return this.findTarget();
    }

    /**
     * Picks a semi-random location to set as a target.
     *
     * @return true if a valid location is found, false otherwise.
     */
    protected boolean findTarget() {
        Vec3d vec3d = NoPenaltyTargeting.find(this.mob, 10, 4);
        if (vec3d == null) {
            return false;
        }
        this.targetX = vec3d.x;
        this.targetY = vec3d.y;
        this.targetZ = vec3d.z;
        return true;
    }

    /**
     * Whether the goal is active.
     *
     * @return true if it is active, false otherwise.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Starts the goal, setting active to true and having the mob navigate to the target location.
     */
    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
        this.active = true;
    }

    @Override
    public void stop() {
        this.active = false;
    }

    /**
     * The goal should continue if the mob's navigation isn't idle (current path = null or finished)
     */
    @Override
    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle();
    }
}
