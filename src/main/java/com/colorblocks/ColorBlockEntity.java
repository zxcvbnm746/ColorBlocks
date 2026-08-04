package com.colorblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ColorBlockEntity extends BlockEntity {

    public int r, g, b, blockType;

    public ColorBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.r = 0;
        this.g = 0;
        this.b = 0;
        this.blockType = 0;
    }

    public void setColor(int r, int g, int b, int blockType) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.blockType = blockType;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("r", r);
        tag.putInt("g", g);
        tag.putInt("b", b);
        tag.putInt("type", blockType);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.r = tag.getInt("r");
        this.g = tag.getInt("g");
        this.b = tag.getInt("b");
        this.blockType = tag.getInt("type");
    }
}