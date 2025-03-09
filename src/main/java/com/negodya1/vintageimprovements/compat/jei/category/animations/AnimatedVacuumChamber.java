package com.negodya1.vintageimprovements.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintagePartialModels;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class AnimatedVacuumChamber extends AnimatedKinetics {

	public void draw(GuiGraphics graphics, int xOffset, int yOffset, boolean mode) {
		int scale = 23;

		draw(graphics, xOffset, yOffset);

		PoseStack matrixStack = graphics.pose();
		matrixStack.pushPose();
		matrixStack.translate(xOffset, yOffset, 200);
		matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
		matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));

		if (!mode)
			blockElement(VintagePartialModels.VACUUM_CHAMBER_ARROWS)
					.atLocal(0, 0, 0)
					.scale(scale)
					.render(graphics);
		else
			blockElement(VintagePartialModels.VACUUM_CHAMBER_ARROWS)
					.atLocal(0, 0, 0)
					.rotateBlock(0, 0, 180)
					.scale(scale)
					.render(graphics);

		matrixStack.popPose();
	}

	@Override
	public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
		PoseStack matrixStack = graphics.pose();
		matrixStack.pushPose();
		matrixStack.translate(xOffset, yOffset, 200);
		matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
		matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
		int scale = 23;

		blockElement(VintagePartialModels.VACUUM_COG)
			.rotateBlock(0, getCurrentAngle() * 2, 0)
			.atLocal(0, 0, 0)
			.scale(scale)
			.render(graphics);

		blockElement(VintageBlocks.VACUUM_CHAMBER.getDefaultState())
			.atLocal(0, 0, 0)
			.scale(scale)
			.render(graphics);

		float animation = ((Mth.sin(AnimationTickHolder.getRenderTime() / 32f) + 1) / 5) + .5f;

		animation = Mth.clamp(animation, 0, 11.2f / 16f);

		blockElement(VintagePartialModels.VACUUM_PIPE)
			.atLocal(0, animation, 0)
			.scale(scale)
			.render(graphics);

		blockElement(AllBlocks.BASIN.getDefaultState())
			.atLocal(0, 1.65, 0)
			.scale(scale)
			.render(graphics);

		matrixStack.popPose();
	}

}
