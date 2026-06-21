package com.negodya1.vintageimprovements.content.kinetics.helve_hammer;

import com.mojang.serialization.MapCodec;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageLang;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyHammering;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import net.createmod.catnip.data.Iterate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.*;
import java.util.function.Supplier;

public class HammeringRecipe extends ProcessingRecipe<RecipeInput, HammeringRecipeParams> implements IAssemblyRecipe {

	final int hammerBlows;
	final Item anvilBlock;

	public HammeringRecipe(HammeringRecipeParams params) {
		super(VintageRecipes.HAMMERING, params);
		hammerBlows = params.hammerBlows();
		anvilBlock = params.anvilBlock();
	}

	public Item getAnvilBlock() {
		return anvilBlock;
	}

	public static boolean match(HelveBlockEntity centrifuge, Recipe<?> recipe) {
		return apply(centrifuge, recipe, true);
	}

	public static boolean apply(HelveBlockEntity centrifuge, Recipe<?> recipe) {
		return apply(centrifuge, recipe, false);
	}

	private static boolean apply(HelveBlockEntity centrifuge, Recipe<?> recipe, boolean test) {
		IItemHandlerModifiable availableItems = centrifuge.capability;

		if (availableItems == null)
			return false;

		if (recipe instanceof HammeringRecipe hammeringRecipe)
			if (!centrifuge.anvilBlock.asItem().getDefaultInstance().is(hammeringRecipe.anvilBlock))
				return false;

		List<ItemStack> recipeOutputItems = new ArrayList<>();
		List<Ingredient> ingredients = new LinkedList<>(recipe.getIngredients());

		for (boolean simulate : Iterate.trueAndFalse) {
			if (!simulate && test)
				return true;

			int[] extractedItemsFromSlot = new int[availableItems.getSlots()];

			Ingredients: for (int i = 0; i < ingredients.size(); i++) {
				Ingredient ingredient = ingredients.get(i);

				for (int slot = 0; slot < availableItems.getSlots(); slot++) {
					if (simulate && availableItems.getStackInSlot(slot)
							.getCount() <= extractedItemsFromSlot[slot])
						continue;
					ItemStack extracted = availableItems.getStackInSlot(slot);

					if (!ingredient.test(extracted))
						continue;
					if (!simulate)
						extracted.shrink(1);

					extractedItemsFromSlot[slot]++;
					continue Ingredients;
				}

				// something wasn't found
				return false;
			}

			if (simulate) {
				if (recipe instanceof HammeringRecipe hammeringRecipe) {
					RandomSource random = centrifuge.getLevel() != null ? centrifuge.getLevel().random : RandomSource.create();
					recipeOutputItems.addAll(hammeringRecipe.rollResults(random));
				}
			}

			if (!centrifuge.acceptOutputs(recipeOutputItems, simulate))
				return false;
		}

		return true;
	}

	@Override
	protected int getMaxInputCount() {
		return 3;
	}

	@Override
	protected int getMaxOutputCount() {
		return 3;
	}


	@Override
	public void addAssemblyIngredients(List<Ingredient> list) {}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Component getDescriptionForAssembly() {
		MutableComponent result = VintageLang.translateDirect("recipe.assembly.hammering");
		if (ingredients.size() > 1) {
			if (ingredients.get(1).getItems().length > 0)
				result.append(" ").append(VintageLang.translateDirect("recipe.assembly.with")).append(" ").append(ingredients.get(1).getItems()[0].getItem().getDescription());

			if (ingredients.size() > 2) {
				for (int i = 2; i < ingredients.size() - 1; i++)
					if (ingredients.get(i).getItems().length > 0)
						result.append(", ").append(ingredients.get(i).getItems()[0].getItem().getDescription());
				if (ingredients.get(ingredients.size() - 1).getItems().length > 0)
					result.append(" ").append(VintageLang.translateDirect("recipe.assembly.and").append(" ").append(ingredients.get(ingredients.size() - 1).getItems()[0].getItem().getDescription()));
			}
		}

		if (anvilBlock != Blocks.AIR.asItem()) {
			result.append(" ").append(VintageLang.translateDirect("recipe.assembly.on")).append(" ")
					.append(Component.translatable(anvilBlock.getDescriptionId()));
		}

		return result;
	}

	@Override
	protected boolean canSpecifyDuration() {
		return true;
	}

	@Override
	public void addRequiredMachines(Set<ItemLike> list) {
		list.add(VintageBlocks.HELVE.get());
	}

	@Override
	public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
		return () -> AssemblyHammering::new;
	}

	public int getHammerBlows() {
		return hammerBlows;
	}

	@Override
	public boolean matches(RecipeInput container, Level level) {
		return false;
	}

	@FunctionalInterface
	public interface Factory<R extends HammeringRecipe> extends ProcessingRecipe.Factory<HammeringRecipeParams, R> {
		R create(HammeringRecipeParams params);
	}

	public static class Serializer<R extends HammeringRecipe> implements RecipeSerializer<R> {
		private final MapCodec<R> codec;
		private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

		public Serializer(Factory<R> factory) {
			this.codec = ProcessingRecipe.codec(factory, HammeringRecipeParams.CODEC);
			this.streamCodec = ProcessingRecipe.streamCodec(factory, HammeringRecipeParams.STREAM_CODEC);
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

