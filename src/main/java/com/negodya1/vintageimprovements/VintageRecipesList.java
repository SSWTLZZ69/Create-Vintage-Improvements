package com.negodya1.vintageimprovements;

import com.negodya1.vintageimprovements.content.kinetics.grinder.PolishingRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VintageRecipesList {
    static List<CraftingRecipe> curving;
    static List<CraftingRecipe> curving2;
    static List<CraftingRecipe> curving3;
    static List<CraftingRecipe> curving4;

    static List<CraftingRecipe> unpacking;
    static List<RecipeHolder<PolishingRecipe>> polishing;
    static List<SmithingRecipe> smithing;

    static public void init(MinecraftServer level) {
        unpacking = new ArrayList<>();
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
        smithing = level.getRecipeManager()
                .getAllRecipesFor(RecipeType.SMITHING)
                .stream()
                .map(RecipeHolder::value)
                .collect(Collectors.toList());
    }

    static void initUnpacking(MinecraftServer level) {
        List<CraftingRecipe> recipes = level.getRecipeManager()
                .getAllRecipesFor(RecipeType.CRAFTING)
                .stream()
                .map(RecipeHolder::value)
                .collect(Collectors.toList());
        for (CraftingRecipe recipe : recipes) {
            if (recipe.getIngredients().size() > 1) continue;

            unpacking.add(recipe);
        }
    }

    static void initCurving(MinecraftServer level) {
        List<CraftingRecipe> recipes = level.getRecipeManager()
                .getAllRecipesFor(RecipeType.CRAFTING)
                .stream()
                .map(RecipeHolder::value)
                .collect(Collectors.toList());
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

        for (RecipeHolder<PolishingRecipe> holder : polishing)
            for (ItemStack stack : r.getIngredients().get(0).getItems())
                if (holder.value().getIngredients().get(0).test(stack)) return false;

        return true;
    }
}

