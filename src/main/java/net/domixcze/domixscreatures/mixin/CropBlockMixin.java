package net.domixcze.domixscreatures.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.domixcze.domixscreatures.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CropBlock.class)
public class CropBlockMixin {
    @WrapOperation(
            method = "getAvailableMoisture",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z")
    )
    private static boolean domixscreatures$farmlandMoisture(BlockState state, Block block, Operation<Boolean> original) {
        if (block == Blocks.FARMLAND) {
            return original.call(state, block) || state.isOf(ModBlocks.RICH_FARMLAND);
        }
        return original.call(state, block);
    }
}