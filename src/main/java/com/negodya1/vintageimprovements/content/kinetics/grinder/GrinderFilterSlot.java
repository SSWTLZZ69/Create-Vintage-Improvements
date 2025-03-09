package com.negodya1.vintageimprovements.content.kinetics.grinder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class GrinderFilterSlot extends ValueBoxTransform {



	@Override
	public Vec3 getLocalOffset(LevelAccessor levelAccessor, BlockPos blockPos, BlockState state) {
		int offset = 6;
		if (state.getValue(GrinderBlock.HORIZONTAL_FACING) == Direction.NORTH || state.getValue(GrinderBlock.HORIZONTAL_FACING) == Direction.SOUTH)
			return VecHelper.voxelSpace(8, 14.5f, 8 + (state.getValue(GrinderBlock.HORIZONTAL_FACING) == Direction.SOUTH ? offset : -offset));
		return VecHelper.voxelSpace(8 + (state.getValue(GrinderBlock.HORIZONTAL_FACING) == Direction.EAST ? offset : -offset), 14.5f, 8);
	}

	@Override
	public void rotate(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, PoseStack poseStack) {

		int yRot = 180;
		TransformStack.of(poseStack)
				.rotateY(yRot)
				.rotateX(90);
	}
}
