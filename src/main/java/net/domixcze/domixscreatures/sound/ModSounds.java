package net.domixcze.domixscreatures.sound;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent ZAP = registerSoundEvent("zap");

    public static final SoundEvent CONCH_SHELL_USE = registerSoundEvent("conch_shell_use");

    public static final SoundEvent BLOWGUN_USE = registerSoundEvent("blowgun_use");

    public static final SoundEvent COMMAND_FLUTE_SIT = registerSoundEvent("command_flute_sit");
    public static final SoundEvent COMMAND_FLUTE_FOLLOW = registerSoundEvent("command_flute_follow");

    public static final SoundEvent COPPER_TRUMPET_1 = registerSoundEvent("copper_trumpet_1");
    public static final SoundEvent COPPER_TRUMPET_2 = registerSoundEvent("copper_trumpet_2");
    public static final SoundEvent COPPER_TRUMPET_EXPOSED_1 = registerSoundEvent("copper_trumpet_exposed_1");
    public static final SoundEvent COPPER_TRUMPET_EXPOSED_2 = registerSoundEvent("copper_trumpet_exposed_2");
    public static final SoundEvent COPPER_TRUMPET_WEATHERED_1 = registerSoundEvent("copper_trumpet_weathered_1");
    public static final SoundEvent COPPER_TRUMPET_WEATHERED_2 = registerSoundEvent("copper_trumpet_weathered_2");
    public static final SoundEvent COPPER_TRUMPET_OXIDIZED_1 = registerSoundEvent("copper_trumpet_oxidized_1");
    public static final SoundEvent COPPER_TRUMPET_OXIDIZED_2 = registerSoundEvent("copper_trumpet_oxidized_2");

    public static final SoundEvent EEL_ATTACK = registerSoundEvent("eel_attack");

    public static final SoundEvent SPECTRAL_BAT_SCREECH = registerSoundEvent("spectral_bat_screech");

    public static final SoundEvent WHALE_AMBIENT = registerSoundEvent("whale_ambient");
    public static final SoundEvent WHALE_HURT = registerSoundEvent("whale_hurt");
    public static final SoundEvent WHALE_DEATH = registerSoundEvent("whale_death");

    public static final SoundEvent FIRE_SALAMANDER_HURT = registerSoundEvent("fire_salamander_hurt");
    public static final SoundEvent FIRE_SALAMANDER_DEATH = registerSoundEvent("fire_salamander_death");

    public static final SoundEvent CROCODILE_AMBIENT = registerSoundEvent("crocodile_ambient");
    public static final SoundEvent CROCODILE_HURT = registerSoundEvent("crocodile_hurt");
    public static final SoundEvent CROCODILE_DEATH = registerSoundEvent("crocodile_death");
    public static final SoundEvent CROCODILE_ATTACK = registerSoundEvent("crocodile_attack");

    public static final SoundEvent HIPPO_AMBIENT = registerSoundEvent("hippo_ambient");
    public static final SoundEvent HIPPO_HURT = registerSoundEvent("hippo_hurt");

    public static final SoundEvent VINE_HURT = registerSoundEvent("vine_hurt");
    public static final SoundEvent VINE_DEATH = registerSoundEvent("vine_death");
    public static final SoundEvent VINE_GRAB = registerSoundEvent("vine_grab");

    public static final SoundEvent SHAMAN_AMBIENT = registerSoundEvent("shaman_ambient");
    public static final SoundEvent SHAMAN_HURT = registerSoundEvent("shaman_hurt");
    public static final SoundEvent SHAMAN_DEATH = registerSoundEvent("shaman_death");

    public static final SoundEvent TIGER_HURT = registerSoundEvent("tiger_hurt");

    public static final SoundEvent RACCOON_AMBIENT = registerSoundEvent("raccoon_ambient");
    public static final SoundEvent RACCOON_HURT = registerSoundEvent("raccoon_hurt");
    public static final SoundEvent RACCOON_DEATH = registerSoundEvent("raccoon_death");

    public static final SoundEvent CAPYBARA_HURT = registerSoundEvent("capybara_hurt");

    public static final SoundEvent BISON_AMBIENT = registerSoundEvent("bison_ambient");
    public static final SoundEvent BISON_HURT = registerSoundEvent("bison_hurt");
    public static final SoundEvent BISON_DEATH = registerSoundEvent("bison_death");

    public static final SoundEvent MOOSE_HURT = registerSoundEvent("moose_hurt");
    public static final SoundEvent MOOSE_DEATH = registerSoundEvent("moose_death");

    public static final SoundEvent RAT_AMBIENT = registerSoundEvent("rat_ambient");
    public static final SoundEvent RAT_HURT = registerSoundEvent("rat_hurt");
    public static final SoundEvent RAT_DEATH = registerSoundEvent("rat_death");

    public static final SoundEvent PENGUIN_AMBIENT = registerSoundEvent("penguin_ambient");
    public static final SoundEvent PENGUIN_HURT = registerSoundEvent("penguin_hurt");
    public static final SoundEvent PENGUIN_NOOT_NOOT = registerSoundEvent("penguin_noot_noot");

    public static final SoundEvent CAVE_WYVERN_BREATH = registerSoundEvent("cave_wyvern_breath");
    public static final SoundEvent CAVE_WYVERN_CONVERT = registerSoundEvent("cave_wyvern_convert");

    public static final SoundEvent TERMITE_EAT = registerSoundEvent("termite_eat");

    public static final SoundEvent CROW_AMBIENT = registerSoundEvent("crow_ambient");
    public static final SoundEvent CROW_HURT = registerSoundEvent("crow_hurt");
    public static final SoundEvent CROW_ALERT = registerSoundEvent("crow_alert");

    public static final SoundEvent OWL_AMBIENT = registerSoundEvent("owl_ambient");
    public static final SoundEvent OWL_HURT = registerSoundEvent("owl_hurt");
    public static final SoundEvent OWL_DEATH = registerSoundEvent("owl_death");

    public static final SoundEvent PIGEON_AMBIENT = registerSoundEvent("pigeon_ambient");

    public static final SoundEvent COCONUT_LAND = registerSoundEvent("coconut_land");

    public static final SoundEvent MAGNET_IN = registerSoundEvent("magnet_in");
    public static final SoundEvent MAGNET_OUT = registerSoundEvent("magnet_out");

    public static final SoundEvent MAGNET = registerSoundEvent("magnet");

    public static final SoundEvent STATUE_PUSH = registerSoundEvent("statue_push");

    public static final SoundEvent SPIKE_TRAP_EXTEND = registerSoundEvent("spike_trap_extend");
    public static final SoundEvent SPIKE_TRAP_RETRACT = registerSoundEvent("spike_trap_retract");

    public static final SoundEvent ANCIENT_CHEST_OPEN = registerSoundEvent("ancient_chest_open");
    public static final SoundEvent ANCIENT_CHEST_CLOSE = registerSoundEvent("ancient_chest_close");

    public static final SoundEvent GOLD_PILE_BREAK = registerSoundEvent("gold_pile_break");
    public static final SoundEvent GOLD_PILE_PLACE = registerSoundEvent("gold_pile_place");
    public static final SoundEvent GOLD_PILE_STEP = registerSoundEvent("gold_pile_step");
    public static final SoundEvent GOLD_PILE_HIT = registerSoundEvent("gold_pile_hit");
    public static final SoundEvent GOLD_PILE_FALL = registerSoundEvent("gold_pile_fall");

    public static final BlockSoundGroup GOLD_PILE_SOUNDS = new BlockSoundGroup(1f, 1f,
            ModSounds.GOLD_PILE_BREAK, ModSounds.GOLD_PILE_STEP, ModSounds.GOLD_PILE_PLACE, ModSounds.GOLD_PILE_HIT, ModSounds.GOLD_PILE_FALL);

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(DomiXsCreatures.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        DomiXsCreatures.LOGGER.info("Registering Sounds for " + DomiXsCreatures.MOD_ID);
    }
}