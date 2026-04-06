package net.domixcze.domixscreatures.entity.client.komodo_dragon;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.KomodoDragonEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class KomodoDragonModel extends GeoModel<KomodoDragonEntity> {
    @Override
    public Identifier getModelResource(KomodoDragonEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_komodo_dragon.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/komodo_dragon.geo.json");
    }

    @Override
    public Identifier getTextureResource(KomodoDragonEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_komodo_dragon.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/komodo_dragon.png");
    }

    @Override
    public Identifier getAnimationResource(KomodoDragonEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_komodo_dragon.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/komodo_dragon.animation.json");
    }
}