package com.colorblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ColorBlockEntity extends BlockEntity {

    public final int r, g, b, blockType;

    public ColorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                           int r, int g, int b, int blockType) {
        super(type, pos, state);
        this.r = r;
        this.g = g;
        this.b = b;
        this.blockType = blockType;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    }
}
