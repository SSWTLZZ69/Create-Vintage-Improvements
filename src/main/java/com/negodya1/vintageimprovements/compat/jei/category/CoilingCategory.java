package com.negodya1.vintageimprovements.compat.jei.category;

import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedCoiling;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugationRecipe;
import com.negodya1.vintageimprovements.content.kinetics.coiling.CoilingRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class CoilingCategory extends CreateRecipeCategory<CoilingRecipe> {

	private final AnimatedCoiling coiling = new AnimatedCoiling();

	public CoilingCategory(Info<CoilingRecipe> info) {
		super(info);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CoilingRecipe recipe, IFocusGroup focuses) {
		builder
				.addSlot(RecipeIngredientRole.INPUT, 44, 5)
				.setBackground(getRenderedSlot(), -1, -1)
				.addIngredients(recipe.getIngredients().get(0));

		List<ProcessingOutput> results = recipe.getRollableResults();
		int i = 0;
		for (ProcessingOutput output : results) {
			int xOffset = i % 2 == 0 ? 0 : 19;
			int yOffset = (i / 2) * -19;
			builder
					.addSlot(RecipeIngredientRole.OUTPUT, 118 + xOffset, 48 + yOffset)
					.setBackground(getRenderedSlot(output), -1, -1)
					.addItemStack(output.getStack())
					.addTooltipCallback(addStochasticTooltip(output));
			i++;
		}
	}

	@Override
	public void draw(CoilingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 70, 6);
		AllGuiTextures.JEI_SHADOW.render(graphics, 72 - 17, 42 + 13);

		coiling.draw(graphics, 72, 42);
	}

	@Override
	public List<Component> getTooltipStrings(CoilingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		List<Component> tooltip = new ArrayList<>();
		if (mouseX > 63 && mouseX < 97 && mouseY > 33 && mouseY < 64) {
			int duration = recipe.getProcessingDuration();
			if (duration == 0) duration = 50;
			tooltip.add(Component.translatable("vintageimprovements.jei.text.processing_duration", duration));
		}
		return tooltip;
	}
}
