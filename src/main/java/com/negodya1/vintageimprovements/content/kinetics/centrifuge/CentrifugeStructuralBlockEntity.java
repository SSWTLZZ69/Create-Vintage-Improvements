package com.negodya1.vintageimprovements.content.kinetics.centrifuge;

import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import java.util.List;

public class CentrifugeStructuralBlockEntity extends SmartBlockEntity {

    CentrifugeBlockEntity cbe;

    public CentrifugeStructuralBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        cbe = null;
    }

    @Override
    public void tick() {
        super.tick();

        if (cbe == null) {
            if (level.getBlockEntity(CentrifugeStructuralBlock.getMaster(level, getBlockPos(), getBlockState())) instanceof CentrifugeBlockEntity be) {
                cbe = be;
            }
        }

        //sendData();
        //setChanged();
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

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cbe != null) return cbe.getCapability(cap, side);
        return super.getCapability(cap, side);
    }

    public boolean canProcess() {
        if (cbe != null) return cbe.canProcess();
        return false;
    }

    private class CentrifugeStructuralTanksHandler extends CombinedTankWrapper {
        public CentrifugeStructuralTanksHandler(IFluidHandler... fluidHandlers) {
            super(fluidHandlers);
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            if (cbe.outputTank == getHandlerFromIndex(getIndexForSlot(tank)))
                return false;
            return canProcess() && super.isFluidValid(tank, stack);
        }
    }

    private class CentrifugeStructuralInventoryHandler extends CombinedInvWrapper {

        public CentrifugeStructuralInventoryHandler() {
            super(cbe.inputInv, cbe.outputInv);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (cbe.outputInv == getHandlerFromIndex(getIndexForSlot(slot)))
                return false;
            return canProcess() && super.isItemValid(slot, stack);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (cbe.outputInv == getHandlerFromIndex(getIndexForSlot(slot)))
                return stack;
            if (!isItemValid(slot, stack))
                return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (cbe.inputInv == getHandlerFromIndex(getIndexForSlot(slot)))
                return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }

    }

    public int getAnalogSignal() {
        if (cbe == null) return 0;
        if (!cbe.getRedstoneApp()) return 0;

        return (cbe.isProcessingNow() ? 15 : 0);
    }

}
