package com.github.klyser8.earthbounds.entity.mob;

import com.github.klyser8.earthbounds.registry.EarthboundDamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.Hand;
import software.bernie.geckolib3.core.IAnimatable;

public interface Earthen extends IAnimatable {

    Entity getLastDamager();

    void setLastDamager(Entity entity);

    String getLastDamageSourceName();

    void setLastDamageSourceName(String name);

    static float handleDamage(DamageSource source, LivingEntity entity, float damage) {
        if (entity instanceof Earthen) {
            if (isDamagePickaxe(source)
                    || source.getName().equalsIgnoreCase(EarthboundDamageSource.SHIMMER_EXPLOSION_NAME)
                    || source.getName().equalsIgnoreCase(EarthboundDamageSource.SHIMMER_EXPLOSION_PLAYER_NAME)
                    || source.getName().equalsIgnoreCase(EarthboundDamageSource.COPPER_BUCK_NAME)
                    || source.getName().equalsIgnoreCase(EarthboundDamageSource.COPPER_BUCK_PLAYER_NAME)
                    || source.getSource() instanceof Earthen) {
                return damage;
            }
        }
        damage /= 2.5f;
        return damage;
    }

    /**
     * If the current damage source has an attacker, it returns whether it is holding a pickaxe in main hand or not.
     * @param source the damage source
     * @return true if the entity is holding a pickaxe in the main hand
     */
    static boolean isDamagePickaxe(DamageSource source) {
        return source.getAttacker() instanceof LivingEntity living
                && living.getStackInHand(Hand.MAIN_HAND).getItem() instanceof PickaxeItem;
    }

}
