package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.component.ModDataComponents;
import net.domixcze.domixscreatures.config.ModConfig;
import net.domixcze.domixscreatures.entity.custom.RatEntity;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.domixcze.domixscreatures.util.ModGameRules;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class CommandFluteItem extends Item {

    public CommandFluteItem(Settings settings) {
        super(settings);
    }

    public enum Mode {
        FOLLOW,
        SIT;

        public Mode toggle() {
            return this == FOLLOW ? SIT : FOLLOW;
        }
    }

    private Mode getMode(ItemStack stack) {
        ModDataComponents.FluteModeComponent comp = stack.get(ModDataComponents.FLUTE_MODE);
        if (comp == null) return Mode.FOLLOW;
        try {
            return Mode.valueOf(comp.mode());
        } catch (Exception e) {
            return Mode.FOLLOW;
        }
    }

    private void setMode(ItemStack stack, Mode mode) {
        stack.set(ModDataComponents.FLUTE_MODE, new ModDataComponents.FluteModeComponent(mode.name()));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (player.isSneaking()) {
            Mode old = getMode(stack);
            Mode next = old.toggle();
            setMode(stack, next);

            if (world.isClient) {
                player.sendMessage(
                        Text.translatable("message.domixs-creatures.command_flute.mode_switch")
                                .styled(style -> style.withColor(Formatting.GREEN))
                                .append(
                                        Text.literal(" ")
                                )
                                .append(
                                        Text.translatable("message.domixs-creatures.command_flute.mode." + next.name().toLowerCase())
                                                .styled(style -> style.withColor(Formatting.YELLOW))
                                ),
                        true
                );
            }

            return TypedActionResult.success(stack);
        }

        player.setCurrentHand(hand);

        if (!world.isClient) {
            Mode mode = getMode(stack);
            commandRats(world, player, mode);

            if (world.getGameRules().getBoolean(ModGameRules.FLUTE_AFFECTS_ALL_TAMED)) {
                commandOtherTameables(world, player, mode);
            }

            SoundEvent sound = switch (mode) {
                case SIT -> ModSounds.COMMAND_FLUTE_SIT;
                case FOLLOW -> ModSounds.COMMAND_FLUTE_FOLLOW;
            };

            world.playSoundFromEntity(
                    null,
                    player,
                    sound,
                    SoundCategory.PLAYERS,
                    1.0F,
                    1.0F
            );
        }

        player.getItemCooldownManager().set(this, 40);
        player.incrementStat(Stats.USED.getOrCreateStat(this));

        return TypedActionResult.consume(stack);
    }

    private void commandRats(World world, PlayerEntity player, Mode mode) {
        double radius = 8;

        List<RatEntity> rats = world.getEntitiesByClass(
                RatEntity.class,
                new Box(
                        player.getX() - radius, player.getY() - radius, player.getZ() - radius,
                        player.getX() + radius, player.getY() + radius, player.getZ() + radius
                ),
                rat -> rat.isAlive()
                        && rat.isTamed()
                        && player.getUuid().equals(rat.getOwnerUuid())
        );

        for (RatEntity rat : rats) {
            switch (mode) {
                case SIT -> {
                    if (!rat.isSitting()) {
                        rat.setSitting(true);
                    }
                }
                case FOLLOW -> {
                    if (rat.isSitting()) {
                        rat.setSitting(false);
                    }
                }
            }
        }
    }

    private void commandOtherTameables(World world, PlayerEntity player, Mode mode) {
        double radius = 8;

        List<TameableEntity> mobs = world.getEntitiesByClass(
                TameableEntity.class,
                new Box(
                        player.getX() - radius, player.getY() - radius, player.getZ() - radius,
                        player.getX() + radius, player.getY() + radius, player.getZ() + radius
                ),
                mob -> mob.isAlive()
                        && mob.isTamed()
                        && player.getUuid().equals(mob.getOwnerUuid())
                        && !(mob instanceof RatEntity)
        );

        for (TameableEntity tame : mobs) {
            switch (mode) {
                case SIT -> {
                    if (!tame.isSitting()) tame.setSitting(true);
                }
                case FOLLOW -> {
                    if (tame.isSitting()) tame.setSitting(false);
                }
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 500;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.TOOT_HORN;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("domixs-creatures.tooltip.command_flute.toggle_mode").formatted(Formatting.GOLD));
            tooltip.add(Text.translatable("domixs-creatures.tooltip.command_flute.command").formatted(Formatting.GOLD));
        } else {
            tooltip.add(Text.translatable("domixs-creatures.tooltip.hold_shift"));
        }

        Optional<TextColor> color = TextColor.parse("#6A5ACD").result();
        Mode mode = getMode(stack);
        tooltip.add(Text.translatable("domixs-creatures.tooltip.command_flute.mode." + mode.name().toLowerCase())
                .setStyle(Style.EMPTY.withColor(color.orElse(TextColor.fromFormatting(Formatting.DARK_PURPLE)))));
    }
}