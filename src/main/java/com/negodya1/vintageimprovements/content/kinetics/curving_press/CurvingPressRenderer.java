package com.negodya1.vintageimprovements.content.kinetics.curving_press;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.negodya1.vintageimprovements.VintagePartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;

public class CurvingPressRenderer extends KineticBlockEntityRenderer<CurvingPressBlockEntity> {

	public CurvingPressRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public boolean shouldRenderOffScreen(CurvingPressBlockEntity be) {
		return true;
	}

	@Override
	protected void renderSafe(CurvingPressBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
		int light, int overlay) {
		super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

		BlockState blockState = be.getBlockState();
		CurvingBehaviour pressingBehaviour = be.getPressingBehaviour();
		float renderedHeadOffset =
			pressingBehaviour.getRenderedHeadOffset(partialTicks) * pressingBehaviour.mode.headOffset;

		SuperByteBuffer poleRender = CachedBufferer.partialFacing(VintagePartialModels.CURVING_POLE, blockState,
			blockState.getValue(HORIZONTAL_FACING));
		poleRender.translate(0, -renderedHeadOffset, 0)
			.light(light)
			.renderInto(ms, buffer.getBuffer(RenderType.solid()));

		if (be.mode > 0) {
			PartialModel partialModel;

			switch (be.mode) {
				case 2 -> partialModel = VintagePartialModels.CURVING_HEAD_2;
				case 3 -> partialModel = VintagePartialModels.CURVING_HEAD_3;
				case 4 -> partialModel = VintagePartialModels.CURVING_HEAD_4;
				case 5 -> {
					partialModel = VintagePartialModels.CURVING_HEAD_5;
					ItemRenderer itemRenderer = Minecraft.getInstance()
							.getItemRenderer();
					ms.pushPose();
					ms.translate(0.5, -renderedHeadOffset + 0.5f / 16, 0.5);
					ms.mulPose(Axis.XP.rotationDegrees(90));
					ms.scale(0.625f, 0.625f, 0.625f);
					if (be.itemAsHead != null) itemRenderer.renderStatic(be.itemAsHead.getItem(0), ItemDisplayContext.FIXED, light, overlay, ms, buffer, be.getLevel(), 0);
					ms.popPose();
				}
				default -> partialModel = VintagePartialModels.CURVING_HEAD;
			}

			SuperByteBuffer headRender = CachedBufferer.partialFacing(partialModel, blockState,
					blockState.getValue(HORIZONTAL_FACING));
			headRender.translate(0, -renderedHeadOffset, 0)
					.light(light)
					.renderInto(ms, buffer.getBuffer(RenderType.solid()));
		}

		if (be.redstoneModule) {
			SuperByteBuffer redstone = CachedBufferer.partialFacing(VintagePartialModels.REDSTONE_MODULE_CURVING_PRESS, blockState,
					blockState.getValue(HORIZONTAL_FACING));
			redstone.light(light)
					.renderInto(ms, buffer.getBuffer(RenderType.solid()));
		}
	}

	@Override
	protected BlockState getRenderedBlockState(CurvingPressBlockEntity be) {
		return shaft(getRotationAxisOf(be));
	}

}
