package com.negodya1.vintageimprovements.content.kinetics.coiling;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_AXIS;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintagePartialModels;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CoilingRenderer extends KineticBlockEntityRenderer<CoilingBlockEntity> {

	public CoilingRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(CoilingBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		renderParts(be, ms, buffer, light);
		renderSpring(be, partialTicks, ms, buffer, light, overlay);
		FilteringRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);


		if (VisualizationManager.supportsVisualization(be.getLevel()))
			return;

		renderShaft(be, ms, buffer, light, overlay);
	}

	protected void renderParts(CoilingBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light) {
		float speed = -Math.abs(be.getSpeed());
		float time = AnimationTickHolder.getRenderTime(be.getLevel());
		float angle = ((time * speed * 6 / 10f) % 360);

		BlockState blockState = be.getBlockState();
		PartialModel partial = VintagePartialModels.COILING_WHEEL;

		SuperByteBuffer superBuffer = CachedBuffers.partial(partial, blockState);
		rotateWheel(superBuffer, angle, blockState.getValue(HORIZONTAL_FACING));

		superBuffer.color(0xFFFFFF)
			.light(light)
			.renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
	}

	private SuperByteBuffer rotateWheel(SuperByteBuffer buffer, float angle, Direction facing) {
		float pivotX = 5 / 16f;
		float pivotY = 10.5f / 16f;
		float pivotZ = 11.5f / 16f;
		buffer.rotateCentered(AngleHelper.rad(AngleHelper.horizontalAngle(facing.getCounterClockWise())), Direction.UP);
		buffer.translate(pivotX, pivotY, pivotZ);
		buffer.rotate(AngleHelper.rad(angle), Direction.EAST);
		buffer.translate(-pivotX, -pivotY, -pivotZ);
		return buffer;
	}

	private SuperByteBuffer rotateSpring(SuperByteBuffer buffer, float angle, Direction facing) {
		float pivotX = 17 / 16f;
		float pivotY = 9.5f / 16f;
		float pivotZ = 7.5f / 16f;
		buffer.rotateCentered(AngleHelper.rad(AngleHelper.horizontalAngle(facing.getCounterClockWise())), Direction.UP);
		buffer.translate(pivotX, pivotY, pivotZ);
		buffer.rotate(AngleHelper.rad(angle), Direction.EAST);
		buffer.translate(-pivotX, -pivotY, -pivotZ);
		return buffer;
	}

	protected void renderShaft(CoilingBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		BlockState blockState = be.getBlockState();
		PartialModel partial = AllPartialModels.SHAFT_HALF;

		VertexConsumer vb = buffer.getBuffer(RenderType.solid());

		SuperByteBuffer superBuffer = CachedBuffers.partial(partial, blockState);
		standardKineticRotationTransform(superBuffer, be, light);
		superBuffer.rotateCentered(AngleHelper.rad(be.getBlockState().getValue(HORIZONTAL_FACING) == Direction.NORTH ? 0 :
                                be.getBlockState().getValue(HORIZONTAL_FACING) == Direction.SOUTH ? 180 :
                                        be.getBlockState().getValue(HORIZONTAL_FACING) == Direction.EAST ? 270 : 90),
                Direction.UP);

		superBuffer.renderInto(ms, vb);
	}

	protected void renderSpring(CoilingBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		if (!be.inventory.isEmpty()) {
			ms.pushPose();

			boolean moving = be.inventory.recipeDuration != 0;
			float offset = moving ? (float) (be.inventory.remainingTime) / be.inventory.recipeDuration : 0;
			float processingSpeed = Mth.clamp(Math.abs(be.getSpeed()) / 32, 1, 128);
			if (moving) {
				offset = Mth
						.clamp(offset + ((-partialTicks + .5f) * processingSpeed)
								/ be.inventory.recipeDuration, 0.05f, 0.75f);
				if (!be.inventory.appliedRecipe)
					offset += 1;
				offset /= 2;
			}

			if (be.getSpeed() == 0)
				offset = .5f;
			offset = 0.3f - offset;

			for (int i = 0; i < be.inventory.getSlots(); i++) {
				BlockState blockState = be.getBlockState();
				PartialModel partial = VintagePartialModels.COILING_SPRING;

				SuperByteBuffer superBuffer = CachedBuffers.partial(partial, blockState);

				float speed = -Math.abs(be.getSpeed());
				float time = AnimationTickHolder.getRenderTime(be.getLevel());
				float angle = ((time * speed * 6 / 10f) % 360);

				rotateSpring(superBuffer, angle, blockState.getValue(HORIZONTAL_FACING));

				superBuffer.rotateCentered((180*(float)Math.PI/180f), Direction.UP);

				superBuffer.translate(offset, 0, 0);

				superBuffer.color(be.getSpringColor())
						.light(light)
						.renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
				break;
			}

			ms.popPose();
		}
	}

}
