package com.negodya1.vintageimprovements.content.kinetics.helve_hammer;

import com.negodya1.vintageimprovements.*;
import com.negodya1.vintageimprovements.foundation.advancement.VintageAdvancementBehaviour;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.block.IBE;

import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class HelveBlock extends HorizontalDirectionalBlock implements IBE<HelveBlockEntity> {
	public static final VoxelShaper HELVE_SHAPE = VintageShapes.shape(4, 4, 4, 12, 12, 16).add(5, 0, 5, 11, 14, 11).forDirectional();

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		VintageAdvancementBehaviour.setPlacedBy(level, pos, placer);
		super.setPlacedBy(level, pos, state, placer, stack);
	}

	public HelveBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACING));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return HELVE_SHAPE.get(state.getValue(FACING));
	}

	@Override
	public Class<HelveBlockEntity> getBlockEntityClass() {
		return HelveBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends HelveBlockEntity> getBlockEntityType() {
		return VintageBlockEntity.HELVE.get();
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState stateForPlacement = super.getStateForPlacement(context);
		Direction direction = context.getHorizontalDirection();
		Player player = context.getPlayer();

		stateForPlacement = stateForPlacement.setValue(FACING, direction.getOpposite());

		for (int x = 0; x <= 2; x++) {
			int xOffset = x;
			if (direction == Direction.NORTH || direction == Direction.WEST) xOffset *= -1;

			BlockPos offset = new BlockPos((direction == Direction.NORTH || direction == Direction.SOUTH ? 0 : xOffset), 0, (direction == Direction.NORTH || direction == Direction.SOUTH ? xOffset : 0));
			if (offset.equals(BlockPos.ZERO))
				continue;
			BlockState occupiedState = context.getLevel()
					.getBlockState(context.getClickedPos().offset(offset));
			if (!occupiedState.canBeReplaced())
				return null;
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
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		IBE.onRemove(pState, pLevel, pPos, pNewState);
		pLevel.removeBlockEntity(pPos);
	}

	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {

		Direction side = pState.getValue(FACING);
		BlockPos structurePos = pPos.relative(side.getOpposite());
		BlockState occupiedState = pLevel.getBlockState(structurePos);
		BlockState requiredStructure = VintageBlocks.HELVE_STRUCTURAL.getDefaultState()
				.setValue(HelveStructuralBlock.FACING, side);
		if (occupiedState != requiredStructure) {
			if (!occupiedState.canBeReplaced()) {
				pLevel.destroyBlock(pPos, false);
				return;
			}
			pLevel.setBlockAndUpdate(structurePos, requiredStructure);
		}

		structurePos = structurePos.relative(side.getOpposite());
		occupiedState = pLevel.getBlockState(structurePos);
		requiredStructure = VintageBlocks.HELVE_KINETIC.getDefaultState()
				.setValue(HelveKineticBlock.FACING, side);
		if (occupiedState != requiredStructure) {
			if (!occupiedState.canBeReplaced()) {
				pLevel.destroyBlock(pPos, false);
				return;
			}
			pLevel.setBlockAndUpdate(structurePos, requiredStructure);
		}
	}

	public static BlockPos getSlave(BlockGetter level, BlockPos pos, BlockState state) {
		Direction direction = state.getValue(FACING);
		BlockPos targetedPos = pos.relative(direction.getOpposite(), 2);
		BlockState targetedState = level.getBlockState(targetedPos);
		if (targetedState.is(VintageBlocks.HELVE_STRUCTURAL.get()) || targetedState.is(VintageBlocks.HELVE.get()))
			return getSlave(level, targetedPos, targetedState);
		return targetedPos;
	}

	@Override
	public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
		if (entityIn.level().isClientSide)
			return;
		if (!(entityIn instanceof ItemEntity))
			return;
		if (!entityIn.isAlive())
			return;

		HelveBlockEntity centrifuge = null;
		if (worldIn.getBlockState(pos).is(VintageBlocks.HELVE.get()))
			centrifuge = (HelveBlockEntity) worldIn.getBlockEntity(pos);
		if (centrifuge == null)
			return;

		ItemEntity itemEntity = (ItemEntity) entityIn;
		LazyOptional<IItemHandler> capability = centrifuge.getCapability(ForgeCapabilities.ITEM_HANDLER);
		if (!capability.isPresent())
			return;

		for (int i = 0; i < 3; i++) {
			ItemStack remainder = capability.orElse(new ItemStackHandler())
					.insertItem(i, itemEntity.getItem(), false);
			if (remainder.isEmpty())
				itemEntity.discard();
			if (remainder.getCount() < itemEntity.getItem()
					.getCount())
				itemEntity.setItem(remainder);

			if (remainder.isEmpty()) break;
		}
	}

	@Override
	public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
		super.updateEntityAfterFallOn(worldIn, entityIn);

		if (entityIn.level().isClientSide)
			return;
		if (!(entityIn instanceof ItemEntity))
			return;
		if (!entityIn.isAlive())
			return;

		HelveBlockEntity centrifuge = null;
		for (BlockPos pos : Iterate.hereAndBelow(entityIn.blockPosition()))
			if (worldIn.getBlockState(pos).is(VintageBlocks.HELVE.get()))
				if (centrifuge == null)
					centrifuge = (HelveBlockEntity) worldIn.getBlockEntity(pos);
		if (centrifuge == null)
			return;

		ItemEntity itemEntity = (ItemEntity) entityIn;
		LazyOptional<IItemHandler> capability = centrifuge.getCapability(ForgeCapabilities.ITEM_HANDLER);
		if (!capability.isPresent())
			return;

		for (int i = 0; i < 3; i++) {
			ItemStack remainder = capability.orElse(new ItemStackHandler())
					.insertItem(i, itemEntity.getItem(), false);
			if (remainder.isEmpty())
				itemEntity.discard();
			if (remainder.getCount() < itemEntity.getItem()
					.getCount())
				itemEntity.setItem(remainder);

			if (remainder.isEmpty()) break;
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
								 BlockHitResult hit) {
		if (!player.getItemInHand(handIn).isEmpty()) {
			if (!player.getItemInHand(handIn).is(VintageItems.HELVE_HAMMER_SLOT_COVER.get())) {
				if (!player.getItemInHand(handIn).is(AllItems.WRENCH.asItem()))
					return InteractionResult.PASS;
				withBlockEntityDo(worldIn, pos, helve -> {
					ItemStack stackInSlot = helve.resetBlockedSlots();
					player.getInventory()
							.placeItemBackInInventory(stackInSlot);

					helve.setChanged();
					helve.sendData();
				});
				return InteractionResult.SUCCESS;
			}
			withBlockEntityDo(worldIn, pos, helve -> {
				if (helve.addBlockedSlots() && !player.isCreative())
					player.getItemInHand(handIn).shrink(1);

				helve.setChanged();
				helve.sendData();
			});

			return InteractionResult.SUCCESS;
		}
		if (worldIn.isClientSide)
			return InteractionResult.SUCCESS;

		withBlockEntityDo(worldIn, pos, helve -> {
			boolean emptyOutput = true;
			IItemHandlerModifiable inv = helve.outputInv;
			for (int slot = 0; slot < inv.getSlots(); slot++) {
				ItemStack stackInSlot = inv.getStackInSlot(slot);
				if (!stackInSlot.isEmpty())
					emptyOutput = false;
				player.getInventory()
						.placeItemBackInInventory(stackInSlot);
				inv.setStackInSlot(slot, ItemStack.EMPTY);
			}

			if (emptyOutput) {
				inv = helve.inputInv;
				for (int slot = 0; slot < inv.getSlots(); slot++) {
					player.getInventory()
							.placeItemBackInInventory(inv.getStackInSlot(slot));
					inv.setStackInSlot(slot, ItemStack.EMPTY);
				}

				helve.resetRecipes();
			}

			helve.setChanged();
			helve.sendData();
		});

		return InteractionResult.SUCCESS;
	}

}
