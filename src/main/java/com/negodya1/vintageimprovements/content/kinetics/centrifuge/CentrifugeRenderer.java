package com.negodya1.vintageimprovements.content.kinetics.centrifuge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintagePartialModels;
import com.negodya1.vintageimprovements.content.kinetics.coiling.CoilingBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.grinder.GrinderBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.vibration.VibratingTableBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.backend.Backend;
import dev.engine_room.flywheel.api.backend.BackendManager;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.ForgeCatnipServices;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class CentrifugeRenderer extends KineticBlockEntityRenderer<CentrifugeBlockEntity> {

	List<Vec3> translates;

	public CentrifugeRenderer(BlockEntityRendererProvider.Context context) {
		super(context);

		translates = new ArrayList<>();
		translates.add(new Vec3(28 / 16f, 0, 0));
		translates.add(new Vec3(-28 / 16f, 0, 0));
		translates.add(new Vec3(0, 0, 28 / 16f));
		translates.add(new Vec3(0, 0, -28 / 16f));
	}

	@Override
	public boolean shouldRenderOffScreen(CentrifugeBlockEntity be) {
		return true;
	}

	@Override
	protected void renderSafe(CentrifugeBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
		int light, int overlay) {

//		if (!VisualizationManager.supportsVisualization(be.getLevel()))
//			return;
		BlockState blockState = be.getBlockState();

		VertexConsumer vb = buffer.getBuffer(RenderType.solid());

		SuperByteBuffer superBuffer = CachedBuffers.partial(VintagePartialModels.CENTRIFUGE_BEAMS, blockState);
		standardKineticRotationTransform(superBuffer, be, light).renderInto(ms, vb);

		if (be.getRedstoneApp()) {
			SuperByteBuffer redstoneBuffer = CachedBuffers.partial(VintagePartialModels.REDSTONE_MODULE_CENTRIFUGE, blockState);
			standardKineticRotationTransform(redstoneBuffer, be, light).renderInto(ms, vb);
		}

		if (be.getBasins() > 0) {
			superBuffer = CachedBuffers.partial(VintagePartialModels.BASIN, blockState);
			standardKineticRotationTransform(superBuffer, be, light).translate(28 / 16f, 0, 0).renderInto(ms, vb);

			if (be.getBasins() > 1) {
				standardKineticRotationTransform(superBuffer, be, light).translate(-28 / 16f, 0, 0).renderInto(ms, vb);

				if (be.getBasins() > 2) {
					standardKineticRotationTransform(superBuffer, be, light).translate(0, 0, 28 / 16f).renderInto(ms, vb);

					if (be.getBasins() > 3) {
						standardKineticRotationTransform(superBuffer, be, light).translate(0, 0, -28 / 16f).renderInto(ms, vb);

						for (Vec3 translate : translates) {
							ms.pushPose();

							BlockPos pos = be.getBlockPos();
							TransformStack.of(ms)
									.rotateCentered(getAngleForBe(be, pos, Axis.Y), Direction.UP)
									.translate(translate);

							float fluidLevel = renderFluids(be, partialTicks, ms, buffer, light, overlay);
							float level = Mth.clamp(fluidLevel - .3f, .125f, .6f);

							ms.translate(.5, .2f, .5);
							TransformStack.of(ms)
									.rotateY(be.ingredientRotation.getValue(partialTicks));

							RandomSource r = RandomSource.create(pos.hashCode());
							Vec3 baseVector = new Vec3(.125, level, 0);

							IItemHandlerModifiable inv = be.capability.orElse(new ItemStackHandler());
							int itemCount = 0;
							for (int slot = 0; slot < inv.getSlots(); slot++)
								if (!inv.getStackInSlot(slot)
										.isEmpty())
									itemCount++;

							if (itemCount == 1)
								baseVector = new Vec3(0, level, 0);

							float anglePartition = 360f / itemCount;
							for (int slot = 0; slot < inv.getSlots(); slot++) {
								ItemStack stack = inv.getStackInSlot(slot);
								if (stack.isEmpty())
									continue;

								ms.pushPose();

								if (fluidLevel > 0) {
									ms.translate(0,
											(Mth.sin(
													AnimationTickHolder.getRenderTime(be.getLevel()) / 12f + anglePartition * itemCount) + 1.5f)
													* 1 / 32f,
											0);
								}

								Vec3 itemPosition = VecHelper.rotate(baseVector, anglePartition * itemCount, Axis.Y);
								ms.translate(itemPosition.x, itemPosition.y, itemPosition.z);
								TransformStack.of(ms)
										.rotateY(anglePartition * itemCount + 35)
										.rotateX(65);

								for (int i = 0; i <= stack.getCount() / 8; i++) {
									ms.pushPose();

									Vec3 vec = VecHelper.offsetRandomly(Vec3.ZERO, r, 1 / 16f);

									ms.translate(vec.x, vec.y, vec.z);
									renderItem(be, pos, ms, buffer, light, overlay, stack);
									ms.popPose();
								}
								ms.popPose();


								itemCount--;
							}
							ms.popPose();

							if (!(blockState.getBlock() instanceof CentrifugeBlock))
								return;
							Direction direction = Direction.UP;
							Vec3 directionVec = Vec3.atLowerCornerOf(direction.getNormal());
							Vec3 outVec = VecHelper.getCenterOf(BlockPos.ZERO)
									.add(directionVec.scale(.55)
											.subtract(0, 1 / 2f, 0));

							boolean outToBasin = be.getLevel()
									.getBlockState(be.getBlockPos()
											.relative(direction))
									.getBlock() instanceof CentrifugeBlock;

							for (IntAttached<ItemStack> intAttached : be.visualizedOutputItems) {
								float progress = 1 - (intAttached.getFirst() - partialTicks) / CentrifugeBlockEntity.OUTPUT_ANIMATION_TIME;

								if (!outToBasin && progress > .35f)
									continue;

								ms.pushPose();
								TransformStack.of(ms)
										.translate(outVec)
										.translate(new Vec3(0, Math.max(-.55f, -(progress * progress * 2)), 0))
										.translate(directionVec.scale(progress * .5f))
										.rotateY(AngleHelper.horizontalAngle(direction))
										.rotateX(progress * 180);
								renderItem(be, pos, ms, buffer, light, overlay, intAttached.getValue());
								ms.popPose();
							}
						}
					}
				}
			}
		}
		if (VisualizationManager.supportsVisualization(be.getLevel()))
			return;

		renderShaft(be, ms, buffer, light, overlay);
	}

	protected void renderItem(CentrifugeBlockEntity be, BlockPos pos, PoseStack ms, MultiBufferSource buffer, int light, int overlay, ItemStack stack) {
		Minecraft mc = Minecraft.getInstance();
		mc.getItemRenderer()
				.renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, ms, buffer, mc.level, 0);
	}

	protected float renderFluids(CentrifugeBlockEntity basin, float partialTicks, PoseStack ms, MultiBufferSource buffer,
								 int light, int overlay) {

		SmartFluidTankBehaviour inputFluids = basin.getBehaviour(SmartFluidTankBehaviour.INPUT);
		SmartFluidTankBehaviour outputFluids = basin.getBehaviour(SmartFluidTankBehaviour.OUTPUT);
		SmartFluidTankBehaviour[] tanks = { inputFluids, outputFluids };
		float totalUnits = basin.getTotalFluidUnits(partialTicks);
		if (totalUnits < 1)
			return 0;

		float fluidLevel = Mth.clamp(totalUnits / 2000, 0, 1);

		fluidLevel = 1 - ((1 - fluidLevel) * (1 - fluidLevel));

		float xMin = 2 / 16f;
		float xMax = 2 / 16f;
		final float yMin = 2 / 16f;
		final float yMax = yMin + 12 / 16f * fluidLevel;
		final float zMin = 2 / 16f;
		final float zMax = 14 / 16f;

		for (SmartFluidTankBehaviour behaviour : tanks) {
			if (behaviour == null)
				continue;
			for (TankSegment tankSegment : behaviour.getTanks()) {
				FluidStack renderedFluid = tankSegment.getRenderedFluid();
				if (renderedFluid.isEmpty())
					continue;
				float units = tankSegment.getTotalUnits(partialTicks);
				if (units < 1)
					continue;

				float partial = Mth.clamp(units / totalUnits, 0, 1);
				xMax += partial * 12 / 16f;
				ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(renderedFluid, xMin, yMin, zMin, xMax, yMax, zMax, buffer, ms, light, false, false);

				xMin = xMax;
			}
		}

		return yMax;
	}

	protected void renderShaft(CentrifugeBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		KineticBlockEntityRenderer.renderRotatingBuffer(be, getRotatedModel(be, be.getBlockState()), ms, buffer.getBuffer(RenderType.solid()), light);
	}

	protected SuperByteBuffer getRotatedModel(CentrifugeBlockEntity be, BlockState state) {
		return CachedBuffers.block(KineticBlockEntityRenderer.KINETIC_BLOCK,
				getRenderedBlockState(be));
	}

	protected BlockState getRenderedBlockState(CentrifugeBlockEntity be) {
		return KineticBlockEntityRenderer.shaft(KineticBlockEntityRenderer.getRotationAxisOf(be));
	}
}
