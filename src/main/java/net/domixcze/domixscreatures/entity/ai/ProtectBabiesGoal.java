package net.domixcze.domixscreatures.entity.ai;

import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class ProtectBabiesGoal<T extends MobEntity> extends ActiveTargetGoal<PlayerEntity> {
    private final T parent;
    private final Class<? extends MobEntity> babyClass;
    private final double searchRadius;

    public ProtectBabiesGoal(T parent, Class<? extends MobEntity> babyClass, double searchRadius) {
        super(parent, PlayerEntity.class, 20, true, true, null);
        this.parent = parent;
        this.babyClass = babyClass;
        this.searchRadius = searchRadius;
    }

    @Override
    public boolean canStart() {
        if (parent.isBaby()) {
            return false;
        }

        // Check if a baby of the specified class is nearby
        List<? extends MobEntity> nearbyBabies = parent.getWorld()
                .getNonSpectatingEntities(babyClass, parent.getBoundingBox().expand(searchRadius, 4.0, searchRadius))
                .stream()
                .filter(MobEntity::isBaby)
                .toList();

        return !nearbyBabies.isEmpty() && super.canStart();
    }

    @Override
    protected double getFollowRange() {
        return super.getFollowRange() * 0.5;
    }
}