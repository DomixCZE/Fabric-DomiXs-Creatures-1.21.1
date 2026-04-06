package net.domixcze.domixscreatures.mixin;

import net.domixcze.domixscreatures.entity.custom.OwlEntity;
import net.domixcze.domixscreatures.util.ModGameRules;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin {

    @Shadow @Final private ScreenHandlerContext context;
    @Shadow @Final private Random random;

    @Unique
    private PlayerEntity domixscreatures$player;

    @Unique
    private static final double OWL_RANGE = 5.0D;

    @Unique
    private static final float TREASURE_CHANCE = 0.08F;

    @Inject(
            method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V",
            at = @At("TAIL")
    )
    private void domixscreatures$capturePlayer(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, CallbackInfo ci) {
        this.domixscreatures$player = playerInventory.player;
    }

    @Inject(
            method = "generateEnchantments(Lnet/minecraft/registry/DynamicRegistryManager;Lnet/minecraft/item/ItemStack;II)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void domixscreatures$addTreasureEnchantFromNearbyOwl(
            DynamicRegistryManager registryManager,
            ItemStack stack,
            int slot,
            int level,
            CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir
    ) {
        if (this.domixscreatures$player == null) {
            return;
        }

        if (slot != 2 || level < 30) {
            return;
        }

        if (!this.domixscreatures$hasNearbyOwnedOwl()) {
            return;
        }

        if (this.random.nextFloat() >= TREASURE_CHANCE) {
            return;
        }

        List<EnchantmentLevelEntry> originalList = cir.getReturnValue();
        if (originalList == null) {
            return;
        }

        Optional<RegistryEntryList.Named<Enchantment>> treasureOptional =
                registryManager.get(RegistryKeys.ENCHANTMENT).getEntryList(EnchantmentTags.TREASURE);

        if (treasureOptional.isEmpty()) {
            return;
        }

        List<EnchantmentLevelEntry> treasureCandidates = new ArrayList<>(
                EnchantmentHelper.getPossibleEntries(level, stack, treasureOptional.get().stream())
        );

        if (treasureCandidates.isEmpty()) {
            return;
        }

        treasureCandidates.removeIf(candidate -> {
            for (EnchantmentLevelEntry existing : originalList) {
                if (existing.enchantment.equals(candidate.enchantment)) {
                    return true;
                }

                if (!Enchantment.canBeCombined(existing.enchantment, candidate.enchantment)) {
                    return true;
                }
            }

            return false;
        });

        if (treasureCandidates.isEmpty()) {
            return;
        }

        EnchantmentLevelEntry pickedTreasure = treasureCandidates.get(this.random.nextInt(treasureCandidates.size()));

        List<EnchantmentLevelEntry> result = new ArrayList<>(originalList);
        result.add(pickedTreasure);

        cir.setReturnValue(result);
    }

    @Unique
    private boolean domixscreatures$hasNearbyOwnedOwl() {
        return this.context.get((world, pos) -> {
            if (!world.getGameRules().getBoolean(ModGameRules.ENABLE_OWL_TREASURE_ENCHANTING)) {
                return false;
            }

            return domixscreatures$hasNearbyOwnedOwl(world, pos, this.domixscreatures$player);
        }).orElse(false);
    }

    @Unique
    private static boolean domixscreatures$hasNearbyOwnedOwl(World world, BlockPos pos, PlayerEntity player) {
        Box box = new Box(pos).expand(OWL_RANGE);

        List<OwlEntity> owls = world.getEntitiesByClass(
                OwlEntity.class,
                box,
                owl -> owl.grantsEnchantingBonusTo(player)
        );

        return !owls.isEmpty();
    }
}
