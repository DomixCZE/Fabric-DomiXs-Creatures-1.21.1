package net.domixcze.domixscreatures.util;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.effect.ModEffects;
import net.domixcze.domixscreatures.entity.ai.Chainable;
import net.domixcze.domixscreatures.entity.custom.ChainAnchorEntity;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.item.custom.IronShackleItem;
import net.domixcze.domixscreatures.item.custom.MacuahuitlItem;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ModEvents {

    public static void registerModEvents() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(ModEvents::onLivingEntityDamage);

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {

            ActionResult tillingResult = tryTillingRichDirt(player, world, hand, hitResult);
            if (tillingResult != ActionResult.PASS) return tillingResult;

            ActionResult extractionResult = tryExtractingWorms(player, world, hand, hitResult);
            if (extractionResult != ActionResult.PASS) return extractionResult;

            if (world.isClient || hand != Hand.MAIN_HAND) return ActionResult.PASS;

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);

            if (state.isIn(BlockTags.WALLS)) {
                List<Entity> leashedEntities = world.getOtherEntities(player,
                        player.getBoundingBox().expand(10.0),
                        entity -> entity instanceof Chainable c && player.equals(c.getChainHolder()));

                if (!leashedEntities.isEmpty()) {
                    ChainAnchorEntity anchor = IronShackleItem.getOrCreateAnchor(world, pos);

                    for (Entity entity : leashedEntities) {
                        ((Chainable) entity).attachChain(anchor);
                    }

                    world.playSound(null, pos, SoundEvents.BLOCK_CHAIN_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }

    private static ActionResult tryExtractingWorms(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = player.getStackInHand(hand);

        if (state.isOf(ModBlocks.RICH_DIRT) && stack.getItem() instanceof ShovelItem) {

            world.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0f, 0.8f);

            if (!world.isClient) {
                world.setBlockState(pos, Blocks.DIRT.getDefaultState());

                if (world.random.nextFloat() < 0.50f) {
                    Block.dropStack(world, pos, hitResult.getSide(), new ItemStack(ModItems.WORM));
                }

                if (!player.getAbilities().creativeMode) {
                    stack.damage(1, (ServerWorld) world, (ServerPlayerEntity) player, (item) -> {
                        player.sendEquipmentBreakStatus(item, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                    });
                }
            }
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    private static ActionResult tryTillingRichDirt(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = player.getStackInHand(hand);

        if (state.isOf(ModBlocks.RICH_DIRT) && stack.getItem() instanceof HoeItem) {
            ItemUsageContext context = new ItemUsageContext(player, hand, hitResult);

            if (HoeItem.canTillFarmland(context)) {
                world.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0f, 1.0f);

                if (!world.isClient) {
                    world.setBlockState(pos, ModBlocks.RICH_FARMLAND.getDefaultState());

                    if (!player.getAbilities().creativeMode) {
                        stack.damage(1, (ServerWorld) world, (ServerPlayerEntity) player, (item) -> {
                            player.sendEquipmentBreakStatus(item, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                        });
                    }
                }
                return ActionResult.success(world.isClient);
            }
        }
        return ActionResult.PASS;
    }

    private static boolean onLivingEntityDamage(LivingEntity entity, DamageSource source, float amount) {
        if (source.getAttacker() instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandStack();

            if (heldStack.getItem() instanceof MacuahuitlItem
                    && !attacker.getWorld().isClient()
                    && !(attacker instanceof PlayerEntity)) {

                heldStack.damage(1, attacker, EquipmentSlot.MAINHAND);

                if (attacker.getWorld().random.nextFloat() < 0.15f) { // 15% chance
                    entity.addStatusEffect(new StatusEffectInstance(ModEffects.BLEEDING, 100, 0));
                }
            }
        }

        if (source.getSource() != null && source.getSource().getType().isIn(ModTags.EntityTypes.REFLECTABLE_PROJECTILES)) {
            int reflectiveArmorPieces = 0;
            for (ItemStack armorStack : entity.getArmorItems()) {
                if (armorStack.isIn(ModTags.Items.REFLECTIVE_ARMOR)) {
                    reflectiveArmorPieces++;
                }
            }

            // Determine chance
            double reflectionChance = switch (reflectiveArmorPieces) {
                case 1 -> 0.10;
                case 2 -> 0.20;
                case 3 -> 0.40;
                case 4 -> 0.60;
                default -> 0.0;
            };

            if (reflectionChance > 0 && entity.getRandom().nextFloat() < reflectionChance) {
                entity.getWorld().playSound(
                        null,
                        entity.getBlockPos(),
                        SoundEvents.ENTITY_ARROW_HIT,
                        SoundCategory.PLAYERS,
                        1.0F,
                        1.0F + (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.2F
                );

                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                        ItemStack armorStack = entity.getEquippedStack(slot);
                        if (armorStack.isIn(ModTags.Items.REFLECTIVE_ARMOR)) {
                            armorStack.damage(1, entity, slot);
                        }
                    }
                }

                return false;
            }
        }

        return true;
    }
}