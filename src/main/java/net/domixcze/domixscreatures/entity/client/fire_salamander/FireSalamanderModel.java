package net.domixcze.domixscreatures.entity.client.fire_salamander;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.FireSalamanderEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class FireSalamanderModel extends GeoModel<FireSalamanderEntity> {
    @Override
    public Identifier getModelResource(FireSalamanderEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/fire_salamander.geo.json");
    }

    @Override
    public Identifier getTextureResource(FireSalamanderEntity animatable) {
        return animatable.isObsidianVariant()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/fire_salamander_obsidian.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/fire_salamander.png");
    }

    @Override
    public Identifier getAnimationResource(FireSalamanderEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/fire_salamander.animation.json");
    }
}
