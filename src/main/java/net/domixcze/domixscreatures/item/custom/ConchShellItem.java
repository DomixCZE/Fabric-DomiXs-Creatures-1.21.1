package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.effect.ModEffects;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ConchShellItem extends Item {

    public ConchShellItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        player.setCurrentHand(hand);

        player.addStatusEffect(new StatusEffectInstance(ModEffects.OCEAN_BLESSING, 6000, 0)); // 5 minutes

        world.playSoundFromEntity(null, player, ModSounds.CONCH_SHELL_USE, SoundCategory.RECORDS, 5.0F, 1.0F);

        world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, player.getPos(), GameEvent.Emitter.of(player));

        player.getItemCooldownManager().set(this, 1000); // Set a cooldown (1000 = 50 seconds)
        player.incrementStat(Stats.USED.getOrCreateStat(this));

        return TypedActionResult.consume(itemStack);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 500; // How long the "use" animation plays
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.TOOT_HORN;
    }
}