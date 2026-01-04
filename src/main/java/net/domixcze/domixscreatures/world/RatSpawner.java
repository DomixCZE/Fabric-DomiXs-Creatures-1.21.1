package net.domixcze.domixscreatures.world;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.custom.RatEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.spawner.SpecialSpawner;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RatSpawner implements SpecialSpawner {

    private static final int SPAWN_RADIUS = 100;
    private static final int MAX_RATS_PER_VILLAGE = 3;
    private int ticksUntilNextSpawn = 0;

    @Override
    public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {

        if (!spawnAnimals || !world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
            return 0;
        }

        if (--ticksUntilNextSpawn > 0) return 0;

        ticksUntilNextSpawn = 6000; //5 minutes
        Random random = world.random;
        AtomicInteger count = new AtomicInteger();

        world.getPlayers().forEach(player -> {

            int dx = (8 + random.nextInt(32)) * (random.nextBoolean() ? -1 : 1);
            int dz = (8 + random.nextInt(32)) * (random.nextBoolean() ? -1 : 1);
            BlockPos offsetPos = player.getBlockPos().add(dx, 0, dz);

            BlockPos pos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, offsetPos);

            if (!SpawnHelper.isClearForSpawn(world, pos,
                    world.getBlockState(pos),
                    world.getFluidState(pos),
                    ModEntities.RAT))
                return;

            BlockPos structurePos = world.locateStructure(StructureTags.VILLAGE, pos, 5, false);

            if (structurePos != null && pos.getManhattanDistance(structurePos) < 300) {

                List<VillagerEntity> villagers = world.getEntitiesByType(
                        EntityType.VILLAGER,
                        new Box(pos).expand(SPAWN_RADIUS),
                        v -> true
                );

                if (!villagers.isEmpty() && areChunksLoaded(world, pos)) {

                    List<RatEntity> existingRats = world.getEntitiesByType(
                            ModEntities.RAT,
                            new Box(pos).expand(SPAWN_RADIUS),
                            r -> true
                    );

                    if (existingRats.size() >= MAX_RATS_PER_VILLAGE) return;

                    int amount = Math.min(1 + random.nextInt(3), MAX_RATS_PER_VILLAGE - existingRats.size());
                    for (int i = 0; i < amount; i++) {
                        spawnRat(world, pos);
                        count.getAndIncrement();
                    }
                }
            }
        });

        return count.get();
    }

    private boolean areChunksLoaded(ServerWorld world, BlockPos pos) {
        int minChunkX = (pos.getX() - 10) >> 4;
        int maxChunkX = (pos.getX() + 10) >> 4;
        int minChunkZ = (pos.getZ() - 10) >> 4;
        int maxChunkZ = (pos.getZ() + 10) >> 4;

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                if (!world.isChunkLoaded(cx, cz)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void spawnRat(ServerWorld world, BlockPos pos) {
        RatEntity rat = ModEntities.RAT.create(world);
        if (rat == null) return;

        rat.refreshPositionAndAngles(pos, 0, 0);
        rat.initialize(world, world.getLocalDifficulty(pos), SpawnReason.NATURAL, null);
        world.spawnEntity(rat);
    }
}