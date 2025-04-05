package com.negodya1.vintageimprovements.content.kinetics.laser;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintagePartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

public class LaserRenderer extends KineticBlockEntityRenderer<LaserBlockEntity> {

	public LaserRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(LaserBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		renderHead(be, partialTicks, ms, buffer, light);
//		renderShaft(be, ms, buffer, light, overlay);
	}

	protected void renderHead(LaserBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light) {
		BlockState blockState = be.getBlockState();
		SuperByteBuffer superBuffer = CachedBuffers.partialFacing(VintagePartialModels.LASER_HEAD, blockState, Direction.NORTH);
		if (blockState.getValue(POWERED))
			superBuffer.translate(((0.5 - partialTicks) * -5f) / 16f, 0, (3. - Math.abs(getAngleForBe(be, be.getBlockPos(), Direction.Axis.Y))) / 16f);
		superBuffer.color(0xFFFFFF)
				.light(light)
				.renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));

		if (blockState.getValue(POWERED)) {
			SuperByteBuffer beamBuffer = CachedBuffers.partialFacing(VintagePartialModels.LASER_BEAM, blockState, Direction.NORTH);
			beamBuffer.translate(((0.5 - partialTicks) * -5f) / 16f, -3f / 16f, (3. - Math.abs(getAngleForBe(be, be.getBlockPos(), Direction.Axis.Y))) / 16f);
			beamBuffer.color(0xFFFFFF)
					.light(light)
					.renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
		}
	}

	protected void renderShaft(LaserBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		KineticBlockEntityRenderer.renderRotatingBuffer(be, getRotatedModel(be), ms, buffer.getBuffer(RenderType.solid()), light);
	}

	protected SuperByteBuffer getRotatedModel(BlockEntity be) {
		return CachedBuffers.partial(AllPartialModels.SHAFT_HALF, be.getBlockState());
	}

	protected BlockState getRenderedBlockState(LaserBlockEntity be) {
		return KineticBlockEntityRenderer.shaft(KineticBlockEntityRenderer.getRotationAxisOf(be));
	}

}
