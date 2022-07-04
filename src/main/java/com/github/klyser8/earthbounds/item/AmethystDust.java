package com.github.klyser8.earthbounds.item;

import com.github.klyser8.earthbounds.registry.EarthboundParticles;
import com.github.klyser8.earthbounds.registry.EarthboundsAdvancementCriteria;
import com.github.klyser8.earthbounds.util.EarthMath;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class AmethystDust extends Item {
    public AmethystDust(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        Vec3d origin = user.getEyePos();
        ItemStack stack = user.getStackInHand(hand);
        Vec3d direction = user.getRotationVector().normalize();
        trigger(world, origin, direction, false);
        if (!world.isClient) {
            EarthboundsAdvancementCriteria.USED_AMETHYST_DUST.trigger((ServerPlayerEntity) user, stack);
        }
        if (!user.getAbilities().creativeMode) {
            stack.decrement(1);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.success(stack, world.isClient);
    }

    /**
     * Method used by dispensers
     */
    public void trigger(World world, Vec3d origin, Vec3d direction, boolean isServer) {
        Vec3d target = origin.add(direction.multiply(30)).add(0, -5, 0);
        Random random = world.random;
        if (!world.isClient) {
            for (int i = 0; i < 50; i++) {
                Vec3d modTargetLoc = target.add(
                        random.nextInt(40) - 20,
                        random.nextInt(20) - 5,
                        random.nextInt(40) - 20);
                Vec3d dir = EarthMath.dirBetweenVecs(origin, modTargetLoc).normalize();
                float speed = random.nextFloat() / 4 + 0.01f;
                ((ServerWorld) world).spawnParticles(EarthboundParticles.AMETHYST_SHIMMER, origin.x, origin.y, origin.z,
                        0, dir.x * speed, dir.y * speed, dir.z * speed, 1);
            }
        }
        if (world.isClient && !isServer) {
            world.playSound(origin.x, origin.y, origin.z, SoundEvents.BLOCK_AMETHYST_CLUSTER_HIT,
                    SoundCategory.AMBIENT, 0.7f, 1.25f, true);
        } else if (isServer) {
            world.playSound(null, origin.x, origin.y, origin.z,
                    SoundEvents.BLOCK_AMETHYST_CLUSTER_HIT, SoundCategory.AMBIENT, 0.7f, 1.25f);
        }
    }

//
//    float x = -MathHelper.sin(yaw * ((float)Math.PI / random.nextInt(180))) * 0.1f;
//    float y = -MathHelper.sin((pitch) * ((float)Math.PI / 90)) * 0.1f;
//    float z = MathHelper.cos(yaw * ((float)Math.PI / random.nextInt(180))) * 0.1f;
}
