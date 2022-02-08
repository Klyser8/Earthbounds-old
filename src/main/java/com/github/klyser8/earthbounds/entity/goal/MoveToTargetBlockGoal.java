package com.github.klyser8.earthbounds.entity.goal;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.entity.RubroEntity;
import com.github.klyser8.earthbounds.util.EarthUtil;
import net.minecraft.block.Block;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import java.util.EnumSet;
import java.util.function.Predicate;

public class MoveToTargetBlockGoal extends Goal {

    protected final World world;
    protected final PathAwareEntity entity;
    protected final Predicate<BlockPos> predicate;
    protected long lastAttemptTime;
    protected final int cooldown;
    protected BlockPos targetPos;
    protected Path path;

    public MoveToTargetBlockGoal(PathAwareEntity entity, Predicate<BlockPos> predicate, int cooldown) {
        setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        this.cooldown = cooldown;
        this.entity = entity;
        this.predicate = predicate;
        world = entity.world;
        lastAttemptTime = world.getTime() - cooldown + 10;
    }

    @Override
    public boolean canStart() {
        if (EarthUtil.isOnCooldown(world.getTime(), lastAttemptTime, cooldown)) {
            return false;
        }
//        targetPos = EarthUtil.search(entity.getBlockPos(), 10, predicate);
        targetPos = BlockPos.findClosest(
                entity.getBlockPos(), 12, 12, predicate).orElse(null);
        lastAttemptTime = world.getTime();
        if (targetPos == null) {
            return false;
        }
        path = entity.getNavigation().findPathTo(targetPos, 1, 16);
        return path != null;
    }

    @Override
    public void start() {
        entity.getNavigation().startMovingAlong(path, 1.0);
    }

    @Override
    public void stop() {
        targetPos = null;
        entity.getNavigation().stop();
        entity.getNavigation().resetRangeMultiplier();
    }

    protected boolean isCloserThan(double distance) {
        if (targetPos == null) {
            return false;
        }
        return entity.getPos().distanceTo(Vec3d.ofCenter(targetPos)) < distance;
    }

    /*protected final World world;
    protected final PathAwareEntity entity;
    protected BlockSeeker blockSeeker;
    protected final Block[] blocks;

    public FindBlockGoal(PathAwareEntity entity, Block ...blocks) {
        this.entity = entity;
        this.blocks = blocks;
        world = entity.world;
        if (entity instanceof BlockSeeker blockSeeker) {
            this.blockSeeker = blockSeeker;
        } else {
            Earthbounds.LOGGER.log(Level.ERROR, "The entity must implement 'BlockSeeker'!");
        }
        new FindBlockGoal(entity, Blocks.ACACIA_BUTTON);
    }

    @Override
    public boolean canStart() {
        Block targetBlock = world.getBlockState(blockSeeker.getTargetBlockPos()).getBlock();
        boolean canStart = false;
        for (Block block : blocks) {
            if (targetBlock.equals(block)) {
                canStart = true;
                break;
            }
        }
        return canStart;
    }

    @Override
    public void start() {
        System.out.println("AYO");
    }

    @Override
    public boolean shouldContinue() {
        return canStart();
    }*/
}
