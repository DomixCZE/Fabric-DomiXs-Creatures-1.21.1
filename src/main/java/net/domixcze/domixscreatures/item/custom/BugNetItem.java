package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.component.ModDataComponents;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class BugNetItem extends Item {
    public BugNetItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
        if (stack.get(ModDataComponents.CAPTURED_ENTITY) != null) return ActionResult.FAIL;

        if (!player.getWorld().isClient && target.getType().isIn(ModTags.EntityTypes.CAPTURABLE)) {
            Identifier typeId = Registries.ENTITY_TYPE.getId(target.getType());
            NbtCompound entityData = new NbtCompound();
            target.writeNbt(entityData);
            ModDataComponents.CapturedEntityComponent component = new ModDataComponents.CapturedEntityComponent(typeId, entityData);

            stack.set(ModDataComponents.CAPTURED_ENTITY, component);

            if (player.getAbilities().creativeMode) { // if this is not here you won't be able to release the capture entity in creative.
                player.setStackInHand(hand, stack.copy());
            }

            player.swingHand(hand);
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_BUNDLE_INSERT, SoundCategory.PLAYERS, 1.0F, 1.0F);
            target.discard();

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getStack();
        ModDataComponents.CapturedEntityComponent component = stack.get(ModDataComponents.CAPTURED_ENTITY);
        if (component == null) return ActionResult.FAIL;

        World world = context.getWorld();
        if (world.isClient) return ActionResult.SUCCESS;

        EntityType<?> type = Registries.ENTITY_TYPE.get(component.type());

        Entity entity = type.create(world);
        if (entity == null) return ActionResult.FAIL;

        entity.readNbt(component.nbt());
        Vec3d pos = Vec3d.ofCenter(context.getBlockPos().up());
        entity.refreshPositionAndAngles(pos.x, pos.y, pos.z, context.getPlayerYaw(), 0);
        world.spawnEntity(entity);

        stack.remove(ModDataComponents.CAPTURED_ENTITY);

        PlayerEntity player = context.getPlayer();
        if (player != null && !player.getAbilities().creativeMode) {
            stack.damage(1, player, EquipmentSlot.MAINHAND);
        }

        world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ITEM_BUNDLE_REMOVE_ONE, SoundCategory.PLAYERS, 1.0F, 1.0F);
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        ModDataComponents.CapturedEntityComponent component = stack.get(ModDataComponents.CAPTURED_ENTITY);
        Optional<TextColor> color = TextColor.parse("#6A5ACD").result();

        if (color.isPresent()) {
            if (component != null) {
                Identifier id = component.type();
                tooltip.add(Text.translatable("domixs-creatures.tooltip.bug_net.contains",
                                Text.translatable("entity." + id.getNamespace() + "." + id.getPath()))
                        .setStyle(Style.EMPTY.withColor(color.get())));
            } else {
                tooltip.add(Text.translatable("domixs-creatures.tooltip.bug_net.empty")
                        .setStyle(Style.EMPTY.withColor(color.get())));
            }
        } else {
            if (component != null) {
                Identifier id = component.type();
                tooltip.add(Text.translatable("domixs-creatures.tooltip.bug_net.contains",
                                Text.translatable("entity." + id.getNamespace() + "." + id.getPath()))
                        .setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)));
            } else {
                tooltip.add(Text.translatable("domixs-creatures.tooltip.bug_net.empty")
                        .setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)));
            }
        }
    }
}