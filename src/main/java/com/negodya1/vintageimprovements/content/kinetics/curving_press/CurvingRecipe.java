package com.negodya1.vintageimprovements.content.kinetics.curving_press;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonObject;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageItems;
import com.negodya1.vintageimprovements.VintageLang;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyCurving;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.utility.Components;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;

@ParametersAreNonnullByDefault
public class CurvingRecipe extends ProcessingRecipe<RecipeWrapper> implements IAssemblyRecipe {

	int mode;
	int headDamage;
	Item itemAsHead;

	public CurvingRecipe(ProcessingRecipeParams params) {
		super(VintageRecipes.CURVING, params);
		mode = 1;
		itemAsHead = Items.AIR;
		headDamage = 0;
	}

	public int getMode() {return mode;}

	public int getHeadDamage() {return headDamage;}

	public Item getItemAsHead() {return itemAsHead;}

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
		MutableComponent result =  VintageLang.translateDirect("recipe.assembly.curving");

		result.append(" ").append(VintageLang.translateDirect("recipe.assembly.with_head")).append(" ");
		switch (mode) {
			case 2 -> result.append(Components.translatable(VintageItems.CONCAVE_CURVING_HEAD.get().getDescriptionId()));
			case 3 -> result.append(Components.translatable(VintageItems.W_SHAPED_CURVING_HEAD.get().getDescriptionId()));
			case 4 -> result.append(Components.translatable(VintageItems.V_SHAPED_CURVING_HEAD.get().getDescriptionId()));
			case 5 -> result.append(Components.translatable(itemAsHead.getDescriptionId()));
			default -> result.append(Components.translatable(VintageItems.CONVEX_CURVING_HEAD.get().getDescriptionId()));
		}

		return result;
	}
	
	@Override
	public void addRequiredMachines(Set<ItemLike> list) {
		list.add(VintageBlocks.CURVING_PRESS.get());
	}
	
	@Override
	public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
		return () -> AssemblyCurving::new;
	}

	@Override
	public void readAdditional(JsonObject json) {
		if (json.has("headDamage")) headDamage = json.get("headDamage").getAsInt();
		else headDamage = 0;

		if (json.has("itemAsHead")) {
			itemAsHead = ForgeRegistries.ITEMS.getValue(new ResourceLocation(json.get("itemAsHead").getAsString()));
			if (itemAsHead != null) {
				mode = 5;
				return;
			}
		}

		if (json.has("mode")) mode = json.get("mode").getAsInt();
		else mode = 1;
	}

	@Override
	public void readAdditional(FriendlyByteBuf buffer) {
		mode = buffer.readInt();
		itemAsHead = buffer.readItem().getItem();
		if (itemAsHead != Items.AIR) mode = 5;
		headDamage = buffer.readInt();
	}

	@Override
	public void writeAdditional(JsonObject json) {
		json.addProperty("mode", mode);
		if (itemAsHead != Items.AIR) json.addProperty("itemAsHead", itemAsHead.toString());
		if (headDamage >= 0) json.addProperty("headDamage", mode);
	}

	@Override
	public void writeAdditional(FriendlyByteBuf buffer) {
		buffer.writeInt(mode);
		buffer.writeItem(new ItemStack(itemAsHead));
		buffer.writeInt(headDamage);
	}

}
