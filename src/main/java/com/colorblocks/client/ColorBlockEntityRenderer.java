package com.colorblocks.client;

import com.colorblocks.ColorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ColorBlockEntityRenderer implements BlockEntityRenderer<ColorBlockEntity> {

    public ColorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(ColorBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ColorBlockRenderer.renderColorBlock(
                poseStack, buffer,
                be.r, be.g, be.b,
                be.blockType,
                packedLight, packedOverlay
        );
    }
}