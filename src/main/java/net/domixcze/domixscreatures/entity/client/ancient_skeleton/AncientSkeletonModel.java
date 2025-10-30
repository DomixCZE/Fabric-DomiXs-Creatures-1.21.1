package net.domixcze.domixscreatures.entity.client.ancient_skeleton;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.AncientSkeletonEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class AncientSkeletonModel extends GeoModel<AncientSkeletonEntity>  {
    @Override
    public Identifier getModelResource(AncientSkeletonEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/ancient_skeleton.geo.json");
    }

    @Override
    public Identifier getTextureResource(AncientSkeletonEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/ancient_skeleton.png");
    }

    @Override
    public Identifier getAnimationResource(AncientSkeletonEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/ancient_skeleton.animation.json");
    }
}
