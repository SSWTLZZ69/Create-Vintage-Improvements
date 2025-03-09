package com.negodya1.vintageimprovements.content.kinetics.centrifuge;

import com.negodya1.vintageimprovements.*;
import com.negodya1.vintageimprovements.content.kinetics.vibration.VibratingTableBlockEntity;
import com.negodya1.vintageimprovements.foundation.advancement.VintageAdvancementBehaviour;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;

import com.simibubi.create.foundation.fluid.FluidHelper;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS;

public class CentrifugeBlock extends KineticBlock implements IBE<CentrifugeBlockEntity> {
	public static final VoxelShaper CENTRIFUGE_SHAPE = VintageShapes.shape(0, 0, 0, 16, 14, 16).forDirectional();

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		VintageAdvancementBehaviour.setPlacedBy(level, pos, placer);
		super.setPlacedBy(level, pos, state, placer, stack);
	}

	public CentrifugeBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return CENTRIFUGE_SHAPE.get(Direction.UP);
	}

	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
								 BlockHitResult hit) {
		ItemStack heldItem = player.getItemInHand(handIn);

		return onBlockEntityUse(worldIn, pos, be -> {
			if (!heldItem.isEmpty()) {
				if (player.getItemInHand(handIn).getItem() == AllBlocks.BASIN.asItem()) {
					if (be.addBasin(player.getItemInHand(handIn))) {
						if (!player.isCreative())
							player.getItemInHand(handIn).shrink(1);
						return InteractionResult.SUCCESS;
					}
					return InteractionResult.PASS;
				}
				else if (player.getItemInHand(handIn).getItem() == VintageItems.REDSTONE_MODULE.get() && be.getBasins() == 4) {
					if (be.addRedstoneApp(player.getItemInHand(handIn))) {
						if (!player.isCreative())
							player.getItemInHand(handIn).shrink(1);
						return InteractionResult.SUCCESS;
					}
					return InteractionResult.PASS;
				}

				if (be.getBasins() < 4 || be.getSpeed() != 0) return InteractionResult.PASS;

				if (FluidHelper.tryEmptyItemIntoBE(worldIn, player, handIn, heldItem, be))
					return InteractionResult.SUCCESS;
				if (FluidHelper.tryFillItemFromBE(worldIn, player, handIn, heldItem, be))
					return InteractionResult.SUCCESS;

				if (GenericItemEmptying.canItemBeEmptied(worldIn, heldItem)
						|| GenericItemFilling.canItemBeFilled(worldIn, heldItem))
					return InteractionResult.SUCCESS;
				if (heldItem.getItem()
						.equals(Items.SPONGE)
						&& !be.getCapability(ForgeCapabilities.FLUID_HANDLER)
						.map(iFluidHandler -> iFluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE))
						.orElse(FluidStack.EMPTY)
						.isEmpty()) {
					return InteractionResult.SUCCESS;
				}
				return InteractionResult.PASS;
			}

			if (be.getBasins() < 4 || be.getSpeed() != 0) return InteractionResult.PASS;

			IItemHandlerModifiable inv = be.capability.orElse(new ItemStackHandler(1));
			boolean success = false;
			for (int slot = 0; slot < inv.getSlots(); slot++) {
				ItemStack stackInSlot = inv.getStackInSlot(slot);
				if (stackInSlot.isEmpty())
					continue;
				player.getInventory()
						.placeItemBackInInventory(stackInSlot);
				inv.setStackInSlot(slot, ItemStack.EMPTY);
				success = true;
			}
			if (success)
				worldIn.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f,
						1f + Create.RANDOM.nextFloat());
			return InteractionResult.SUCCESS;
		});
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return Axis.Y;
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() == Axis.Y;
	}

	@Override
	public Class<CentrifugeBlockEntity> getBlockEntityClass() {
		return CentrifugeBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends CentrifugeBlockEntity> getBlockEntityType() {
		return VintageBlockEntity.CENTRIFUGE.get();
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState stateForPlacement = super.getStateForPlacement(context);
		BlockPos pos = context.getClickedPos();
		Axis axis = Axis.Y;

		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					if (axis.choose(x, y, z) != 0)
						continue;
					BlockPos offset = new BlockPos(x, y, z);
					if (offset.equals(BlockPos.ZERO))
						continue;
					BlockState occupiedState = context.getLevel()
							.getBlockState(pos.offset(offset));
					if (!occupiedState.canBeReplaced())
						return null;
				}
			}
		}

		return stateForPlacement;
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, level, pos, oldState, isMoving);
		if (!level.getBlockTicks()
				.hasScheduledTick(pos, this))
			level.scheduleTick(pos, this, 1);
	}

	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		Axis axis = Axis.Y;
		for (Direction side : Iterate.directions) {
			if (side.getAxis() == axis)
				continue;
			for (boolean secondary : Iterate.falseAndTrue) {
				Direction targetSide = secondary ? side.getClockWise(axis) : side;
				BlockPos structurePos = (secondary ? pPos.relative(side) : pPos).relative(targetSide);
				BlockState occupiedState = pLevel.getBlockState(structurePos);
				BlockState requiredStructure = VintageBlocks.CENTRIFUGE_STRUCTURAL.getDefaultState()
						.setValue(CentrifugeStructuralBlock.FACING, targetSide.getOpposite());
				if (occupiedState == requiredStructure)
					continue;
				if (!occupiedState.canBeReplaced()) {
					pLevel.destroyBlock(pPos, false);
					return;
				}
				pLevel.setBlockAndUpdate(structurePos, requiredStructure);
			}
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		BlockEntity be = getBlockEntity(worldIn, pos);
		if (be instanceof CentrifugeBlockEntity cbe) {
			if (!cbe.getRedstoneApp()) return 0;
			return (cbe.isProccesingNow() ? 15 : 0);
		}

		return 0;
	}

}
