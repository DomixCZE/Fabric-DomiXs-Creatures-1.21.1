package net.domixcze.domixscreatures.entity.client.chain;

import net.domixcze.domixscreatures.entity.custom.ChainAnchorEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;

public class ChainAnchorModel extends SinglePartEntityModel<ChainAnchorEntity> {
	private final ModelPart root;
	private final ModelPart anchor;

	public ChainAnchorModel(ModelPart root) {
		this.root = root;
		this.anchor = root.getChild("anchor");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		modelPartData.addChild("anchor",
				ModelPartBuilder.create().uv(0, 0)
						.cuboid(-5.0F, -14.0F, -5.0F, 10.0F, 4.0F, 10.0F, new Dilation(0.0F)),
				ModelTransform.pivot(0.0F, 12.0F, 0.0F));

		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(ChainAnchorEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}
}