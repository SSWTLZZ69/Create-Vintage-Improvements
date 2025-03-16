package com.negodya1.vintageimprovements.content.kinetics.coiling;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CoilingFilterSlot extends ValueBoxTransform {

	@Override
	public Vec3 getLocalOffset(LevelAccessor levelAccessor, BlockPos blockPos, BlockState state) {
		int offset = 2;
		if (state.getValue(CoilingBlock.HORIZONTAL_FACING) == Direction.NORTH || state.getValue(CoilingBlock.HORIZONTAL_FACING) == Direction.SOUTH)
			return VecHelper.voxelSpace(8, 14.5f, 8 + (state.getValue(CoilingBlock.HORIZONTAL_FACING) == Direction.NORTH ? offset : -offset));
		return VecHelper.voxelSpace(8 + (state.getValue(CoilingBlock.HORIZONTAL_FACING) == Direction.WEST ? offset : -offset), 14.5f, 8);
	}

	@Override
	public void rotate(LevelAccessor levelAccessor, BlockPos blockPos, BlockState state, PoseStack poseStack) {
		float yRot = 0;
		if       (state.getValue(CoilingBlock.HORIZONTAL_FACING) == Direction.SOUTH) { yRot = (float) Math.PI;
		} else if (state.getValue(CoilingBlock.HORIZONTAL_FACING) == Direction.WEST) { yRot = (float) Math.PI/2;
		} else if (state.getValue(CoilingBlock.HORIZONTAL_FACING) == Direction.EAST) { yRot = (float) Math.PI*3/2;
		}
		TransformStack.of(poseStack)
				.rotateY(yRot)
				.rotateX((float) Math.PI/2);
	}
}
