package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.advancement.*;
import net.minecraft.advancement.criterion.Criteria;

public class EarthboundsAdvancementCriteria {

    public static final UsedAmethystDustCriterion USED_AMETHYST_DUST = new UsedAmethystDustCriterion();
    public static final ShotFlingshotCriterion SHOT_FLINGSHOT = new ShotFlingshotCriterion();
    public static final ShootGlowGreaseAgainstWallCriterion SHOOT_GLOW_GREASE_AGAINST_WALL =
            new ShootGlowGreaseAgainstWallCriterion();
    public static final KilledByShimmerShellCriterion KILLED_BY_SHIMMER_SHELL =
            new KilledByShimmerShellCriterion();
    public static final GrowUpRubroCriterion GROW_UP_RUBRO =
            new GrowUpRubroCriterion();
    public static final KilledByRubroCriterion KILLED_BY_RUBRO =
            new KilledByRubroCriterion();
    public static final HitByFlingingPotionCriterion HIT_BY_FLINGING_POTION =
            new HitByFlingingPotionCriterion();
    public static final HitByCopperBuckCriterion HIT_BY_COPPER_BUCK =
            new HitByCopperBuckCriterion();
    public static final BreakRedstoneFossilCriterion BREAK_REDSTONE_FOSSIL =
            new BreakRedstoneFossilCriterion();
    public static final EscortPertilyoCriterion ESCORT_PERTILYO =
            new EscortPertilyoCriterion();

    public static void register() {
        Criteria.register(USED_AMETHYST_DUST);
        Criteria.register(SHOT_FLINGSHOT);
        Criteria.register(SHOOT_GLOW_GREASE_AGAINST_WALL);
        Criteria.register(GROW_UP_RUBRO);
        Criteria.register(KILLED_BY_RUBRO);
        Criteria.register(KILLED_BY_SHIMMER_SHELL);
        Criteria.register(HIT_BY_FLINGING_POTION);
        Criteria.register(HIT_BY_COPPER_BUCK);
        Criteria.register(BREAK_REDSTONE_FOSSIL);
        Criteria.register(ESCORT_PERTILYO);
    }

}
