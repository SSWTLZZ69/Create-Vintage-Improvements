package com.negodya1.vintageimprovements.content.kinetics.helve_hammer;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

public class HelveStructuralBlock extends DirectionalBlock implements IWrenchable, IProxyHoveringInformation {
	public static final VoxelShaper CENTRIFUGE_SHAPE = VintageShapes.shape(5, 0, 5, 11, 16, 11).forDirectional();

	public HelveStructuralBlock(Properties p_52591_) {
		super(p_52591_);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return CENTRIFUGE_SHAPE.get(state.getValue(FACING));
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
	public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
		return VintageBlocks.HELVE.asStack();
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
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		if (stillValid(pLevel, pPos, pState, false))
			pLevel.destroyBlock(getMaster(pLevel, pPos, pState), true);
	}

	public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
		if (stillValid(pLevel, pPos, pState, false)) {
			BlockPos masterPos = getMaster(pLevel, pPos, pState);
			pLevel.destroyBlockProgress(masterPos.hashCode(), masterPos, -1);
			if (!pLevel.isClientSide() && pPlayer.isCreative())
				pLevel.destroyBlock(masterPos, false);
		}
		super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel,
								  BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (stillValid(pLevel, pCurrentPos, pState, false)) {
			BlockPos masterPos = getMaster(pLevel, pCurrentPos, pState);
			if (!pLevel.getBlockTicks()
					.hasScheduledTick(masterPos, VintageBlocks.HELVE.get()))
				pLevel.scheduleTick(masterPos, VintageBlocks.HELVE.get(), 1);
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
		if (targetedState.is(VintageBlocks.HELVE_STRUCTURAL.get()) || targetedState.is(VintageBlocks.HELVE_KINETIC.get()))
			return getMaster(level, targetedPos, targetedState);
		return targetedPos;
	}

	public static BlockPos getSlave(BlockGetter level, BlockPos pos, BlockState state) {
		Direction direction = state.getValue(FACING);
		BlockPos targetedPos = pos.relative(direction.getOpposite());
		BlockState targetedState = level.getBlockState(targetedPos);
		if (targetedState.is(VintageBlocks.HELVE_STRUCTURAL.get()) || targetedState.is(VintageBlocks.HELVE.get()))
			return getSlave(level, targetedPos, targetedState);
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
		return targetedState.getBlock() instanceof HelveBlock;
	}

	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (!stillValid(pLevel, pPos, pState, false))
			pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
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
				HelveStructuralBlock centrifugeStructuralBlock = VintageBlocks.HELVE_STRUCTURAL.get();
				if (centrifugeStructuralBlock.stillValid(level, targetPos, state, false))
					manager.crack(HelveStructuralBlock.getMaster(level, targetPos, state), bhr.getDirection());
				return true;
			}
			return IClientBlockExtensions.super.addHitEffects(state, level, target, manager);
		}

		@Override
		@Nullable
		public Set<BlockPos> getExtraPositions(ClientLevel level, BlockPos pos, BlockState blockState, int progress) {
			HelveStructuralBlock centrifugeStructuralBlock = VintageBlocks.HELVE_STRUCTURAL.get();
			if (!centrifugeStructuralBlock.stillValid(level, pos, blockState, false))
				return null;
			HashSet<BlockPos> set = new HashSet<>();
			set.add(HelveStructuralBlock.getMaster(level, pos, blockState));
			return set;
		}
	}

	@Override
	public BlockPos getInformationSource(Level level, BlockPos pos, BlockState state) {
		return stillValid(level, pos, state, false) ? getMaster(level, pos, state) : pos;
	}
}