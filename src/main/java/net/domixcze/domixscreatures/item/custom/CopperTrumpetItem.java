package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.entity.custom.CapybaraEntity;
import net.domixcze.domixscreatures.item.ModItems;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class CopperTrumpetItem extends Item {

    private final SoundEvent mainSound;
    private final SoundEvent altSound;

    public CopperTrumpetItem(Settings settings, SoundEvent mainSound, SoundEvent altSound) {
        super(settings);
        this.mainSound = mainSound;
        this.altSound = altSound;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        player.setCurrentHand(hand);

        if (!world.isClient) {
            double radius = 10.0;
            Box box = player.getBoundingBox().expand(radius);

            List<CapybaraEntity> capybaras = world.getEntitiesByClass(
                    CapybaraEntity.class,
                    box,
                    capy -> capy.isSubmergedInWater() || capy.isTouchingWater()
            );

            boolean inMountains = world.getBiome(player.getBlockPos())
                    .isIn(net.minecraft.registry.tag.BiomeTags.IS_MOUNTAIN);

            if (!capybaras.isEmpty() && inMountains && player instanceof ServerPlayerEntity serverPlayer) {
                MinecraftServer server = serverPlayer.getServer();
                if (server != null) {
                    Identifier advId = Identifier.of("domixs-creatures", "domixs-creatures/thats_peak");
                    AdvancementEntry entry = server.getAdvancementLoader().get(advId);

                    if (entry != null) {
                        String criterion = "played_trumpet_near_capybara";
                        if (!serverPlayer.getAdvancementTracker().getProgress(entry).isDone()) {
                            serverPlayer.getAdvancementTracker().grantCriterion(entry, criterion);
                        }
                    }
                }
            }

            SoundEvent soundToPlay = player.isSneaking() ? altSound : mainSound;

            world.playSoundFromEntity(
                    null,
                    player,
                    soundToPlay,
                    SoundCategory.PLAYERS,
                    1.0F,
                    1.0F
            );
        }

        player.getItemCooldownManager().set(this, 40);
        return TypedActionResult.consume(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (this == ModItems.WAXED_COPPER_TRUMPET ||
                this == ModItems.WAXED_COPPER_TRUMPET_EXPOSED ||
                this == ModItems.WAXED_COPPER_TRUMPET_WEATHERED ||
                this == ModItems.WAXED_COPPER_TRUMPET_OXIDIZED) {
            return;
        }

        if (!world.isClient && entity instanceof PlayerEntity player) {
            if (world.getTime() % 24000 == 0) { // once per day
                double chance = 0.25; // 25% chance per day
                if (world.random.nextDouble() < chance) {
                    Item nextStage = getNextStageItem(this);
                    if (nextStage != null) {
                        ItemStack newStack = new ItemStack(nextStage, stack.getCount());
                        player.getInventory().setStack(slot, newStack);
                    }
                }
            }
        }
    }

    private Item getNextStageItem(Item current) {
        if (current == ModItems.COPPER_TRUMPET) return ModItems.COPPER_TRUMPET_EXPOSED;
        if (current == ModItems.COPPER_TRUMPET_EXPOSED) return ModItems.COPPER_TRUMPET_WEATHERED;
        if (current == ModItems.COPPER_TRUMPET_WEATHERED) return ModItems.COPPER_TRUMPET_OXIDIZED;
        return null;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 500;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.TOOT_HORN;
    }
}