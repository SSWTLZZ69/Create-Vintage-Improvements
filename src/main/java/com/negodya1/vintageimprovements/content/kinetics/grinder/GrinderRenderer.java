package com.negodya1.vintageimprovements.content.kinetics.grinder;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.negodya1.vintageimprovements.VintagePartialModels;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.contraptions.render.ContraptionRenderDispatcher;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class GrinderRenderer extends SafeBlockEntityRenderer<GrinderBlockEntity> {

	public GrinderRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	protected void renderSafe(GrinderBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		renderBelt(be, ms, buffer, light);
		renderItems(be, partialTicks, ms, buffer, light, overlay);
		FilteringRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);

		if (Backend.canUseInstancing(be.getLevel()))
			return;

		renderShaft(be, ms, buffer, light, overlay);
	}

	protected void renderBelt(GrinderBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light) {
		BlockState blockState = be.getBlockState();
		PartialModel partial;
		float speed = be.getSpeed();

		if (speed > 0) {
			partial = switch (be.getTextureType()) {
				case 1 -> VintagePartialModels.GRINDER_BELT_ACTIVE_RED;
				case 2 -> VintagePartialModels.GRINDER_BELT_ACTIVE_DIAMOND;
				case 3 -> VintagePartialModels.GRINDER_BELT_ACTIVE_IRON;
				case 4 -> VintagePartialModels.GRINDER_BELT_ACTIVE_OBSIDIAN;
				default -> VintagePartialModels.GRINDER_BELT_ACTIVE;
			};
		} else if (speed < 0) {
			partial = switch (be.getTextureType()) {
				case 1 -> VintagePartialModels.GRINDER_BELT_REVERSED_RED;
				case 2 -> VintagePartialModels.GRINDER_BELT_REVERSED_DIAMOND;
				case 3 -> VintagePartialModels.GRINDER_BELT_REVERSED_IRON;
				case 4 -> VintagePartialModels.GRINDER_BELT_REVERSED_OBSIDIAN;
				default -> VintagePartialModels.GRINDER_BELT_REVERSED;
			};
		} else {
			partial = switch (be.getTextureType()) {
				case 1 -> VintagePartialModels.GRINDER_BELT_INACTIVE_RED;
				case 2 -> VintagePartialModels.GRINDER_BELT_INACTIVE_DIAMOND;
				case 3 -> VintagePartialModels.GRINDER_BELT_INACTIVE_IRON;
				case 4 -> VintagePartialModels.GRINDER_BELT_INACTIVE_OBSIDIAN;
				default -> VintagePartialModels.GRINDER_BELT_INACTIVE;
			};
		}

		SuperByteBuffer superBuffer = CachedBufferer
				.partialFacing(partial, blockState, blockState.getValue(HORIZONTAL_FACING))
				.rotateCentered(Direction.UP, blockState.getValue(HORIZONTAL_FACING) == Direction.WEST || blockState.getValue(HORIZONTAL_FACING) == Direction.NORTH ? 0 : (180*(float)Math.PI/180f));

		superBuffer.color(0xFFFFFF)
			.light(light)
			.renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
	}

	protected void renderShaft(GrinderBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		KineticBlockEntityRenderer.renderRotatingBuffer(be, getRotatedModel(be), ms, buffer.getBuffer(RenderType.solid()), light);
	}

	protected void renderItems(GrinderBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
		int light, int overlay) {
		if (!be.inventory.isEmpty()) {
			boolean alongZ = (be.getBlockState().getValue(HORIZONTAL_FACING) == Direction.NORTH || be.getBlockState().getValue(HORIZONTAL_FACING) == Direction.SOUTH);
			ms.pushPose();

			boolean moving = be.inventory.recipeDuration != 0;
			float offset = moving ? (float) (be.inventory.remainingTime) / be.inventory.recipeDuration : 0;
			float processingSpeed = Mth.clamp(Math.abs(be.getSpeed()) / 32, 1, 128);
			if (moving) {
				offset = Mth
						.clamp(offset + ((-partialTicks + .5f) * processingSpeed) / be.inventory.recipeDuration, 0.125f, 1f);
				if (!be.inventory.appliedRecipe)
					offset += 1;
				offset /= 2;
			}

			if (be.getSpeed() == 0)
				offset = .5f;
			if (be.getSpeed() < 0 ^ alongZ)
				offset = 1 - offset;

			for (int i = 0; i < be.inventory.getSlots(); i++) {
				ItemStack stack = be.inventory.getStackInSlot(i);
				if (stack.isEmpty())
					continue;

				ItemRenderer itemRenderer = Minecraft.getInstance()
						.getItemRenderer();
				BakedModel modelWithOverrides = itemRenderer.getModel(stack, be.getLevel(), null, 0);
				boolean blockItem = modelWithOverrides.isGui3d();

				ms.translate(alongZ ? offset : .5, blockItem ? .925f : 13f / 16f, alongZ ? .5 : offset);

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
		return CachedBufferer.block(KineticBlockEntityRenderer.KINETIC_BLOCK,
			getRenderedBlockState(be));
	}

	protected BlockState getRenderedBlockState(KineticBlockEntity be) {
		return KineticBlockEntityRenderer.shaft(KineticBlockEntityRenderer.getRotationAxisOf(be));
	}

	public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
		ContraptionMatrices matrices, MultiBufferSource buffer) {
		BlockState state = context.state;
		Direction facing = state.getValue(GrinderBlock.HORIZONTAL_FACING);

		Vec3 facingVec = Vec3.atLowerCornerOf(context.state.getValue(GrinderBlock.HORIZONTAL_FACING)
			.getNormal());
		facingVec = context.rotation.apply(facingVec);

		Direction closestToFacing = Direction.getNearest(facingVec.x, facingVec.y, facingVec.z);

		boolean horizontal = closestToFacing.getAxis()
			.isHorizontal();
		boolean backwards = VecHelper.isVecPointingTowards(context.relativeMotion, facing.getOpposite());
		boolean moving = context.getAnimationSpeed() != 0;
		boolean shouldAnimate =
			(context.contraption.stalled && horizontal) || (!context.contraption.stalled && !backwards && moving);

		SuperByteBuffer superBuffer;
		if (shouldAnimate)
			superBuffer = CachedBufferer.partial(VintagePartialModels.GRINDER_BELT_ACTIVE, state);
		else
			superBuffer = CachedBufferer.partial(VintagePartialModels.GRINDER_BELT_INACTIVE, state);

		superBuffer.transform(matrices.getModel())
			.centre()
			.rotateY(AngleHelper.horizontalAngle(facing))
			.rotateX(AngleHelper.verticalAngle(facing));

		if (!GrinderBlock.isHorizontal(state)) {
			superBuffer.rotateZ(0);
		}

		superBuffer.unCentre()
			.light(matrices.getWorld(), ContraptionRenderDispatcher.getContraptionWorldLight(context, renderWorld))
			.renderInto(matrices.getViewProjection(), buffer.getBuffer(RenderType.cutoutMipped()));
	}

}
