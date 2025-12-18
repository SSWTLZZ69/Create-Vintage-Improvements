package com.negodya1.vintageimprovements.compat.jei.category;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintageItems;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedCurvingPress;
import com.negodya1.vintageimprovements.content.kinetics.curving_press.CurvingRecipe;
import com.negodya1.vintageimprovements.infrastructure.config.VintageConfig;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.Pair;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.commons.lang3.mutable.MutableInt;

@ParametersAreNonnullByDefault
public class CurvingCategory extends CreateRecipeCategory<CurvingRecipe> {

	private final AnimatedCurvingPress press = new AnimatedCurvingPress();

	public CurvingCategory(Info<CurvingRecipe> info) {
		super(info);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CurvingRecipe recipe, IFocusGroup focuses) {
		ItemStack stack;

		switch (recipe.getMode()) {
			case 2 -> stack = new ItemStack(VintageItems.CONCAVE_CURVING_HEAD.get());
			case 3 -> stack = new ItemStack(VintageItems.W_SHAPED_CURVING_HEAD.get());
			case 4 -> stack = new ItemStack(VintageItems.V_SHAPED_CURVING_HEAD.get());
			case 5 -> stack = new ItemStack(recipe.getItemAsHead());
			default -> stack = new ItemStack(VintageItems.CONVEX_CURVING_HEAD.get());
		}

		builder.addSlot(RecipeIngredientRole.INPUT, 4, 28)
				.setBackground(getRenderedSlot(), -1, -1)
				.addItemStack(stack);

		List<Pair<Ingredient, MutableInt>> condensedIngredients = ItemHelper.condenseIngredients(recipe.getIngredients());

		for (Pair<Ingredient, MutableInt> pair : condensedIngredients) {
			List<ItemStack> stacks = new ArrayList<>();
			for (ItemStack itemStack : pair.getFirst().getItems()) {
				ItemStack copy = itemStack.copy();
				copy.setCount(pair.getSecond().getValue());
				stacks.add(copy);
			}

			builder.addSlot(RecipeIngredientRole.INPUT, 27, 51)
					.setBackground(getRenderedSlot(), -1, -1)
					.addItemStacks(stacks);
			break;
		}

		List<ProcessingOutput> results = recipe.getRollableResults();
		int i = 0;
		for (ProcessingOutput output : results) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 131 + 19 * i, 50)
					.setBackground(getRenderedSlot(output), -1, -1)
					.addItemStack(output.getStack())
					.addTooltipCallback(addStochasticTooltip(output));
			i++;
		}
	}

	@Override
	public void draw(CurvingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_SHADOW.render(graphics, 61, 41);
		AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 54);
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 23, 32);

		press.draw(graphics, getBackground().getWidth() / 2 - 17, 22, recipe.getMode());

		if (recipe.getHeadDamage() != 0) {
			if (recipe.getMode() == 5)
				if (!new ItemStack(recipe.getItemAsHead()).isDamageableItem()) return;
			if (recipe.getHeadDamage() < 0) {
				if (VintageConfig.server().recipes.damageHeadAfterAutoCurvingRecipe.get() == 0) return;
				graphics.drawCenteredString(Minecraft.getInstance().font,
						Component.translatable(VintageImprovements.MODID + ".jei.text.curving_head_damage").append( ": "
								+ VintageConfig.server().recipes.damageHeadAfterAutoCurvingRecipe.get()),
						88, 75, 0xFFFFFF);
			}
			else graphics.drawCenteredString(Minecraft.getInstance().font,
						Component.translatable(VintageImprovements.MODID + ".jei.text.curving_head_damage").append( ": "
								+ recipe.getHeadDamage()),
					88, 75, 0xFFFFFF);
		}
	}

}
