package net.domixcze.domixscreatures.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.domixcze.domixscreatures.DomiXsCreatures;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class ModDataComponents {

    public static final ComponentType<CapturedEntityComponent> CAPTURED_ENTITY =
            register("captured_entity", builder -> builder.codec(CapturedEntityComponent.CODEC));

    public static final ComponentType<BoundItemComponent> BOUND_ITEM =
            register("bound_item", builder -> builder.codec(BoundItemComponent.CODEC));

    public static final ComponentType<DeathWhistleComponent> DEATH_WHISTLE =
            register("death_whistle_sound", builder -> builder.codec(DeathWhistleComponent.CODEC));

    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                Identifier.of(DomiXsCreatures.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build()
        );
    }

    public static void registerDataComponents() {
        DomiXsCreatures.LOGGER.info("Registering data components for " + DomiXsCreatures.MOD_ID);
    }

    public record CapturedEntityComponent(Identifier type, NbtCompound nbt) {
        public static final Codec<CapturedEntityComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("type").forGetter(CapturedEntityComponent::type),
                NbtCompound.CODEC.fieldOf("nbt").forGetter(CapturedEntityComponent::nbt)
        ).apply(instance, CapturedEntityComponent::new));
    }

    public record BoundItemComponent(ItemStack stack) {
        public static final Codec<BoundItemComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("stack").forGetter(BoundItemComponent::stack)
        ).apply(instance, BoundItemComponent::new));
    }

    public record DeathWhistleComponent(Identifier soundId, Identifier entityTypeId) {
        public static final Codec<DeathWhistleComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("sound").forGetter(DeathWhistleComponent::soundId),
                Identifier.CODEC.fieldOf("entity_type").forGetter(DeathWhistleComponent::entityTypeId)
        ).apply(instance, DeathWhistleComponent::new));
    }
}