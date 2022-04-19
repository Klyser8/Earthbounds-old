package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.entity.CopperBuckEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import org.jetbrains.annotations.Nullable;

public class EarthboundDamageSource extends DamageSource {

    public static final String SHIMMER_EXPLOSION_NAME = "shimmer_explosion";
    public static final String SHIMMER_EXPLOSION_PLAYER_NAME = "shimmer_explosion.player";
    public static final String COPPER_BUCK_PLAYER_NAME = "copper_buck.player";
    public static final String COPPER_BUCK_NAME = "copper_buck";

    protected EarthboundDamageSource(String name) {
        super(name);
    }

    public static DamageSource shimmerExplosion(@Nullable LivingEntity attacker) {
        if (attacker != null) {
            return new EntityDamageSource(SHIMMER_EXPLOSION_PLAYER_NAME, attacker)
                    .setScaledWithDifficulty().setExplosive();
        }
        return new EarthboundDamageSource(SHIMMER_EXPLOSION_NAME)
                .setScaledWithDifficulty().setExplosive();
    }

    public static DamageSource copperBuck(CopperBuckEntity projectile, @Nullable Entity attacker) {
        if (attacker != null) {
            return new ProjectileDamageSource(COPPER_BUCK_PLAYER_NAME, projectile, attacker).setProjectile();
        }
        return new ProjectileDamageSource(COPPER_BUCK_NAME, projectile, null)
                    .setProjectile();
    }
}
