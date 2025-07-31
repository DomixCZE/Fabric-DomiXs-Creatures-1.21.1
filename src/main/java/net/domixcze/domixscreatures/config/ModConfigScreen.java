package net.domixcze.domixscreatures.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModConfigScreen {
    public static Screen getScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("domixs-creatures.config.title"));

        builder.setSavingRunnable(ModConfig::saveConfig);

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("domixs-creatures.config.category.general"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();


        // sleep particle spawning
        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("domixs-creatures.config.option.enableSleepParticles"),
                        ModConfig.INSTANCE.enableSleepParticles
                )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("domixs-creatures.config.option.enableSleepParticles.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.enableSleepParticles = newValue)
                .build());

        // water strider riding
        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("domixs-creatures.config.option.enableWaterStriderRiding"),
                        ModConfig.INSTANCE.enableWaterStriderRiding
                )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("domixs-creatures.config.option.enableWaterStriderRiding.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.enableWaterStriderRiding = newValue)
                .build());

        // fire salamander smelting
        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("domixs-creatures.config.option.enableFireSalamanderSmelting"),
                        ModConfig.INSTANCE.enableFireSalamanderSmelting
                )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("domixs-creatures.config.option.enableFireSalamanderSmelting.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.enableFireSalamanderSmelting = newValue)
                .build());

        // fire salamander magma ball attack
        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("domixs-creatures.config.option.enableFireSalamanderMagmaBallAttack"),
                        ModConfig.INSTANCE.enableFireSalamanderMagmaBallAttack
                )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("domixs-creatures.config.option.enableFireSalamanderMagmaBallAttack.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.enableFireSalamanderMagmaBallAttack = newValue)
                .build());

        // spectral bat screech attack
        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("domixs-creatures.config.option.enableSpectralBatScreechAttack"),
                        ModConfig.INSTANCE.enableSpectralBatScreechAttack
                )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("domixs-creatures.config.option.enableSpectralBatScreechAttack.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.enableSpectralBatScreechAttack = newValue)
                .build());

        // caterpillar transformation
        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("domixs-creatures.config.option.enableCaterpillarTransformation"),
                        ModConfig.INSTANCE.enableCaterpillarTransformation
                )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("domixs-creatures.config.option.enableCaterpillarTransformation.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.enableCaterpillarTransformation = newValue)
                .build());

        // porcupine shooting quills
        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("domixs-creatures.config.option.enablePorcupineQuillAttack"),
                        ModConfig.INSTANCE.enablePorcupineQuillAttack
                )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("domixs-creatures.config.option.enablePorcupineQuillAttack.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.enablePorcupineQuillAttack = newValue)
                .build());

        // hermit crab daily trade limit
        general.addEntry(entryBuilder.startIntField(
                        Text.translatable("domixs-creatures.config.option.hermitCrabDailyTradeLimit"),
                        ModConfig.INSTANCE.hermitCrabDailyTradeLimit
                )
                .setDefaultValue(20)
                .setMin(0)
                .setMax(60)
                .setTooltip(Text.translatable("domixs-creatures.config.option.hermitCrabDailyTradeLimit.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.hermitCrabDailyTradeLimit = newValue)
                .build());

        // coconut "bong" sound effect
        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("domixs-creatures.config.option.enableCoconutBongSound"),
                        ModConfig.INSTANCE.enableCoconutBongSound
                )
                .setDefaultValue(false)
                .setTooltip(Text.translatable("domixs-creatures.config.option.enableCoconutBongSound.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.enableCoconutBongSound = newValue)
                .build());

        // Coconut Positive Effect Removal
        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("domixs-creatures.config.option.enable_coconut_positive_effect_removal"), ModConfig.INSTANCE.enableCoconutPositiveEffectRemoval)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("domixs-creatures.config.option.enable_coconut_positive_effect_removal.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.enableCoconutPositiveEffectRemoval = newValue)
                .build());

        // Coconut Negative Effect Removal
        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("domixs-creatures.config.option.enable_coconut_negative_effect_removal"), ModConfig.INSTANCE.enableCoconutNegativeEffectRemoval)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("domixs-creatures.config.option.enable_coconut_negative_effect_removal.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.enableCoconutNegativeEffectRemoval = newValue)
                .build());

        return builder.build();
    }
}