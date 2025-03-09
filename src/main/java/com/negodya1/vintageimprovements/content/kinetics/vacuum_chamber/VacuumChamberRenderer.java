package com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintagePartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class VacuumChamberRenderer extends KineticBlockEntityRenderer<VacuumChamberBlockEntity> {

	public VacuumChamberRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public boolean shouldRenderOffScreen(VacuumChamberBlockEntity be) {
		return true;
	}

	@Override
	protected void renderSafe(VacuumChamberBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
		int light, int overlay) {

		BlockState blockState = be.getBlockState();

		VertexConsumer vb = buffer.getBuffer(RenderType.solid());

		SuperByteBuffer arrowsRender = CachedBuffers.partial(VintagePartialModels.VACUUM_CHAMBER_ARROWS, blockState);
		if (be.mode) arrowsRender.rotateCentered(AngleHelper.rad(180), Direction.EAST);
		arrowsRender.translate(0, 0, 0)
				.light(light)
				.renderInto(ms, vb);


		if (VisualizationManager.supportsVisualization(be.getLevel()))
			return;

		SuperByteBuffer superBuffer = CachedBuffers.partial(VintagePartialModels.VACUUM_COG, blockState);
		standardKineticRotationTransform(superBuffer, be, light).renderInto(ms, vb);

		float renderedHeadOffset = be.getRenderedHeadOffset(partialTicks);

		SuperByteBuffer poleRender = CachedBuffers.partial(VintagePartialModels.VACUUM_PIPE, blockState);
		poleRender.translate(0, -renderedHeadOffset, 0)
				.light(light)
				.renderInto(ms, vb);
	}

}
