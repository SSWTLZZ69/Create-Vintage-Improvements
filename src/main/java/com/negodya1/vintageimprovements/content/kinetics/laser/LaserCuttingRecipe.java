package com.negodya1.vintageimprovements.content.kinetics.laser;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonObject;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintageLang;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyLaserCutting;
import com.negodya1.vintageimprovements.infrastructure.config.VintageConfig;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;

import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.wrapper.RecipeWrapper;

@ParametersAreNonnullByDefault
public class LaserCuttingRecipe extends ProcessingRecipe<RecipeWrapper> implements IAssemblyRecipe {

	public int energy;
	public int maxChargeRate;

	public LaserCuttingRecipe(ProcessingRecipeParams params) {
		super(VintageRecipes.LASER_CUTTING, params);
		energy = 0;
		maxChargeRate = 0;
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
		return 2;
	}

	@Override
	public void addAssemblyIngredients(List<Ingredient> list) {}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Component getDescriptionForAssembly() {
		MutableComponent result = VintageLang.translateDirect("recipe.assembly.laser_cutting");
		if (VintageImprovements.useEnergy || VintageConfig.server().energy.forceEnergy.get())
			result.append(" ").append(VintageLang.translateDirect("recipe.assembly.with")).append(" " + energy).append("fe");

		return result;
	}
	
	@Override
	public void addRequiredMachines(Set<ItemLike> list) {
		list.add(VintageBlocks.LASER.get());
	}
	
	@Override
	public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
		return () -> AssemblyLaserCutting::new;
	}

	@Override
	public void readAdditional(JsonObject json) {
		if (json.has("energy")) energy = json.get("energy").getAsInt();
		if (json.has("maxChargeRate")) maxChargeRate = json.get("maxChargeRate").getAsInt();
	}

	@Override
	public void readAdditional(FriendlyByteBuf buffer) {
		energy = buffer.readInt();
		maxChargeRate = buffer.readInt();
	}

	@Override
	public void writeAdditional(JsonObject json) {
		json.addProperty("energy", energy);
		json.addProperty("maxChargeRate", maxChargeRate);
	}

	@Override
	public void writeAdditional(FriendlyByteBuf buffer) {
		buffer.writeInt(energy);
		buffer.writeInt(maxChargeRate);
	}

	public int getEnergy() {
		return energy;
	}

	public int getMaxChargeRate() {
		return maxChargeRate;
	}

}
