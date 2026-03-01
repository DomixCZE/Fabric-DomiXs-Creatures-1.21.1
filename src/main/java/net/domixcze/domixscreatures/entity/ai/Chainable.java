package net.domixcze.domixscreatures.entity.ai;

import com.mojang.datafixers.util.Either;
import net.domixcze.domixscreatures.entity.custom.ChainAnchorEntity;
import net.domixcze.domixscreatures.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Chainable {

    @Nullable
    UUID getChainOwner();
    void setChainOwner(@Nullable UUID uuid);

    @Nullable
    ChainData getChainData();
    void setChainData(@Nullable ChainData chainData);

    default boolean isChained() {
        return this.getChainData() != null || this.getChainOwner() != null;
    }

    @Nullable
    default ChainData readChainDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("chain", 10)) {
            return new ChainData(Either.left(nbt.getCompound("chain").getUuid("UUID")));
        } else if (nbt.contains("chain", 11)) {
            return NbtHelper.toBlockPos(nbt, "chain").map(pos -> new ChainData(Either.right(pos))).orElse(null);
        }
        return null;
    }

    default void writeChainDataToNbt(NbtCompound nbt, @Nullable ChainData chainData) {
        if (chainData != null) {
            Either<UUID, BlockPos> either = chainData.unresolvedChainData;
            Entity holder = chainData.chainHolder;

            if (holder != null) {
                either = Either.left(holder.getUuid());
            }

            if (either != null) {
                nbt.put("chain", either.map(uuid -> {
                    NbtCompound tag = new NbtCompound();
                    tag.putUuid("UUID", uuid);
                    return tag;
                }, NbtHelper::fromBlockPos));
            }
        }
    }

    static <E extends Entity & Chainable> void tickChain(E entity) {
        ChainData data = entity.getChainData();
        if (data == null && entity.getChainOwner() != null) {
            data = new ChainData(entity.getChainOwner());
            entity.setChainData(data);
        }

        if (data != null && data.unresolvedChainData != null) {
            resolveChainData(entity, data);
        }

        Entity holder = entity.getChainHolder();
        if (holder != null && holder.getWorld() == entity.getWorld()) {
            if (!entity.isAlive() || !holder.isAlive()) {
                entity.detachChain(true);
                return;
            }

            Vec3d holderPos = holder.getPos();
            Vec3d delta = holderPos.subtract(entity.getPos());
            double distance = delta.length();

            if (distance > 32.0) {
                entity.detachChain(true);
                return;
            }

            double maxDist = 16.0;
            if (distance > maxDist && holder instanceof ServerPlayerEntity player) {
                double overDist = distance - maxDist;
                double pullStrength = 0.05 * Math.pow(overDist, 1.5);
                Vec3d pullVel = delta.normalize().multiply(-pullStrength);

                player.addVelocity(pullVel.x, pullVel.y, pullVel.z);
                player.velocityDirty = true;
                player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket(player));
            }

            double pullStart = 8.0;
            if (distance > pullStart) {
                double pullFactor = Math.min((distance - pullStart) / (maxDist - pullStart), 1.0);
                Vec3d entityVel = delta.normalize().multiply(0.5 * pullFactor);
                entity.addVelocity(entityVel.x, entityVel.y, entityVel.z);
                entity.velocityDirty = true;
            }
        }
    }

    private static <E extends Entity & Chainable> void resolveChainData(E entity, ChainData data) {
        if (data.unresolvedChainData != null && entity.getWorld() instanceof ServerWorld serverWorld) {
            Optional<UUID> uuidOpt = data.unresolvedChainData.left();
            if (uuidOpt.isPresent()) {
                Entity found = serverWorld.getEntity(uuidOpt.get());
                if (found != null) {
                    entity.attachChain(found);
                    return;
                }
            }

            Optional<BlockPos> posOpt = data.unresolvedChainData.right();
            if (posOpt.isPresent()) {
                BlockPos pos = posOpt.get();
                List<ChainAnchorEntity> anchors = serverWorld.getEntitiesByClass(ChainAnchorEntity.class,
                        new Box(pos).expand(1.0),
                        a -> a.getAttachedBlockPos().equals(pos));

                if (!anchors.isEmpty()) {
                    entity.attachChain(anchors.getFirst());
                    return;
                }
            }

            if (entity.age > 100) {
                entity.detachChain(true);
            }
        }
    }

    default void detachChain(boolean dropItem) {
        this.setChainData(null);
        this.setChainOwner(null);

        Entity entity = (Entity)this;
        if (!entity.getWorld().isClient) {
            if (dropItem) entity.dropItem(ModItems.IRON_SHACKLE);
            entity.getWorld().playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_CHAIN_BREAK, SoundCategory.NEUTRAL, 1.0f, 1.0f);
        }
    }

    default void attachChain(Entity holder) {
        this.setChainData(new ChainData(holder));
        this.setChainOwner(holder.getUuid());
    }

    @Nullable
    default Entity getChainHolder() {
        ChainData data = this.getChainData();
        return data == null ? null : data.chainHolder;
    }

    class ChainData {
        @Nullable public Entity chainHolder;
        @Nullable public Either<UUID, BlockPos> unresolvedChainData;

        public ChainData(Either<UUID, BlockPos> unresolved) { this.unresolvedChainData = unresolved; }
        public ChainData(Entity holder) { this.chainHolder = holder; }
        public ChainData(UUID uuid) { this.unresolvedChainData = Either.left(uuid); }

        public void setChainHolder(Entity holder) {
            this.chainHolder = holder;
            this.unresolvedChainData = null;
        }
    }
}