package net.domixcze.domixscreatures.entity.ai.brain.entity.owl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.brain.task.bird.*;
import net.domixcze.domixscreatures.entity.custom.OwlEntity;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.task.*;

public class OwlBrain {
    public OwlBrain() {
    }

    public static Brain<?> create(Brain<OwlEntity> brain) {
        addCoreActivities(brain);
        addIdleActivities(brain);

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();

        return brain;
    }

    private static void addCoreActivities(Brain<OwlEntity> brain) {
        brain.setTaskList(
                Activity.CORE,
                0,
                ImmutableList.of(
                        new StayAboveWaterTask(1.0F),
                        new LookAroundTask(45, 90),
                        new MoveToTargetTask()
                )
        );
    }

    private static void addIdleActivities(Brain<OwlEntity> brain) {
        brain.setTaskList(
                Activity.IDLE,
                ImmutableList.of(
                        Pair.of(0, new BreedTask(ModEntities.OWL)),
                        Pair.of(1, new FollowOwnerBirdTask<>()),
                        Pair.of(1, new BirdHuntTask<>(ModTags.EntityTypes.OWL_HUNTS)),
                        Pair.of(2, new TakeoffFlightTask<>()),
                        Pair.of(3, new LandFlightTask<>()),
                        Pair.of(4, new AirWanderFlightTask<>()),
                        Pair.of(5, new RandomTask<>(
                                ImmutableList.of(
                                        Pair.of(StrollTask.create(1.0F), 1),
                                        Pair.of(new WaitTask(100, 200), 4)
                                )
                        ))
                )
        );
    }

    public static void updateActivities(OwlEntity owl) {
        owl.getBrain().resetPossibleActivities(
                ImmutableList.of(Activity.IDLE)
        );
    }
}
