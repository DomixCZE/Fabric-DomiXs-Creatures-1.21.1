package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.Chainable;
import net.domixcze.domixscreatures.entity.custom.ChainAnchorEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class IronShackleItem extends Item {
    public IronShackleItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof Chainable chainable) {
            if (!user.getWorld().isClient) {
                if (chainable.getChainHolder() == null) {
                    chainable.attachChain(user);

                    if (!user.getAbilities().creativeMode) {
                        stack.decrement(1);
                    }

                    user.getWorld().playSound(null, entity.getBlockPos(),
                            SoundEvents.BLOCK_CHAIN_PLACE, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.success(user.getWorld().isClient);
        }
        return ActionResult.PASS;
    }

    public static ChainAnchorEntity getOrCreateAnchor(World world, BlockPos pos) {
        List<ChainAnchorEntity> existing = world.getEntitiesByClass(ChainAnchorEntity.class,
                new Box(pos).expand(0.1),
                entity -> entity.getAttachedBlockPos().equals(pos));

        if (!existing.isEmpty()) {
            return existing.getFirst();
        }

        ChainAnchorEntity newAnchor = new ChainAnchorEntity(ModEntities.CHAIN_ANCHOR, world, pos);
        world.spawnEntity(newAnchor);
        newAnchor.onPlace();
        return newAnchor;
    }
}