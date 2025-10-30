package net.domixcze.domixscreatures.screen;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.screen.custom.FishTrapScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModScreenHandlers {
    public static final ScreenHandlerType<FishTrapScreenHandler> FISH_TRAP_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(DomiXsCreatures.MOD_ID, "fish_trap_screen_handler"),
                    new ExtendedScreenHandlerType<>(FishTrapScreenHandler::new, BlockPos.PACKET_CODEC));

    public static void registerScreenHandlers() {
        DomiXsCreatures.LOGGER.info("Registering Screen Handlers for " + DomiXsCreatures.MOD_ID);
    }
}