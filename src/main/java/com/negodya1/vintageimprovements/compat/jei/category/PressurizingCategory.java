package com.negodya1.vintageimprovements.compat.jei.category;

import javax.annotation.ParametersAreNonnullByDefault;

import com.negodya1.vintageimprovements.compat.jei.VintageRecipeUtil;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedVacuumChamber;
import com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber.PressurizingRecipe;
import com.negodya1.vintageimprovements.foundation.gui.VintageGuiTextures;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.basin.BasinRecipe;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class PressurizingCategory extends BasinCategory {

	private final AnimatedVacuumChamber vacuum = new AnimatedVacuumChamber();
	private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();

	public PressurizingCategory(Info<BasinRecipe> info) {
		super(info, true);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, BasinRecipe recipe, IFocusGroup focuses) {
		List<Pair<Ingredient, MutableInt>> condensedIngredients = ItemHelper.condenseIngredients(recipe.getIngredients());

		int size = condensedIngredients.size() + recipe.getFluidIngredients().size();
		// 原先仅计算输出流体槽位时剔除了副流体数量，其余槽位仍显示错误
		if (recipe instanceof PressurizingRecipe r) {
			size -= r.getSecondaryFluidInputs() > -1 ? 1 : 0;
		}
		int xOffset = size < 3 ? (3 - size) * 19 / 2 : 0;
		int i = 0;

		for (Pair<Ingredient, MutableInt> pair : condensedIngredients) {
			List<ItemStack> stacks = new ArrayList<>();
			for (ItemStack itemStack : pair.getFirst().getItems()) {
				ItemStack copy = itemStack.copy();
				copy.setCount(pair.getSecond().getValue());
				stacks.add(copy);
			}

			builder
					.addSlot(RecipeIngredientRole.INPUT, 21 + xOffset + (i % 3) * 19, 52 - (i / 3) * 19)
					.setBackground(getRenderedSlot(), -1, -1)
					.addItemStacks(stacks);
			i++;
		}

		int j = 0;
		for (FluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
			if (recipe instanceof PressurizingRecipe r && j == r.getSecondaryFluidInputs())
				CreateRecipeCategory.addFluidSlot(builder, 21, 14, fluidIngredient)
						.addRichTooltipCallback(VintageRecipeUtil.addTooltip("jei.text.secondary_fluid_ingredient"));
			else {
				CreateRecipeCategory.addFluidSlot(builder, 17 + xOffset + (i % 3) * 19, 52 - (i / 3) * 19, fluidIngredient);
				// 副流体不占据主原料的显示空间
				i++;
			}
			j++;
		}

		size = recipe.getRollableResults().size() + recipe.getFluidResults().size();
		// 同上
		if (recipe instanceof PressurizingRecipe r) {
			size -= r.getSecondaryFluidResults() > -1 ? 1 : 0;
		}
		i = 0;

		for (ProcessingOutput result : recipe.getRollableResults()) {
			int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
			int yPosition = -19 * (i / 2) + 52;

			builder
					.addSlot(RecipeIngredientRole.OUTPUT, xPosition, yPosition)
					.setBackground(getRenderedSlot(result), -1, -1)
					.addItemStack(result.getStack())
					.addRichTooltipCallback(addStochasticTooltip(result));
			i++;
		}

		j = 0;
		for (FluidStack fluidResult : recipe.getFluidResults()) {
			if (recipe instanceof PressurizingRecipe vRecipe) {
				int secondary = vRecipe.getSecondaryFluidResults();
				int xPosition;
				int yPosition;

				if (j == secondary) {
					xPosition = 140;
					yPosition = 2;
					CreateRecipeCategory.addFluidSlot(builder, xPosition, yPosition, fluidResult)
							.addRichTooltipCallback(VintageRecipeUtil.addTooltip("jei.text.secondary_fluid_result"));
				}
				else {
					xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
					yPosition = -19 * (i / 2) + 52;
					CreateRecipeCategory.addFluidSlot(builder, xPosition, yPosition, fluidResult);
					// 同上
					i++;
				}
				j++;
			}
		}

		HeatCondition requiredHeat = recipe.getRequiredHeat();
		if (!requiredHeat.testBlazeBurner(BlazeBurnerBlock.HeatLevel.NONE)) {
			builder
					.addSlot(RecipeIngredientRole.RENDER_ONLY, 134, 81)
					.addItemStack(AllBlocks.BLAZE_BURNER.asStack());
		}
		if (!requiredHeat.testBlazeBurner(BlazeBurnerBlock.HeatLevel.KINDLED)) {
			builder
					.addSlot(RecipeIngredientRole.CATALYST, 153, 81)
					.addItemStack(AllItems.BLAZE_CAKE.asStack());
		}
	}

	@Override
	public void draw(BasinRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		HeatCondition requiredHeat = recipe.getRequiredHeat();

		boolean noHeat = requiredHeat == HeatCondition.NONE;
		// 修复工作盆默认显示逻辑箭头错位的问题
		int size = recipe.getRollableResults().size() + recipe.getFluidResults().size();
		if (recipe instanceof PressurizingRecipe r) {
			size -= r.getSecondaryFluidResults() > -1 ? 1 : 0;
		}
		int vRows = (1 + size) / 2;

		if (size > 0 && vRows <= 2)
			AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 136, -19 * (vRows - 1) + 33);

		AllGuiTextures shadow = noHeat ? AllGuiTextures.JEI_SHADOW : AllGuiTextures.JEI_LIGHT;
		shadow.render(graphics, 81, 58 + (noHeat ? 10 : 30));

		AllGuiTextures heatBar = noHeat ? AllGuiTextures.JEI_NO_HEAT_BAR : AllGuiTextures.JEI_HEAT_BAR;
		heatBar.render(graphics, 4, 80);
		graphics.drawString(Minecraft.getInstance().font, CreateLang.translateDirect(requiredHeat.getTranslationKey()), 9,
				86, requiredHeat.getColor(), false);


		// 防呆检测
		if (recipe instanceof PressurizingRecipe vrecipe) {
			if (vrecipe.getSecondaryFluidResults() >= 0 && vrecipe.getFluidResults().size() > vrecipe.getSecondaryFluidResults())
				VintageGuiTextures.JEI_UP_TO_RIGHT_ARROW.render(graphics, 120, 2);
			if (vrecipe.getSecondaryFluidInputs() >= 0 && vrecipe.getFluidIngredients().size() > vrecipe.getSecondaryFluidInputs())
				AllGuiTextures.JEI_ARROW.render(graphics, 45, 18);
		}

		if (requiredHeat != HeatCondition.NONE)
			heater.withHeat(requiredHeat.visualizeAsBlazeBurner())
					.draw(graphics, getBackground().getWidth() / 2 + 3, 55);
		vacuum.draw(graphics, getBackground().getWidth() / 2 + 3, 34, true);
	}

	@Override
	public void getTooltip(ITooltipBuilder tooltip, BasinRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		if (mouseX > 90 && mouseX < 120 && mouseY > 9 && mouseY < 76) {
			int duration = recipe.getProcessingDuration();
			if (duration == 0) duration = 100;
			tooltip.add(Component.translatable("vintageimprovements.jei.text.processing_duration", duration));
		}
	}
}
