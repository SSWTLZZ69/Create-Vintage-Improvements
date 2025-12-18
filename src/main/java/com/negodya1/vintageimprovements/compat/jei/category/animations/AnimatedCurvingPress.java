package com.negodya1.vintageimprovements.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintagePartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;

public class AnimatedCurvingPress extends AnimatedKinetics {

	public AnimatedCurvingPress() {}

	public void draw(GuiGraphics graphics, int xOffset, int yOffset, int mode) {
		draw(graphics, xOffset, yOffset);

		PoseStack matrixStack = graphics.pose();
		matrixStack.pushPose();
		matrixStack.translate(xOffset, yOffset, 200);
		matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
		matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));

		int scale = 24;

		switch (mode) {
			case 2 -> blockElement(VintagePartialModels.CURVING_HEAD_2)
					.atLocal(0, -getAnimatedHeadOffset(), 0)
					.scale(scale)
					.render(graphics);
			case 3 -> blockElement(VintagePartialModels.CURVING_HEAD_3)
					.atLocal(0, -getAnimatedHeadOffset(), 0)
					.scale(scale)
					.render(graphics);
			case 4 -> blockElement(VintagePartialModels.CURVING_HEAD_4)
					.atLocal(0, -getAnimatedHeadOffset(), 0)
					.scale(scale)
					.render(graphics);
			case 5 -> blockElement(VintagePartialModels.CURVING_HEAD_5)
					.atLocal(0, -getAnimatedHeadOffset(), 0)
					.scale(scale)
					.render(graphics);
			default -> blockElement(VintagePartialModels.CURVING_HEAD)
					.atLocal(0, -getAnimatedHeadOffset(), 0)
					.scale(scale)
					.render(graphics);
		}

		matrixStack.popPose();
	}

	@Override
	public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
		PoseStack matrixStack = graphics.pose();
		matrixStack.pushPose();
		matrixStack.translate(xOffset, yOffset, 200);
		matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
		matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
		int scale = 24;

		blockElement(shaft(Direction.Axis.Z))
				.rotateBlock(0, 0, getCurrentAngle())
				.scale(scale)
				.render(graphics);

		blockElement(VintageBlocks.CURVING_PRESS.getDefaultState())
				.scale(scale)
				.render(graphics);

		blockElement(VintagePartialModels.CURVING_POLE)
				.atLocal(0, -getAnimatedHeadOffset(), 0)
				.scale(scale)
				.render(graphics);

		matrixStack.popPose();
	}

	private float getAnimatedHeadOffset() {
		float cycle = (AnimationTickHolder.getRenderTime() - offset * 8) % 30;
		if (cycle < 10) {
			float progress = cycle / 10;
			return -(progress * progress * progress);
		}
		if (cycle < 15)
			return -1;
		if (cycle < 20)
			return -1 + (1 - ((20 - cycle) / 5));
		return 0;
	}

}
