package net.domixcze.domixscreatures.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config", "domixs-creatures.json");

    public boolean enableWaterStriderRiding = true;
    public boolean enableSleepParticles = true;
    public boolean enableFireSalamanderSmelting = true;
    public boolean enableFireSalamanderMagmaBallAttack = true;
    public boolean enableSpectralBatScreechAttack = true;
    public boolean enableCaterpillarTransformation = true;
    public boolean enablePorcupineQuillAttack = true;
    public boolean enableCoconutPositiveEffectRemoval = true;

    public boolean enableBeaverLogStripping = true;
    public boolean enableRaccoonStealing = true;
    public boolean enableCoconutNegativeEffectRemoval = true;

    public boolean enableCoconutBonkSound = false;

    public int hermitCrabDailyTradeLimit = 20;

    public static ModConfig INSTANCE = new ModConfig();

    // Load config from file
    public static void loadConfig() {
        if (Files.exists(CONFIG_PATH)) {
            try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
                INSTANCE = GSON.fromJson(reader, ModConfig.class);
                if (INSTANCE == null) { // Handle case where file is empty or malformed
                    INSTANCE = new ModConfig();
                    saveConfig();
                }
            } catch (IOException e) {
                System.err.println("Failed to load config for Domix's Creatures: " + e.getMessage());
                INSTANCE = new ModConfig(); // Reset to defaults on error
                saveConfig(); // Save defaults
            }
        } else {
            saveConfig(); // Create default config if file doesn't exist
        }
    }

    // Save config to file
    public static void saveConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent()); // Ensure config directory exists
            try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (IOException e) {
            System.err.println("Failed to save config for Domix's Creatures: " + e.getMessage());
        }
    }
}