package com.colorblocks;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class ColorBlockItem extends BlockItem {

    public final int r5, g5, b5, blockType;

    public ColorBlockItem(Block block, Item.Properties props, int r5, int g5, int b5, int blockType) {
        super(block, props);
        this.r5 = r5;
        this.g5 = g5;
        this.b5 = b5;
        this.blockType = blockType;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        String hex = BlockRegistry.colorName(r5, g5, b5);
        char lum = getLuminanceChar();
        tooltip.add(Component.literal("\u00a7" + lum + "\u25A0 \u00a7r" + hex));
    }

    @Override
    public Component getName(ItemStack stack) {
        String hex = BlockRegistry.colorName(r5, g5, b5);
        char lum = getLuminanceChar();
        return Component.literal("\u00a7" + lum + "\u25A0 \u00a7r" + hex);
    }

    private char getLuminanceChar() {
        float r8 = r5 / 31.0f;
        float g8 = g5 / 31.0f;
        float b8 = b5 / 31.0f;
        float lum = 0.299f * r8 + 0.587f * g8 + 0.114f * b8;
        return lum > 0.5f ? 'f' : '0';
    }
}