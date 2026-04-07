package com.negodya1.vintageimprovements.content.kinetics.centrifuge;

import com.mojang.serialization.MapCodec;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageLang;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyCentrifugation;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import net.createmod.catnip.data.Iterate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class CentrifugationRecipe extends ProcessingRecipe<RecipeInput, CentrifugationRecipeParams> implements IAssemblyRecipe {

	private final int minimalRPM;

	public static boolean match(CentrifugeBlockEntity centrifuge, Recipe<?> recipe) {
		return apply(centrifuge, recipe, true);
	}

	public static boolean apply(CentrifugeBlockEntity centrifuge, Recipe<?> recipe) {
		return apply(centrifuge, recipe, false);
	}

	private static boolean apply(CentrifugeBlockEntity centrifuge, Recipe<?> recipe, boolean test) {
		IItemHandlerModifiable availableItems = centrifuge.capability;
		IFluidHandler availableFluids = centrifuge.fluidCapability;

		if (availableItems == null || availableFluids == null)
			return false;

		List<ItemStack> recipeOutputItems = new ArrayList<>();
		List<FluidStack> recipeOutputFluids = new ArrayList<>();

		List<Ingredient> ingredients = new LinkedList<>(recipe.getIngredients());
		List<SizedFluidIngredient> fluidIngredients = ((CentrifugationRecipe) recipe).getFluidIngredients();

		for (boolean simulate : Iterate.trueAndFalse) {
			if (!simulate && test)
				return true;

			int[] extractedItemsFromSlot = new int[availableItems.getSlots()];
			int[] extractedFluidsFromTank = new int[availableFluids.getTanks()];

			Ingredients: for (int i = 0; i < ingredients.size(); i++) {
				Ingredient ingredient = ingredients.get(i);

				for (int slot = 0; slot < availableItems.getSlots(); slot++) {
					if (simulate && availableItems.getStackInSlot(slot).getCount() <= extractedItemsFromSlot[slot])
						continue;
					ItemStack extracted = availableItems.getStackInSlot(slot);

					if (!ingredient.test(extracted))
						continue;
					if (!simulate)
						extracted.shrink(1);

					extractedItemsFromSlot[slot]++;
					continue Ingredients;
				}

				return false;
			}

			boolean fluidsAffected = false;
			FluidIngredients: for (int i = 0; i < fluidIngredients.size(); i++) {
				SizedFluidIngredient SizedFluidIngredient = fluidIngredients.get(i);
				int amountRequired = SizedFluidIngredient.amount();

				for (int tank = 0; tank < availableFluids.getTanks(); tank++) {
					FluidStack fluidStack = availableFluids.getFluidInTank(tank);
					if (simulate && fluidStack.getAmount() <= extractedFluidsFromTank[tank])
						continue;
					if (!SizedFluidIngredient.test(fluidStack))
						continue;
					int drainedAmount = Math.min(amountRequired, fluidStack.getAmount());
					if (!simulate) {
						fluidStack.shrink(drainedAmount);
						fluidsAffected = true;
					}
					amountRequired -= drainedAmount;
					if (amountRequired != 0)
						continue;
					extractedFluidsFromTank[tank] += drainedAmount;
					continue FluidIngredients;
				}

				return false;
			}

			if (fluidsAffected) {
				centrifuge.getBehaviour(SmartFluidTankBehaviour.INPUT).forEach(TankSegment::onFluidStackChanged);
				centrifuge.getBehaviour(SmartFluidTankBehaviour.OUTPUT).forEach(TankSegment::onFluidStackChanged);
			}

			if (simulate) {
				if (recipe instanceof CentrifugationRecipe centrifugeRecipe) {
					RandomSource random = centrifuge.getLevel() != null ? centrifuge.getLevel().random : RandomSource.create();
					recipeOutputItems.addAll(centrifugeRecipe.rollResults(random));
					recipeOutputFluids.addAll(centrifugeRecipe.getFluidResults());
				}
			}

			if (!centrifuge.acceptOutputs(recipeOutputItems, recipeOutputFluids, simulate))
				return false;
		}

		return true;
	}

	public CentrifugationRecipe(CentrifugationRecipeParams params) {
		super(VintageRecipes.CENTRIFUGATION, params);
		minimalRPM = params.minimalRPM();
	}

	@Override
	protected int getMaxInputCount() {
		return 9;
	}

	@Override
	protected int getMaxOutputCount() {
		return 4;
	}

	@Override
	protected int getMaxFluidInputCount() {
		return 2;
	}

	@Override
	protected int getMaxFluidOutputCount() {
		return 2;
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
		MutableComponent result = VintageLang.translateDirect("recipe.assembly.centrifugation");
		if (ingredients.size() > 1 || !fluidIngredients.isEmpty())
			result.append(" ").append(VintageLang.translateDirect("recipe.assembly.with")).append(" ");
		else
			return result;
		if (ingredients.size() > 1) {
			if (ingredients.get(1).getItems().length > 0)
				result.append(ingredients.get(1).getItems()[0].getItem().getDescription());
		} else if (!fluidIngredients.isEmpty()) {
			if (fluidIngredients.get(0).getFluids().length != 0)
				result.append(fluidIngredients.get(0).getFluids()[0].getDisplayName());
		}

		if (ingredients.size() > 2) {
			for (int i = 2; i < ingredients.size() - 1; i++)
				if (ingredients.get(i).getItems().length > 0)
					result.append(", ").append(ingredients.get(i).getItems()[0].getItem().getDescription());
			if (ingredients.get(ingredients.size() - 1).getItems().length > 0) {
				if (fluidIngredients.isEmpty())
					result.append(" ").append(VintageLang.translateDirect("recipe.assembly.and").append(" ")
							.append(ingredients.get(ingredients.size() - 1).getItems()[0].getItem().getDescription()));
				else
					result.append(", ").append(ingredients.get(ingredients.size() - 1).getItems()[0].getItem().getDescription());
			}
		}

		int startNum = ingredients.size() > 1 ? 0 : 1;

		if (fluidIngredients.size() > startNum) {
			for (int i = startNum; i < fluidIngredients.size() - 1; i++)
				if (fluidIngredients.get(i).getFluids().length != 0)
					result.append(", ").append(fluidIngredients.get(i).getFluids()[0].getDisplayName());
			if (fluidIngredients.get(fluidIngredients.size() - 1).getFluids().length != 0)
				result.append(" ").append(VintageLang.translateDirect("recipe.assembly.and").append(" ")
						.append(fluidIngredients.get(fluidIngredients.size() - 1).getFluids()[0].getDisplayName()));

		}

		return result;
	}

	@Override
	public void addRequiredMachines(Set<ItemLike> list) {
		list.add(VintageBlocks.CENTRIFUGE.get());
	}

	@Override
	public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
		return () -> AssemblyCentrifugation::new;
	}

	public int getMinimalRPM() {
		return minimalRPM;
	}

	@Override
	public boolean matches(RecipeInput container, Level level) {
		return false;
	}

	@FunctionalInterface
	public interface Factory<R extends CentrifugationRecipe> extends ProcessingRecipe.Factory<CentrifugationRecipeParams, R> {
		R create(CentrifugationRecipeParams params);
	}

	public static class Serializer<R extends CentrifugationRecipe> implements RecipeSerializer<R> {
		private final MapCodec<R> codec;
		private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

		public Serializer(Factory<R> factory) {
			this.codec = ProcessingRecipe.codec(factory, CentrifugationRecipeParams.CODEC);
			this.streamCodec = ProcessingRecipe.streamCodec(factory, CentrifugationRecipeParams.STREAM_CODEC);
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
