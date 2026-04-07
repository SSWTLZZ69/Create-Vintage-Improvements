package com.negodya1.vintageimprovements.content.energy.base;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public abstract class ElectricKineticBlockEntity extends KineticBlockEntity {

	protected final VintageInternalEnergyStorage localEnergy;
	protected IEnergyStorage lazyEnergy;

	private boolean firstTickState = true;
	// protected final int CAPACITY, MAX_IN, MAX_OUT;

	public ElectricKineticBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
		localEnergy = new VintageInternalEnergyStorage(getCapacity(), getMaxIn(), getMaxOut());
		lazyEnergy = localEnergy;
		setLazyTickRate(20);
	}

	public abstract int getCapacity();
	public abstract int getMaxIn();
	public abstract int getMaxOut();

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

	public abstract boolean isEnergyInput(Direction side);
	public abstract boolean isEnergyOutput(Direction side);

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		localEnergy.read(compound);
	}

	@Override
	public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(compound, registries, clientPacket);
		localEnergy.write(compound);
	}

	@Override
	public void remove() {
		super.remove();
	}

	@Deprecated
	public void outputTick(int max) {
		for(Direction side : Direction.values()) {
			if(!isEnergyOutput(side))
				continue;
			localEnergy.outputToSide(level, worldPosition, side, max);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if(firstTickState) {
			firstTickState = false;
			firstTick();
		}
	}

	public void firstTick() {
		updateCache();
	}

	public boolean ignoreCapSide() {
		return false;
	}

	public void updateCache() {
		if(level.isClientSide())
			return;
		for(Direction side : Direction.values()) {
			updateCache(side);
		}
	}

	public void updateCache(Direction side) {
		if (!level.isLoaded(worldPosition.relative(side))) {
			setCache(side, null);
			return;
		}
		BlockEntity te = level.getBlockEntity(worldPosition.relative(side));
		if(te == null) {
			setCache(side, null);
			return;
		}
		IEnergyStorage resolved = level.getCapability(Capabilities.EnergyStorage.BLOCK, worldPosition.relative(side), side.getOpposite());
		if(ignoreCapSide() && resolved == null) resolved = level.getCapability(Capabilities.EnergyStorage.BLOCK, worldPosition.relative(side), null);
		// Make sure the side isn't already cached.
		if (resolved == getCachedEnergy(side)) return;
		setCache(side, resolved);
	}

	private IEnergyStorage escacheUp;
	private IEnergyStorage escacheDown;
	private IEnergyStorage escacheNorth;
	private IEnergyStorage escacheEast;
	private IEnergyStorage escacheSouth;
	private IEnergyStorage escacheWest;

	public void setCache(Direction side, IEnergyStorage storage) {
		switch(side) {
			case DOWN:
				escacheDown = storage;
				break;
			case EAST:
				escacheEast = storage;
				break;
			case NORTH:
				escacheNorth = storage;
				break;
			case SOUTH:
				escacheSouth = storage;
				break;
			case UP:
				escacheUp = storage;
				break;
			case WEST:
				escacheWest = storage;
				break;
		}
	}

	public IEnergyStorage getCachedEnergy(Direction side) {
		switch(side) {
			case DOWN:
				return escacheDown;
			case EAST:
				return escacheEast;
			case NORTH:
				return escacheNorth;
			case SOUTH:
				return escacheSouth;
			case UP:
				return escacheUp;
			case WEST:
				return escacheWest;
		}
		return null;
	}

	public IEnergyStorage getEnergyStorage() {
		return lazyEnergy;
	}
}
