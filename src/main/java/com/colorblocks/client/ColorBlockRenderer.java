package com.colorblocks.client;

import com.colorblocks.BlockRegistry;
import com.colorblocks.ColorBlockEntity;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.*;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.io.*;
import java.util.*;

public class ColorBlockRenderer {

    private static final Map<String, DynamicTexture> TEXTURE_CACHE = new HashMap<>();

    /**
     * Generate a 16x16 PNG image filled with a solid color.
     * Input: 8-bit RGB values.
     * Returns: PNG bytes.
     */
    public static byte[] generateColorPNG(int r8, int g8, int b8) {
        NativeImage image = new NativeImage(16, 16, NativeImage.Format.RGBA);
        int pixel = (0xFF << 24) | (r8 << 16) | (g8 << 8) | b8;
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                image.setPixelRGBA(x, y, pixel);
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.writeToStream(baos, null);
        image.close();
        return baos.toByteArray();
    }

    /**
     * Get or create a cached dynamic texture for a given 8-bit RGB color.
     */
    public static DynamicTexture getOrCreateColorTexture(int r8, int g8, int b8) {
        String key = String.format("%02X%02X%02X", r8, g8, b8);
        if (TEXTURE_CACHE.containsKey(key)) {
            return TEXTURE_CACHE.get(key);
        }
        byte[] png = generateColorPNG(r8, g8, b8);
        DynamicTexture tex = new DynamicTexture(png);
        TEXTURE_CACHE.put(key, tex);
        return tex;
    }

    /**
     * Render a color block directly with colored quads.
     * Bypasses the texture system - vertex color only.
     */
    public static void renderColorBlock(PoseStack poseStack, MultiBufferSource buffer,
                                        int r5, int g5, int b5, int blockType,
                                        int packedLight, int packedOverlay) {
        int ri = BlockRegistry.to8bit(r5);
        int gi = BlockRegistry.to8bit(g5);
        int bi = BlockRegistry.to8bit(b5);
        int color = 0xFF000000 | (ri << 16) | (gi << 8) | bi;

        float x1, x2, y1, y2, z1, z2;
        if (blockType == BlockRegistry.TYPE_SLAB) {
            x1 = 0; x2 = 1; y1 = 0; y2 = 0.5f; z1 = 0; z2 = 1;
        } else if (blockType == BlockRegistry.TYPE_VERTICAL_SLAB) {
            x1 = 0; x2 = 0.5f; y1 = 0; y2 = 1; z1 = 0; z2 = 1;
        } else {
            x1 = 0; x2 = 1; y1 = 0; y2 = 1; z1 = 0; z2 = 1;
        }

        VertexConsumer vc = buffer.getBuffer(RenderType.solid());
        Matrix4f m = poseStack.last().pose();
        Matrix3f n = poseStack.last().normal();

        // Front (Z-), Back (Z+), Bottom (Y-), Top (Y+), Left (X-), Right (X+)
        quad(vc, m, n, x1, y1, z1, x2, y2, z1, color, packedLight, packedOverlay,  0,  0, -1);
        quad(vc, m, n, x2, y1, z2, x1, y2, z2, color, packedLight, packedOverlay,  0,  0,  1);
        quad(vc, m, n, x1, y1, z1, x2, y1, z2, color, packedLight, packedOverlay,  0, -1,  0);
        quad(vc, m, n, x1, y2, z2, x2, y2, z1, color, packedLight, packedOverlay,  0,  1,  0);
        quad(vc, m, n, x1, y1, z2, x1, y2, z1, color, packedLight, packedOverlay, -1,  0,  0);
        quad(vc, m, n, x2, y1, z1, x2, y2, z2, color, packedLight, packedOverlay,  1,  0,  0);
    }

    private static void quad(VertexConsumer vc, Matrix4f m, Matrix3f n,
                             float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             int color, int light, int overlay,
                             float nx, float ny, float nz) {
        vc.addVertex(m, x1, y1, z1).setColor(color).setUv(0, 0).setOverlay(overlay).setLight(light).setNormal(n, nx, ny, nz);
        vc.addVertex(m, x2, y1, z2).setColor(color).setUv(1, 0).setOverlay(overlay).setLight(light).setNormal(n, nx, ny, nz);
        vc.addVertex(m, x2, y2, z2).setColor(color).setUv(1, 1).setOverlay(overlay).setLight(light).setNormal(n, nx, ny, nz);
        vc.addVertex(m, x1, y2, z1).setColor(color).setUv(0, 1).setOverlay(overlay).setLight(light).setNormal(n, nx, ny, nz);
    }
}
