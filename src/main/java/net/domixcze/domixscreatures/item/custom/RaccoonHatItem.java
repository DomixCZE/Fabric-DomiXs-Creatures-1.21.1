package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.item.client.RaccoonHatRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.DataTickets;

import java.util.function.Consumer;

public class RaccoonHatItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public RaccoonHatItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private RaccoonHatRenderer renderer;

            @Override
            public <T extends LivingEntity> BipedEntityModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable BipedEntityModel<T> original) {
                if (this.renderer == null)
                    this.renderer = new RaccoonHatRenderer();

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private <T extends GeoItem> PlayState predicate(AnimationState<T> state) {
        LivingEntity livingEntity = state.getData(DataTickets.ENTITY) instanceof LivingEntity le ? le : null;

        if (livingEntity == null) {
            return PlayState.STOP;
        }

        if (!livingEntity.isOnGround() && livingEntity.fallDistance > 1.0F) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.raccoon_hat.fall", Animation.LoopType.LOOP));
        }
        else if (livingEntity.getVelocity().horizontalLengthSquared() > 1.0E-6) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.raccoon_hat.walk", Animation.LoopType.LOOP));
        }
        else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.raccoon_hat.idle", Animation.LoopType.LOOP));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}