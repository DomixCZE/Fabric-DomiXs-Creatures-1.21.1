package net.domixcze.domixscreatures.item.custom.armor;

import net.domixcze.domixscreatures.item.client.wyvern_armor.WyvernScaleChestplateRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

import java.util.List;
import java.util.function.Consumer;

public class WyvernScaleChestplateItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public WyvernScaleChestplateItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private WyvernScaleChestplateRenderer renderer;

            @Override
            public <T extends LivingEntity> BipedEntityModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable BipedEntityModel<T> original) {
                if (this.renderer == null)
                    this.renderer = new WyvernScaleChestplateRenderer();

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("domixs-creatures.tooltip.armor_set_bonus")
                .formatted(Formatting.GOLD)
                .append(Text.translatable("domixs-creatures.tooltip.wyvern_set_bonus_desc")
                        .formatted(Formatting.YELLOW)));

        super.appendTooltip(stack, context, tooltip, type);
    }
}