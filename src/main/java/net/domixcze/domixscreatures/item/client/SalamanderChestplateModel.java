package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.SalamanderChestplateItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SalamanderChestplateModel extends GeoModel<SalamanderChestplateItem> {

    @Override
    public Identifier getModelResource(SalamanderChestplateItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/salamander_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(SalamanderChestplateItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/salamander_armor.png");
    }

    @Override
    public Identifier getAnimationResource(SalamanderChestplateItem animatable) {
        return null;
    }
}