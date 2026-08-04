package com.colorblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod(ColorBlocksMod.MOD_ID)
public class ColorBlocksMod {
    public static final String MOD_ID = "colorblocks";
    public static final Logger LOG = LoggerFactory.getLogger(ColorBlocksMod.class);

    public static final List<BlockSupplier> BLOCK_SUPPLIERS = new ArrayList<>();
    public static final List<ItemSupplier> ITEM_SUPPLIERS = new ArrayList<>();

    public static final Map<String, DeferredHolder<Block, Block>> BLOCK_HOLDERS = new HashMap<>();

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ColorBlockEntity>> COLOR_BLOCK_ENTITY;

    public ColorBlocksMod(IEventBus bus) {
        long start = System.currentTimeMillis();

        BlockRegistry.forEachColor((info) -> {
            int idx = info[0], r = info[1], g = info[2], b = info[3];
            for (int type = 0; type < BlockRegistry.TYPE_COUNT; type++) {
                int globalIdx = idx * BlockRegistry.TYPE_COUNT + type;
                String id = "cb_" + String.format("%05d", globalIdx);
                BLOCK_SUPPLIERS.add(new BlockSupplier(id, r, g, b, type));
                ITEM_SUPPLIERS.add(new ItemSupplier(id, r, g, b, type));
            }
        });
        LOG.info("ColorBlocks: pre-built {} blocks, {} items", BLOCK_SUPPLIERS.size(), ITEM_SUPPLIERS.size());

        for (BlockSupplier bs : BLOCK_SUPPLIERS) {
            final int fr = bs.r, fg = bs.g, fb = bs.b, ftype = bs.type;
            DeferredHolder<Block, Block> holder = BlockRegistry.BLOCKS.register(
                    bs.id, () -> new ColorBlock(fr, fg, fb, ftype));
            BLOCK_HOLDERS.put(bs.id, holder);
        }

        Block[] allBlocks = BLOCK_HOLDERS.values().stream()
                .map(h -> (Block) h.get())
                .toArray(Block[]::new);

        COLOR_BLOCK_ENTITY = BlockRegistry.BLOCK_ENTITY_TYPES.register("color_block_entity", () ->
                BlockEntityType.Builder.of(
                        ColorBlockEntity::new,
                        allBlocks
                ).build(null)
        );

        for (ItemSupplier is : ITEM_SUPPLIERS) {
            final int fr = is.r, fg = is.g, fb = is.b, ftype = is.type;
            DeferredHolder<Block, Block> blockHolder = BLOCK_HOLDERS.get(is.id);
            BlockRegistry.ITEMS.register(is.id, () -> new ColorBlockItem(
                    blockHolder.get(), new Item.Properties(), fr, fg, fb, ftype));
        }

        BlockRegistry.BLOCKS.register(bus);
        BlockRegistry.ITEMS.register(bus);
        BlockRegistry.BLOCK_ENTITY_TYPES.register(bus);

        long elapsed = System.currentTimeMillis() - start;
        LOG.info("ColorBlocks: registered in {}ms", elapsed);
    }

    public record BlockSupplier(String id, int r, int g, int b, int type) {}
    public record ItemSupplier(String id, int r, int g, int b, int type) {}
}