package com.negodya1.vintageimprovements.compat.jei.category;

import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedCentrifuge;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugationRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class CentrifugationCategory extends CreateRecipeCategory<CentrifugationRecipe> {

	private final AnimatedCentrifuge centrifuge = new AnimatedCentrifuge();

	public CentrifugationCategory(Info<CentrifugationRecipe> info) {
		super(info);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CentrifugationRecipe recipe, IFocusGroup focuses) {

		int i = 0;
		for (Ingredient input : recipe.getIngredients()) {
			int xOffset = i % 3 * 19;
			int yOffset = (i / 3) * 19;
			builder
					.addSlot(RecipeIngredientRole.INPUT, 10 + xOffset, 5 + yOffset)
					.setBackground(getRenderedSlot(), -1, -1)
					.addIngredients(input);
			i++;
		}
		for (FluidIngredient input : recipe.getFluidIngredients()) {
			int xOffset = i % 3 * 19;
			int yOffset = (i / 3) * 19;
			builder
					.addSlot(RecipeIngredientRole.INPUT, 10 + xOffset, 5 + yOffset)
					.setBackground(getRenderedSlot(), -1, -1)
					.addIngredients(ForgeTypes.FLUID_STACK, withImprovedVisibility(input.getMatchingFluidStacks()))
					.addTooltipCallback(addFluidTooltip(input.getRequiredAmount()));
			i++;
		}


		List<ProcessingOutput> results = recipe.getRollableResults();
		i = 0;
		for (ProcessingOutput output : results) {
			int xOffset = i % 2 * 19;
			int yOffset = (i / 2) * 19;
			builder
					.addSlot(RecipeIngredientRole.OUTPUT, 128 + xOffset, 56 + yOffset)
					.setBackground(getRenderedSlot(output), -1, -1)
					.addItemStack(output.getStack())
					.addTooltipCallback(addStochasticTooltip(output));
			i++;
		}
		for (FluidStack output : recipe.getFluidResults()) {
			int xOffset = i % 2 * 19;
			int yOffset = (i / 2) * 19;
			builder
					.addSlot(RecipeIngredientRole.OUTPUT, 128 + xOffset, 56 + yOffset)
					.setBackground(getRenderedSlot(), -1, -1)
					.addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(output))
					.addTooltipCallback(addFluidTooltip(output.getAmount()));
			i++;
		}
	}

	@Override
	public void draw(CentrifugationRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 70, 6);
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 134, 36);
		AllGuiTextures.JEI_SHADOW.render(graphics, 56 - 17, 66 + 13);

		centrifuge.draw(graphics, 56, 66);

		graphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable(VintageImprovements.MODID + ".jei.text.minimal_rpm").append(" " + recipe.getMinimalRPM()),
				88, 103, 0xFFFF00);
	}

}
