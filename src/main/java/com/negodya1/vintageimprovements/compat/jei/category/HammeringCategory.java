package com.negodya1.vintageimprovements.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedCentrifuge;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedHelve;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugationRecipe;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.HammeringRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class HammeringCategory extends CreateRecipeCategory<HammeringRecipe> {

	private final AnimatedHelve helve = new AnimatedHelve();

	public HammeringCategory(Info<HammeringRecipe> info) {
		super(info);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, HammeringRecipe recipe, IFocusGroup focuses) {
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

		if (!recipe.getAnvilBlock().getDefaultInstance().is(Items.AIR))
			builder.addSlot(RecipeIngredientRole.INPUT, 4, 14)
					.setBackground(getRenderedSlot(), -1, -1)
					.addItemStack(recipe.getAnvilBlock().getDefaultInstance());

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
	public void draw(HammeringRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 132, 28);
		AllGuiTextures.JEI_LONG_ARROW.render(graphics, 2, 55);

		if (!recipe.getAnvilBlock().getDefaultInstance().is(Items.AIR)) {
			helve.draw(graphics, 86, 6,2);
			helve.renderBlock(graphics, 86, 6, recipe.getAnvilBlock());
		}
		else helve.draw(graphics, 86, 6,1);

		graphics.drawCenteredString(Minecraft.getInstance().font,
				Component.translatable(VintageImprovements.MODID + ".jei.text.hammer_blows").append(" " + recipe.getHammerBlows()),
				88, 75, 0xFFFFFF);
	}

}
