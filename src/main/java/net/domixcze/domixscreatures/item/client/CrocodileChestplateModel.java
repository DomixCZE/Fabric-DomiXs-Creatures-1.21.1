package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.CrocodileChestplateItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CrocodileChestplateModel extends GeoModel<CrocodileChestplateItem> {

    @Override
    public Identifier getModelResource(CrocodileChestplateItem animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID,"geo/crocodile_chestplate.geo.json");
    }

    @Override
    public Identifier getTextureResource(CrocodileChestplateItem animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID,"textures/armor/crocodile_chestplate.png");
    }

    @Override
    public Identifier getAnimationResource(CrocodileChestplateItem animatable) {
        return null;
    }
}