package com.negodya1.vintageimprovements.content.kinetics.lathe.recipe_card;

import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintageMenuTypes;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.content.kinetics.grinder.PolishingRecipe;
import com.negodya1.vintageimprovements.content.kinetics.lathe.TurningRecipe;
import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import java.util.Comparator;
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RecipeCardMenu extends GhostItemMenu<ItemStack> {

	private static final Object turningRecipesKey = new Object();
	private final Level level;
	private final DataSlot selectedRecipeIndex = DataSlot.standalone();
	List<TurningRecipe> recipes;
	public ItemStackHandler resultInventory;

	public RecipeCardMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
		super(type, id, inv, extraData);
		level = inv.player.level();
		selectedRecipeIndex.set(RecipeCardItem.getIndex(contentHolder));

		if (isValidRecipeIndex(selectedRecipeIndex.get()))
			if (getRecipes().get(selectedRecipeIndex.get()).getResultItem(RegistryAccess.EMPTY)
				.is(resultInventory.getStackInSlot(0).getItem()))
				for (int i = 0; i < getRecipes().size(); i++)
					if (getRecipes().get(i).getResultItem(RegistryAccess.EMPTY).is(resultInventory.getStackInSlot(0).getItem())) {
						selectedRecipeIndex.set(i);
						break;
					}

		addDataSlot(selectedRecipeIndex);
		recipes = new ArrayList<>();
	}

	public RecipeCardMenu(MenuType<?> type, int id, Inventory inv, ItemStack filterItem) {
		super(type, id, inv, filterItem);
		level = inv.player.level();
		selectedRecipeIndex.set(RecipeCardItem.getIndex(contentHolder));

		if (isValidRecipeIndex(selectedRecipeIndex.get()))
			if (!getRecipes().get(selectedRecipeIndex.get()).getResultItem(RegistryAccess.EMPTY)
					.is(resultInventory.getStackInSlot(0).getItem()))
				for (int i = 0; i < getRecipes().size(); i++)
					if (getRecipes().get(i).getResultItem(RegistryAccess.EMPTY).is(resultInventory.getStackInSlot(0).getItem())) {
						selectedRecipeIndex.set(i);
						break;
					}

		addDataSlot(selectedRecipeIndex);
		recipes = new ArrayList<>();
	}

	public static RecipeCardMenu create(int id, Inventory inv, ItemStack filterItem) {
		return new RecipeCardMenu(VintageMenuTypes.RECIPE_CARD.get(), id, inv, filterItem);
	}

	@Override
	protected void initAndReadInventory(ItemStack contentHolder) {
		super.initAndReadInventory(contentHolder);
		resultInventory = createResultInventory();
	}

	@Override
	protected ItemStack createOnClient(RegistryFriendlyByteBuf extraData) {
		return ItemStack.STREAM_CODEC.decode(extraData);
	}

	@Override
	protected ItemStackHandler createGhostInventory() {return RecipeCardItem.getFrequencyItems(contentHolder);}

	protected ItemStackHandler createResultInventory() {return RecipeCardItem.getResultItems(contentHolder);}

	@Override
	protected void addSlots() {
		addPlayerSlots(8, 131);
		addSlot(new SlotItemHandler(ghostInventory, 0, 23, 51));
	}

	@Override
	protected void saveData(ItemStack contentHolder) {
		CustomData.update(DataComponents.CUSTOM_DATA, contentHolder, tag -> {
			tag.put("Items", ghostInventory.serializeNBT(level.registryAccess()));
			tag.put("Results", resultInventory.serializeNBT(level.registryAccess()));
			CompoundTag recipeTag = tag.contains("Recipe") ? tag.getCompound("Recipe") : new CompoundTag();
			recipeTag.putInt("Index", selectedRecipeIndex.get());
			tag.put("Recipe", recipeTag);
		});
	}

	@Override
	protected boolean allowRepeats() {
		return false;
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
		if (slotId == playerInventory.selected && clickTypeIn != ClickType.THROW)
			return;
		super.clicked(slotId, dragType, clickTypeIn, player);
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return playerInventory.getSelected() == contentHolder;
	}

	private void setupRecipeList() {
		if (recipes != null)
			recipes.clear();

		Optional<RecipeHolder<TurningRecipe>> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(level, ghostInventory.getStackInSlot(0),
				VintageRecipes.TURNING.getType(), TurningRecipe.class);

		List<TurningRecipe> startedSearch = new ArrayList<>();

		if (assemblyRecipe.isPresent())
			startedSearch.add(assemblyRecipe.get().value());

		Predicate<RecipeHolder<? extends Recipe<?>>> types = RecipeConditions.isOfType(VintageRecipes.TURNING.getType());

		for (RecipeHolder<? extends Recipe<?>> holder : RecipeFinder.get(turningRecipesKey, level, types))
			if (holder.value() instanceof TurningRecipe turningRecipe) startedSearch.add(turningRecipe);

		startedSearch = startedSearch.stream()
				.filter(r -> !r.getIngredients().isEmpty() && r.getIngredients().get(0).test(ghostInventory.getStackInSlot(0)))
				.filter(r -> !VintageRecipes.shouldIgnoreInAutomation(r))
				.sorted(Comparator.comparing(r -> r.getResultItem(level.registryAccess()).getDescriptionId()))
				.collect(Collectors.toList());

		recipes = startedSearch;
	}

	public List<TurningRecipe> getRecipes() {
		if (recipes != null)
			if (!recipes.isEmpty())
				if (recipes.get(0).getIngredients().get(0).test(ghostInventory.getStackInSlot(0)))
					return recipes;

		setupRecipeList();
		return recipes;
	}

	public int getSelectedRecipeIndex() {
		return selectedRecipeIndex.get();
	}

	@Override
	public boolean clickMenuButton(Player player, int index) {
		if (isValidRecipeIndex(index)) {
			selectedRecipeIndex.set(index);
			resultInventory.setSize(1);
			resultInventory.setStackInSlot(0, getRecipes().get(index).getResultItem(level.registryAccess()));
		}

		return true;
	}

	private boolean isValidRecipeIndex(int index) {
		return index >= 0 && index < getRecipes().size();
	}

	@Override
	public void clearContents() {
		super.clearContents();
		selectedRecipeIndex.set(-1);
		recipes.clear();
		for (int i = 0; i < resultInventory.getSlots(); i++)
			resultInventory.setStackInSlot(i, ItemStack.EMPTY);
	}
}

