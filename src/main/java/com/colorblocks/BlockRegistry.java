package com.colorblocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistry {

    public static final int STEPS = 32;
    public static final int TOTAL_COLORS = STEPS * STEPS * STEPS; // 32768

    public static final int TYPE_FULL = 0;
    public static final int TYPE_SLAB = 1;
    public static final int TYPE_VERTICAL_SLAB = 2;
    public static final int TYPE_COUNT = 3;

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.create(Registries.BLOCK, ColorBlocksMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.create(Registries.ITEM, ColorBlocksMod.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(
            Registries.BLOCK_ENTITY_TYPE, ColorBlocksMod.MOD_ID);

    public static void init() {}

    // 5-bit RGB (0-31 per channel) to hex string
    public static String colorName(int r5, int g5, int b5) {
        return String.format("%02X%02X%02X", r5, g5, b5);
    }

    public static String blockItemName(int r5, int g5, int b5, int type) {
        String suffix = switch (type) {
            case TYPE_FULL -> " Full Block";
            case TYPE_SLAB -> " Slab";
            case TYPE_VERTICAL_SLAB -> " Vertical Slab";
            default -> "";
        };
        return colorName(r5, g5, b5) + suffix;
    }

    public static int redFromIndex(int idx) { return (idx >> 10) & 0x1F; }
    public static int greenFromIndex(int idx) { return (idx >> 5) & 0x1F; }
    public static int blueFromIndex(int idx) { return idx & 0x1F; }

    public static void forEachColor(java.util.function.Consumer<int[]> callback) {
        for (int idx = 0; idx < TOTAL_COLORS; idx++) {
            callback.accept(new int[]{idx, redFromIndex(idx), greenFromIndex(idx), blueFromIndex(idx)});
        }
    }

    // 5-bit (0-31) to 8-bit (0-255)
    public static int to8bit(int v5) { return (v5 * 255 + 15) / 31; }

    public static int colorToMinecraft(int r5, int g5, int b5) {
        return (to8bit(r5) << 16) | (to8bit(g5) << 8) | to8bit(b5);
    }

    /**
     * Parse color from block ID in cb_NNNNN format.
     * NNNNN = global index (0 to 98303)
     *   colorIdx = NNNNN / 3
     *   type = NNNNN % 3
     * Returns int[]{r5, g5, b5, type}
     */
    public static int[] parseColorFromBlockId(ResourceLocation id) {
        String path = id.getPath();
        if (path.startsWith("cb_")) {
            try {
                int globalIdx = Integer.parseInt(path.substring("cb_".length()));
                int colorIdx = globalIdx / TYPE_COUNT;
                int type = globalIdx % TYPE_COUNT;
                return new int[]{redFromIndex(colorIdx), greenFromIndex(colorIdx), blueFromIndex(colorIdx), type};
            } catch (NumberFormatException e) {
                return new int[]{0, 0, 0, 0};
            }
        }
        return new int[]{0, 0, 0, 0};
    }

    public static int[] parseColorFromItemId(ResourceLocation id) {
        return parseColorFromBlockId(id);
    }
}
