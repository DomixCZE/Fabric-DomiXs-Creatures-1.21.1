package net.domixcze.domixscreatures;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.client.beaver.BeaverRenderer;
import net.domixcze.domixscreatures.entity.client.crocodile.CrocodileRenderer;
import net.domixcze.domixscreatures.entity.client.deer.DeerRenderer;
import net.domixcze.domixscreatures.entity.client.fire_salamander.FireSalamanderRenderer;
import net.domixcze.domixscreatures.entity.client.goldfish.GoldfishRenderer;
import net.domixcze.domixscreatures.entity.client.iguana.IguanaRenderer;
import net.domixcze.domixscreatures.entity.client.spectral_bat.SpectralBatRenderer;
//import net.domixcze.domixscreatures.entity.client.test.TestRenderer;
import net.domixcze.domixscreatures.entity.client.tiger.TigerRenderer;
import net.domixcze.domixscreatures.entity.client.whale.WhaleRenderer;
import net.domixcze.domixscreatures.entity.client.wisp.WispRenderer;
import net.domixcze.domixscreatures.particle.InkParticle;
import net.domixcze.domixscreatures.particle.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.client.render.RenderLayer;

public class DomiXsCreaturesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
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

        //EntityRendererRegistry.register(ModEntities.TEST, TestRenderer::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.SCREECH, SonicBoomParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.INK, InkParticle.Factory::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CRACKED_GLASS_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PILE_OF_STICKS_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SPECTRAL_LEAVES, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SPECTRAL_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SPECTRAL_TRAPDOOR, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SPECTRAL_SAPLING, RenderLayer.getCutout());
    }
}