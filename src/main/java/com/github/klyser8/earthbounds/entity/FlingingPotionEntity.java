package com.github.klyser8.earthbounds.entity;

import com.github.klyser8.earthbounds.client.EarthboundsClient;
import com.github.klyser8.earthbounds.item.flingshot.FlingingPotionItem;
import com.github.klyser8.earthbounds.registry.EarthboundItems;
import com.github.klyser8.earthbounds.registry.EarthboundParticles;
import com.github.klyser8.earthbounds.registry.EarthboundsAdvancementCriteria;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.network.Packet;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;

public class FlingingPotionEntity extends PotionEntity {

    private int red, green, blue = -1;

    public FlingingPotionEntity(EntityType<? extends FlingingPotionEntity> entityType, World world) {
        super(entityType, world);
    }

    public FlingingPotionEntity(LivingEntity owner, World world, double x, double y, double z) {
        super(world, x, y, z);
        setOwner(owner);
    }

    private void initColor() {
        int colorInt = PotionUtil.getColor(getItem());
        Color potionColor = new Color(colorInt);
        this.red = potionColor.getRed();
        this.green = potionColor.getGreen();
        this.blue = potionColor.getBlue();
    }

    @Override
    protected Item getDefaultItem() {
        return EarthboundItems.FLINGING_POTION;
    }

    @Override
    public void tick() {
        if (red == -1 || green == -1 || blue == -1) {
            initColor();
        }
        super.tick();
        Vec3d vel = getVelocity().normalize();
        DustColorTransitionParticleEffect effect = new DustColorTransitionParticleEffect(
                new Vec3f(this.red / 255f, this.green / 255f, this.blue / 255f),
                new Vec3f(0.8f, 0.8f, 0.8f), 1);
        for (int i = 0; i < 2; ++i) {
            //Plays particles slightly behind the potion
            ((ServerWorld) world).spawnParticles(effect,
                    getX() - vel.x / 2, getY() - vel.y / 2, getZ() - vel.z / 2,
                    1, 0, 0, 0, 0);
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!(getOwner() instanceof ServerPlayerEntity player)) {
            return;
        }
        Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);
        List<LivingEntity> list = this.world.getNonSpectatingEntities(LivingEntity.class, box);
        for (LivingEntity entity : list) {
            EarthboundsAdvancementCriteria.HIT_BY_FLINGING_POTION.trigger(player, entity, player.getEyePos(),
                    entity.getPos(), PotionUtil.getPotion(getItem()));
        }
    }
}
