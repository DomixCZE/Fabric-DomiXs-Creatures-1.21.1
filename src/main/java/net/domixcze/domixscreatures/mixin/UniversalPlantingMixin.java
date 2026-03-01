package net.domixcze.domixscreatures.mixin;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({CropBlock.class, StemBlock.class, AttachedStemBlock.class, PitcherCropBlock.class})
public class UniversalPlantingMixin {

    @Inject(method = "canPlantOnTop", at = @At("HEAD"), cancellable = true)
    private void domixscreatures$universalFarmlandCheck(BlockState floor, BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (floor.isOf(ModBlocks.RICH_FARMLAND)) {
            cir.setReturnValue(true);
        }
    }
}