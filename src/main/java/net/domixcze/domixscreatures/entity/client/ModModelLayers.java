package net.domixcze.domixscreatures.entity.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer CHAIN_ANCHOR =
            new EntityModelLayer(Identifier.of(DomiXsCreatures.MOD_ID, "chain_anchor"), "main");
}