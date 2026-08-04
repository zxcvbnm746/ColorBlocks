package com.colorblocks.client;

import com.colorblocks.BlockRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;

public class ColorBlockRenderer {

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

        // West (-X), East (+X), Bottom (-Y), Top (+Y), North (-Z), South (+Z)
        quad(vc, m, x1, y1, z2, x1, y2, z1, color, packedLight, packedOverlay, -1, 0, 0, poseStack);
        quad(vc, m, x2, y1, z1, x2, y2, z2, color, packedLight, packedOverlay,  1, 0, 0, poseStack);
        quad(vc, m, x1, y1, z1, x2, y1, z2, color, packedLight, packedOverlay,  0,-1, 0, poseStack);
        quad(vc, m, x1, y2, z2, x2, y2, z1, color, packedLight, packedOverlay,  0, 1, 0, poseStack);
        quad(vc, m, x1, y1, z1, x2, y2, z1, color, packedLight, packedOverlay,  0, 0,-1, poseStack);
        quad(vc, m, x2, y1, z2, x1, y2, z2, color, packedLight, packedOverlay,  0, 0, 1, poseStack);
    }

    private static void quad(VertexConsumer vc, Matrix4f m,
                             float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             int color, int light, int overlay,
                             float nx, float ny, float nz,
                             PoseStack poseStack) {
        vc.addVertex(m, x1, y1, z1).setColor(color).setUv(0, 0).setOverlay(overlay).setLight(light).setNormal(poseStack.last(), nx, ny, nz);
        vc.addVertex(m, x2, y1, z2).setColor(color).setUv(1, 0).setOverlay(overlay).setLight(light).setNormal(poseStack.last(), nx, ny, nz);
        vc.addVertex(m, x2, y2, z2).setColor(color).setUv(1, 1).setOverlay(overlay).setLight(light).setNormal(poseStack.last(), nx, ny, nz);
        vc.addVertex(m, x1, y2, z1).setColor(color).setUv(0, 1).setOverlay(overlay).setLight(light).setNormal(poseStack.last(), nx, ny, nz);
    }
}