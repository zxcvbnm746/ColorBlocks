package com.colorblocks.client;

import com.colorblocks.ColorBlocksMod;
import com.colorblocks.BlockRegistry;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.resources.*;
import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class ColorBlockResourcePack implements Pack {

    private final PackInfo info;

    public ColorBlockResourcePack() {
        this.info = new PackInfo(PackType.CLIENT_RESOURCES, "ColorBlocks Dynamic Textures", false,
                Pack.Position.TOP, ResourceKey.create(Registries.PACK_METADATA_TYPE,
                        new ResourceLocation(ColorBlocksMod.MOD_ID, "pack")));
    }

    @Override public String getId() { return "colorblocks_dynamic"; }
    @Override public PackType getPackType() { return PackType.CLIENT_RESOURCES; }

    @Override public InputSupplier<InputStream> getRootResource(String... elements) throws IOException {
        return InputSupplier.empty();
    }

    @Override
    public InputSupplier<InputStream> getResource(PackType type, ResourceLocation id) throws IOException {
        if (type != PackType.CLIENT_RESOURCES) return InputSupplier.empty();

        String ns = id.getNamespace();
        String path = id.getPath();

        // Intercept: colorblocks:textures/block/color_XXXXXX.png
        if (ColorBlocksMod.MOD_ID.equals(ns) && path.startsWith("textures/block/color_")) {
            String rest = path.substring("textures/".length()); // block/color_FFFFFF.png
            String[] parts = rest.replace(".png", "").split("_");
            if (parts.length >= 3) {
                String hex = parts[2]; // "FFFFFF"
                try {
                    int r = Integer.parseInt(hex.substring(0, 2), 16);
                    int g = Integer.parseInt(hex.substring(2, 4), 16);
                    int b = Integer.parseInt(hex.substring(4, 6), 16);
                    byte[] png = ColorBlockRenderer.generateColorPNG(r, g, b);
                    return InputSupplier.create(new ByteArrayInputStream(png));
                } catch (Exception e) {
                    return InputSupplier.empty();
                }
            }
        }
        return InputSupplier.empty();
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        if (type == PackType.CLIENT_RESOURCES) {
            return Set.of(ColorBlocksMod.MOD_ID);
        }
        return Set.of();
    }

    @Override public void close() {}
    @Override public java.util.Optional<Path> getRoot() { return java.util.Optional.empty(); }
}
