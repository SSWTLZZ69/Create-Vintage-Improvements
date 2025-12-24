package com.negodya1.vintageimprovements.content.kinetics.lathe;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.negodya1.vintageimprovements.VintagePartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;

import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class LatheRotatingRenderer extends KineticBlockEntityRenderer<LatheRotatingBlockEntity> {

	public LatheRotatingRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public boolean shouldRenderOffScreen(LatheRotatingBlockEntity be) {
		return true;
	}

	@Override
	protected void renderSafe(LatheRotatingBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {

		BlockState blockState = be.getBlockState();
		VertexConsumer vb = buffer.getBuffer(RenderType.solid());

		SuperByteBuffer superBuffer = CachedBufferer.partial(VintagePartialModels.LATHE_ROTATING_HEAD, blockState);
		standardKineticRotationTransform(superBuffer, be, light).rotateCentered(Direction.UP,
				blockState.getValue(HORIZONTAL_FACING) == Direction.SOUTH ? (180*(float)Math.PI/180f) :
						blockState.getValue(HORIZONTAL_FACING) == Direction.WEST ? (90*(float)Math.PI/180f) :
								blockState.getValue(HORIZONTAL_FACING) == Direction.EAST ? (270*(float)Math.PI/180f) : 0)
				.light(light).renderInto(ms, vb);

		if (!be.inputInv.isEmpty() || !be.outputInv.isEmpty()) {
			ItemStack stack;
			if (!be.inputInv.isEmpty()) stack = be.inputInv.getItem(0).copy();
			else stack = be.outputInv.getItem(0).copy();

			ItemRenderer itemRenderer = Minecraft.getInstance()
					.getItemRenderer();
			BakedModel modelWithOverrides = itemRenderer.getModel(stack, be.getLevel(), null, 0);
			ms.pushPose();
			if (stack.getItem() instanceof BlockItem) {
				if (blockState.getValue(HORIZONTAL_FACING) == Direction.WEST)
					ms.translate(11/16f, 0.5f, 0.5f);
				else if (blockState.getValue(HORIZONTAL_FACING) == Direction.EAST)
					ms.translate(5/16f, 0.5f, 0.5f);
				else if (blockState.getValue(HORIZONTAL_FACING) == Direction.NORTH)
					ms.translate(0.5f, 0.5f, 11/16f);
				else if (blockState.getValue(HORIZONTAL_FACING) == Direction.SOUTH)
					ms.translate(0.5f, 0.5f, 5/16f);
				ms.scale(0.5f, 0.5f, 0.5f);
			}
			else {
				if (blockState.getValue(HORIZONTAL_FACING) == Direction.WEST)
					ms.translate(9/16f, 0.5f, 0.5f);
				else if (blockState.getValue(HORIZONTAL_FACING) == Direction.EAST)
					ms.translate(7/16f, 0.5f, 0.5f);
				else if (blockState.getValue(HORIZONTAL_FACING) == Direction.NORTH)
					ms.translate(0.5f, 0.5f, 9/16f);
				else if (blockState.getValue(HORIZONTAL_FACING) == Direction.SOUTH)
					ms.translate(0.5f, 0.5f, 7/16f);
				if (modelWithOverrides.isGui3d())
					ms.scale(0.45f, 0.45f, 0.45f);
				else
					ms.scale(0.35f, 0.35f, 0.35f);
			}

			if (blockState.getValue(HORIZONTAL_FACING) == Direction.SOUTH || blockState.getValue(HORIZONTAL_FACING) == Direction.NORTH)
				ms.mulPose(Axis.ZP.rotationDegrees(AngleHelper.deg(getAngleForTe(be, be.getBlockPos(), Direction.Axis.Z))));
			else
				ms.mulPose(Axis.XP.rotationDegrees(AngleHelper.deg(getAngleForTe(be, be.getBlockPos(), Direction.Axis.Z))));
			ms.mulPose(Axis.YP.rotationDegrees(blockState.getValue(HORIZONTAL_FACING) == Direction.SOUTH ? 0 :
					blockState.getValue(HORIZONTAL_FACING) == Direction.WEST ? 270 :
							blockState.getValue(HORIZONTAL_FACING) == Direction.EAST ? 90 : 180));

			itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, ms, buffer, be.getLevel(), 0);
			ms.popPose();
		}

		VertexConsumer vb2 = buffer.getBuffer(RenderType.solid());

		SuperByteBuffer superByteBuffer = CachedBufferer.partial(VintagePartialModels.LATHE_MOVING_HEAD, blockState);
		superByteBuffer.rotateCentered(Direction.UP,
				blockState.getValue(HORIZONTAL_FACING) == Direction.SOUTH ? (180*(float)Math.PI/180f) :
						blockState.getValue(HORIZONTAL_FACING) == Direction.WEST ? (90*(float)Math.PI/180f) :
								blockState.getValue(HORIZONTAL_FACING) == Direction.EAST ? (270*(float)Math.PI/180f) : 0)
				.translate(0, 0, -be.getRenderedHeadOffset()).light(light).renderInto(ms, vb2);

		if (Backend.canUseInstancing(be.getLevel()))
			return;

		renderShaft(be, ms, buffer, light, overlay);
	}

	protected void renderShaft(LatheRotatingBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		BlockState blockState = be.getBlockState();
		PartialModel partial = AllPartialModels.SHAFT_HALF;

		VertexConsumer vb = buffer.getBuffer(RenderType.solid());

		SuperByteBuffer superBuffer = CachedBufferer.partial(partial, blockState);
		standardKineticRotationTransform(superBuffer, be, light);
		superBuffer.rotateCentered(Direction.UP,
				AngleHelper.rad(be.getBlockState().getValue(HORIZONTAL_FACING) == Direction.NORTH ? 180 :
						be.getBlockState().getValue(HORIZONTAL_FACING) == Direction.SOUTH ? 0 :
								be.getBlockState().getValue(HORIZONTAL_FACING) == Direction.EAST ? 90 : 270));

		superBuffer.light(light).renderInto(ms, vb);
	}

	protected SuperByteBuffer getRotatedModel(LatheRotatingBlockEntity be, BlockState state) {
		return CachedBufferer.block(KineticBlockEntityRenderer.KINETIC_BLOCK,
				getRenderedBlockState(be));
	}

	protected BlockState getRenderedBlockState(LatheRotatingBlockEntity be) {
		return KineticBlockEntityRenderer.shaft(KineticBlockEntityRenderer.getRotationAxisOf(be));
	}

}
