package com.negodya1.vintageimprovements.content.kinetics.laser;

import com.mojang.serialization.MapCodec;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintageLang;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyLaserCutting;
import com.negodya1.vintageimprovements.infrastructure.config.VintageConfig;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class LaserCuttingRecipe extends ProcessingRecipe<RecipeWrapper, LaserCuttingRecipeParams> implements IAssemblyRecipe {

	private final int energy;
	private final int maxChargeRate;

	public LaserCuttingRecipe(LaserCuttingRecipeParams params) {
		super(VintageRecipes.LASER_CUTTING, params);
		energy = params.energy();
		maxChargeRate = params.maxChargeRate();
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

	public int getEnergy() {
		return energy;
	}

	public int getMaxChargeRate() {
		return maxChargeRate;
	}

	@FunctionalInterface
	public interface Factory<R extends LaserCuttingRecipe> extends ProcessingRecipe.Factory<LaserCuttingRecipeParams, R> {
		R create(LaserCuttingRecipeParams params);
	}

	public static class Serializer<R extends LaserCuttingRecipe> implements RecipeSerializer<R> {
		private final MapCodec<R> codec;
		private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

		public Serializer(Factory<R> factory) {
			this.codec = ProcessingRecipe.codec(factory, LaserCuttingRecipeParams.CODEC);
			this.streamCodec = ProcessingRecipe.streamCodec(factory, LaserCuttingRecipeParams.STREAM_CODEC);
		}

		@Override
		public MapCodec<R> codec() {
			return codec;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
			return streamCodec;
		}
	}
}

