package net.domixcze.domixscreatures.util;

import net.domixcze.domixscreatures.entity.custom.CrowEntity;
import net.domixcze.domixscreatures.entity.custom.IguanaEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;

public final class ShoulderPetHelper {
    private ShoulderPetHelper() {
    }

    public static boolean canAddCustomShoulderPet(PlayerEntity player) {
        if (!player.getShoulderEntityLeft().isEmpty()) {
            return false;
        }

        for (Entity passenger : player.getPassengerList()) {
            if (passenger instanceof CrowEntity || passenger instanceof IguanaEntity) {
                return false;
            }
        }

        return true;
    }

    public static boolean shouldIgnoreMountedOwnerDamage(TameableEntity entity, DamageSource source) {
        if (!entity.hasVehicle()) {
            return false;
        }

        Entity vehicle = entity.getVehicle();
        if (!(vehicle instanceof PlayerEntity player)) {
            return false;
        }

        if (!entity.isOwner(player)) {
            return false;
        }

        return source.getAttacker() == player;
    }
}