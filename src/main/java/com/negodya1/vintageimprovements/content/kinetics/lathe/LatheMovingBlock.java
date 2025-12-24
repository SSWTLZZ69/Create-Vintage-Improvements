package com.negodya1.vintageimprovements.content.kinetics.lathe;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.negodya1.vintageimprovements.*;
import com.negodya1.vintageimprovements.content.kinetics.lathe.recipe_card.RecipeCardItem;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.equipment.goggles.IProxyHoveringInformation;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

public class LatheMovingBlock extends DirectionalKineticBlock implements IWrenchable, IProxyHoveringInformation, IBE<LatheMovingBlockEntity> {

	private static final Component CONTAINER_TITLE = VintageLang.translateDirect("container.lathe");

	public LatheMovingBlock(Properties p_52591_) {
		super(p_52591_);
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState state) {
		return state.getValue(FACING).getClockWise().getAxis();
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() == state.getValue(FACING).getClockWise().getAxis();
	}

	@Override
	public Class<LatheMovingBlockEntity> getBlockEntityClass() {
		return LatheMovingBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends LatheMovingBlockEntity> getBlockEntityType() {
		return VintageBlockEntity.LATHE_MOVING.get();
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
		return VintageBlocks.LATHE_ROTATING.asStack();
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

		return super.onSneakWrenched(state, context);
	}

	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		pLevel.removeBlockEntity(pPos);
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
					.hasScheduledTick(masterPos, VintageBlocks.LATHE_ROTATING.get()))
				pLevel.scheduleTick(masterPos, VintageBlocks.LATHE_ROTATING.get(), 1);
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
		if (targetedState.is(VintageBlocks.LATHE_MOVING.get()))
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
		return targetedState.getBlock() instanceof LatheRotatingBlock;
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
				LatheMovingBlock centrifugeStructuralBlock = VintageBlocks.LATHE_MOVING.get();
				if (centrifugeStructuralBlock.stillValid(level, targetPos, state, false))
					manager.crack(LatheMovingBlock.getMaster(level, targetPos, state), bhr.getDirection());
				return true;
			}
			return IClientBlockExtensions.super.addHitEffects(state, level, target, manager);
		}

		@Override
		@Nullable
		public Set<BlockPos> getExtraPositions(ClientLevel level, BlockPos pos, BlockState blockState, int progress) {
			LatheMovingBlock centrifugeStructuralBlock = VintageBlocks.LATHE_MOVING.get();
			if (!centrifugeStructuralBlock.stillValid(level, pos, blockState, false))
				return null;
			HashSet<BlockPos> set = new HashSet<>();
			set.add(LatheMovingBlock.getMaster(level, pos, blockState));
			return set;
		}
	}

	@Override
	public BlockPos getInformationSource(Level level, BlockPos pos, BlockState state) {
		return stillValid(level, pos, state, false) ? getMaster(level, pos, state) : pos;
	}

	@Override
	public SpeedLevel getMinimumRequiredSpeedLevel() {
		return SpeedLevel.MEDIUM;
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if (player.getItemInHand(handIn).isEmpty())
			withBlockEntityDo(worldIn, pos, lathe -> {
				if (lathe.recipeSlot.isEmpty()) {
					lathe.currentRecipe = null;
					lathe.resetRecipe();
					if (worldIn.isClientSide) return;
					if (player instanceof DeployerFakePlayer);
					else NetworkHooks.openScreen((ServerPlayer) player, lathe, lathe::sendToMenu);
				}
				else {
					lathe.resetRecipe();
					if (worldIn.isClientSide) return;
					player.getInventory()
							.placeItemBackInInventory(lathe.recipeSlot.getStackInSlot(0));
					lathe.recipeSlot.setStackInSlot(0, ItemStack.EMPTY);
					lathe.setChanged();
					lathe.sendData();
				}
			});
		else if (player.getItemInHand(handIn).getItem() instanceof RecipeCardItem) {
			withBlockEntityDo(worldIn, pos, lathe -> {
				if (!lathe.recipeSlot.isEmpty()) return;
				lathe.resetRecipe();
				if (worldIn.isClientSide) return;
				player.setItemInHand(handIn, lathe.recipeSlot.insertItem(0, player.getItemInHand(handIn), false));

				lathe.setChanged();
				lathe.sendData();
			});
		}

		return InteractionResult.SUCCESS;
	}
}