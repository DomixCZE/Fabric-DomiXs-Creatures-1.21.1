package net.domixcze.domixscreatures;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.config.ModConfigScreen;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.client.anglerfish.AnglerfishRenderer;
import net.domixcze.domixscreatures.entity.client.arapaima.ArapaimaRenderer;
import net.domixcze.domixscreatures.entity.client.beaver.BeaverRenderer;
import net.domixcze.domixscreatures.entity.client.betta_fish.BettaFishRenderer;
import net.domixcze.domixscreatures.entity.client.bison.BisonRenderer;
import net.domixcze.domixscreatures.entity.client.boar.BoarRenderer;
import net.domixcze.domixscreatures.entity.client.butterfly.ButterflyRenderer;
import net.domixcze.domixscreatures.entity.client.caterpillar.CaterpillarRenderer;
import net.domixcze.domixscreatures.entity.client.cheetah.CheetahRenderer;
import net.domixcze.domixscreatures.entity.client.crocodile.CrocodileRenderer;
import net.domixcze.domixscreatures.entity.client.deer.DeerRenderer;
import net.domixcze.domixscreatures.entity.client.eel.EelRenderer;
import net.domixcze.domixscreatures.entity.client.fire_salamander.FireSalamanderRenderer;
import net.domixcze.domixscreatures.entity.client.freshwater_stingray.FreshwaterStingrayRenderer;
import net.domixcze.domixscreatures.entity.client.goldfish.GoldfishRenderer;
import net.domixcze.domixscreatures.entity.client.gorilla.GorillaRenderer;
import net.domixcze.domixscreatures.entity.client.hermit_crab.HermitCrabRenderer;
import net.domixcze.domixscreatures.entity.client.hippo.HippoRenderer;
import net.domixcze.domixscreatures.entity.client.iguana.IguanaRenderer;
import net.domixcze.domixscreatures.entity.client.magma_ball.MagmaBallRenderer;
import net.domixcze.domixscreatures.entity.client.mole.MoleRenderer;
import net.domixcze.domixscreatures.entity.client.moose.MooseRenderer;
import net.domixcze.domixscreatures.entity.client.mud_golem.MudGolemRenderer;
import net.domixcze.domixscreatures.entity.client.neon_tetra.NeonTetraRenderer;
import net.domixcze.domixscreatures.entity.client.peacock_bass.PeacockBassRenderer;
import net.domixcze.domixscreatures.entity.client.piranha.PiranhaRenderer;
import net.domixcze.domixscreatures.entity.client.porcupine.PorcupineRenderer;
import net.domixcze.domixscreatures.entity.client.quill_projectile.QuillProjectileRenderer;
import net.domixcze.domixscreatures.entity.client.shaman.ShamanRenderer;
import net.domixcze.domixscreatures.entity.client.shark.SharkRenderer;
import net.domixcze.domixscreatures.entity.client.spectral_bat.SpectralBatRenderer;
import net.domixcze.domixscreatures.entity.client.sun_bear.SunBearRenderer;
import net.domixcze.domixscreatures.entity.client.test.TestRenderer;
import net.domixcze.domixscreatures.entity.client.tiger.TigerRenderer;
import net.domixcze.domixscreatures.entity.client.vine.VineRenderer;
import net.domixcze.domixscreatures.entity.client.water_strider.WaterStriderRenderer;
import net.domixcze.domixscreatures.entity.client.whale.WhaleRenderer;
import net.domixcze.domixscreatures.entity.client.wisp.WispRenderer;
import net.domixcze.domixscreatures.entity.client.worm.WormRenderer;
import net.domixcze.domixscreatures.particle.*;
import net.domixcze.domixscreatures.util.ModModelPredicates;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class DomiXsCreaturesClient implements ClientModInitializer {
    public static KeyBinding OPEN_CONFIG_KEY;

    @Override
    public void onInitializeClient() {
        ModModelPredicates.registerModPredicates();

        EntityRendererRegistry.register(ModEntities.FIRE_SALAMANDER, FireSalamanderRenderer::new);
        EntityRendererRegistry.register(ModEntities.SPECTRAL_BAT, SpectralBatRenderer::new);
        EntityRendererRegistry.register(ModEntities.WHALE, WhaleRenderer::new);
        EntityRendererRegistry.register(ModEntities.GOLDFISH, GoldfishRenderer::new);
        EntityRendererRegistry.register(ModEntities.WISP, WispRenderer::new);
        EntityRendererRegistry.register(ModEntities.CROCODILE, CrocodileRenderer::new);
        EntityRendererRegistry.register(ModEntities.BEAVER, BeaverRenderer::new);
        EntityRendererRegistry.register(ModEntities.IGUANA, IguanaRenderer::new);
        EntityRendererRegistry.register(ModEntities.TIGER, TigerRenderer::new);
        EntityRendererRegistry.register(ModEntities.DEER, DeerRenderer::new);
        EntityRendererRegistry.register(ModEntities.MOOSE, MooseRenderer::new);
        EntityRendererRegistry.register(ModEntities.SHARK, SharkRenderer::new);
        EntityRendererRegistry.register(ModEntities.EEL, EelRenderer::new);
        EntityRendererRegistry.register(ModEntities.MUD_GOLEM, MudGolemRenderer::new);
        EntityRendererRegistry.register(ModEntities.HIPPO, HippoRenderer::new);
        EntityRendererRegistry.register(ModEntities.SHAMAN, ShamanRenderer::new);
        EntityRendererRegistry.register(ModEntities.VINE, VineRenderer::new);
        EntityRendererRegistry.register(ModEntities.MOLE, MoleRenderer::new);
        EntityRendererRegistry.register(ModEntities.WORM, WormRenderer::new);
        EntityRendererRegistry.register(ModEntities.PORCUPINE, PorcupineRenderer::new);
        EntityRendererRegistry.register(ModEntities.WATER_STRIDER, WaterStriderRenderer::new);
        EntityRendererRegistry.register(ModEntities.BOAR, BoarRenderer::new);
        EntityRendererRegistry.register(ModEntities.BISON, BisonRenderer::new);
        EntityRendererRegistry.register(ModEntities.SUN_BEAR, SunBearRenderer::new);
        EntityRendererRegistry.register(ModEntities.BUTTERFLY, ButterflyRenderer::new);
        EntityRendererRegistry.register(ModEntities.CATERPILLAR, CaterpillarRenderer::new);
        EntityRendererRegistry.register(ModEntities.GORILLA, GorillaRenderer::new);
        EntityRendererRegistry.register(ModEntities.PIRANHA, PiranhaRenderer::new);
        EntityRendererRegistry.register(ModEntities.PEACOCK_BASS, PeacockBassRenderer::new);
        EntityRendererRegistry.register(ModEntities.NEON_TETRA, NeonTetraRenderer::new);
        EntityRendererRegistry.register(ModEntities.BETTA_FISH, BettaFishRenderer::new);
        EntityRendererRegistry.register(ModEntities.ARAPAIMA, ArapaimaRenderer::new);
        EntityRendererRegistry.register(ModEntities.ANGLERFISH, AnglerfishRenderer::new);
        EntityRendererRegistry.register(ModEntities.FRESHWATER_STINGRAY, FreshwaterStingrayRenderer::new);
        EntityRendererRegistry.register(ModEntities.CHEETAH, CheetahRenderer::new);
        EntityRendererRegistry.register(ModEntities.HERMIT_CRAB, HermitCrabRenderer::new);

        EntityRendererRegistry.register(ModEntities.MAGMA_BALL, MagmaBallRenderer::new);
        EntityRendererRegistry.register(ModEntities.QUILL_PROJECTILE, QuillProjectileRenderer::new);

        EntityRendererRegistry.register(ModEntities.TEST, TestRenderer::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.SCREECH, SonicBoomParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.INK, InkParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SLEEP, SleepParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SPECTRAL_LEAVES, SpectralLeavesParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.BLOOD_PUDDLE, BloodPuddleParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.FALLING_BLOOD, FallingBloodParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.ELECTRIC, ElectricParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.NEGATIVE_MAGNET, GlowParticle.ElectricSparkFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.POSITIVE_MAGNET, GlowParticle.ElectricSparkFactory::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CRACKED_GLASS_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PILE_OF_STICKS_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SPECTRAL_LEAVES, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SPECTRAL_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SPECTRAL_TRAPDOOR, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COCONUT_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PALM_LEAVES, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PALM_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PALM_TRAPDOOR, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SPECTRAL_SAPLING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PALM_SAPLING, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MUD_BLOSSOM, RenderLayer.getCutout());

        OPEN_CONFIG_KEY = new KeyBinding(
                "key.domixs-creatures.open_config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O, // default keybind O
                "category.domixs-creatures.general"
        );
        KeyBindingHelper.registerKeyBinding(OPEN_CONFIG_KEY);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (OPEN_CONFIG_KEY.wasPressed()) {
                client.setScreen(ModConfigScreen.getScreen(null));
            }
        });
    }
}