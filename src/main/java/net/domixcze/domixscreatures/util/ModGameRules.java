package net.domixcze.domixscreatures.util;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class ModGameRules {
    //MOB BEHAVIOR
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_WATER_STRIDER_RIDING;
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_FIRE_SALAMANDER_SMELTING;
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_FIRE_SALAMANDER_MAGMA_ATTACK;
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_SPECTRAL_BAT_SCREECH;
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_CATERPILLAR_TRANSFORMATION;
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_PORCUPINE_QUILL_ATTACK;
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_BEAVER_LOG_STRIPPING;
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_RACCOON_STEALING;
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_RAT_CROP_BREAKING;
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_MOLE_WORM_DIGGING;
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_HIPPO_BOAT_ATTACKING;

    public static GameRules.Key<GameRules.BooleanRule> ENABLE_DEER_ANTLER_SHEDDING;
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_TERMITE_LOG_HOLLOWING;
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_ANT_LEAF_COLLECTING;

    //BLOCKS
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_RAIN_BREAKING_CRACKED_GLASS;

    //ITEMS
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_COCONUT_POSITIVE_EFFECT_REMOVAL;
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_COCONUT_NEGATIVE_EFFECT_REMOVAL;
    public static GameRules.Key<GameRules.BooleanRule> FLUTE_AFFECTS_ALL_TAMED;

    //OTHER
    public static GameRules.Key<GameRules.IntRule> HERMIT_CRAB_DAILY_TRADE_LIMIT;

    public static void register() {
        //MOBS
        ENABLE_WATER_STRIDER_RIDING = GameRuleRegistry.register(
                "enableWaterStriderRiding",
                GameRules.Category.MOBS,
                GameRuleFactory.createBooleanRule(true)
        );

        ENABLE_FIRE_SALAMANDER_SMELTING = GameRuleRegistry.register(
                "enableFireSalamanderSmelting",
                GameRules.Category.MOBS,
                GameRuleFactory.createBooleanRule(true)
        );

        ENABLE_FIRE_SALAMANDER_MAGMA_ATTACK = GameRuleRegistry.register(
                "enableFireSalamanderMagmaAttack",
                GameRules.Category.MOBS,
                GameRuleFactory.createBooleanRule(true)
        );

        ENABLE_SPECTRAL_BAT_SCREECH = GameRuleRegistry.register(
                "enableSpectralBatScreech",
                GameRules.Category.MOBS,
                GameRuleFactory.createBooleanRule(true)
        );

        ENABLE_CATERPILLAR_TRANSFORMATION = GameRuleRegistry.register(
                "enableCaterpillarTransformation",
                GameRules.Category.MOBS,
                GameRuleFactory.createBooleanRule(true)
        );

        ENABLE_PORCUPINE_QUILL_ATTACK = GameRuleRegistry.register(
                "enablePorcupineQuillAttack",
                GameRules.Category.MOBS,
                GameRuleFactory.createBooleanRule(true)
        );

        ENABLE_BEAVER_LOG_STRIPPING = GameRuleRegistry.register(
                "enableBeaverLogStripping",
                GameRules.Category.MOBS,
                GameRuleFactory.createBooleanRule(true)
        );

        ENABLE_RACCOON_STEALING = GameRuleRegistry.register(
                "enableRaccoonStealing",
                GameRules.Category.MOBS,
                GameRuleFactory.createBooleanRule(true)
        );

        ENABLE_RAT_CROP_BREAKING = GameRuleRegistry.register(
                "enableRatCropBreaking",
                GameRules.Category.MOBS,
                GameRuleFactory.createBooleanRule(true)
        );

        ENABLE_MOLE_WORM_DIGGING = GameRuleRegistry.register(
                "enableMoleWormDigging",
                GameRules.Category.MOBS,
                GameRuleFactory.createBooleanRule(true)
        );

        ENABLE_HIPPO_BOAT_ATTACKING = GameRuleRegistry.register(
                "enableHippoBoatAttacking",
                GameRules.Category.MOBS,
                GameRuleFactory.createBooleanRule(true)
        );

        ENABLE_DEER_ANTLER_SHEDDING = GameRuleRegistry.register(
                "enableDeerAntlerShedding",
                GameRules.Category.MOBS,
                GameRuleFactory.createBooleanRule(true)
        );

        ENABLE_TERMITE_LOG_HOLLOWING = GameRuleRegistry.register(
                "enableTermiteLogHollowing",
                GameRules.Category.MOBS,
                GameRuleFactory.createBooleanRule(true)
        );

        ENABLE_ANT_LEAF_COLLECTING = GameRuleRegistry.register(
                "enableAntLeafCollecting",
                GameRules.Category.MOBS,
                GameRuleFactory.createBooleanRule(true)
        );

        HERMIT_CRAB_DAILY_TRADE_LIMIT = GameRuleRegistry.register(
                "hermitCrabDailyTradeLimit",
                GameRules.Category.MOBS,
                GameRuleFactory.createIntRule(20, 1, 64)
        );

        //MISC
        ENABLE_RAIN_BREAKING_CRACKED_GLASS = GameRuleRegistry.register(
                "enableRainBreakingCrackedGlass",
                GameRules.Category.MISC,
                GameRuleFactory.createBooleanRule(true)
        );

        ENABLE_COCONUT_POSITIVE_EFFECT_REMOVAL = GameRuleRegistry.register(
                "enableCoconutPositiveEffectRemoval",
                GameRules.Category.MISC,
                GameRuleFactory.createBooleanRule(true)
        );

        ENABLE_COCONUT_NEGATIVE_EFFECT_REMOVAL = GameRuleRegistry.register(
                "enableCoconutNegativeEffectRemoval",
                GameRules.Category.MISC,
                GameRuleFactory.createBooleanRule(true)
        );

        FLUTE_AFFECTS_ALL_TAMED = GameRuleRegistry.register(
                "fluteAffectsAllTamed",
                GameRules.Category.MISC,
                GameRuleFactory.createBooleanRule(false)
        );
    }
}
