package com.negodya1.vintageimprovements.content.kinetics.vibration;

import com.negodya1.vintageimprovements.VintageRecipes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class LeavesVibratingRecipe extends ProcessingRecipe<RecipeWrapper, ProcessingRecipeParams> {
	public LeavesVibratingRecipe(ProcessingRecipeParams params) {
		super(VintageRecipes.LEAVES_VIBRATING, params);
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
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 4;
	}

	@Override
	protected boolean canSpecifyDuration() {
		return true;
	}
}

