package com.negodya1.vintageimprovements.content.kinetics.grinder;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonObject;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageLang;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyPolishing;
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
public class PolishingRecipe extends ProcessingRecipe<RecipeWrapper> implements IAssemblyRecipe {

	int speedLimits;
	boolean fragile;
	public PolishingRecipe(ProcessingRecipeParams params) {
		super(VintageRecipes.POLISHING, params);
		speedLimits = 0;
		fragile = false;
	}

	@Override
	public void readAdditional(JsonObject json) {
		if (json.has("speedLimits")) speedLimits = json.get("speedLimits").getAsInt();
		else if (json.has("speed_limits")) speedLimits = json.get("speed_limits").getAsInt();
		else speedLimits = 0;

		if (json.has("fragile")) fragile = json.get("fragile").getAsBoolean();
		else fragile = false;
	}

	@Override
	public void readAdditional(FriendlyByteBuf buffer) {
		speedLimits = buffer.readInt();
		fragile = buffer.readBoolean();
	}

	@Override
	public void writeAdditional(JsonObject json) {
		json.addProperty("speedLimits", speedLimits);
		json.addProperty("fragile", fragile);
	}

	@Override
	public void writeAdditional(FriendlyByteBuf buffer) {
		buffer.writeInt(speedLimits);
		buffer.writeBoolean(fragile);
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

	@Override
	public void addAssemblyIngredients(List<Ingredient> list) {}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Component getDescriptionForAssembly() {
		MutableComponent result = VintageLang.translateDirect("recipe.assembly.polishing");
		result.append(" ").append(VintageLang.translateDirect("recipe.assembly.on")).append(" ");
		switch (speedLimits) {
			case 1 -> result.append(VintageLang.translateDirect("recipe.assembly.low"));
			case 2 -> result.append(VintageLang.translateDirect("recipe.assembly.medium"));
			case 3 -> result.append(VintageLang.translateDirect("recipe.assembly.high"));
			default -> result.append(VintageLang.translateDirect("recipe.assembly.any"));
		}
		result.append(" ").append(VintageLang.translateDirect("recipe.assembly.speed"));

		return result;
	}
	
	@Override
	public void addRequiredMachines(Set<ItemLike> list) {
		list.add(VintageBlocks.BELT_GRINDER.get());
	}
	
	@Override
	public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
		return () -> AssemblyPolishing::new;
	}

	public int getSpeedLimits() {return speedLimits;}

	public boolean isFragile() {return fragile;}

}
