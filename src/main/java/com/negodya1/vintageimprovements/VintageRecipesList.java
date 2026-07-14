package com.negodya1.vintageimprovements;

import com.negodya1.vintageimprovements.content.kinetics.curving_press.CurvingRecipe;
import com.negodya1.vintageimprovements.content.kinetics.grinder.PolishingRecipe;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.foundation.item.ItemHelper;
import mezz.jei.api.constants.RecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VintageRecipesList {
    static List<CraftingRecipe> curving;
    static List<CraftingRecipe> curving2;
    static List<CraftingRecipe> curving3;
    static List<CraftingRecipe> curving4;

    static List<CraftingRecipe> unpacking = List.of();
    static Map<Item, CraftingRecipe> unpackingByInput = Map.of();
    static List<PolishingRecipe> polishing;
    static List<SmithingRecipe> smithing;

    static public void init(MinecraftServer level) {
        curving = new ArrayList<>();
        curving2 = new ArrayList<>();
        curving3 = new ArrayList<>();
        curving4 = new ArrayList<>();
        smithing = new ArrayList<>();

        polishing = level.getRecipeManager().getAllRecipesFor(VintageRecipes.POLISHING.getType());

        initUnpacking(level);
        initCurving(level);
        initSmithing(level);
    }

    static void initSmithing(MinecraftServer level) {
        smithing = level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);
    }

    static void initUnpacking(MinecraftServer level) {
        List<CraftingRecipe> recipes = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
        List<CraftingRecipe> unpackingRecipes = findUnpackingRecipes(recipes, level.registryAccess());
        unpacking = List.copyOf(unpackingRecipes);

        Map<Item, CraftingRecipe> recipesByInput = new HashMap<>();
        List<CraftingRecipe> packingRecipes = findPackingRecipes(recipes);
        for (CraftingRecipe unpackingRecipe : unpackingRecipes) {
            for (CraftingRecipe packingRecipe : packingRecipes) {
                if (!isReversePair(unpackingRecipe, packingRecipe, level.registryAccess())) continue;
                ItemStack packedResult = packingRecipe.getResultItem(level.registryAccess());
                recipesByInput.putIfAbsent(packedResult.getItem(), unpackingRecipe);
            }
        }
        unpackingByInput = Map.copyOf(recipesByInput);
    }

    /**
     * Mirrors Create's automatic packing rule, then keeps only the reverse
     * recipes that turn the packed result back into the same 4 or 9 items.
     */
    public static List<CraftingRecipe> findUnpackingRecipes(
            List<? extends Recipe<?>> recipes, RegistryAccess registries) {
        List<CraftingRecipe> result = new ArrayList<>();
        List<CraftingRecipe> packingRecipes = findPackingRecipes(recipes);

        for (Recipe<?> recipe : recipes) {
            if (!(recipe instanceof CraftingRecipe unpackingRecipe)) continue;
            if (!isUnpackingRecipe(unpackingRecipe, packingRecipes, registries)) continue;
            result.add(unpackingRecipe);
        }

        return result;
    }

    private static List<CraftingRecipe> findPackingRecipes(List<? extends Recipe<?>> recipes) {
        List<CraftingRecipe> packingRecipes = new ArrayList<>();

        for (Recipe<?> recipe : recipes) {
            if (!(recipe instanceof CraftingRecipe packingRecipe)) continue;
            if (packingRecipe instanceof MechanicalCraftingRecipe) continue;
            if (AllRecipeTypes.shouldIgnoreInAutomation(packingRecipe)) continue;

            NonNullList<Ingredient> ingredients = packingRecipe.getIngredients();
            if ((ingredients.size() != 4 && ingredients.size() != 9)
                    || !ItemHelper.matchAllIngredients(ingredients)) continue;

            packingRecipes.add(packingRecipe);
        }

        return packingRecipes;
    }

    private static boolean isUnpackingRecipe(CraftingRecipe unpackingRecipe,
                                             List<CraftingRecipe> packingRecipes,
                                             RegistryAccess registries) {
        if (AllRecipeTypes.shouldIgnoreInAutomation(unpackingRecipe)) return false;

        for (CraftingRecipe packingRecipe : packingRecipes) {
            if (isReversePair(unpackingRecipe, packingRecipe, registries)) return true;
        }

        return false;
    }

    private static boolean isReversePair(CraftingRecipe unpackingRecipe, CraftingRecipe packingRecipe,
                                         RegistryAccess registries) {
        NonNullList<Ingredient> unpackingIngredients = unpackingRecipe.getIngredients();
        if (unpackingIngredients.size() != 1 || unpackingIngredients.get(0).isEmpty()) return false;

        ItemStack unpackedResult = unpackingRecipe.getResultItem(registries);
        int unpackedCount = unpackedResult.getCount();
        if (unpackedResult.isEmpty() || (unpackedCount != 4 && unpackedCount != 9)) return false;

        NonNullList<Ingredient> packingIngredients = packingRecipe.getIngredients();
        if (packingIngredients.size() != unpackedCount) return false;

        ItemStack packedResult = packingRecipe.getResultItem(registries);
        if (packedResult.isEmpty() || packedResult.getCount() != 1
                || !unpackingIngredients.get(0).test(packedResult)) return false;

        for (Ingredient ingredient : packingIngredients) {
            if (!ingredient.test(unpackedResult)) return false;
        }

        return true;
    }

    public static Optional<CraftingRecipe> findUnpacking(ItemStack stack) {
        if (stack.isEmpty()) return Optional.empty();

        CraftingRecipe recipe = unpackingByInput.get(stack.getItem());
        if (recipe == null || !recipe.getIngredients().get(0).test(stack)) return Optional.empty();
        return Optional.of(recipe);
    }

    static void initCurving(MinecraftServer level) {
        List<CraftingRecipe> recipes = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
        Recipe: for (CraftingRecipe recipe : recipes) {
            if (recipe instanceof ShapelessRecipe) continue;

            if (!recipe.canCraftInDimensions(2, 2)) {
                if (!recipe.canCraftInDimensions(3, 2)) continue;
                if (recipe.getIngredients().size() != 6) continue;

                ItemStack item = null;

                NonNullList<Ingredient> in = recipe.getIngredients();
                if (in.get(0).isEmpty()) {
                    if (in.get(1).isEmpty()) continue;

                    int matches = 0;
                    boolean it = true;

                    for (Ingredient i : in) {
                        it = !it;

                        if (it) {
                            if (!i.isEmpty()) {
                                if (item == null) {
                                    if (i.getItems().length <= 0)
                                        continue Recipe;
                                    item = i.getItems()[0];
                                }
                            }
                            else continue Recipe;

                            if (i.test(item)) {
                                matches++;
                                continue;
                            }
                        }
                        if (!i.isEmpty()) continue Recipe;
                    }

                    if (matches != 3) continue;

                    curving2.add(recipe);
                }
                else {
                    int matches = 0;
                    boolean it = false;

                    for (Ingredient i : in) {
                        it = !it;

                        if (it) {
                            if (!i.isEmpty()) {
                                if (item == null) {
                                    if (i.getItems().length <= 0)
                                        continue Recipe;
                                    item = i.getItems()[0];
                                }
                            }
                            else continue Recipe;

                            if (i.test(item)) {
                                matches++;
                                continue;
                            }
                        }
                        if (!i.isEmpty()) continue Recipe;
                    }

                    if (matches != 3) continue;

                    curving.add(recipe);
                }
            }
            else {
                if (recipe.getIngredients().size() != 4) continue;

                ItemStack item = null;

                NonNullList<Ingredient> in = recipe.getIngredients();
                if (in.get(0).isEmpty() || in.get(1).isEmpty()) {
                    if (in.get(2).isEmpty() || in.get(3).isEmpty()) continue;

                    int matches = 0;
                    int empty = 0;

                    for (Ingredient i : in) {
                        if (!i.isEmpty()) {
                            if (item == null) {
                                if (i.getItems().length <= 0)
                                    continue Recipe;
                                item = i.getItems()[0];
                            }

                            if (i.test(item)) {
                                matches++;
                                continue;
                            }
                            else continue Recipe;
                        }
                        else {
                            empty++;
                            if (empty > 1) continue Recipe;
                        }
                    }

                    if (matches != 3 || empty != 1) continue;

                    curving4.add(recipe);
                }
                else {
                    if (!in.get(2).isEmpty() && !in.get(3).isEmpty()) continue;

                    int matches = 0;
                    int empty = 0;

                    for (Ingredient i : in) {
                        if (!i.isEmpty()) {
                            if (item == null) {
                                if (i.getItems().length <= 0)
                                    continue Recipe;
                                item = i.getItems()[0];
                            }

                            if (i.test(item)) {
                                matches++;
                                continue;
                            }
                            else continue Recipe;
                        }
                        else {
                            empty++;
                            if (empty > 1) continue Recipe;
                        }
                    }

                    if (matches != 3 || empty != 1) continue;

                    curving3.add(recipe);
                }
            }
        }
    }

    static public List<CraftingRecipe> getUnpacking() {
        return unpacking;
    }

    static public List<CraftingRecipe> getCurving(int mode) {
        switch (mode) {
            case 2 -> {return curving2;}
            case 3 -> {return curving3;}
            case 4 -> {return curving4;}
            default -> {return curving;}
        }
    }

    static public List<SmithingRecipe> getSmithing() {
        return smithing;
    }

    static public boolean isPolishing(Recipe<?> r) {
        if (polishing == null) return true;
        if (polishing.isEmpty()) return true;

        for (PolishingRecipe recipe : polishing)
            for (ItemStack stack : r.getIngredients().get(0).getItems())
                if (recipe.getIngredients().get(0).test(stack)) return false;

        return true;
    }
}
