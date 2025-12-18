package com.negodya1.vintageimprovements.content.kinetics.laser;

import com.negodya1.vintageimprovements.VintageBlockEntity;
import com.negodya1.vintageimprovements.foundation.advancement.VintageAdvancementBehaviour;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class LaserBlock extends KineticBlock implements IBE<LaserBlockEntity>, IWrenchable {

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		VintageAdvancementBehaviour.setPlacedBy(level, pos, placer);
		super.setPlacedBy(level, pos, state, placer, stack);
	}

	public LaserBlock(Properties props) {
		super(props);
		registerDefaultState(defaultBlockState().setValue(POWERED, false));
	}
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return AllShapes.CASING_13PX.get(Direction.DOWN);
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState state) {
		return Direction.Axis.Y;
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == Direction.UP;
	}
	
	@Override
	public Class<LaserBlockEntity> getBlockEntityClass() {
		return LaserBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends LaserBlockEntity> getBlockEntityType() {
		return VintageBlockEntity.LASER.get();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return VintageBlockEntity.LASER.create(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}

	public void setPowered(Level world, BlockPos pos, boolean powered) {
		world.setBlock(pos, defaultBlockState().setValue(POWERED, powered), 3);
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		return true;
	}
}
