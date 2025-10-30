package net.domixcze.domixscreatures.util;

import net.domixcze.domixscreatures.entity.ai.SnowLayerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class SnowLayerUtil {

    public static boolean isBeingSnowedOn(LivingEntity entity) {
        BlockPos pos = entity.getBlockPos();
        return entity.getWorld().isRaining()
                && isInSnowyBiome(entity)
                && (hasSnow(entity, pos)
                || hasSnow(entity, BlockPos.ofFloored(pos.getX(), entity.getBoundingBox().maxY, pos.getZ())));
    }

    public static boolean hasSnow(LivingEntity entity, BlockPos pos) {
        if (!entity.getWorld().isRaining()) return false;
        if (!entity.getWorld().isSkyVisible(pos)) return false;
        if (entity.getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) return false;
        Biome biome = entity.getWorld().getBiome(pos).value();
        return biome.getPrecipitation(pos) == Biome.Precipitation.SNOW;
    }

    public static boolean isInSnowyBiome(LivingEntity entity) {
        BlockPos pos = entity.getBlockPos();
        RegistryEntry<Biome> biomeEntry = entity.getWorld().getBiome(pos);
        Biome biome = biomeEntry.value();
        return biome.getPrecipitation(pos) == Biome.Precipitation.SNOW;
    }

    public static void handleSnowLayerTick(LivingEntity entity, SnowLayerable snowEntity) {
        if (entity.getWorld().isClient()) return;

        if (!snowEntity.hasSnowLayer() && isBeingSnowedOn(entity)) {
            snowEntity.setSnowTicks(snowEntity.getSnowTicks() + 1);
            if (snowEntity.getSnowTicks() >= 600) {
                snowEntity.setHasSnowLayer(true);
                snowEntity.setSnowTicks(0);
            }
        }

        if ((entity.isTouchingWater() || !isInSnowyBiome(entity)) && snowEntity.hasSnowLayer()) {
            snowEntity.setSnowMeltTimer(snowEntity.getSnowMeltTimer() + 1);
            if (snowEntity.getSnowMeltTimer() >= 200) {
                snowEntity.setHasSnowLayer(false);
                snowEntity.setSnowMeltTimer(0);
            }
        }
    }

    public static void spawnSnowParticles(LivingEntity entity) {
        World world = entity.getWorld();
        Random random = entity.getRandom();

        Box box = entity.getBoundingBox();
        double minX = box.minX;
        double minY = box.minY;
        double minZ = box.minZ;
        double maxX = box.maxX;
        double maxY = box.maxY;
        double maxZ = box.maxZ;

        for (int i = 0; i < 15; i++) {
            double x = minX + random.nextDouble() * (maxX - minX);
            double y = minY + random.nextDouble() * (maxY - minY);
            double z = minZ + random.nextDouble() * (maxZ - minZ);
            world.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0, 0.05, 0);
        }
    }
}