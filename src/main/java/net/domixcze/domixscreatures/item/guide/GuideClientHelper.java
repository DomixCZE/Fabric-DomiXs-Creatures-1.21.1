package net.domixcze.domixscreatures.item.guide;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class GuideClientHelper {
    public static void openGuideScreen() {
        MinecraftClient.getInstance().setScreen(new GuideMainScreen());
    }
}