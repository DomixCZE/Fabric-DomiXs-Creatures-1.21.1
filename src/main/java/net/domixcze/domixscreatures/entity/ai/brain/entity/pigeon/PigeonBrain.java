package net.domixcze.domixscreatures.entity.ai.brain.entity.pigeon;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.brain.task.bird.*;
import net.domixcze.domixscreatures.entity.custom.PigeonEntity;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.task.*;

public class PigeonBrain {
    public PigeonBrain() {
    }

    public static Brain<?> create(Brain<PigeonEntity> brain) {
        addCoreActivities(brain);
        addIdleActivities(brain);

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();

        return brain;
    }

    private static void addCoreActivities(Brain<PigeonEntity> brain) {
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

    private static void addIdleActivities(Brain<PigeonEntity> brain) {
        brain.setTaskList(
                Activity.IDLE,
                ImmutableList.of(
                        Pair.of(0, new BreedTask(ModEntities.PIGEON)),
                        Pair.of(1, new FollowOwnerBirdTask<>()),
                        Pair.of(1, new BirdHuntTask<>(ModTags.EntityTypes.PIGEON_HUNTS)),
                        Pair.of(1, new TakeoffFlightTask<>()),
                        Pair.of(2, new LandFlightTask<>()),
                        Pair.of(3, new AirWanderFlightTask<>()),
                        Pair.of(4, new RandomTask<>(
                                ImmutableList.of(
                                        Pair.of(StrollTask.create(1.0F), 2),
                                        Pair.of(new WaitTask(30, 60), 1)
                                )
                        ))
                )
        );
    }

    public static void updateActivities(PigeonEntity pigeon) {
        pigeon.getBrain().resetPossibleActivities(
                ImmutableList.of(Activity.IDLE)
        );
    }
}
