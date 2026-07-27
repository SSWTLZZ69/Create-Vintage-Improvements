package com.negodya1.vintageimprovements.foundation.contraption;

import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugeStructuralBlock;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.HelveKineticBlock;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.HelveStructuralBlock;
import com.negodya1.vintageimprovements.content.kinetics.lathe.LatheMovingBlock;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class VintageBlockMovementChecks {

	private VintageBlockMovementChecks() {}

	public static void register() {
		BlockMovementChecks.registerAttachedCheck(VintageBlockMovementChecks::isAttachedToMultiblock);
	}

	private static BlockMovementChecks.CheckResult isAttachedToMultiblock(BlockState state, Level level,
				BlockPos pos, Direction direction) {
		BlockPos master = getMaster(level, pos, state);
		if (master == null)
			return BlockMovementChecks.CheckResult.PASS;

		BlockPos adjacentPos = pos.relative(direction);
		BlockPos adjacentMaster = getMaster(level, adjacentPos, level.getBlockState(adjacentPos));
		return master.equals(adjacentMaster)
				? BlockMovementChecks.CheckResult.SUCCESS
				: BlockMovementChecks.CheckResult.PASS;
	}

	private static BlockPos getMaster(Level level, BlockPos pos, BlockState state) {
		if (state.is(VintageBlocks.CENTRIFUGE.get()))
			return pos;
		if (state.is(VintageBlocks.CENTRIFUGE_STRUCTURAL.get())) {
			CentrifugeStructuralBlock structural = VintageBlocks.CENTRIFUGE_STRUCTURAL.get();
			if (structural.stillValid(level, pos, state, false)) {
				BlockPos master = CentrifugeStructuralBlock.getMaster(level, pos, state);
				if (level.getBlockState(master).is(VintageBlocks.CENTRIFUGE.get()))
					return master;
			}
		}

		if (state.is(VintageBlocks.HELVE.get()))
			return pos;
		if (state.is(VintageBlocks.HELVE_STRUCTURAL.get())) {
			HelveStructuralBlock structural = VintageBlocks.HELVE_STRUCTURAL.get();
			if (structural.stillValid(level, pos, state, false)) {
				BlockPos master = HelveStructuralBlock.getMaster(level, pos, state);
				if (level.getBlockState(master).is(VintageBlocks.HELVE.get()))
					return master;
			}
		}
		if (state.is(VintageBlocks.HELVE_KINETIC.get())) {
			HelveKineticBlock kinetic = VintageBlocks.HELVE_KINETIC.get();
			if (kinetic.stillValid(level, pos, state, false)) {
				BlockPos master = HelveKineticBlock.getMaster(level, pos, state);
				if (level.getBlockState(master).is(VintageBlocks.HELVE.get()))
					return master;
			}
		}

		if (state.is(VintageBlocks.LATHE_ROTATING.get()))
			return pos;
		if (state.is(VintageBlocks.LATHE_MOVING.get())) {
			LatheMovingBlock moving = VintageBlocks.LATHE_MOVING.get();
			if (moving.stillValid(level, pos, state, false)) {
				BlockPos master = LatheMovingBlock.getMaster(level, pos, state);
				if (level.getBlockState(master).is(VintageBlocks.LATHE_ROTATING.get()))
					return master;
			}
		}

		return null;
	}
}
