package com.negodya1.vintageimprovements.compat.jei.category;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedVibratingTable;
import com.negodya1.vintageimprovements.content.kinetics.vibration.VibratingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;

@ParametersAreNonnullByDefault
public class VibratingCategory extends CreateRecipeCategory<VibratingRecipe> {

	private final AnimatedVibratingTable table = new AnimatedVibratingTable();

	public VibratingCategory(Info<VibratingRecipe> info) {
		super(info);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, VibratingRecipe recipe, IFocusGroup focuses) {
		builder
				.addSlot(RecipeIngredientRole.INPUT, 15, 9)
				.setBackground(getRenderedSlot(), -1, -1)
				.addIngredients(recipe.getIngredients().get(0));

		List<ProcessingOutput> results = recipe.getRollableResults();
		int globalYOffset = (results.size() / 3 - 1) * 19 / 2;
		int i = 0;
		for (ProcessingOutput output : results) {
			int xOffset = (i % 3) * 19;
			int yOffset = (i / 3) * 19;

			builder
					.addSlot(RecipeIngredientRole.OUTPUT, 104 + xOffset, 25 + yOffset - globalYOffset)
					.setBackground(getRenderedSlot(output), -1, -1)
					.addItemStack(output.getStack())
					.addRichTooltipCallback(addStochasticTooltip(output));

			i++;
		}
	}

	@Override
	public void draw(VibratingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		List<ProcessingOutput> results = recipe.getRollableResults();
		int yOffset = results.size() / 3 * 19 / 2;
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 98, 19 - yOffset);
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 43, 0);

		AllGuiTextures.JEI_SHADOW.render(graphics, 48 - 17, 35 + 13);

		table.draw(graphics, 48, 35);
	}

	@Override
	public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<VibratingRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		if (mouseX > 39 && mouseX < 73 && mouseY > 19 && mouseY < 57) {
			int duration = recipe.value().getProcessingDuration();
			if (duration == 0) duration = 100;
			tooltip.add(Component.translatable("vintageimprovements.jei.text.processing_duration", duration));
		}
	}
}
