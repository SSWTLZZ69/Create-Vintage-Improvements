package com.negodya1.vintageimprovements.content.kinetics.grinder;

import com.mojang.serialization.MapCodec;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageLang;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyPolishing;
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
public class PolishingRecipe extends ProcessingRecipe<RecipeWrapper, PolishingRecipeParams> implements IAssemblyRecipe {

	final int speedLimits;
	final boolean fragile;

	public PolishingRecipe(PolishingRecipeParams params) {
		super(VintageRecipes.POLISHING, params);
		speedLimits = params.speedLimits();
		fragile = params.fragile();
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

	@FunctionalInterface
	public interface Factory<R extends PolishingRecipe> extends ProcessingRecipe.Factory<PolishingRecipeParams, R> {
		R create(PolishingRecipeParams params);
	}

	public static class Serializer<R extends PolishingRecipe> implements RecipeSerializer<R> {
		private final MapCodec<R> codec;
		private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

		public Serializer(Factory<R> factory) {
			this.codec = ProcessingRecipe.codec(factory, PolishingRecipeParams.CODEC);
			this.streamCodec = ProcessingRecipe.streamCodec(factory, PolishingRecipeParams.STREAM_CODEC);
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

