package net.domixcze.domixscreatures.entity.client.mud_golem;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.MudGolemEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class MudGolemModel extends GeoModel<MudGolemEntity> {
    @Override
    public Identifier getModelResource(MudGolemEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/mud_golem.geo.json");
    }

    @Override
    public Identifier getTextureResource(MudGolemEntity animatable) {
        boolean isWet = animatable.getDataTracker().get(MudGolemEntity.WET_STATE);
        return Identifier.of(DomiXsCreatures.MOD_ID, isWet ? "textures/entity/mud_golem_wet.png" : "textures/entity/mud_golem_dry.png");
    }

    @Override
    public Identifier getAnimationResource(MudGolemEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/mud_golem.animation.json");
    }
}