package com.negodya1.vintageimprovements.content.kinetics.helve_hammer;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HelveKineticBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {

	public HelveKineticBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).inflate(2);
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public void invalidate() {
		super.invalidate();
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}

	public float getHammerAngle() {
		if (level.getBlockEntity(HelveKineticBlock.getMaster(level, worldPosition, getBlockState())) instanceof HelveBlockEntity be)
			return be.getHammerAngle();
		return 0.0f;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
	}

}
