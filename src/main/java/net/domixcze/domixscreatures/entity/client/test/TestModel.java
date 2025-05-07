package net.domixcze.domixscreatures.entity.client.test;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.TestEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class TestModel extends GeoModel<TestEntity> {
    @Override
    public Identifier getModelResource(TestEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/test.geo.json");
    }

    @Override
    public Identifier getTextureResource(TestEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/test.png");
    }

    @Override
    public Identifier getAnimationResource(TestEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/test.animation.json");
    }
}