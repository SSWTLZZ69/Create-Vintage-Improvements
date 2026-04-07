package com.negodya1.vintageimprovements.compat.jei.category;

import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedCentrifuge;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedLaser;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugationRecipe;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.HammeringRecipe;
import com.negodya1.vintageimprovements.content.kinetics.laser.LaserCuttingRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class LaserCuttingCategory extends CreateRecipeCategory<LaserCuttingRecipe> {

	private final AnimatedLaser laser = new AnimatedLaser();

	public LaserCuttingCategory(Info<LaserCuttingRecipe> info) {
		super(info);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, LaserCuttingRecipe recipe, IFocusGroup focuses) {
		List<Ingredient> inputs = recipe.getIngredients();
		int i = 0;
		for (Ingredient ingredient : inputs) {
			int xOffset = i * 19;
			builder
					.addSlot(RecipeIngredientRole.INPUT, 4 + xOffset, 36)
					.setBackground(getRenderedSlot(), -1, -1)
					.addIngredients(ingredient);
			i++;
		}

		i = 0;
		List<ProcessingOutput> results = recipe.getRollableResults();

		for (ProcessingOutput result : results) {
			builder
					.addSlot(RecipeIngredientRole.OUTPUT, 148 - (10 * results.size()) + 19 * i, 48)
					.setBackground(getRenderedSlot(result), -1, -1)
					.addItemStack(result.getStack())
					.addRichTooltipCallback(addStochasticTooltip(result));
			i++;
		}
	}

	@Override
	public void draw(LaserCuttingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 132, 28);
		AllGuiTextures.JEI_LONG_ARROW.render(graphics, 2, 55);

		laser.draw(graphics, 86, 22);

		graphics.drawCenteredString(Minecraft.getInstance().font,
				Component.translatable(VintageImprovements.MODID + ".jei.text.energy")
						.append(" " + recipe.getEnergy() + "fe"),
				88, 75, 0xFFFFFF);
	}

}

