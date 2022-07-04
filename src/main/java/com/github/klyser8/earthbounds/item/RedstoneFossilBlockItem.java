package com.github.klyser8.earthbounds.item;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RedstoneFossilBlockItem extends BlockItem {

    public RedstoneFossilBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(this.getFlavorText().formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
    }

    public MutableText getFlavorText() {
        return MutableText.of(new TranslatableTextContent("block.earth.redstone_fossil.desc"));
    }
}
