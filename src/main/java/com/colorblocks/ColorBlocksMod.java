package com.colorblocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Mod(ColorBlocksMod.MOD_ID)
public class ColorBlocksMod {
    public static final String MOD_ID = "colorblocks";
    public static final Logger LOG = LoggerFactory.getLogger(ColorBlocksMod.class);

    // Pre-built supplier lists (populated before any registry calls)
    public static final List<BlockSupplier> BLOCK_SUPPLIERS = new ArrayList<>();
    public static final List<ItemSupplier> ITEM_SUPPLIERS = new ArrayList<>();

    // Registry holders (populated during registration)
    public static final java.util.Map<String, DeferredHolder<Block, Block>> BLOCK_HOLDERS = new java.util.HashMap<>();
    public static final java.util.Map<String, DeferredHolder<Item, Item>> ITEM_HOLDERS = new java.util.HashMap<>();

    // BlockEntityType holder (populated after block registration)
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ColorBlockEntity>> COLOR_BLOCK_ENTITY;

    public ColorBlocksMod(IEventBus bus) {
        long start = System.currentTimeMillis();

        // Step 1: Pre-build all supplier data
        BlockRegistry.forEachColor((info) -> {
            int idx = info[0], r = info[1], g = info[2], b = info[3];
            for (int type = 0; type < BlockRegistry.TYPE_COUNT; type++) {
                int globalIdx = idx * BlockRegistry.TYPE_COUNT + type;
                String id = "cb_" + String.format("%05d", globalIdx);
                BLOCK_SUPPLIERS.add(new BlockSupplier(id, r, g, b, type, globalIdx));
                ITEM_SUPPLIERS.add(new ItemSupplier(id, r, g, b, type, globalIdx));
            }
        });
        LOG.info("ColorBlocks: pre-built {} blocks, {} items", BLOCK_SUPPLIERS.size(), ITEM_SUPPLIERS.size());

        // Step 2: Register all blocks
        for (BlockSupplier bs : BLOCK_SUPPLIERS) {
            final int fr = bs.r, fg = bs.g, fb = bs.b, ftype = bs.type;
            DeferredHolder<Block, Block> holder = BlockRegistry.BLOCKS.register(
                    bs.id, () -> new ColorBlock(fr, fg, fb, ftype));
            BLOCK_HOLDERS.put(bs.id, holder);
        }

        // Step 3: Register BlockEntityType (after blocks are registered, so validBlocks is non-empty)
        Block firstBlock = BLOCK_HOLDERS.get("cb_00000").get();
        COLOR_BLOCK_ENTITY = BlockRegistry.BLOCK_ENTITY_TYPES.register("color_block_entity", () ->
                BlockEntityType.Builder.<ColorBlockEntity>of(
                        (type, pos, state) -> {
                            Block block = state.getBlock();
                            if (block instanceof ColorBlock cb) {
                                return new ColorBlockEntity(type, pos, state,
                                        cb.r, cb.g, cb.b, cb.blockType);
                            }
                            return null;
                        },
                        firstBlock
                ).build(null)
        );

        // Step 4: Register all items
        for (ItemSupplier is : ITEM_SUPPLIERS) {
            final int fr = is.r, fg = is.g, fb = is.b, ftype = is.type, fidx = is.globalIdx;
            DeferredHolder<Block, Block> blockHolder = BLOCK_HOLDERS.get(is.id);
            BlockRegistry.ITEMS.register(is.id, () -> new ColorBlockItem(
                    blockHolder.get(), new Item.Properties(), fr, fg, fb, ftype, fidx));
        }

        // Step 5: Register with event bus
        BlockRegistry.BLOCKS.register(bus);
        BlockRegistry.ITEMS.register(bus);
        BlockRegistry.BLOCK_ENTITY_TYPES.register(bus);

        long elapsed = System.currentTimeMillis() - start;
        LOG.info("ColorBlocks: {} blocks + {} items registered in {}ms",
                BLOCK_SUPPLIERS.size(), ITEM_SUPPLIERS.size(), elapsed);

        // Data generation
        bus.addListener(this::onGatherData);
    }

    private void onGatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(true, new ColorBlockDataProvider(
                event.getGenerator().getPackOutput(),
                event.getLookupProvider()
        ));
    }

    public record BlockSupplier(String id, int r, int g, int b, int type, int globalIdx) {}
    public record ItemSupplier(String id, int r, int g, int b, int type, int globalIdx) {}
}
