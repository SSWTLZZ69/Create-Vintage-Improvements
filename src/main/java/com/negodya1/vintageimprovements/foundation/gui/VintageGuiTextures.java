package com.negodya1.vintageimprovements.foundation.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.simibubi.create.Create;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public enum VintageGuiTextures implements ScreenElement {

    // JEI
    JEI_UP_ARROW("jei/widgets", 0, 0, 18, 14),
    JEI_UP_TO_RIGHT_ARROW("jei/widgets", 17, 0, 14, 18),
    JEI_FRAGILE("jei/widgets", 31, 0, 14, 23),

    //Recipe Card
    RECIPE_CARD("recipe_card", 0, 0, 179, 109),
    RECIPE_BUTTON("recipe_card", 0, 109, 16, 18),
    RECIPE_BUTTON_SELECTED("recipe_card", 0, 127, 16, 18),
    RECIPE_BUTTON_SELECTION("recipe_card", 0, 145, 16, 18),
    SCROLL_ACTIVE("recipe_card", 16, 109, 12, 15),
    SCROLL_INACTIVE("recipe_card", 28, 109, 12, 15);

    public static final int FONT_COLOR = 0x575F7A;

    public final ResourceLocation location;
    public int width, height;
    public int startX, startY;

    private VintageGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    private VintageGuiTextures(int startX, int startY) {
        this("icons", startX * 16, startY * 16, 16, 16);
    }

    private VintageGuiTextures(String location, int startX, int startY, int width, int height) {
        this(VintageImprovements.MODID, location, startX, startY, width, height);
    }

    private VintageGuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = ResourceLocation.fromNamespaceAndPath(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    @OnlyIn(Dist.CLIENT)
    public void bind() {
        RenderSystem.setShaderTexture(0, location);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(location, x, y, startX, startY, width, height);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(graphics, c, x, y, startX, startY, width, height);
    }
}

