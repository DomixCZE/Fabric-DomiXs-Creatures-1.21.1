package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.component.ModDataComponents;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class QuillProjectileEntity extends PersistentProjectileEntity {

    private ModDataComponents.CoatedQuillComponent dartEffect;

    private static final TrackedData<Boolean> COATED = DataTracker.registerData(QuillProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public QuillProjectileEntity(EntityType<? extends QuillProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public QuillProjectileEntity(World world, double x, double y, double z) {
        this(ModEntities.QUILL_PROJECTILE, world);
        this.setPos(x, y, z);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(COATED, false);
    }

    public void setDartEffect(ModDataComponents.CoatedQuillComponent effect) {
        this.dartEffect = effect;
        this.dataTracker.set(COATED, effect != null);
    }

    public boolean isCoated() {
        return this.dataTracker.get(COATED);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.inGround) {
            if (!this.getWorld().isClient) {
                this.inGroundTime++;
                if (this.inGroundTime > 20 * 60 * 5) { // 5 minutes
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

            if (!this.getWorld().isClient && dartEffect != null && target instanceof LivingEntity living) {
                living.addStatusEffect(dartEffect.toInstance());
            }

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
    protected ItemStack asItemStack() {
        ItemStack stack = new ItemStack(ModItems.QUILL);
        if (dartEffect != null) {
            stack.set(ModDataComponents.COATED_QUILL, dartEffect);
        }
        return stack;
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
