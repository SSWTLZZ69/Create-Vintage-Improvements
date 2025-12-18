package com.negodya1.vintageimprovements.content.kinetics.centrifuge;

import java.util.*;
import java.util.function.Supplier;

import com.google.gson.JsonObject;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageLang;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyCentrifugation;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.DummyCraftingContainer;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class CentrifugationRecipe extends ProcessingRecipe<Container> implements IAssemblyRecipe {

	int minimalRPM;

	public static boolean match(CentrifugeBlockEntity centrifuge, Recipe<?> recipe) {
		return apply(centrifuge, recipe, true);
	}

	public static boolean apply(CentrifugeBlockEntity centrifuge, Recipe<?> recipe) {
		return apply(centrifuge, recipe, false);
	}

	private static boolean apply(CentrifugeBlockEntity centrifuge, Recipe<?> recipe, boolean test) {
		IItemHandlerModifiable availableItems = (IItemHandlerModifiable) centrifuge.getCapability(ForgeCapabilities.ITEM_HANDLER)
				.orElse(null);
		IFluidHandler availableFluids = centrifuge.getCapability(ForgeCapabilities.FLUID_HANDLER)
				.orElse(null);

		if (availableItems == null || availableFluids == null)
			return false;

		List<ItemStack> recipeOutputItems = new ArrayList<>();
		List<FluidStack> recipeOutputFluids = new ArrayList<>();

		List<Ingredient> ingredients = new LinkedList<>(recipe.getIngredients());
		List<FluidIngredient> fluidIngredients = ((CentrifugationRecipe) recipe).getFluidIngredients();

		for (boolean simulate : Iterate.trueAndFalse) {
			if (!simulate && test)
				return true;

			int[] extractedItemsFromSlot = new int[availableItems.getSlots()];
			int[] extractedFluidsFromTank = new int[availableFluids.getTanks()];

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

			boolean fluidsAffected = false;
			FluidIngredients: for (int i = 0; i < fluidIngredients.size(); i++) {
				FluidIngredient fluidIngredient = fluidIngredients.get(i);
				int amountRequired = fluidIngredient.getRequiredAmount();

				for (int tank = 0; tank < availableFluids.getTanks(); tank++) {
					FluidStack fluidStack = availableFluids.getFluidInTank(tank);
					if (simulate && fluidStack.getAmount() <= extractedFluidsFromTank[tank])
						continue;
					if (!fluidIngredient.test(fluidStack))
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

				// something wasn't found
				return false;
			}

			if (fluidsAffected) {
				centrifuge.getBehaviour(SmartFluidTankBehaviour.INPUT)
						.forEach(TankSegment::onFluidStackChanged);
				centrifuge.getBehaviour(SmartFluidTankBehaviour.OUTPUT)
						.forEach(TankSegment::onFluidStackChanged);
			}

			if (simulate) {
				if (recipe instanceof CentrifugationRecipe centrifugeRecipe) {
					recipeOutputItems.addAll(centrifugeRecipe.rollResults());
					recipeOutputFluids.addAll(centrifugeRecipe.getFluidResults());
					CraftingContainer remainderContainer = new DummyCraftingContainer(availableItems, extractedItemsFromSlot);

					for (ItemStack stack : centrifugeRecipe.getRemainingItems(remainderContainer))
						if (!stack.isEmpty())
							recipeOutputItems.add(stack);
				}
			}

			if (!centrifuge.acceptOutputs(recipeOutputItems, recipeOutputFluids, simulate))
				return false;
		}

		return true;
	}

	public CentrifugationRecipe(ProcessingRecipeParams params) {
		super(VintageRecipes.CENTRIFUGATION, params);
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
		else return result;
		if (ingredients.size() > 1) {
			if (ingredients.get(1).getItems().length > 0)
				result.append(ingredients.get(1).getItems()[0].getItem().getDescription());
		}
		else if (!fluidIngredients.isEmpty()) {
			if (!fluidIngredients.get(0).getMatchingFluidStacks().isEmpty())
				result.append(fluidIngredients.get(0).getMatchingFluidStacks().get(0).getDisplayName());
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
				if (!fluidIngredients.get(i).getMatchingFluidStacks().isEmpty())
					result.append(", ").append(fluidIngredients.get(i).getMatchingFluidStacks().get(0).getDisplayName());
			if (!fluidIngredients.get(fluidIngredients.size() - 1).getMatchingFluidStacks().isEmpty())
				result.append(" ").append(VintageLang.translateDirect("recipe.assembly.and").append(" ")
						.append(fluidIngredients.get(fluidIngredients.size() - 1).getMatchingFluidStacks().get(0).getDisplayName()));

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

	@Override
	public void readAdditional(JsonObject json) {
		if (json.has("minimalRPM")) minimalRPM = json.get("minimalRPM").getAsInt();
		else minimalRPM = 100;
	}

	@Override
	public void readAdditional(FriendlyByteBuf buffer) {
		minimalRPM = buffer.readInt();
	}

	@Override
	public void writeAdditional(JsonObject json) {
		json.addProperty("minimalRPM", minimalRPM);
	}

	@Override
	public void writeAdditional(FriendlyByteBuf buffer) {
		buffer.writeInt(minimalRPM);
	}

	public int getMinimalRPM() {
		return minimalRPM;
	}

	@Override
	public boolean matches(Container container, Level level) {
		return false;
	}
}
