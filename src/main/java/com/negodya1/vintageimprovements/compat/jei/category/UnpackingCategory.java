package com.negodya1.vintageimprovements.compat.jei.category;

import javax.annotation.ParametersAreNonnullByDefault;

import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedVibratingTable;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.CraftingRecipe;

@ParametersAreNonnullByDefault
public class UnpackingCategory extends CreateRecipeCategory<CraftingRecipe> {

	private final AnimatedVibratingTable table = new AnimatedVibratingTable();

	public UnpackingCategory(Info<CraftingRecipe> info) {
		super(info);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CraftingRecipe recipe, IFocusGroup focuses) {
		builder
				.addSlot(RecipeIngredientRole.INPUT, 15, 9)
				.setBackground(getRenderedSlot(), -1, -1)
				.addIngredients(recipe.getIngredients().get(0));

		ProcessingOutput result = new ProcessingOutput(recipe.getResultItem(RegistryAccess.EMPTY).copy(), 1.0f);

		builder
				.addSlot(RecipeIngredientRole.OUTPUT, 139, 27)
				.setBackground(getRenderedSlot(result), -1, -1)
				.addItemStack(result.getStack())
				.addRichTooltipCallback(addStochasticTooltip(result));
	}

	@Override
	public void draw(CraftingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_ARROW.render(graphics, 85, 32);
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 43, 4);

		AllGuiTextures.JEI_SHADOW.render(graphics, 48 - 17, 35 + 13);

		table.draw(graphics, 48, 35);
	}

}
