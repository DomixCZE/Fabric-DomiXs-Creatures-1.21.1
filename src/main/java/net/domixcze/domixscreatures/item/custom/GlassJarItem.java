package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.component.ModDataComponents;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GlassJarItem extends Item {
    public static final int MAX_CAPACITY = 16;

    public GlassJarItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!entity.getType().isIn(ModTags.EntityTypes.CAPTURABLE_IN_JAR)) return ActionResult.PASS;

        ItemStack itemStack = user.getStackInHand(hand);
        ModDataComponents.JarContentsComponent contents = itemStack.get(ModDataComponents.JAR_CONTENTS);
        Identifier targetType = Registries.ENTITY_TYPE.getId(entity.getType());

        if (contents == null || contents.type().equals(targetType)) {
            List<NbtCompound> entityList = contents == null ? new ArrayList<>() : new ArrayList<>(contents.entities());

            if (entityList.size() < MAX_CAPACITY) {
                if (!user.getWorld().isClient) {
                    NbtCompound nbt = new NbtCompound();
                    entity.writeNbt(nbt);
                    entityList.add(nbt);

                    itemStack.set(ModDataComponents.JAR_CONTENTS, new ModDataComponents.JarContentsComponent(targetType, entityList));

                    entity.discard();
                    user.getWorld().playSound(null, user.getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1f, 1.2f);
                }
                return ActionResult.success(user.getWorld().isClient);
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = context.getStack();

        ModDataComponents.JarContentsComponent contents = stack.get(ModDataComponents.JAR_CONTENTS);
        if (contents == null || contents.entities().isEmpty()) return ActionResult.PASS;

        if (contents.type().getPath().contains("worm")) {
            if (state.isOf(Blocks.FARMLAND)) {
                if (!world.isClient) {
                    int moisture = state.get(FarmlandBlock.MOISTURE);
                    world.setBlockState(pos, ModBlocks.RICH_FARMLAND.getDefaultState().with(FarmlandBlock.MOISTURE, moisture), 3);
                    spawnSuccessParticles(world, pos, stack, contents);
                }
                return ActionResult.success(world.isClient);
            }
            if (state.isOf(Blocks.DIRT)) {
                if (!world.isClient) {
                    world.setBlockState(pos, ModBlocks.RICH_DIRT.getDefaultState(), 3);
                    spawnSuccessParticles(world, pos, stack, contents);
                }
                return ActionResult.success(world.isClient);
            }
        }

        if (!world.isClient) {
            List<NbtCompound> entityList = new ArrayList<>(contents.entities());
            NbtCompound nbt = entityList.removeLast();

            EntityType<?> type = Registries.ENTITY_TYPE.get(contents.type());
            Entity entity = type.create(world);
            if (entity != null) {
                entity.readNbt(nbt);
                BlockPos spawnPos = pos.offset(context.getSide());
                entity.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
                world.spawnEntity(entity);

                if (entityList.isEmpty()) {
                    stack.remove(ModDataComponents.JAR_CONTENTS);
                } else {
                    stack.set(ModDataComponents.JAR_CONTENTS, new ModDataComponents.JarContentsComponent(contents.type(), entityList));
                }
                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1f, 1f);
            }
        }
        return ActionResult.success(world.isClient);
    }

    private void spawnSuccessParticles(World world, BlockPos pos, ItemStack stack, ModDataComponents.JarContentsComponent contents) {
        List<NbtCompound> entityList = new ArrayList<>(contents.entities());
        entityList.removeLast();

        if (entityList.isEmpty()) {
            stack.remove(ModDataComponents.JAR_CONTENTS);
        } else {
            stack.set(ModDataComponents.JAR_CONTENTS, new ModDataComponents.JarContentsComponent(contents.type(), entityList));
        }

        world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_FILL_SUCCESS, SoundCategory.BLOCKS, 1f, 1f);
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    15, 0.4, 0.1, 0.4, 0.05);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        ModDataComponents.JarContentsComponent contents = stack.get(ModDataComponents.JAR_CONTENTS);
        Optional<TextColor> color = TextColor.parse("#6A5ACD").result();
        Style style = color.map(Style.EMPTY::withColor).orElse(Style.EMPTY.withFormatting(Formatting.DARK_PURPLE));

        if (contents != null && !contents.entities().isEmpty()) {
            Text entityName = Text.translatable("entity." + contents.type().getNamespace() + "." + contents.type().getPath());

            tooltip.add(Text.translatable("domixs-creatures.tooltip.glass_jar.contains")
                    .append(" " + contents.entities().size() + "/" + MAX_CAPACITY + " ")
                    .append(entityName)
                    .setStyle(style));
        } else {
            tooltip.add(Text.translatable("domixs-creatures.tooltip.glass_jar.empty").setStyle(style));
        }
    }
}