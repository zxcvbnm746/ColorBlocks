package com.colorblocks;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;

public class ColorBlock extends Block {
    public final int r, g, b, blockType;

    public ColorBlock(int r, int g, int b, int blockType) {
        super(Properties.of()
                .mapColor(MapColor.WOOL)
                .strength(5.0f, 6.0f)
                .sound(SoundType.METAL)
                .noOcclusion()
                .isRedstoneConductor((s, c, b) -> false)
        );
        this.r = r;
        this.g = g;
        this.b = b;
        this.blockType = blockType;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return false;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return blockType != BlockRegistry.TYPE_FULL;
    }

    @Override
    public float getExplosionResistance() {
        return 6.0f;
    }

    @Override
    public int getLightBlock( BlockState state) {
        return 0; // No light blocking
    }
}
