package net.domixcze.domixscreatures.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SaplingGenerator;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class ModSaplingBlock extends SaplingBlock {
    private final TagKey<Block> placeableOn;

    public ModSaplingBlock(SaplingGenerator generator, Settings settings, TagKey<Block> placeableOn) {
        super(generator, settings);
        this.placeableOn = placeableOn;
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isIn(this.placeableOn);
    }
}