package com.negodya1.vintageimprovements.compat.jei.category;

import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedVibratingTable;
import com.negodya1.vintageimprovements.content.kinetics.vibration.LeavesVibratingRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.CraftingRecipe;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class LeavesVibratingCategory extends CreateRecipeCategory<LeavesVibratingRecipe> {

	private final AnimatedVibratingTable table = new AnimatedVibratingTable();

	public LeavesVibratingCategory(Info<LeavesVibratingRecipe> info) {
		super(info);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, LeavesVibratingRecipe recipe, IFocusGroup focuses) {
		builder
				.addSlot(RecipeIngredientRole.INPUT, 15, 9)
				.setBackground(getRenderedSlot(), -1, -1)
				.addIngredients(recipe.getIngredients().get(0));
	}

	@Override
	public void draw(LeavesVibratingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 43, 4);

		AllGuiTextures.JEI_SHADOW.render(graphics, 48 - 17, 35 + 13);

		table.draw(graphics, 48, 35);

		graphics.drawString(Minecraft.getInstance().font,  Component.translatable(VintageImprovements.MODID + ".jei.text.leaves_vibrating.text1"), 87, 3, 0xFAFAFA);
		graphics.drawString(Minecraft.getInstance().font,  Component.translatable(VintageImprovements.MODID + ".jei.text.leaves_vibrating.text2"), 87, 14, 0xFAFAFA);
		graphics.drawString(Minecraft.getInstance().font,  Component.translatable(VintageImprovements.MODID + ".jei.text.leaves_vibrating.text3"), 87, 25, 0xFAFAFA);
		graphics.drawString(Minecraft.getInstance().font,  Component.translatable(VintageImprovements.MODID + ".jei.text.leaves_vibrating.text4"), 87, 36, 0xFAFAFA);
		graphics.drawString(Minecraft.getInstance().font,  Component.translatable(VintageImprovements.MODID + ".jei.text.leaves_vibrating.text5"), 87, 47, 0xFAFAFA);
		graphics.drawString(Minecraft.getInstance().font,  Component.translatable(VintageImprovements.MODID + ".jei.text.leaves_vibrating.text6"), 87, 58, 0xFAFAFA);
	}

	@Override
	public List<Component> getTooltipStrings(LeavesVibratingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		List<Component> tooltip = new ArrayList<>();
		if (mouseX > 39 && mouseX < 73 && mouseY > 19 && mouseY < 57) {
			int duration = 100;
			tooltip.add(Component.translatable("vintageimprovements.jei.text.processing_duration", duration));
		}
		return tooltip;
	}
}
