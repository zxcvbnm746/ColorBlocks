package com.colorblocks;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class ColorBlockItem extends BlockItem {

    public final int r5, g5, b5, blockType, globalIdx;

    public ColorBlockItem(Block block, Item.Properties props, int r5, int g5, int b5, int blockType, int globalIdx) {
        super(block, props);
        this.r5 = r5;
        this.g5 = g5;
        this.b5 = b5;
        this.blockType = blockType;
        this.globalIdx = globalIdx;
    }

    @Override
    public void appendHoverText(ItemStack stack, ItemStack tool, TooltipFlag flag, List<Component> tooltip) {
        String hex = BlockRegistry.colorName(r5, g5, b5);
        String name = BlockRegistry.blockItemName(r5, g5, b5, blockType);
        char lum = getLuminanceChar();
        tooltip.add(Component.literal("§" + lum + "■ §r" + name));
        tooltip.add(Component.literal("§7RGB565: §" + lum + hex + " §7| §fIron Pickaxe §7| §8No Light"));
    }

    @Override
    public Component getName(ItemStack stack) {
        String hex = BlockRegistry.colorName(r5, g5, b5);
        String suffix = switch (blockType) {
            case BlockRegistry.TYPE_FULL -> " Full Block";
            case BlockRegistry.TYPE_SLAB -> " Slab";
            case BlockRegistry.TYPE_VERTICAL_SLAB -> " Vertical Slab";
            default -> "";
        };
        char lum = getLuminanceChar();
        return Component.literal("§" + lum + "■ §r" + hex + suffix);
    }

    private char getLuminanceChar() {
        // Convert 5-bit to 8-bit for luminance calculation
        float r8 = r5 / 31.0f;
        float g8 = g5 / 31.0f;
        float b8 = b5 / 31.0f;
        float lum = 0.299f * r8 + 0.587f * g8 + 0.114f * b8;
        return lum > 0.5f ? 'f' : '0';
    }
}
