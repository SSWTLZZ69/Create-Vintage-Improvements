package com.negodya1.vintageimprovements.content.kinetics.helve_hammer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.negodya1.vintageimprovements.VintageItems;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class HelveItemsRenderer extends SmartBlockEntityRenderer<HelveBlockEntity> {

	public HelveItemsRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public boolean shouldRenderOffScreen(HelveBlockEntity be) {
		return true;
	}

	@Override
	protected void renderSafe(HelveBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
		int light, int overlay) {

		renderItems(be, ms, buffer, light, overlay);
	}

	protected void renderItems(HelveBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		boolean input = !be.inputInv.getStackInSlot(0).isEmpty();
		boolean output = !be.outputInv.getStackInSlot(0).isEmpty();

		if (!input && !output) return;

		int j = 0;

		if (input) {
			for (int i = 0; i < be.inputInv.getSlots(); i++) {
				ItemStack stack = be.inputInv.getStackInSlot(i);
				if (stack.isEmpty())
					continue;

				ms.pushPose();

				ItemRenderer itemRenderer = Minecraft.getInstance()
						.getItemRenderer();

				ms.translate(.5, 0, .5);

				ms.mulPose(Axis.YP.rotationDegrees(j * 60));
				ms.translate(-.20, 0, 0);

				ms.scale(.33f, .33f, .33f);
				ms.mulPose(Axis.XP.rotationDegrees(90));
				itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, ms, buffer, be.getLevel(), 0);
				j++;

				ms.popPose();
			}
		}

		for (int i = 0; i < be.getBlockedSlots(); i++) {
			ItemStack stack = new ItemStack(VintageItems.HELVE_HAMMER_SLOT_COVER.get(), 1);
			ms.pushPose();
			ItemRenderer itemRenderer = Minecraft.getInstance()
					.getItemRenderer();
			ms.translate(.5, 0, .5);
			ms.mulPose(Axis.YP.rotationDegrees(j * 60));
			ms.translate(-.20, 0, 0);
			ms.scale(.33f, .33f, .33f);
			ms.mulPose(Axis.XP.rotationDegrees(90));
			itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, ms, buffer, be.getLevel(), 0);
			j++;
			ms.popPose();
		}

		if (output) {
			for (int i = 0; i < be.outputInv.getSlots(); i++) {
				ItemStack stack = be.outputInv.getStackInSlot(i);
				if (stack.isEmpty())
					continue;

				ms.pushPose();

				ItemRenderer itemRenderer = Minecraft.getInstance()
						.getItemRenderer();

				ms.translate(.5, 0, .5);

				ms.mulPose(Axis.YP.rotationDegrees(j * 60));
				ms.translate(-.35, 0, 0);

				ms.scale(.33f, .33f, .33f);
				ms.mulPose(Axis.XP.rotationDegrees(90));
				itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, ms, buffer, be.getLevel(), 0);
				j++;

				ms.popPose();
			}
		}
	}

}
