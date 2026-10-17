package com.negodya1.vintageimprovements.compat.jei.category;

import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedVibratingTable;
import com.negodya1.vintageimprovements.content.kinetics.vibration.LeavesVibratingRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class LeavesVibratingCategory extends CreateRecipeCategory<LeavesVibratingRecipe> {

	private final AnimatedVibratingTable table = new AnimatedVibratingTable();

	public LeavesVibratingCategory(Info<LeavesVibratingRecipe> info) {
		super(info);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, LeavesVibratingRecipe recipe, IFocusGroup focuses) {
		builder
				.addSlot(RecipeIngredientRole.INPUT, 15, 9)
				.setBackground(getRenderedSlot(), -1, -1)
				.addIngredients(recipe.getIngredients().get(0));
		List<ProcessingOutput> results = recipe.getRollableResults();
		int globalYOffset = (results.size() / 4 - 1) * 19 / 2;
		int i = 0;
		for (ProcessingOutput output : results) {
			int xOffset = (i % 4) * 19;
			int yOffset = (i / 4) * 19;

			builder
					.addSlot(RecipeIngredientRole.OUTPUT, 88 + xOffset, 64 + yOffset - globalYOffset)
					.setBackground(getRenderedSlot(output), -1, -1)
					.addItemStack(output.getStack())
					.addRichTooltipCallback(addStochasticTooltip(output));

			i++;
		}
	}

	@Override
	public void draw(LeavesVibratingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 43, 4);

		AllGuiTextures.JEI_SHADOW.render(graphics, 48 - 17, 35 + 13);

		table.draw(graphics, 48, 35);

		graphics.drawString(Minecraft.getInstance().font,  Component.translatable(VintageImprovements.MODID + ".jei.text.leaves_vibrating.text1"), 87, 3, 0xFAFAFA);
		graphics.drawString(Minecraft.getInstance().font,  Component.translatable(VintageImprovements.MODID + ".jei.text.leaves_vibrating.text2"), 87, 14, 0xFAFAFA);
		graphics.drawString(Minecraft.getInstance().font,  Component.translatable(VintageImprovements.MODID + ".jei.text.leaves_vibrating.text3"), 87, 25, 0xFAFAFA);
		graphics.drawString(Minecraft.getInstance().font,  Component.translatable(VintageImprovements.MODID + ".jei.text.leaves_vibrating.text4"), 87, 36, 0xFAFAFA);
		graphics.drawString(Minecraft.getInstance().font,  Component.translatable(VintageImprovements.MODID + ".jei.text.leaves_vibrating.text5"), 87, 47, 0xFAFAFA);
		graphics.drawString(Minecraft.getInstance().font,  Component.translatable(VintageImprovements.MODID + ".jei.text.leaves_vibrating.text6"), 87, 58, 0xFAFAFA);
		if (recipe.getRollableResults().size() == 0) {return;}
		graphics.drawString(Minecraft.getInstance().font,  Component.translatable(VintageImprovements.MODID + ".jei.text.leaves_vibrating.text7"), 15, 74, 0xFAFAFA);
	}

	@Override
	public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<LeavesVibratingRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		if (mouseX > 39 && mouseX < 73 && mouseY > 19 && mouseY < 57) {
			int duration = 100;
			tooltip.add(Component.translatable("vintageimprovements.jei.text.processing_duration", duration));
		}
	}
}
