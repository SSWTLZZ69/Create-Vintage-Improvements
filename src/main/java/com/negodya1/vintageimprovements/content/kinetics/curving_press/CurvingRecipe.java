package com.negodya1.vintageimprovements.content.kinetics.curving_press;

import com.mojang.serialization.MapCodec;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageItems;
import com.negodya1.vintageimprovements.VintageLang;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyCurving;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
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
public class CurvingRecipe extends ProcessingRecipe<RecipeWrapper, CurvingRecipeParams> implements IAssemblyRecipe {

	final int mode;
	final int headDamage;
	final Item itemAsHead;

	public CurvingRecipe(CurvingRecipeParams params) {
		super(VintageRecipes.CURVING, params);
		mode = params.mode();
		headDamage = params.headDamage();
		itemAsHead = params.itemAsHead();
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
			case 2 -> result.append(Component.translatable(VintageItems.CONCAVE_CURVING_HEAD.get().getDescriptionId()));
			case 3 -> result.append(Component.translatable(VintageItems.W_SHAPED_CURVING_HEAD.get().getDescriptionId()));
			case 4 -> result.append(Component.translatable(VintageItems.V_SHAPED_CURVING_HEAD.get().getDescriptionId()));
			case 5 -> result.append(Component.translatable(itemAsHead.getDescriptionId()));
			default -> result.append(Component.translatable(VintageItems.CONVEX_CURVING_HEAD.get().getDescriptionId()));
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

	@FunctionalInterface
	public interface Factory<R extends CurvingRecipe> extends ProcessingRecipe.Factory<CurvingRecipeParams, R> {
		R create(CurvingRecipeParams params);
	}

	public static class Serializer<R extends CurvingRecipe> implements RecipeSerializer<R> {
		private final MapCodec<R> codec;
		private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

		public Serializer(Factory<R> factory) {
			this.codec = ProcessingRecipe.codec(factory, CurvingRecipeParams.CODEC);
			this.streamCodec = ProcessingRecipe.streamCodec(factory, CurvingRecipeParams.STREAM_CODEC);
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

