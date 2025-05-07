package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.AdventurerHatItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class AdventurerHatModel extends GeoModel<AdventurerHatItem> {

    @Override
    public Identifier getModelResource(AdventurerHatItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/adventurer_hat.geo.json");
    }

    @Override
    public Identifier getTextureResource(AdventurerHatItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/adventurer_hat.png");
    }

    @Override
    public Identifier getAnimationResource(AdventurerHatItem animatable) {
        return null;
    }
}