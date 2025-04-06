package net.domixcze.domixscreatures.entity.client.mud_golem;

import net.domixcze.domixscreatures.entity.custom.MudGolemEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MudGolemRenderer extends GeoEntityRenderer<MudGolemEntity> {
    public MudGolemRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new MudGolemModel());
    }
}