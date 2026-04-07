package com.negodya1.vintageimprovements.compat.jei.category;

import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedCentrifuge;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugationRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

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
		for (SizedFluidIngredient input : recipe.getFluidIngredients()) {
			int xOffset = i % 3 * 19;
			int yOffset = (i / 3) * 19;
			CreateRecipeCategory.addFluidSlot(builder, 10 + xOffset, 5 + yOffset * 16, input);
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
					.addRichTooltipCallback(addStochasticTooltip(output));
			i++;
		}
		for (FluidStack output : recipe.getFluidResults()) {
			int xOffset = i % 2 * 19;
			int yOffset = (i / 2) * 19;
			CreateRecipeCategory.addFluidSlot(builder, 128 + xOffset, 56 + yOffset, output);
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

	@Override
	public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<CentrifugationRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		if (mouseX > 23 && mouseX < 105 && mouseY > 47 && mouseY < 97) {
			int duration = recipe.value().getProcessingDuration();
			if (duration == 0) duration = 100;
			tooltip.add(Component.translatable("vintageimprovements.jei.text.processing_duration", duration));
		}
	}
}

