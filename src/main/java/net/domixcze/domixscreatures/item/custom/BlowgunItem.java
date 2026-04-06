package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.component.ModDataComponents;
import net.domixcze.domixscreatures.entity.custom.QuillProjectileEntity;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class BlowgunItem extends RangedWeaponItem {

    public static final Predicate<ItemStack> QUILL_ONLY = (stack) -> stack.isOf(ModItems.QUILL);

    public BlowgunItem(Settings settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return QUILL_ONLY;
    }

    @Override
    public int getRange() {
        return 20; // Block distance
    }

    @Override
    protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
        projectile.setVelocity(shooter, shooter.getPitch(), yaw, 0.0F, speed, divergence);

        if (projectile instanceof QuillProjectileEntity quill && shooter instanceof PlayerEntity player) {
            ItemStack ammoStack = player.getProjectileType(shooter.getStackInHand(shooter.getActiveHand()));

            var effect = ammoStack.get(ModDataComponents.COATED_QUILL);
            if (effect != null) {
                quill.setDartEffect(effect);
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        boolean hasAmmo = !player.getProjectileType(itemStack).isEmpty();

        if (!player.getAbilities().creativeMode && !hasAmmo) {
            return TypedActionResult.fail(itemStack);
        } else {
            player.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTime) {
        if (user instanceof PlayerEntity player) {
            ItemStack ammoStack = player.getProjectileType(stack);

            if (!ammoStack.isEmpty() || player.getAbilities().creativeMode) {
                if (!world.isClient) {
                    QuillProjectileEntity quillEntity = new QuillProjectileEntity(world, player.getX(), player.getEyeY() - 0.1, player.getZ());
                    quillEntity.setOwner(player);

                    if (player.getAbilities().creativeMode) {
                        quillEntity.pickupType = QuillProjectileEntity.PickupPermission.CREATIVE_ONLY;
                    } else {
                        int ammoNeeded = EnchantmentHelper.getAmmoUse((ServerWorld) world, stack, ammoStack, 1);

                        if (ammoNeeded > 0) {
                            ammoStack.decrement(1);
                            if (ammoStack.isEmpty()) player.getInventory().removeOne(ammoStack);

                            quillEntity.setOwner(player);
                        } else {
                            quillEntity.setOwner(player);
                            quillEntity.pickupType = QuillProjectileEntity.PickupPermission.CREATIVE_ONLY;

                            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5F, 2.0F);
                        }
                    }

                    this.shoot(player, quillEntity, 0, 2.5F, 1.0F, player.getYaw(), null);
                    world.spawnEntity(quillEntity);

                    stack.damage(1, player, LivingEntity.getSlotForHand(player.getActiveHand()));
                }

                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.BLOWGUN_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);

                player.incrementStat(Stats.USED.getOrCreateStat(this));
                player.getItemCooldownManager().set(this, 20);
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.TOOT_HORN;
    }
}
