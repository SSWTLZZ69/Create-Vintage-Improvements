package com.negodya1.vintageimprovements.content.kinetics.vibration;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.negodya1.vintageimprovements.VintagePartialModels;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class VibratingTableRenderer extends SafeBlockEntityRenderer<VibratingTableBlockEntity> {

	public VibratingTableRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	protected void renderSafe(VibratingTableBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		renderTable(be, partialTicks, ms, buffer, light);
		renderItems(be, partialTicks, ms, buffer, light, overlay);


		if (VisualizationManager.supportsVisualization(be.getLevel()))
			return;

		renderShaft(be, ms, buffer, light, overlay);
	}

	protected void renderTable(VibratingTableBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light) {
		BlockState blockState = be.getBlockState();

		float offset = 0.0f;
		if (be.haveRecipe()) offset = be.getRenderedHeadOffset(partialTicks);

		SuperByteBuffer superBuffer = CachedBuffers.partialFacing(VintagePartialModels.VIBRATING_TABLE, blockState, blockState.getValue(HORIZONTAL_FACING)); //.rotateCentered(Direction.UP, blockState.getValue(HORIZONTAL_FACING) == Direction.WEST || blockState.getValue(HORIZONTAL_FACING) == Direction.NORTH ? 0 : (180*(float)Math.PI/180f));
		superBuffer.translate(.0, offset, .0);

		superBuffer.color(0xFFFFFF)
			.light(light)
			.renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
	}

	protected void renderShaft(VibratingTableBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		KineticBlockEntityRenderer.renderRotatingBuffer(be, getRotatedModel(be), ms, buffer.getBuffer(RenderType.solid()), light);
	}

	protected void renderItems(VibratingTableBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		boolean input = !be.inputInv.getStackInSlot(0).isEmpty();
		boolean output = !be.outputInv.getStackInSlot(0).isEmpty();

		if (!input && !output) return;

		float offset = 0.0f;
		if (be.haveRecipe()) offset = be.getRenderedHeadOffset(partialTicks);

		if (input) {
			if (!output) {
				boolean alongZ = (be.getBlockState().getValue(HORIZONTAL_FACING) == Direction.NORTH || be.getBlockState().getValue(HORIZONTAL_FACING) == Direction.SOUTH);
				ms.pushPose();

				for (int i = 0; i < be.inputInv.getSlots(); i++) {
					ItemStack stack = be.inputInv.getStackInSlot(i);
					if (stack.isEmpty())
						continue;

					ItemRenderer itemRenderer = Minecraft.getInstance()
							.getItemRenderer();
					BakedModel modelWithOverrides = itemRenderer.getModel(stack, be.getLevel(), null, 0);
					boolean blockItem = modelWithOverrides.isGui3d();

					ms.translate(.5, (blockItem ? .925f : 14f / 16f) + offset, .5);

					ms.scale(.5f, .5f, .5f);
					if (alongZ)
						ms.mulPose(Axis.YP.rotationDegrees(90));
					ms.mulPose(Axis.XP.rotationDegrees(90));
					itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, ms, buffer, be.getLevel(), 0);
					break;
				}

				ms.popPose();
			}
			else {
				ms.pushPose();

				for (int i = 0; i < be.inputInv.getSlots(); i++) {
					ItemStack stack = be.inputInv.getStackInSlot(i);
					if (stack.isEmpty())
						continue;

					ItemRenderer itemRenderer = Minecraft.getInstance()
							.getItemRenderer();
					BakedModel modelWithOverrides = itemRenderer.getModel(stack, be.getLevel(), null, 0);
					boolean blockItem = modelWithOverrides.isGui3d();

					ms.translate(.33, (blockItem ? .925f : 14f / 16f) + offset, .33);

					ms.scale(.5f, .5f, .5f);
					ms.mulPose(Axis.XP.rotationDegrees(90));
					itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, ms, buffer, be.getLevel(), 0);

					ms.mulPose(Axis.XP.rotationDegrees(-90));
					ms.translate(0, (blockItem ? -.925f : -14f / 16f), 0);
					break;
				}

				for (int i = 0; i < be.outputInv.getSlots(); i++) {
					ItemStack stack = be.outputInv.getStackInSlot(i);
					if (stack.isEmpty())
						continue;

					ItemRenderer itemRenderer = Minecraft.getInstance()
							.getItemRenderer();
					BakedModel modelWithOverrides = itemRenderer.getModel(stack, be.getLevel(), null, 0);
					boolean blockItem = modelWithOverrides.isGui3d();

					ms.translate(.66, (blockItem ? .925f : 14f / 16f), .66);
					ms.mulPose(Axis.XP.rotationDegrees(90));

					itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, ms, buffer, be.getLevel(), 0);
					break;
				}

				ms.popPose();
			}
		}
		else if (output) {
			boolean alongZ = (be.getBlockState().getValue(HORIZONTAL_FACING) == Direction.NORTH || be.getBlockState().getValue(HORIZONTAL_FACING) == Direction.SOUTH);
			ms.pushPose();

			for (int i = 0; i < be.outputInv.getSlots(); i++) {
				ItemStack stack = be.outputInv.getStackInSlot(i);
				if (stack.isEmpty())
					continue;

				ItemRenderer itemRenderer = Minecraft.getInstance()
						.getItemRenderer();
				BakedModel modelWithOverrides = itemRenderer.getModel(stack, be.getLevel(), null, 0);
				boolean blockItem = modelWithOverrides.isGui3d();

				ms.translate(.5, (blockItem ? .925f : 14f / 16f) + offset, .5);

				ms.scale(.5f, .5f, .5f);
				if (alongZ)
					ms.mulPose(Axis.YP.rotationDegrees(90));
				ms.mulPose(Axis.XP.rotationDegrees(90));
				itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, ms, buffer, be.getLevel(), 0);
				break;
			}

			ms.popPose();
		}
	}

	protected SuperByteBuffer getRotatedModel(KineticBlockEntity be) {
		return CachedBuffers.block(KineticBlockEntityRenderer.KINETIC_BLOCK,
			getRenderedBlockState(be));
	}

	protected BlockState getRenderedBlockState(KineticBlockEntity be) {
		return KineticBlockEntityRenderer.shaft(KineticBlockEntityRenderer.getRotationAxisOf(be));
	}

	public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
		ContraptionMatrices matrices, MultiBufferSource buffer) {
		BlockState state = context.state;
		Direction facing = state.getValue(VibratingTableBlock.HORIZONTAL_FACING);

		Vec3 facingVec = Vec3.atLowerCornerOf(context.state.getValue(VibratingTableBlock.HORIZONTAL_FACING)
			.getNormal());
		facingVec = context.rotation.apply(facingVec);

		Direction closestToFacing = Direction.getNearest(facingVec.x, facingVec.y, facingVec.z);

		boolean horizontal = closestToFacing.getAxis()
			.isHorizontal();
		boolean backwards = VecHelper.isVecPointingTowards(context.relativeMotion, facing.getOpposite());
		boolean moving = context.getAnimationSpeed() != 0;
		boolean shouldAnimate =
			(context.contraption.stalled && horizontal) || (!context.contraption.stalled && !backwards && moving);

		SuperByteBuffer superBuffer = CachedBuffers.partial(VintagePartialModels.VIBRATING_TABLE, state);

		superBuffer.transform(matrices.getModel())
			.center()
			.rotateY(AngleHelper.horizontalAngle(facing))
			.rotateX(AngleHelper.verticalAngle(facing));

		if (!VibratingTableBlock.isHorizontal(state)) {
			superBuffer.rotateZ(0);
		}

		superBuffer.uncenter()
			.light(LevelRenderer.getLightColor(renderWorld, context.localPos))
			.renderInto(matrices.getViewProjection(), buffer.getBuffer(RenderType.cutoutMipped()));
	}

}
