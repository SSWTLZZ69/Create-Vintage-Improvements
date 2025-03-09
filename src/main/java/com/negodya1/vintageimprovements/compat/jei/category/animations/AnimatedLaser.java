package com.negodya1.vintageimprovements.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintagePartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class AnimatedLaser extends AnimatedKinetics {

	@Override
	public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
		PoseStack matrixStack = graphics.pose();
		matrixStack.pushPose();
		matrixStack.translate(xOffset, yOffset, 0);
		matrixStack.translate(0, 0, 200);
		matrixStack.translate(2, 22, 0);
		matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
		matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f + 90));
		int scale = 25;

		matrixStack.mulPose(Axis.XP.rotationDegrees(-90f));
		matrixStack.translate(0, 0, -38);
		blockElement(AllPartialModels.SHAFT_HALF)
			.rotateBlock(0, 0, getCurrentAngle())
			.scale(scale)
			.render(graphics);

		matrixStack.translate(0, 0, 38);
		matrixStack.mulPose(Axis.XP.rotationDegrees(90f));

		blockElement(VintageBlocks.LASER.getDefaultState())
			.scale(scale)
			.render(graphics);

		blockElement(VintagePartialModels.LASER_HEAD)
			.atLocal(Math.sin(getCurrentPos()) / 4, 0, -Math.cos(getCurrentPos()) / 4)
			.scale(scale)
			.render(graphics);

		blockElement(VintagePartialModels.LASER_BEAM)
				.atLocal(Math.sin(getCurrentPos()) / 4, 0, -Math.cos(getCurrentPos()) / 4)
				.scale(scale)
				.render(graphics);

		matrixStack.popPose();
	}

	public static float getCurrentPos() {
		return (AnimationTickHolder.getRenderTime()) % 360;
	}

}
