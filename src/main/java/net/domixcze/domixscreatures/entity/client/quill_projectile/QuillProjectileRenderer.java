package net.domixcze.domixscreatures.entity.client.quill_projectile;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.QuillProjectileEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class QuillProjectileRenderer extends ProjectileEntityRenderer<QuillProjectileEntity> {
    public static final Identifier NORMAL_TEXTURE = Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/quill.png");
    public static final Identifier COATED_TEXTURE = Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/quill_coated.png");

    public QuillProjectileRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(QuillProjectileEntity entity) {
        return entity.isCoated() ? COATED_TEXTURE : NORMAL_TEXTURE;
    }
}