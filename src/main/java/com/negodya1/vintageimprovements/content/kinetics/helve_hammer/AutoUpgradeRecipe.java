package com.negodya1.vintageimprovements.content.kinetics.helve_hammer;

import com.negodya1.vintageimprovements.VintageRecipes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AutoUpgradeRecipe extends ProcessingRecipe<RecipeWrapper, ProcessingRecipeParams> {
	public AutoUpgradeRecipe(ProcessingRecipeParams params) {
		super(VintageRecipes.AUTO_UPGRADE, params);
	}

	@Override
	public boolean matches(RecipeWrapper inv, Level worldIn) {
		if (inv.isEmpty())
			return false;
		return ingredients.get(0)
			.test(inv.getItem(0));
	}

	@Override
	protected int getMaxInputCount() {
		return 3;
	}

	@Override
	protected int getMaxOutputCount() {
		return 1;
	}
}

