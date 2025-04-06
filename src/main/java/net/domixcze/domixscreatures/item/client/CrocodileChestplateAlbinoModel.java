package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.CrocodileChestplateAlbinoItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CrocodileChestplateAlbinoModel extends GeoModel<CrocodileChestplateAlbinoItem> {

    @Override
    public Identifier getModelResource(CrocodileChestplateAlbinoItem animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID,"geo/crocodile_chestplate.geo.json");
    }

    @Override
    public Identifier getTextureResource(CrocodileChestplateAlbinoItem animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID,"textures/armor/crocodile_chestplate_albino.png");
    }

    @Override
    public Identifier getAnimationResource(CrocodileChestplateAlbinoItem animatable) {
        return null;
    }
}