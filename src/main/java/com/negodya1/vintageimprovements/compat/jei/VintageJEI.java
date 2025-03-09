package com.negodya1.vintageimprovements.compat.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

import com.negodya1.vintageimprovements.*;
import com.negodya1.vintageimprovements.compat.jei.category.*;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugationRecipe;
import com.negodya1.vintageimprovements.content.kinetics.coiling.CoilingRecipe;
import com.negodya1.vintageimprovements.content.kinetics.curving_press.CurvingPressBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.curving_press.CurvingRecipe;
import com.negodya1.vintageimprovements.content.kinetics.grinder.GrinderBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.grinder.PolishingRecipe;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.AutoSmithingRecipe;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.HammeringRecipe;
import com.negodya1.vintageimprovements.content.kinetics.laser.LaserCuttingRecipe;
import com.negodya1.vintageimprovements.content.kinetics.lathe.TurningRecipe;
import com.negodya1.vintageimprovements.content.kinetics.vibration.LeavesVibratingRecipe;
import com.negodya1.vintageimprovements.content.kinetics.vibration.VibratingRecipe;
import com.negodya1.vintageimprovements.content.kinetics.vibration.VibratingTableBlockEntity;
import com.negodya1.vintageimprovements.infrastructure.config.VCRecipes;
import com.negodya1.vintageimprovements.infrastructure.config.VintageConfig;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.IShapedRecipe;

import static com.simibubi.create.compat.jei.CreateJEI.consumeAllRecipes;

@JeiPlugin
public class VintageJEI implements IModPlugin {

	private static final ResourceLocation ID = new ResourceLocation(VintageImprovements.MODID, "jei_plugin");

	@Override
	@Nonnull
	public ResourceLocation getPluginUid() {
		return ID;
	}

	public IIngredientManager ingredientManager;
	final List<CreateRecipeCategory<?>> ALL = new ArrayList<>();

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		ALL.clear();

		ALL.add(builder(PolishingRecipe.class)
				.addTypedRecipes(VintageRecipes.POLISHING::getType)
				.catalyst(VintageBlocks.BELT_GRINDER::get)
				.itemIcon(VintageBlocks.BELT_GRINDER.get())
				.emptyBackground(177, 85)
				.build("polishing", GrinderPolishingCategory::new));

		ALL.add(builder(CoilingRecipe.class)
				.addTypedRecipes(VintageRecipes.COILING::getType)
				.catalyst(VintageBlocks.SPRING_COILING_MACHINE::get)
				.itemIcon(VintageBlocks.SPRING_COILING_MACHINE.get())
				.emptyBackground(177, 70)
				.build("coiling", CoilingCategory::new));

		ALL.add(builder(BasinRecipe.class)
				.addTypedRecipes(VintageRecipes.VACUUMIZING::getType)
				.catalyst(VintageBlocks.VACUUM_CHAMBER::get)
				.catalyst(AllBlocks.BASIN::get)
				.doubleItemIcon(VintageBlocks.VACUUM_CHAMBER.get(), AllBlocks.BASIN.get())
				.emptyBackground(177, 103)
				.build("vacuumizing", VacuumizingCategory::new));

		ALL.add(builder(BasinRecipe.class)
				.addTypedRecipes(VintageRecipes.PRESSURIZING::getType)
				.catalyst(VintageBlocks.VACUUM_CHAMBER::get)
				.catalyst(AllBlocks.BASIN::get)
				.doubleItemIcon(VintageBlocks.VACUUM_CHAMBER.get(), AllBlocks.BASIN.get())
				.emptyBackground(177, 103)
				.build("pressurizing", PressurizingCategory::new));

		ALL.add(builder(VibratingRecipe.class)
				.addTypedRecipes(VintageRecipes.VIBRATING::getType)
				.catalyst(VintageBlocks.VIBRATING_TABLE::get)
				.itemIcon(VintageBlocks.VIBRATING_TABLE)
				.emptyBackground(177, 70)
				.build("vibrating", VibratingCategory::new));

		ALL.add(builder(CentrifugationRecipe.class)
				.addTypedRecipes(VintageRecipes.CENTRIFUGATION::getType)
				.catalyst(VintageBlocks.CENTRIFUGE::get)
				.catalyst(AllBlocks.BASIN::get)
				.itemIcon(VintageBlocks.CENTRIFUGE)
				.emptyBackground(177, 113)
				.build("centrifugation", CentrifugationCategory::new));

		ALL.add(builder(CurvingRecipe.class)
				.addTypedRecipes(VintageRecipes.CURVING::getType)
				.catalyst(VintageBlocks.CURVING_PRESS::get)
				.itemIcon(VintageBlocks.CURVING_PRESS.get())
				.emptyBackground(177, 85)
				.build("curving", CurvingCategory::new));

