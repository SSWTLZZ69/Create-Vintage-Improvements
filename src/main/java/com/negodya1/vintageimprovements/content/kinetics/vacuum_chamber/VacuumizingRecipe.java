package com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber;

import com.google.common.base.Joiner;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageLang;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyVacuumizing;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import com.simibubi.create.foundation.recipe.DummyCraftingContainer;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.*;
import java.util.function.Supplier;

public class VacuumizingRecipe extends BasinRecipe implements IAssemblyRecipe {

	private final int secondaryFluidResults;
	private final int secondaryFluidInputs;

	public VacuumizingRecipe(VacuumizingRecipeParams params) {
		super(VintageRecipes.VACUUMIZING, params);
		secondaryFluidResults = params.secondaryFluidResults();
		secondaryFluidInputs = params.secondaryFluidInputs();
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
		MutableComponent result = VintageLang.translateDirect("recipe.assembly.vacuumizing");
		if (ingredients.size() > 1 || !fluidIngredients.isEmpty())
			result.append(" ").append(VintageLang.translateDirect("recipe.assembly.with")).append(" ");
		else return result;
		if (ingredients.size() > 1) {
			if (ingredients.get(1).getItems().length > 0)
				result.append(ingredients.get(1).getItems()[0].getItem().getDescription());
		}
		else if (!fluidIngredients.isEmpty()) {
			if (fluidIngredients.get(0).getFluids().length != 0)
				result.append(fluidIngredients.get(0).getFluids()[0].getDisplayName());
		}

		if (ingredients.size() > 2) {
			for (int i = 2; i < ingredients.size() - 1; i++)
				if (ingredients.get(i).getItems().length > 0)
					result.append(", ").append(ingredients.get(i).getItems()[0].getItem().getDescription());
			if (ingredients.get(ingredients.size() - 1).getItems().length > 0) {
				if (fluidIngredients.isEmpty())
					result.append(" ").append(VintageLang.translateDirect("recipe.assembly.and").append(" ").append(ingredients.get(ingredients.size() - 1).getItems()[0].getItem().getDescription()));
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
		list.add(VintageBlocks.VACUUM_CHAMBER.get());
	}

	@Override
	public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
		return () -> AssemblyVacuumizing::new;
	}

	public static boolean match(BasinBlockEntity basin, Recipe<?> recipe, VacuumChamberBlockEntity be, int step) {
		FilteringBehaviour filter = basin.getFilter();
		if (filter == null)
			return false;

		boolean filterTest = filter.test(recipe.getResultItem(basin.getLevel()
				.registryAccess()));
		if (recipe instanceof BasinRecipe) {
			BasinRecipe basinRecipe = (BasinRecipe) recipe;
			if (basinRecipe.getRollableResults()
					.isEmpty()
					&& !basinRecipe.getFluidResults()
					.isEmpty())
				filterTest = filter.test(basinRecipe.getFluidResults()
						.get(0));
		}

		if (!filterTest)
			return false;

		return apply(basin, recipe, be, true, step);
	}

	public static boolean apply(BasinBlockEntity basin, Recipe<?> recipe, VacuumChamberBlockEntity be, int step) {
		return apply(basin, recipe, be, false, step);
	}

	private static boolean apply(BasinBlockEntity basin, Recipe<?> recipe, VacuumChamberBlockEntity be, boolean test, int step) {
		boolean isBasinRecipe = recipe instanceof BasinRecipe;
		IItemHandler availableItems = basin.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, basin.getBlockPos(), null);
		IFluidHandler availableFluids = basin.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, basin.getBlockPos(), null);
		IFluidHandler availableSecondaryFluids = be.fluidCapability;

		if (availableItems == null || availableFluids == null || availableSecondaryFluids == null)
			return false;

		BlazeBurnerBlock.HeatLevel heat = BasinBlockEntity.getHeatLevelOf(basin.getLevel()
				.getBlockState(basin.getBlockPos()
						.below(1)));
		if (isBasinRecipe && !((BasinRecipe) recipe).getRequiredHeat()
				.testBlazeBurner(heat))
			return false;

		List<ItemStack> recipeOutputItems = new ArrayList<>();
		List<FluidStack> recipeOutputFluids = new ArrayList<>();
		List<FluidStack> recipeSecondaryOutputFluids = new ArrayList<>();

		List<Ingredient> ingredients = new LinkedList<>(recipe.getIngredients());
		List<SizedFluidIngredient> fluidIngredients =
				isBasinRecipe ? ((BasinRecipe) recipe).getFluidIngredients() : Collections.emptyList();

