package com.negodya1.vintageimprovements.content.equipment;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CopperSulfateItem extends Item {

	public CopperSulfateItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		BlockState state = context.getLevel()
			.getBlockState(context.getClickedPos());
		Block block = state.getBlock();

		if (context.getLevel().isClientSide) {
			BoneMealItem.addGrowthParticles(context.getLevel(), context.getClickedPos(), 100);
			return InteractionResult.SUCCESS;
		}

		if (block instanceof BonemealableBlock bonemealableBlock) {
			bonemealableBlock.performBonemeal((ServerLevel) context.getLevel(), context.getLevel().getRandom(), context.getClickedPos(), context.getLevel().getBlockState(context.getClickedPos()));

			if (context.getPlayer() != null && !context.getPlayer()
					.isCreative())
				context.getItemInHand()
						.shrink(1);

			return InteractionResult.SUCCESS;
		}

		return super.useOn(context);
	}

}