		ALL.add(builder(HammeringRecipe.class)
				.addTypedRecipes(VintageRecipes.HAMMERING::getType)
				.catalyst(VintageBlocks.HELVE::get)
				.catalyst(Blocks.ANVIL::asItem)
				.doubleItemIcon(VintageBlocks.HELVE, Blocks.ANVIL)
				.emptyBackground(177, 85)
				.build("hammering", HammeringCategory::new));

		ALL.add(builder(AutoSmithingRecipe.class)
				.addTypedRecipes(VintageRecipes.AUTO_SMITHING::getType)
				.catalyst(VintageBlocks.HELVE::get)
				.catalyst(Blocks.SMITHING_TABLE::asItem)
				.doubleItemIcon(VintageBlocks.HELVE, Blocks.SMITHING_TABLE)
				.emptyBackground(177, 70)
				.build("auto_smithing", AutoSmithingCategory::new));

		ALL.add(builder(SandPaperPolishingRecipe.class)
				.enableWhen(c -> c.allowSandpaperPolishingOnGrinder)
				.addAllRecipesIf(r -> r instanceof SandPaperPolishingRecipe
						&& VintageRecipesList.isPolishing(r))
				.catalyst(VintageBlocks.BELT_GRINDER::get)
				.doubleItemIcon(VintageBlocks.BELT_GRINDER.get(), AllItems.SAND_PAPER)
				.emptyBackground(177, 85)
				.build("grinder_sandpaper_polishing", GrinderSandpaperPolishingCategory::new));

		ALL.add(builder(CraftingRecipe.class)
				.enableWhen(c -> c.allowUnpackingOnVibratingTable)
				.addAllRecipesIf(r -> r instanceof CraftingRecipe && !(r instanceof IShapedRecipe<?>)
						&& r.getIngredients()
						.size() == 1
						&& VibratingTableBlockEntity.canUnpack(r) && !AllRecipeTypes.shouldIgnoreInAutomation(r))
				.catalyst(VintageBlocks.VIBRATING_TABLE::get)
				.doubleItemIcon(VintageBlocks.VIBRATING_TABLE.get(), Blocks.IRON_BLOCK)
				.emptyBackground(177, 70)
				.build("unpacking", UnpackingCategory::new));

		ALL.add(builder(LeavesVibratingRecipe.class)
				.enableWhen(c -> c.allowVibratingLeaves)
				.addTypedRecipes(VintageRecipes.LEAVES_VIBRATING::getType)
				.catalyst(VintageBlocks.VIBRATING_TABLE::get)
				.doubleItemIcon(VintageBlocks.VIBRATING_TABLE.get(), Blocks.OAK_LEAVES)
				.emptyBackground(177, 70)
				.build("leaves_vibrating", LeavesVibratingCategory::new));

		ALL.add(builder(CraftingRecipe.class)
				.enableWhen(c -> c.allowAutoCurvingRecipes)
				.addAllRecipesIf(r -> r instanceof CraftingRecipe && r instanceof IShapedRecipe<?>
						&& r.getIngredients().size() == 6
						&& r.canCraftInDimensions(3, 2)
						&& CurvingPressBlockEntity.canCurve(r) && !AllRecipeTypes.shouldIgnoreInAutomation(r))
				.addAllRecipesIf(r -> r instanceof CraftingRecipe && r instanceof IShapedRecipe<?>
						&& r.getIngredients().size() == 6
						&& r.canCraftInDimensions(3, 2)
						&& CurvingPressBlockEntity.canCurve(r, 2) && !AllRecipeTypes.shouldIgnoreInAutomation(r))
				.addAllRecipesIf(r -> r instanceof CraftingRecipe && r instanceof IShapedRecipe<?>
						&& r.getIngredients().size() == 4
						&& r.canCraftInDimensions(2, 2)
						&& CurvingPressBlockEntity.canCurve(r, 3) && !AllRecipeTypes.shouldIgnoreInAutomation(r))
				.addAllRecipesIf(r -> r instanceof CraftingRecipe && r instanceof IShapedRecipe<?>
						&& r.getIngredients().size() == 4
						&& r.canCraftInDimensions(2, 2)
						&& CurvingPressBlockEntity.canCurve(r, 4) && !AllRecipeTypes.shouldIgnoreInAutomation(r))
				.catalyst(VintageBlocks.CURVING_PRESS::get)
				.doubleItemIcon(VintageBlocks.CURVING_PRESS.get(), AllItems.IRON_SHEET)
				.emptyBackground(177, 85)
				.build("auto_curving", AutoCurvingCategory::new));