		for (boolean simulate : Iterate.trueAndFalse) {

			if (!simulate && test)
				return true;

			int[] extractedItemsFromSlot = new int[availableItems.getSlots()];
			int[] extractedFluidsFromTank = new int[availableFluids.getTanks()];
			int[] extractedSecondaryFluidsFromTank = new int[availableSecondaryFluids.getTanks()];

			boolean incompleteItemFound = false;

			Ingredients: for (int i = 0; i < ingredients.size(); i++) {
				Ingredient ingredient = ingredients.get(i);

				for (int slot = 0; slot < availableItems.getSlots(); slot++) {
					if (simulate && availableItems.getStackInSlot(slot)
							.getCount() <= extractedItemsFromSlot[slot])
						continue;
					ItemStack extracted = availableItems.extractItem(slot, 1, true);
					if (!ingredient.test(extracted))
						continue;
					if (step != 0) {
						CustomData customData = extracted.get(DataComponents.CUSTOM_DATA);
						if (customData != null && customData.contains("SequencedAssembly")) {
							if (incompleteItemFound) continue;

							CompoundTag tag = customData.copyTag().getCompound("SequencedAssembly");
							if (step == tag.getInt("Step") + 1) {
								incompleteItemFound = true;
							} else {
								continue;
							}
						} else if (step == 1) {
							incompleteItemFound = true;
						}
					}
					if (!simulate)
						availableItems.extractItem(slot, 1, false);
					extractedItemsFromSlot[slot]++;
					continue Ingredients;
				}

				// something wasn't found
				return false;
			}

			if (step != 0 && !incompleteItemFound) {
				return false;
			}

			boolean fluidsAffected = false;
			FluidIngredients: for (int i = 0; i < fluidIngredients.size(); i++) {
				SizedFluidIngredient SizedFluidIngredient = fluidIngredients.get(i);
				int amountRequired = SizedFluidIngredient.amount();

				if (recipe instanceof VacuumizingRecipe basinRecipe && basinRecipe.secondaryFluidInputs == i) {
					for (int tank = 0; tank < availableSecondaryFluids.getTanks(); tank++) {
						FluidStack fluidStack = availableSecondaryFluids.getFluidInTank(tank);
						if (simulate && fluidStack.getAmount() <= extractedSecondaryFluidsFromTank[tank])
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
						extractedSecondaryFluidsFromTank[tank] += drainedAmount;
						continue FluidIngredients;
					}
				}
				else {
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
				}


				// something wasn't found
				return false;
			}

			if (fluidsAffected) {
				basin.getBehaviour(SmartFluidTankBehaviour.INPUT)
						.forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
				basin.getBehaviour(SmartFluidTankBehaviour.OUTPUT)
						.forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
				be.getBehaviour(SmartFluidTankBehaviour.INPUT)
						.forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
				be.getBehaviour(SmartFluidTankBehaviour.OUTPUT)
						.forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
			}

			if (simulate) {
				if (recipe instanceof VacuumizingRecipe basinRecipe) {
					recipeOutputItems.addAll(basinRecipe.rollResults(basin.getLevel().random));
					DummyCraftingContainer dummyContainer = new DummyCraftingContainer(availableItems, extractedItemsFromSlot);
					List<ItemStack> craftingItems = new ArrayList<>();
					for (int slot = 0; slot < dummyContainer.getContainerSize(); slot++)
						craftingItems.add(dummyContainer.getItem(slot));
					CraftingInput remainderContainer = craftingItems.isEmpty()
							? CraftingInput.EMPTY
							: CraftingInput.of(craftingItems.size(), 1, craftingItems);

					for (ItemStack stack : basinRecipe.getRemainingItems(remainderContainer))
						if (!stack.isEmpty())
							recipeOutputItems.add(stack);
					NonNullList<FluidStack> fss = basinRecipe.getFluidResults();

					if (basinRecipe.secondaryFluidResults >= 0 && basinRecipe.secondaryFluidResults + 1 <= fss.size())
						recipeSecondaryOutputFluids.add(fss.get(basinRecipe.secondaryFluidResults));
					for (int i = 0; i < basinRecipe.secondaryFluidResults; i++)
						recipeOutputFluids.add(fss.get(i));
					for (int i = basinRecipe.secondaryFluidResults + 1; i < fss.size(); i++)
						recipeOutputFluids.add(fss.get(i));
				}
			}

			if (!basin.acceptOutputs(recipeOutputItems, recipeOutputFluids, simulate))
				return false;

			if (recipe instanceof VacuumizingRecipe basinRecipe)
				if (basinRecipe.secondaryFluidResults >= 0)
					if (!be.acceptOutputs(recipeSecondaryOutputFluids, simulate))
						return false;
		}

		return true;
	}

	public int getSecondaryFluidResults() {
		return secondaryFluidResults;
	}

	public int getSecondaryFluidInputs() {
		return secondaryFluidInputs;
	}

	public static class Serializer implements RecipeSerializer<VacuumizingRecipe> {
		private final MapCodec<VacuumizingRecipe> codec;
		private final StreamCodec<RegistryFriendlyByteBuf, VacuumizingRecipe> streamCodec;

		public Serializer() {
			this.codec = VacuumizingRecipeParams.CODEC
					.xmap(VacuumizingRecipe::new, VacuumizingRecipeParams::fromRecipe)
					.validate(Serializer::validateRecipe);
			this.streamCodec = VacuumizingRecipeParams.STREAM_CODEC
					.map(VacuumizingRecipe::new, VacuumizingRecipeParams::fromRecipe);
		}

		private static DataResult<VacuumizingRecipe> validateRecipe(VacuumizingRecipe recipe) {
			var errors = recipe.validate();
			if (errors.isEmpty())
				return DataResult.success(recipe);
			errors.add(recipe.getClass().getSimpleName() + " failed validation:");
			return DataResult.error(() -> Joiner.on('\n').join(errors), recipe);
		}

		@Override
		public MapCodec<VacuumizingRecipe> codec() {
			return codec;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, VacuumizingRecipe> streamCodec() {
			return streamCodec;
		}
	}


	public static String getSequenceId(RecipeHolder<? extends Recipe<?>> recipeHolder) {
		Recipe<?> recipe = recipeHolder.value();
		if (recipe instanceof VacuumizingRecipe vacuumizingRecipe) {
			String key = recipeHolder.id().toString();
			int last = key.lastIndexOf("_step_");
			if (last > -1) return key.substring(0, last);
		}

		return "";
	}
}

