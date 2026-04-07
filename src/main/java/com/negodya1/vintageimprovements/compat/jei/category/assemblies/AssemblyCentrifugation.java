package com.negodya1.vintageimprovements.compat.jei.category.assemblies;

import com.mojang.blaze3d.vertex.PoseStack;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedCentrifuge;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedGrinder;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;

public class AssemblyCentrifugation extends SequencedAssemblySubCategory {

    AnimatedCentrifuge centrifuge;

    public AssemblyCentrifugation() {
        super(25);
        centrifuge = new AnimatedCentrifuge();
    }

    public void setRecipe(IRecipeLayoutBuilder builder, SequencedRecipe<?> recipe, IFocusGroup focuses, int x) {
        if (recipe.getRecipe().getIngredients().size() <= 1 && recipe.getRecipe().getFluidIngredients().isEmpty()) return;

        int offset = 0;
        if (recipe.getRecipe().getIngredients().size() > 1) {
            for (int i = 1; i < recipe.getRecipe().getIngredients().size(); i++) {
                IRecipeSlotBuilder slot = builder
                        .addSlot(RecipeIngredientRole.INPUT, x + 4 + (offset % 2 == 1 ? -8 : +8), 15 + (offset / 2) * 16)
                        .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                        .addIngredients(recipe.getRecipe().getIngredients().get(i));
                offset++;
            }
        }
        if (!recipe.getRecipe().getFluidIngredients().isEmpty()) {
            for (int i = 0; i < recipe.getRecipe().getFluidIngredients().size(); i++) {
                IRecipeSlotBuilder slot = builder
                        .addSlot(RecipeIngredientRole.INPUT, x + 4 + (offset % 2 == 1 ? -8 : +8), 15 + (offset / 2) * 16)
                        .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                        .addFluidStack(recipe.getRecipe().getFluidIngredients().get(i).getFluids()[0].getFluid(), recipe.getRecipe().getFluidIngredients().get(i).amount());
                offset++;
            }
        }
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(6f, 51.5f, 0);
        ms.scale(.3f, .3f, .3f);
        centrifuge.draw(graphics, getWidth() / 2, 30);
        ms.popPose();
    }

}
