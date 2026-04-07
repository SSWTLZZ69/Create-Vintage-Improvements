package com.negodya1.vintageimprovements.content.kinetics.centrifuge;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.mojang.serialization.MapCodec;
import com.negodya1.vintageimprovements.VintageBlockEntity;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintageShapes;
import com.simibubi.create.Create;
import com.simibubi.create.api.equipment.goggles.IProxyHoveringInformation;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.item.ItemHelper;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.render.MultiPosDestructionHandler;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;

public class CentrifugeStructuralBlock extends DirectionalBlock implements IBE<CentrifugeStructuralBlockEntity>, IWrenchable, IProxyHoveringInformation {
	public static final MapCodec<CentrifugeStructuralBlock> CODEC = simpleCodec(CentrifugeStructuralBlock::new);
	public static final VoxelShaper CENTRIFUGE_SHAPE = VintageShapes.shape(0, 2, 0, 16, 14, 16).forDirectional();

	public CentrifugeStructuralBlock(Properties p_52591_) {
		super(p_52591_);
	}

	@Override
	protected MapCodec<? extends DirectionalBlock> codec() {
		return CODEC;
	}

	@Override
	public Class<CentrifugeStructuralBlockEntity> getBlockEntityClass() {
		return CentrifugeStructuralBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends CentrifugeStructuralBlockEntity> getBlockEntityType() {
		return VintageBlockEntity.CENTRIFUGE_STRUCTURAL.get();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return CENTRIFUGE_SHAPE.get(Direction.UP);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACING));
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState pState) {
		return PushReaction.BLOCK;
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		return InteractionResult.PASS;
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader pLevel, BlockPos pPos, BlockState pState) {
		return VintageBlocks.CENTRIFUGE.asStack();
	}

	@Override
	public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
		BlockPos clickedPos = context.getClickedPos();
		Level level = context.getLevel();

		if (stillValid(level, clickedPos, state, false)) {
			BlockPos masterPos = getMaster(level, clickedPos, state);
			context = new UseOnContext(level, context.getPlayer(), context.getHand(), context.getItemInHand(),
					new BlockHitResult(context.getClickLocation(), context.getClickedFace(), masterPos,
							context.isInside()));
			state = level.getBlockState(masterPos);
		}

		return IWrenchable.super.onSneakWrenched(state, context);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
								 BlockHitResult pHit) {
		if (!stillValid(pLevel, pPos, pState, false))
			return ItemInteractionResult.FAIL;
		if (!(pLevel.getBlockEntity(getMaster(pLevel, pPos, pState)) instanceof CentrifugeBlockEntity wwt))
			return ItemInteractionResult.FAIL;
		if (wwt.addBasin(stack)) {
			if (!pPlayer.isCreative())
				stack.shrink(1);
			return ItemInteractionResult.SUCCESS;
		}
		if (wwt.getBasins() >= 4 && wwt.addRedstoneApp(stack)) {
			if (!pPlayer.isCreative())
				stack.shrink(1);
			return ItemInteractionResult.SUCCESS;
		}

		if (wwt.getBasins() < 4 || wwt.getSpeed() != 0) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		if (FluidHelper.tryEmptyItemIntoBE(pLevel, pPlayer, pHand, stack, wwt))
			return ItemInteractionResult.SUCCESS;
		if (FluidHelper.tryFillItemFromBE(pLevel, pPlayer, pHand, stack, wwt))
			return ItemInteractionResult.SUCCESS;

		if (GenericItemEmptying.canItemBeEmptied(pLevel, stack)
				|| GenericItemFilling.canItemBeFilled(pLevel, stack))
			return ItemInteractionResult.SUCCESS;
		if (stack.getItem()
				.equals(Items.SPONGE)
				&& wwt.fluidCapability != null
				&& !wwt.fluidCapability.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE)
				.isEmpty()) {
			return ItemInteractionResult.SUCCESS;
		}

		collectOutputs(pPlayer, pLevel, pPos, wwt);
		return ItemInteractionResult.SUCCESS;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
		if (!stillValid(pLevel, pPos, pState, false))
			return InteractionResult.FAIL;
		if (!(pLevel.getBlockEntity(getMaster(pLevel, pPos, pState)) instanceof CentrifugeBlockEntity wwt))
			return InteractionResult.FAIL;
		if (wwt.getBasins() < 4 || wwt.getSpeed() != 0)
			return InteractionResult.PASS;

		collectOutputs(pPlayer, pLevel, pPos, wwt);
		return InteractionResult.SUCCESS;
	}

	private static void collectOutputs(Player pPlayer, Level pLevel, BlockPos pPos, CentrifugeBlockEntity wwt) {
		IItemHandlerModifiable inv = wwt.capability != null ? wwt.capability : new ItemStackHandler(1);
		boolean success = false;
		for (int slot = 0; slot < inv.getSlots(); slot++) {
			ItemStack stackInSlot = inv.getStackInSlot(slot);
			if (stackInSlot.isEmpty())
				continue;
			pPlayer.getInventory()
					.placeItemBackInInventory(stackInSlot);
			inv.setStackInSlot(slot, ItemStack.EMPTY);
			success = true;
		}
		if (success)
			pLevel.playSound(null, pPos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f,
					1f + Create.RANDOM.nextFloat());
	}

	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		pLevel.removeBlockEntity(pPos);
		if (stillValid(pLevel, pPos, pState, false))
			pLevel.destroyBlock(getMaster(pLevel, pPos, pState), true);
	}

	public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
		if (stillValid(pLevel, pPos, pState, false)) {
			BlockPos masterPos = getMaster(pLevel, pPos, pState);
			pLevel.destroyBlockProgress(masterPos.hashCode(), masterPos, -1);
			if (!pLevel.isClientSide() && pPlayer.isCreative())
				pLevel.destroyBlock(masterPos, false);
		}
		return super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel,
								  BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (stillValid(pLevel, pCurrentPos, pState, false)) {
			BlockPos masterPos = getMaster(pLevel, pCurrentPos, pState);
			if (!pLevel.getBlockTicks()
					.hasScheduledTick(masterPos, VintageBlocks.CENTRIFUGE.get()))
				pLevel.scheduleTick(masterPos, VintageBlocks.CENTRIFUGE.get(), 1);
			return pState;
		}
		if (!(pLevel instanceof Level level) || level.isClientSide())
			return pState;
		if (!level.getBlockTicks()
				.hasScheduledTick(pCurrentPos, this))
			level.scheduleTick(pCurrentPos, this, 1);
		return pState;
	}

	public static BlockPos getMaster(BlockGetter level, BlockPos pos, BlockState state) {
		Direction direction = state.getValue(FACING);
		BlockPos targetedPos = pos.relative(direction);
		BlockState targetedState = level.getBlockState(targetedPos);
		if (targetedState.is(VintageBlocks.CENTRIFUGE_STRUCTURAL.get()))
			return getMaster(level, targetedPos, targetedState);
		return targetedPos;
	}

	public boolean stillValid(BlockGetter level, BlockPos pos, BlockState state, boolean directlyAdjacent) {
		if (!state.is(this))
			return false;

		Direction direction = state.getValue(FACING);
		BlockPos targetedPos = pos.relative(direction);
		BlockState targetedState = level.getBlockState(targetedPos);

		if (!directlyAdjacent && stillValid(level, targetedPos, targetedState, true))
			return true;
		return targetedState.getBlock() instanceof CentrifugeBlock;
	}

	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (!stillValid(pLevel, pPos, pState, false)) {
			getBlockEntity(pLevel, pPos);
			pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
		consumer.accept(new RenderProperties());
	}

	@Override
	public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2,
									 LivingEntity entity, int numberOfParticles) {
		return true;
	}

	public static class RenderProperties implements IClientBlockExtensions, MultiPosDestructionHandler {

		@Override
		public boolean addDestroyEffects(BlockState state, Level Level, BlockPos pos, ParticleEngine manager) {
			return true;
		}

		@Override
		public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
			if (target instanceof BlockHitResult bhr) {
				BlockPos targetPos = bhr.getBlockPos();
				CentrifugeStructuralBlock centrifugeStructuralBlock = VintageBlocks.CENTRIFUGE_STRUCTURAL.get();
				if (centrifugeStructuralBlock.stillValid(level, targetPos, state, false))
					manager.crack(CentrifugeStructuralBlock.getMaster(level, targetPos, state), bhr.getDirection());
				return true;
			}
			return IClientBlockExtensions.super.addHitEffects(state, level, target, manager);
		}

		@Override
		@Nullable
		public Set<BlockPos> getExtraPositions(ClientLevel level, BlockPos pos, BlockState blockState, int progress) {
			CentrifugeStructuralBlock centrifugeStructuralBlock = VintageBlocks.CENTRIFUGE_STRUCTURAL.get();
			if (!centrifugeStructuralBlock.stillValid(level, pos, blockState, false))
				return null;
			HashSet<BlockPos> set = new HashSet<>();
			set.add(CentrifugeStructuralBlock.getMaster(level, pos, blockState));
			return set;
		}
	}

	@Override
	public BlockPos getInformationSource(Level level, BlockPos pos, BlockState state) {
		return stillValid(level, pos, state, false) ? getMaster(level, pos, state) : pos;
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

		CentrifugeBlockEntity centrifuge = null;
		for (BlockPos pos : Iterate.hereAndBelow(entityIn.blockPosition())) {
			if (worldIn.getBlockState(pos).is(VintageBlocks.CENTRIFUGE_STRUCTURAL.get())) {
				if (stillValid(worldIn, pos, worldIn.getBlockState(pos), false)) {
					centrifuge = (CentrifugeBlockEntity) worldIn.getBlockEntity(getMaster(worldIn, pos, worldIn.getBlockState(pos)));
					break;
				}
			}
		}
		if (centrifuge == null)
			return;

		if (centrifuge.getBasins() < 4 || centrifuge.getSpeed() != 0) return;

		ItemEntity itemEntity = (ItemEntity) entityIn;
		IItemHandlerModifiable capability = centrifuge.capability;
		if (capability == null)
			return;

		for (int i = 0; i < 9; i++) {
			ItemStack remainder = capability.insertItem(i, itemEntity.getItem(), false);
			if (remainder.isEmpty())
				itemEntity.discard();
			if (remainder.getCount() < itemEntity.getItem()
					.getCount())
				itemEntity.setItem(remainder);

			if (remainder.isEmpty()) break;
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		return getBlockEntityOptional(worldIn, pos).map(CentrifugeStructuralBlockEntity::getAnalogSignal).orElse(0);
	}
}
