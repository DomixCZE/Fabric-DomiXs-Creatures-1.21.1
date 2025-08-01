package net.domixcze.domixscreatures.block.entity;

import net.domixcze.domixscreatures.block.custom.PositiveMagnetBlock;
import net.domixcze.domixscreatures.particle.ModParticles;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PositiveMagnetBlockEntity extends BlockEntity {

    private static final Set<Item> IRON_ARMOR_ITEMS = new HashSet<>(Set.of(
            Items.IRON_HELMET,
            Items.IRON_CHESTPLATE,
            Items.IRON_LEGGINGS,
            Items.IRON_BOOTS
    ));

    @Environment(EnvType.CLIENT)
    private SoundInstance activeMagnetSound;

    private final Set<ItemEntity> affectedItemEntities = new HashSet<>();

    public PositiveMagnetBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POSITIVE_MAGNET_BLOCK_ENTITY, pos, state);
    }

    private static double calculateEffectiveBeamLength(World world, BlockPos magnetPos, Direction direction) {
        Vec3d magnetCenter = Vec3d.ofCenter(magnetPos);
        Vec3d directionVec = Vec3d.of(direction.getVector());

        double maxBeamEffectDistance = 8.0;
        double raycastTotalLength = maxBeamEffectDistance + 1.5;

        Vec3d raycastStart = magnetCenter.add(directionVec.multiply(0.5 + 0.001));

        Vec3d raycastEnd = raycastStart.add(directionVec.multiply(raycastTotalLength));

        RaycastContext rayCtx = new RaycastContext(
                raycastStart,
                raycastEnd,
                net.minecraft.world.RaycastContext.ShapeType.OUTLINE,
                net.minecraft.world.RaycastContext.FluidHandling.NONE,
                net.minecraft.block.ShapeContext.absent()
        );
        BlockHitResult hitResult = world.raycast(rayCtx);

        double effectiveLength = maxBeamEffectDistance;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            double hitDistance = hitResult.getPos().distanceTo(raycastStart);
            effectiveLength = Math.max(0.0, (int)(hitDistance + 0.5 - 0.001));
            effectiveLength = Math.min(effectiveLength, maxBeamEffectDistance);
        }
        return effectiveLength;
    }


    public static void tick(World world, BlockPos pos, BlockState state, PositiveMagnetBlockEntity be) {
        if (world.isClient()) {
            applyMagnetClient(world, pos, state);
            handleMagnetSound(world, pos, state, be);
            return;
        }

        boolean isPowered = state.get(PositiveMagnetBlock.POWERED);

        if (!isPowered && !be.affectedItemEntities.isEmpty()) {
            for (ItemEntity itemEntity : new HashSet<>(be.affectedItemEntities)) {
                if (itemEntity != null && itemEntity.isAlive()) {
                    itemEntity.setNoGravity(false);
                }
            }
            be.affectedItemEntities.clear();
            return;
        }

        if (!isPowered) return;

        Direction direction = state.get(PositiveMagnetBlock.FACING);
        Vec3d directionVec = Vec3d.of(direction.getVector());

        double dynamicBeamBlocks = calculateEffectiveBeamLength(world, pos, direction);

        Box box;
        if (dynamicBeamBlocks <= 0) {
            box = new Box(0, 0, 0, 0, 0, 0);
        } else {
            Vec3d start = Vec3d.of(pos);
            Vec3d end = Vec3d.of(pos.offset(direction, (int)dynamicBeamBlocks + 1)).add(1.0, 1.0, 1.0);
            box = new Box(start, end);
        }

        if (world instanceof ServerWorld serverWorld && dynamicBeamBlocks > 0) {
            for (int i = 0; i < 4; i++) {
                double range = world.random.nextDouble() * dynamicBeamBlocks;

                Vector3f unitVec = direction.getUnitVector();
                Vec3d base = Vec3d.ofCenter(pos).add(new Vec3d(unitVec.x, unitVec.y, unitVec.z).multiply(range + 0.5));

                double x = base.x + (world.random.nextDouble() - 0.5) * 0.4;
                double y = base.y + (world.random.nextDouble() - 0.5) * 0.4;
                double z = base.z + (world.random.nextDouble() - 0.5) * 0.4;

                serverWorld.spawnParticles(
                        ModParticles.POSITIVE_MAGNET,
                        x, y, z,
                        1,
                        0, 0, 0,
                        0
                );
            }
        }

        List<Entity> currentAffectedGeneralEntities = world.getEntitiesByClass(Entity.class, box, PositiveMagnetBlockEntity::isAffectedEntity);

        Set<ItemEntity> currentItemEntitiesInBeam = new HashSet<>();
        for (Entity entity : currentAffectedGeneralEntities) {
            if (entity instanceof ItemEntity itemEntity) {
                currentItemEntitiesInBeam.add(itemEntity);
            }
        }

        for (ItemEntity itemEntity : new HashSet<>(be.affectedItemEntities)) {
            if (!currentItemEntitiesInBeam.contains(itemEntity)) {
                if (itemEntity != null && itemEntity.isAlive()) {
                    itemEntity.setNoGravity(false);
                }
            }
        }

        be.affectedItemEntities.clear();

        for (Entity entity : currentAffectedGeneralEntities) {
            if (entity.getBlockPos().equals(pos)) continue;

            Vec3d force = directionVec.multiply(-0.1);
            entity.addVelocity(force);
            entity.fallDistance = 0f;

            if (entity instanceof ItemEntity itemEntity) {
                itemEntity.setNoGravity(true);
                be.affectedItemEntities.add(itemEntity);
            }
        }
    }

    private static boolean isAffectedEntity(Entity entity) {
        if (entity instanceof LivingEntity living && isWearingIronArmor(living)) return true;
        if (entity instanceof ItemEntity itemEntity) {
            return IRON_ARMOR_ITEMS.contains(itemEntity.getStack().getItem());
        }
        return false;
    }

    private static boolean isWearingIronArmor(LivingEntity entity) {
        for (ItemStack stack : entity.getArmorItems()) {
            if (IRON_ARMOR_ITEMS.contains(stack.getItem())) return true;
        }
        return false;
    }

    @Environment(EnvType.CLIENT)
    private static void applyMagnetClient(World world, BlockPos pos, BlockState state) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || !state.get(PositiveMagnetBlock.POWERED)) return;
        if (!isWearingIronArmor(player)) return;

        Direction direction = state.get(PositiveMagnetBlock.FACING);
        Vec3d directionVec = Vec3d.of(direction.getVector());
        Vec3d pullVec = directionVec.multiply(-0.1);

        double dynamicBeamBlocks = calculateEffectiveBeamLength(world, pos, direction);

        Box box;
        if (dynamicBeamBlocks <= 0) {
            box = new Box(0, 0, 0, 0, 0, 0);
        } else {
            Vec3d start = Vec3d.of(pos);
            Vec3d end = Vec3d.of(pos.offset(direction, (int)dynamicBeamBlocks + 1)).add(1.0, 1.0, 1.0);
            box = new Box(start, end);
        }

        if (player.getBoundingBox().intersects(box)) {
            player.addVelocity(pullVec);
        }
    }

    @Environment(EnvType.CLIENT)
    private static void handleMagnetSound(World world, BlockPos pos, BlockState state, PositiveMagnetBlockEntity be) {
        boolean isPowered = state.get(PositiveMagnetBlock.POWERED);
        MinecraftClient client = MinecraftClient.getInstance();

        if (isPowered) {
            if (be.activeMagnetSound == null || !client.getSoundManager().isPlaying(be.activeMagnetSound)) {
                MovingSoundInstance sound = new MovingSoundInstance(
                        ModSounds.MAGNET,
                        SoundCategory.BLOCKS,
                        world.random
                ) {
                    {
                        this.volume = 1.0f;
                        this.pitch = 1.0f;
                        this.repeat = true;
                        this.repeatDelay = 0;
                        this.attenuationType = SoundInstance.AttenuationType.LINEAR;
                        this.x = pos.getX() + 0.5;
                        this.y = pos.getY() + 0.5;
                        this.z = pos.getZ() + 0.5;
                        this.relative = false;
                    }

                    @Override
                    public boolean isDone() {
                        return false;
                    }

                    @Override
                    public void tick() {}
                };
                client.getSoundManager().play(sound);
                be.activeMagnetSound = sound;
            }
        } else {
            if (be.activeMagnetSound != null && client.getSoundManager().isPlaying(be.activeMagnetSound)) {
                client.getSoundManager().stop(be.activeMagnetSound);
                be.activeMagnetSound = null;
            }
        }
    }
}