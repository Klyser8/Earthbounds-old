package com.github.klyser8.earthbounds.registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import org.jetbrains.annotations.Nullable;

public class ShimmerDamageSource extends DamageSource {

    public static final String SHIMMER_EXPLOSION_NAME = "shimmer_explosion";
    public static final String SHIMMER_EXPLOSION_PLAYER_NAME = "shimmer_explosion.player";
    public static final String SHIMMER_SHELL_NAME = "shimmer_shell";

    protected ShimmerDamageSource(String name) {
        super(name);
    }

    public static DamageSource shimmerExplosion(@Nullable LivingEntity attacker) {
        if (attacker != null) {
            return new EntityDamageSource(SHIMMER_EXPLOSION_PLAYER_NAME, attacker)
                    .setScaledWithDifficulty().setExplosive();
        }
        return new ShimmerDamageSource(SHIMMER_EXPLOSION_NAME)
                .setScaledWithDifficulty().setExplosive();
    }

    public static DamageSource shell(Entity projectile, @Nullable Entity attacker) {
        return new ProjectileDamageSource(SHIMMER_SHELL_NAME, projectile, attacker)
                    .setProjectile();
    }
}
