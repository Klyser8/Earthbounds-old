package com.github.klyser8.earthbounds.item;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.OriginsCallbacks;
import com.github.klyser8.earthbounds.client.ClientCallbacks;
import com.github.klyser8.earthbounds.registry.EarthboundItems;
import com.github.klyser8.earthbounds.registry.EarthboundSounds;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class RedstoneFoodItem extends Item {

    private final int maxUseTime;

    public RedstoneFoodItem(Settings settings, int maxUseTime) {
        super(settings);
        this.maxUseTime = maxUseTime;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return maxUseTime;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (FabricLoaderImpl.INSTANCE.isModLoaded("origins")) {
            if (OriginsCallbacks.doesPlayerHaveRedstoneFoodPower(user)) {
                FoodComponent component = user.getStackInHand(hand).getItem().getFoodComponent();
                if (component == null || user.getHungerManager().getSaturationLevel() + component.getSaturationModifier() >
                        getMaxSaturationModifier(user.getStackInHand(hand))) {
                    return TypedActionResult.fail(user.getStackInHand(hand));
                } else {
                    return super.use(world, user, hand);
                }
            }
        }
        return TypedActionResult.fail(user.getStackInHand(hand));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        int particleAmount = stack.getItem().getFoodComponent() == null ? 0 : (int) stack.getItem().getFoodComponent().getSaturationModifier();
        if (world.isClient && user instanceof PlayerEntity player) {
            for (int i = 0; i < particleAmount * 4; i++) {
                world.addParticle(DustParticleEffect.DEFAULT,
                        user.getParticleX(0.5), user.getRandomBodyY(), user.getParticleZ(0.5),
                        0, 0, 0);
            }
            if (player.getHungerManager().getSaturationLevel() <= 8) {
                ClientCallbacks.startPoweredSoundInstance(player);
            }
        }
        return super.finishUsing(stack, world, user);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.EAT;
    }

    @Override
    public SoundEvent getEatSound() {
        return EarthboundSounds.RUBRO_CREAK;
    }

    public float getMaxSaturationModifier(ItemStack stack) {
        if (stack.getItem().getFoodComponent() == null) {
            return 0;
        }
        return stack.getItem().getFoodComponent().getSaturationModifier() * 3;
    }

}
