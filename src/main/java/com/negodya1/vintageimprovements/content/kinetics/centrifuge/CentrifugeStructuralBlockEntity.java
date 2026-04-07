package com.negodya1.vintageimprovements.content.kinetics.centrifuge;

import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class CentrifugeStructuralBlockEntity extends SmartBlockEntity {

	private CentrifugeBlockEntity cbe;

	public CentrifugeStructuralBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void tick() {
		super.tick();

		if (cbe != null)
			return;
		if (!(getBlockState().getBlock() instanceof CentrifugeStructuralBlock structuralBlock))
			return;
		if (!structuralBlock.stillValid(getLevel(), getBlockPos(), getBlockState(), false))
			return;

		if (level.getBlockEntity(CentrifugeStructuralBlock.getMaster(level, getBlockPos(), getBlockState())) instanceof CentrifugeBlockEntity be) {
			cbe = be;
		}
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(new DirectBeltInputBehaviour(this));
	}

	@Override
	public void invalidate() {
		cbe = null;
		super.invalidate();
	}

	public boolean canProcess() {
		return cbe != null && cbe.canProcess();
	}

	public int getAnalogSignal() {
		if (cbe == null || !cbe.getRedstoneApp())
			return 0;
		return cbe.isProccesingNow() ? 15 : 0;
	}

	public CentrifugeBlockEntity getMaster() {
		return cbe;
	}
}
