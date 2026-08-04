package com.colorblocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistry {

    public static final int STEPS = 32;
    public static final int TOTAL_COLORS = STEPS * STEPS * STEPS; // 32768

    public static final int TYPE_FULL = 0;
    public static final int TYPE_SLAB = 1;
    public static final int TYPE_VERTICAL_SLAB = 2;
    public static final int TYPE_COUNT = 3;

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(ColorBlocksMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(ColorBlocksMod.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ColorBlocksMod.MOD_ID);

    public static int redFromIndex(int idx) { return (idx >> 10) & 0x1F; }
    public static int greenFromIndex(int idx) { return (idx >> 5) & 0x1F; }
    public static int blueFromIndex(int idx) { return idx & 0x1F; }

    public static void forEachColor(java.util.function.Consumer<int[]> callback) {
        for (int idx = 0; idx < TOTAL_COLORS; idx++) {
            callback.accept(new int[]{idx, redFromIndex(idx), greenFromIndex(idx), blueFromIndex(idx)});
        }
    }

    public static int to8bit(int v5) { return (v5 * 255 + 15) / 31; }

    public static String colorName(int r5, int g5, int b5) {
        return String.format("%02X%02X%02X", r5, g5, b5);
    }
}