package net.domixcze.domixscreatures.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.domixcze.domixscreatures.DomiXsCreatures;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.UnaryOperator;

public class ModDataComponents {

    public static final ComponentType<CapturedEntityComponent> CAPTURED_ENTITY =
            register("captured_entity", builder -> builder.codec(CapturedEntityComponent.CODEC));

    public static final ComponentType<BoundItemComponent> BOUND_ITEM =
            register("bound_item", builder -> builder.codec(BoundItemComponent.CODEC));

    public static final ComponentType<FluteModeComponent> FLUTE_MODE =
            register("flute_mode", builder -> builder.codec(FluteModeComponent.CODEC));

    public static final ComponentType<JarContentsComponent> JAR_CONTENTS =
            register("jar_contents", builder -> builder.codec(JarContentsComponent.CODEC));

    public static final ComponentType<DeathWhistleComponent> DEATH_WHISTLE =
            register("death_whistle_sound", builder -> builder.codec(DeathWhistleComponent.CODEC));

    public static final ComponentType<CoatedQuillComponent> COATED_QUILL =
            register("quill_effect", builder -> builder.codec(CoatedQuillComponent.CODEC));

    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                Identifier.of(DomiXsCreatures.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build()
        );
    }

    public static void registerDataComponents() {
        DomiXsCreatures.LOGGER.info("Registering Data Components for " + DomiXsCreatures.MOD_ID);
    }

    public record JarContentsComponent(Identifier type, List<NbtCompound> entities) {
        public static final Codec<JarContentsComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("type").forGetter(JarContentsComponent::type),
                NbtCompound.CODEC.listOf().fieldOf("entities").forGetter(JarContentsComponent::entities)
        ).apply(instance, JarContentsComponent::new));
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

    public record FluteModeComponent(String mode) {
        public static final Codec<FluteModeComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("mode").forGetter(FluteModeComponent::mode)
        ).apply(instance, FluteModeComponent::new));
    }

    public record CoatedQuillComponent(Identifier effectId, int duration, int amplifier) {

        public static final Codec<CoatedQuillComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("effect").forGetter(CoatedQuillComponent::effectId),
                Codec.INT.fieldOf("duration").forGetter(CoatedQuillComponent::duration),
                Codec.INT.fieldOf("amplifier").forGetter(CoatedQuillComponent::amplifier)
        ).apply(instance, CoatedQuillComponent::new));

        public StatusEffectInstance toInstance() {
            return Registries.STATUS_EFFECT.getEntry(effectId)
                    .map(entry -> new StatusEffectInstance(entry, duration, amplifier))
                    .orElse(null);
        }
    }

    public record DeathWhistleComponent(Identifier soundId, Identifier entityTypeId) {
        public static final Codec<DeathWhistleComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("sound").forGetter(DeathWhistleComponent::soundId),
                Identifier.CODEC.fieldOf("entity_type").forGetter(DeathWhistleComponent::entityTypeId)
        ).apply(instance, DeathWhistleComponent::new));
    }
}