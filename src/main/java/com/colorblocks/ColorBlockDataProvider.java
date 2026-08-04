package com.colorblocks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ColorBlockDataProvider implements DataProvider {

    private final DataGenerator.PackOutput output;
    private final HolderLookup.Provider provider;
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public ColorBlockDataProvider(DataGenerator.PackOutput output, HolderLookup.Provider provider) {
        this.output = output;
        this.provider = provider;
    }

    @Override
    public void run(HolderLookup.Provider provider) throws IOException {
        Path assetsOut = output.getOutputFolder().resolve("assets/colorblocks");
        Path dataOut = output.getOutputFolder().resolve("data/colorblocks");

        generateLangFile(assetsOut);
        generateItemTag(dataOut);
        generateBlockstates(assetsOut);
        generateBlockModels(assetsOut);
        generateLootTables(dataOut);
        generateBlockTags(dataOut);
    }

    private void generateLangFile(Path assetsOut) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        int count = 0;
        for (ColorBlocksMod.BlockSupplier bs : ColorBlocksMod.BLOCK_SUPPLIERS) {
            String hex = BlockRegistry.colorName(bs.r(), bs.g(), bs.b());
            String suffix = switch (bs.type()) {
                case BlockRegistry.TYPE_FULL -> " Full Block";
                case BlockRegistry.TYPE_SLAB -> " Slab";
                case BlockRegistry.TYPE_VERTICAL_SLAB -> " Vertical Slab";
                default -> "";
            };
            sb.append("  \"block.").append(ColorBlocksMod.MOD_ID).append(".").append(bs.id())
              .append("\": \"").append(hex).append(suffix).append("\"");
            if (count < ColorBlocksMod.BLOCK_SUPPLIERS.size() - 1) sb.append(",");
            sb.append("\n");
            count++;
        }
        sb.append("}\n");

        Path langFile = assetsOut.resolve("lang/en_us.json");
        Files.createDirectories(langFile.getParent());
        Files.writeString(langFile, sb.toString());
    }

    private void generateItemTag(Path dataOut) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"replace\":false,\"values\":[\n");

        boolean first = true;
        for (ColorBlocksMod.BlockSupplier bs : ColorBlocksMod.BLOCK_SUPPLIERS) {
            if (!first) sb.append(",\n");
            sb.append("  \"").append(ColorBlocksMod.MOD_ID).append(":").append(bs.id()).append("\"");
            first = false;
        }
        sb.append("\n]}\n");

        Path tagFile = dataOut.resolve("tags/item/colorblocks_items.json");
        Files.createDirectories(tagFile.getParent());
        Files.writeString(tagFile, sb.toString());
    }

    private void generateBlockstates(Path assetsOut) throws IOException {
        Path bsOut = assetsOut.resolve("blockstates");
        Files.createDirectories(bsOut);

        // Single blockstate - all blocks use the same model (with color from texture)
        // The color is determined by the resource pack's dynamic texture system
        String blockstateJson = """
            {
              "variants": {
                "": { "model": "colorblocks:block/color_cube" }
              }
            }
            """;
        Files.writeString(bsOut.resolve("color_cube.json"), blockstateJson);
    }

    private void generateBlockModels(Path assetsOut) throws IOException {
        Path modelsOut = assetsOut.resolve("models/block");
        Path texturesOut = assetsOut.resolve("textures/block");
        Files.createDirectories(modelsOut);
        Files.createDirectories(texturesOut);

        // Generate block models for ALL 98,304 blocks
        int count = 0;
        for (ColorBlocksMod.BlockSupplier bs : ColorBlocksMod.BLOCK_SUPPLIERS) {
            String parent;
            if (bs.type() == BlockRegistry.TYPE_SLAB) {
                parent = "colorblocks:block/color_slab";
            } else if (bs.type() == BlockRegistry.TYPE_VERTICAL_SLAB) {
                parent = "colorblocks:block/color_vertical_slab";
            } else {
                parent = "colorblocks:block/color_cube_model";
            }

            Map<String, Object> model = new LinkedHashMap<>();
            model.put("parent", parent);
            Files.writeString(modelsOut.resolve(bs.id() + ".json"), GSON.toJson(model));
            count++;
        }

        // Parent models
        Map<String, Object> cubeModel = new LinkedHashMap<>();
        cubeModel.put("parent", "minecraft:block/cube");
        cubeModel.put("textures", Map.of("particle", "colorblocks:block/color_000000"));
        Files.writeString(modelsOut.resolve("color_cube_model.json"), GSON.toJson(cubeModel));

        Map<String, Object> slabModel = new LinkedHashMap<>();
        slabModel.put("parent", "minecraft:block/slab");
        slabModel.put("textures", Map.of(
                "bottom", "colorblocks:block/color_000000",
                "top", "colorblocks:block/color_000000",
                "side", "colorblocks:block/color_000000"
        ));
        Files.writeString(modelsOut.resolve("color_slab.json"), GSON.toJson(slabModel));

        Map<String, Object> vslabModel = new LinkedHashMap<>();
        vslabModel.put("parent", "minecraft:block/slab");
        vslabModel.put("textures", Map.of(
                "bottom", "colorblocks:block/color_000000",
                "top", "colorblocks:block/color_000000",
                "side", "colorblocks:block/color_000000"
        ));
        Files.writeString(modelsOut.resolve("color_vertical_slab.json"), GSON.toJson(vslabModel));

        System.out.println("[ColorBlocks] Generated " + count + " block models");
    }

    private void generateBlockTags(Path dataOut) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"replace\":false,\"values\":[\n");

        boolean first = true;
        for (ColorBlocksMod.BlockSupplier bs : ColorBlocksMod.BLOCK_SUPPLIERS) {
            if (!first) sb.append(",\n");
            sb.append("  \"").append(ColorBlocksMod.MOD_ID).append(":").append(bs.id()).append("\"");
            first = false;
        }
        sb.append("\n]}\n");

        Path tagFile = dataOut.resolve("tags/block/colorblocks_blocks.json");
        Files.createDirectories(tagFile.getParent());
        Files.writeString(tagFile, sb.toString());
    }

    private void generateLootTables(Path dataOut) throws IOException {
        Path lootOut = dataOut.resolve("loot_tables/blocks");
        Files.createDirectories(lootOut);

        // Single loot table for all blocks (block drops itself)
        String lootTableJson = """
            {
              "type": "minecraft:block",
              "pools": [
                {
                  "rolls": 1,
                  "entries": [
                    {
                      "type": "minecraft:item",
                      "name": "#colorblocks:colorblocks_items"
                    }
                  ],
                  "conditions": [
                    {
                      "condition": "minecraft:survives_explosion"
                    }
                  ]
                }
              ]
            }
            """;
        Files.writeString(lootOut.resolve("cb_all.json"), lootTableJson);
        Files.writeString(lootOut.resolve("cb_00000.json"), lootTableJson);
    }

    @Override
    public String getName() {
        return "ColorBlocks Data Provider";
    }
}
