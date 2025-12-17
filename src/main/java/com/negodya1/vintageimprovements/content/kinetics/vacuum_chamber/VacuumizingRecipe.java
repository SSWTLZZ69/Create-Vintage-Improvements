package com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyVacuumizing;
import com.negodya1.vintageimprovements.foundation.utility.VintageLang;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.DummyCraftingContainer;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.*;
import java.util.function.Supplier;

public class VacuumizingRecipe extends BasinRecipe implements IAssemblyRecipe {

	int secondaryFluidResults;
	int secondaryFluidInputs;

	public VacuumizingRecipe(ProcessingRecipeParams params) {
		super(VintageRecipes.VACUUMIZING, params);
		secondaryFluidResults = -1;
		secondaryFluidInputs = -1;
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

		// 在simibubi的设计中，配方无法得知过滤器的黑白名单模式，过滤器无法得知配方的完整产出
		// 因此这段代码不是我不想改，而是我没招了
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
		IItemHandler availableItems = basin.getCapability(ForgeCapabilities.ITEM_HANDLER)
				.orElse(null);
		IFluidHandler availableFluids = basin.getCapability(ForgeCapabilities.FLUID_HANDLER)
				.orElse(null);
		IFluidHandler availableSecondaryFluids = be.fluidCapability.orElse(null);

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
		List<FluidIngredient> fluidIngredients =
				isBasinRecipe ? ((BasinRecipe) recipe).getFluidIngredients() : Collections.emptyList();

		for (boolean simulate : Iterate.trueAndFalse) {

			if (!simulate && test)
				return true;

			int[] extractedItemsFromSlot = new int[availableItems.getSlots()];
			int[] extractedFluidsFromTank = new int[availableFluids.getTanks()];
			int[] extractedSecondaryFluidsFromTank = new int[availableSecondaryFluids.getTanks()];

			// 记录是否匹配到正确的半成品原料
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
					// 序列装配时，判断是否为对应半成品原料
					if (step != 0) {
						String sequenceId = getSequenceId(recipe);
						if (extracted.hasTag() && extracted.getTag().contains("SequencedAssembly")) {
							// 已经匹配到主原料时，拒绝任何带有序列装配标签的物品
							if (incompleteItemFound) continue;

							CompoundTag tag = extracted.getTag().getCompound("SequencedAssembly");
							// 匹配正确的序列装配主原料
							if (sequenceId.equals(tag.getString("id"))
									&& step == tag.getInt("Step") + 1) {
								incompleteItemFound = true;
							} else {
								// 拒绝其他任何有序列装配标签的物品
								continue;
							}
						} else if (step == 1) {
							// 起始步骤物品可以没有序列装配标签
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

			// 正在执行序列装配配方，但未找到可用半成品原料
			if (step != 0 && !incompleteItemFound) {
				return false;
			}

			boolean fluidsAffected = false;
			FluidIngredients: for (int i = 0; i < fluidIngredients.size(); i++) {
				FluidIngredient fluidIngredient = fluidIngredients.get(i);
				int amountRequired = fluidIngredient.getRequiredAmount();

				if (recipe instanceof VacuumizingRecipe basinRecipe && basinRecipe.secondaryFluidInputs == i) {
					for (int tank = 0; tank < availableSecondaryFluids.getTanks(); tank++) {
						FluidStack fluidStack = availableSecondaryFluids.getFluidInTank(tank);
						if (simulate && fluidStack.getAmount() <= extractedSecondaryFluidsFromTank[tank])
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
						extractedSecondaryFluidsFromTank[tank] += drainedAmount;
						continue FluidIngredients;
					}
				}
				else {
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
					recipeOutputItems.addAll(basinRecipe.rollResults());
					recipeOutputItems.addAll(basinRecipe.getRemainingItems(basin.getInputInventory()));

					NonNullList<FluidStack> fss = basinRecipe.getFluidResults();

					if (basinRecipe.secondaryFluidResults >= 0 && basinRecipe.secondaryFluidResults + 1 <= fss.size())
						recipeSecondaryOutputFluids.add(fss.get(basinRecipe.secondaryFluidResults));
					for (int i = 0; i < basinRecipe.secondaryFluidResults; i++)
						recipeOutputFluids.add(fss.get(i));
					for (int i = basinRecipe.secondaryFluidResults + 1; i < fss.size(); i++)
						recipeOutputFluids.add(fss.get(i));
				}
			}

			// 这里使用的本体代码有吞流体的bug
			// (工作盆产出流体A和B，而一个输出槽为空，另一输出槽被无关流体C占用时)
			// 不是我不想改，而是我没招了
			if (!basin.acceptOutputs(recipeOutputItems, recipeOutputFluids, simulate))
				return false;

			if (recipe instanceof VacuumizingRecipe basinRecipe)
				if (basinRecipe.secondaryFluidResults >= 0)
					if (!be.acceptOutputs(recipeSecondaryOutputFluids, simulate))
						return false;
		}

		return true;
	}

	@Override
	public void readAdditional(JsonObject json) {
		if (json.has("secondaryFluidOutput")) secondaryFluidResults = json.get("secondaryFluidOutput").getAsInt();
		if (json.has("secondaryFluidInput")) secondaryFluidInputs = json.get("secondaryFluidInput").getAsInt();
	}

	@Override
	public void readAdditional(FriendlyByteBuf buffer) {
		secondaryFluidResults = buffer.readInt();
		secondaryFluidInputs = buffer.readInt();
	}

	@Override
	public void writeAdditional(JsonObject json) {
		json.addProperty("secondaryFluidOutput", secondaryFluidResults);
		json.addProperty("secondaryFluidInput", secondaryFluidInputs);
	}

	@Override
	public void writeAdditional(FriendlyByteBuf buffer) {
		buffer.writeInt(secondaryFluidResults);
		buffer.writeInt(secondaryFluidInputs);
	}

	public int getSecondaryFluidResults() {
		return secondaryFluidResults;
	}

	public int getSecondaryFluidInputs() {
		return secondaryFluidInputs;
	}


	public static String getSequenceId(Recipe<?> recipe) {
		// simibubi解析序列装配配方时，为每个步骤创建一个子配方，并在配方id末尾添加_step_i
		// 但是配方自身没有方法确认是否属于序列装配，也不知道属于哪个序列装配的哪一步
		// 方法返回序列装配配方的id，step后的数字代表单个循环内的步骤，不能匹配物品实际加工步骤
		// 仅在能确定是序列装配的前提下调用这个方法！
		if (recipe instanceof VacuumizingRecipe vacuumizingRecipe) {
			String key = vacuumizingRecipe.getId().toString();
			int last = key.lastIndexOf("_step_");
			if (last > -1) return key.substring(0, last);
		}

		return "";
	}
}
