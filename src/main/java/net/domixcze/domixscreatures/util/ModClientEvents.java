package net.domixcze.domixscreatures.util;

import net.domixcze.domixscreatures.entity.ai.Chainable;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class ModClientEvents {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(ModClientEvents::onClientTick);

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.isIn(ModTags.Items.REFLECTIVE_ARMOR)) {
                int index = Math.min(lines.size(), 1);

                lines.add(index, Text.translatable("domixs-creatures.tooltip.reflective_armor_scaling")
                        .formatted(Formatting.GRAY));

                lines.add(index, Text.translatable("domixs-creatures.tooltip.reflective_armor")
                        .formatted(Formatting.GOLD)
                        .append(Text.translatable("domixs-creatures.tooltip.reflective_armor_desc")
                                .formatted(Formatting.YELLOW)));
            }
        });
    }

    private static void onClientTick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;

        Vec3d playerPos = client.player.getPos();
        UUID playerId = client.player.getUuid();
        double maxDist = 16.0;
        double maxDistSq = maxDist * maxDist;

        for (Entity entity : client.world.getOtherEntities(client.player, client.player.getBoundingBox().expand(20.0))) {
            if (entity instanceof Chainable chainable) {
                UUID owner = chainable.getChainOwner();

                if (owner != null && owner.equals(playerId)) {
                    Vec3d delta = playerPos.subtract(entity.getPos());
                    double distSq = delta.lengthSquared();

                    if (distSq > maxDistSq) {
                        double distance = Math.sqrt(distSq);
                        double over = distance - maxDist;

                        double strength = Math.min(over * 0.12, 1.2);
                        Vec3d pull = delta.normalize().multiply(-strength);

                        client.player.addVelocity(pull.x, pull.y, pull.z);
                    }
                }
            }
        }
    }
}