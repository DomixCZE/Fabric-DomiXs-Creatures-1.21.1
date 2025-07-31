package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class QuillProjectileEntity extends PersistentProjectileEntity {

    public QuillProjectileEntity(EntityType<? extends QuillProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public QuillProjectileEntity(World world, double x, double y, double z) {
        this(ModEntities.QUILL_PROJECTILE, world);
        this.setPos(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.inGround) {
            if (!this.getWorld().isClient) {
                this.inGroundTime++;
                if (this.inGroundTime > 20 * 60 * 5) { //5 minutes
                    this.discard();
                }
            }
        } else {
            this.inGroundTime = 0;
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity target = entityHitResult.getEntity();
        Entity owner = this.getOwner();

        DamageSource damageSource = this.getDamageSources().arrow(this, owner);
        float damage = (float) this.getDamage();

        if (target.damage(damageSource, damage)) {
            this.playSound(this.getSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));

            if (!this.getWorld().isClient) {
                this.discard();
            }
        } else {
            Vec3d bounce = this.getVelocity().multiply(-0.1);
            this.setVelocity(bounce);
            this.setYaw(this.getYaw() + 180.0F);
            this.prevYaw += 180.0F;
            if (!this.getWorld().isClient && this.getVelocity().lengthSquared() < 0.01) {
                this.discard();
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.inGround = true;
        this.pickupType = PickupPermission.ALLOWED; // Allow pickup after block hit
        this.setSound(SoundEvents.ENTITY_ARROW_HIT);
        this.setCritical(false);
    }

    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(ModItems.QUILL);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ModItems.QUILL);
    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }
}