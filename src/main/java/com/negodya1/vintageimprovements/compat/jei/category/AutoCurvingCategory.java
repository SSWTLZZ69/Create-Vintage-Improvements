package com.negodya1.vintageimprovements.compat.jei.category;

import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintageItems;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedCurvingPress;
import com.negodya1.vintageimprovements.content.kinetics.curving_press.CurvingPressBlockEntity;
import com.negodya1.vintageimprovements.infrastructure.config.VintageConfig;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AutoCurvingCategory extends CreateRecipeCategory<CraftingRecipe> {

	private final AnimatedCurvingPress press = new AnimatedCurvingPress();

	public AutoCurvingCategory(Info<CraftingRecipe> info) {
		super(info);
	}

	static int getRecipeMode(CraftingRecipe recipe) {
		if (CurvingPressBlockEntity.canCurve(recipe, 1)) return 1;
		if (CurvingPressBlockEntity.canCurve(recipe, 2)) return 2;
		if (CurvingPressBlockEntity.canCurve(recipe, 3)) return 3;
		if (CurvingPressBlockEntity.canCurve(recipe, 4)) return 4;

		return 0;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CraftingRecipe recipe, IFocusGroup focuses) {
		int mode = getRecipeMode(recipe);

		ItemStack stack;

		switch (mode) {
			case 2 -> stack = new ItemStack(VintageItems.CONCAVE_CURVING_HEAD.get());
			case 3 -> stack = new ItemStack(VintageItems.W_SHAPED_CURVING_HEAD.get());
			case 4 -> stack = new ItemStack(VintageItems.V_SHAPED_CURVING_HEAD.get());
			default -> stack = new ItemStack(VintageItems.CONVEX_CURVING_HEAD.get());
		}

		builder.addSlot(RecipeIngredientRole.INPUT, 4, 28)
				.setBackground(getRenderedSlot(), -1, -1)
				.addItemStack(stack);

		if (recipe.getResultItem(RegistryAccess.EMPTY).getCount() >= 3) {
			builder.addSlot(RecipeIngredientRole.INPUT, 27, 51)
					.setBackground(getRenderedSlot(), -1, -1)
					.addIngredients(recipe.getIngredients().get(mode == 3 ? 0 : mode < 3 ? mode - 1 : 3));

			ProcessingOutput output = new ProcessingOutput(new ItemStack(recipe.getResultItem(RegistryAccess.EMPTY).getItem()), 1);

			builder.addSlot(RecipeIngredientRole.OUTPUT, 131, 50)
					.setBackground(getRenderedSlot(output), -1, -1)
					.addItemStack(output.getStack())
					.addTooltipCallback(addStochasticTooltip(output));
		}
		else {
			builder.addSlot(RecipeIngredientRole.INPUT, 27, 51)
					.setBackground(getRenderedSlot(), -1, -1)
					.addItemStack(new ItemStack(recipe.getIngredients().get(mode < 3 ? mode - 1 : mode == 3 ? 0 : 3).getItems()[0].getItem(), 3));

			ProcessingOutput output = new ProcessingOutput(recipe.getResultItem(RegistryAccess.EMPTY), 1);

			builder.addSlot(RecipeIngredientRole.OUTPUT, 131, 50)
					.setBackground(getRenderedSlot(output), -1, -1)
					.addItemStack(output.getStack())
					.addTooltipCallback(addStochasticTooltip(output));
		}

	}

	@Override
	public void draw(CraftingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_SHADOW.render(graphics, 61, 41);
		AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 54);
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 23, 32);

		press.draw(graphics, getBackground().getWidth() / 2 - 17, 22, getRecipeMode(recipe));

		if (VintageConfig.server().recipes.damageHeadAfterAutoCurvingRecipe.get() > 0)
			graphics.drawString(Minecraft.getInstance().font,
					Component.translatable(VintageImprovements.MODID + ".jei.text.curving_head_damage").append( ": "
							+ VintageConfig.server().recipes.damageHeadAfterAutoCurvingRecipe.get()),
					40, 75, 0xFFFFFF);
	}

}
