package com.colorblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

public class ColorBlock extends BaseEntityBlock {
    public final int r, g, b, blockType;

    public ColorBlock(int r, int g, int b, int blockType) {
        super(Properties.of()
                .mapColor(MapColor.WOOL)
                .strength(5.0f, 6.0f)
                .sound(SoundType.METAL)
                .noOcclusion()
                .isRedstoneConductor((s, c, p) -> false)
        );
        this.r = r;
        this.g = g;
        this.b = b;
        this.blockType = blockType;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE; // We render via BlockEntityRenderer
    }

    @Override
    public float getExplosionResistance() {
        return 6.0f;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        ColorBlockEntity be = new ColorBlockEntity(ColorBlocksMod.COLOR_BLOCK_ENTITY.get(), pos, state);
        be.setColor(r, g, b, blockType);
        return be;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return null; // No tick needed
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return blockType != BlockRegistry.TYPE_FULL;
    }
}
