package net.domixcze.domixscreatures.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class BabyFollowParentGoal extends Goal {
    public static final int HORIZONTAL_CHECK_RANGE = 8;
    public static final int VERTICAL_CHECK_RANGE = 4;
    public static final int MIN_DISTANCE = 3;
    private final AnimalEntity babyAnimal;
    @Nullable
    private AnimalEntity parent;
    private final double speed;
    private int delay;
    private final Sleepy sleepy;

    public BabyFollowParentGoal(AnimalEntity babyAnimal, double speed) {
        this.babyAnimal = babyAnimal;
        this.speed = speed;
        if (babyAnimal instanceof Sleepy) {
            this.sleepy = (Sleepy) babyAnimal;
        } else {
            this.sleepy = null;
        }
    }

    @Override
    public boolean canStart() {
        if (this.babyAnimal.getBreedingAge() >= 0) {
            return false;
        }
        if (this.sleepy != null && this.sleepy.isSleeping()) {
            return false;
        } else {
            List<? extends AnimalEntity> list = this.babyAnimal.getWorld().getNonSpectatingEntities(this.babyAnimal.getClass(), this.babyAnimal.getBoundingBox().expand(HORIZONTAL_CHECK_RANGE, VERTICAL_CHECK_RANGE, HORIZONTAL_CHECK_RANGE));
            AnimalEntity nearestParent = null;
            double nearestDistanceSq = Double.MAX_VALUE;
            Iterator<? extends AnimalEntity> var5 = list.iterator();

            while (var5.hasNext()) {
                AnimalEntity potentialParent = var5.next();
                if (potentialParent.getBreedingAge() >= 0) {
                    double distanceSq = this.babyAnimal.squaredDistanceTo(potentialParent);
                    if (distanceSq < nearestDistanceSq) {
                        nearestDistanceSq = distanceSq;
                        nearestParent = potentialParent;
                    }
                }
            }

            if (nearestParent == null) {
                return false;
            } else if (nearestDistanceSq < MIN_DISTANCE * MIN_DISTANCE) {
                return false;
            } else {
                this.parent = nearestParent;
                return true;
            }
        }
    }

    @Override
    public boolean shouldContinue() {
        if (this.babyAnimal.getBreedingAge() >= 0) {
            return false;
        } else if (this.parent == null || !this.parent.isAlive()) {
            return false;
        } else {
            double distanceSq = this.babyAnimal.squaredDistanceTo(this.parent);
            return !(distanceSq < MIN_DISTANCE * MIN_DISTANCE) && !(distanceSq > HORIZONTAL_CHECK_RANGE * HORIZONTAL_CHECK_RANGE * 4.0);
        }
    }

    @Override
    public void start() {
        this.delay = 0;
    }

    @Override
    public void stop() {
        this.parent = null;
    }

    @Override
    public void tick() {
        if (--this.delay <= 0) {
            this.delay = this.getTickCount(10);
            this.babyAnimal.getNavigation().startMovingTo(this.parent, this.speed);
        }
    }
}