		ALL.add(builder(TurningRecipe.class)
				.addTypedRecipes(VintageRecipes.TURNING::getType)
				.catalyst(VintageBlocks.LATHE_ROTATING::get)
				.itemIcon(VintageBlocks.LATHE_ROTATING.get())
				.emptyBackground(177, 70)
				.build("turning", TurningCategory::new));

		ALL.add(builder(LaserCuttingRecipe.class)
				.addTypedRecipes(VintageRecipes.LASER_CUTTING::getType)
				.catalyst(VintageBlocks.LASER::get)
				.itemIcon(VintageBlocks.LASER)
				.emptyBackground(177, 85)
				.build("laser_cutting", LaserCuttingCategory::new));

		ALL.forEach(registration::addRecipeCategories);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		ingredientManager = registration.getIngredientManager();
		ALL.forEach(c -> c.registerRecipes(registration));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		ALL.forEach(c -> c.registerCatalysts(registration));

		registration.getJeiHelpers().getRecipeType(new ResourceLocation("minecraft", "smithing"), SmithingRecipe.class).ifPresent(type -> {
			registration.addRecipeCatalyst(new ItemStack(VintageBlocks.HELVE.get()), type);
		});
	}

	private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
		return new CategoryBuilder<>(recipeClass);
	}

	private class CategoryBuilder<T extends Recipe<?>> {
		private final Class<? extends T> recipeClass;
		private Predicate<VCRecipes> predicate = cRecipes -> true;

		private IDrawable background;
		private IDrawable icon;

		private final List<Consumer<List<T>>> recipeListConsumers = new ArrayList<>();
		private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();

		public CategoryBuilder(Class<? extends T> recipeClass) {
			this.recipeClass = recipeClass;
		}

		public CategoryBuilder<T> addRecipeListConsumer(Consumer<List<T>> consumer) {
			recipeListConsumers.add(consumer);
			return this;
		}

		public CategoryBuilder<T> enableWhen(Function<VCRecipes, ConfigBase.ConfigBool> configValue) {
			predicate = c -> configValue.apply(c).get();
			return this;
		}

		public CategoryBuilder<T> addTypedRecipes(Supplier<RecipeType<? extends T>> recipeType) {
			return addRecipeListConsumer(recipes -> CreateJEI.<T>consumeTypedRecipes(recipes::add, recipeType.get()));
		}

		public CategoryBuilder<T> catalystStack(Supplier<ItemStack> supplier) {
			catalysts.add(supplier);
			return this;
		}

		public CategoryBuilder<T> catalyst(Supplier<ItemLike> supplier) {
			return catalystStack(() -> new ItemStack(supplier.get()
					.asItem()));
		}

		public CategoryBuilder<T> icon(IDrawable icon) {
			this.icon = icon;
			return this;
		}

		public CategoryBuilder<T> itemIcon(ItemLike item) {
			icon(new ItemIcon(() -> new ItemStack(item)));
			return this;
		}

		public CategoryBuilder<T> doubleItemIcon(ItemLike item1, ItemLike item2) {
			icon(new DoubleItemIcon(() -> new ItemStack(item1), () -> new ItemStack(item2)));
			return this;
		}

		public CategoryBuilder<T> background(IDrawable background) {
			this.background = background;
			return this;
		}

		public CategoryBuilder<T> emptyBackground(int width, int height) {
			background(new EmptyBackground(width, height));
			return this;
		}

		public CategoryBuilder<T> addAllRecipesIf(Predicate<Recipe<?>> pred) {
			return addRecipeListConsumer(recipes -> consumeAllRecipes(recipe -> {
				if (pred.test(recipe)) {
					recipes.add((T) recipe);
				}
			}));
		}

		public CategoryBuilder<T> addAllRecipesIf(Predicate<Recipe<?>> pred, Function<Recipe<?>, T> converter) {
			return addRecipeListConsumer(recipes -> consumeAllRecipes(recipe -> {
				if (pred.test(recipe)) {
					recipes.add(converter.apply(recipe));
				}
			}));
		}

		public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
			Supplier<List<T>> recipesSupplier;
			if (predicate.test(VintageConfig.server().recipes)) {
				recipesSupplier = () -> {
					List<T> recipes = new ArrayList<>();
					for (Consumer<List<T>> consumer : recipeListConsumers)
						consumer.accept(recipes);
					return recipes;
				};
			} else {
				recipesSupplier = () -> Collections.emptyList();
			}

			CreateRecipeCategory.Info<T> info = new CreateRecipeCategory.Info<>(
					new mezz.jei.api.recipe.RecipeType<>(VintageImprovements.asResource(name), recipeClass),
					Component.translatable(VintageImprovements.MODID + ".recipe." + name), background, icon, recipesSupplier, catalysts);
			CreateRecipeCategory<T> category = factory.create(info);
			return category;
		}
	}
}
