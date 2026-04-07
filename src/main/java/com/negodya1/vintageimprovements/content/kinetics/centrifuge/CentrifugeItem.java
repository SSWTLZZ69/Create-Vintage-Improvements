package com.negodya1.vintageimprovements.content.kinetics.centrifuge;

import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class CentrifugeItem extends BlockItem {

	public CentrifugeItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}

	@Override
	public InteractionResult place(BlockPlaceContext ctx) {
		InteractionResult result = super.place(ctx);
		if (result != InteractionResult.FAIL)
			return result;
		Direction clickedFace = ctx.getClickedFace();
		if (clickedFace.getAxis() != Axis.Y)
			result = super.place(BlockPlaceContext.at(ctx, ctx.getClickedPos()
				.relative(clickedFace), clickedFace));
		if (result == InteractionResult.FAIL && ctx.getLevel()
			.isClientSide())
			showBounds(ctx);
		return result;
	}

	@OnlyIn(Dist.CLIENT)
	public void showBounds(BlockPlaceContext context) {
		BlockPos pos = context.getClickedPos();
		Axis axis = Axis.Y;
		Vec3 contract = Vec3.atLowerCornerOf(Direction.get(AxisDirection.POSITIVE, axis)
			.getNormal());
		if (!(context.getPlayer()instanceof LocalPlayer localPlayer))
			return;
		Outliner.getInstance().showAABB(Pair.of("centrifuge", pos), new AABB(pos).inflate(1)
			.deflate(contract.x, contract.y, contract.z))
			.colored(0xFF_ff5d6c);
		CreateLang.translate("large_water_wheel.not_enough_space")
			.color(0xFF_ff5d6c)
			.sendStatus(localPlayer);
	}

}

