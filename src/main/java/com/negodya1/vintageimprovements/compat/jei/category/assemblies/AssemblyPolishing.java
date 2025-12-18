package com.negodya1.vintageimprovements.compat.jei.category.assemblies;

import com.mojang.blaze3d.vertex.PoseStack;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedGrinder;
import com.negodya1.vintageimprovements.content.kinetics.grinder.PolishingRecipe;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class AssemblyPolishing extends SequencedAssemblySubCategory {

    AnimatedGrinder grinder;

    public AssemblyPolishing() {
        super(25);
        grinder = new AnimatedGrinder();
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(0, 51.5f, 0);
        ms.scale(.6f, .6f, .6f);
        grinder.draw(graphics, getWidth() / 2, 30);
        ms.popPose();

        if (recipe.getRecipe() instanceof PolishingRecipe polishing) {
            Font font = Minecraft.getInstance().font;

            ms.pushPose();

            switch (polishing.getSpeedLimits()) {
                case 1 -> graphics.drawString(font, Component.literal("█░░"), 0, 52, 0x00FF00, false);
                case 2 -> graphics.drawString(font, Component.literal("██░"), 0, 52, 0xFFFF00, false);
                case 3 -> graphics.drawString(font, Component.literal("███"), 0, 52, 0xFF0000, false);
                default -> graphics.drawString(font, Component.literal("░░░"), 0, 52, 0xFFFFFF, false);
            }
            ms.popPose();
        }
    }

}
