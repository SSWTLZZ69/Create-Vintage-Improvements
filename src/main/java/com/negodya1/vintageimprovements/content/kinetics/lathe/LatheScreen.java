package com.negodya1.vintageimprovements.content.kinetics.lathe;

import com.google.common.collect.ImmutableList;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageLang;
import com.negodya1.vintageimprovements.foundation.gui.VintageGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

public class LatheScreen extends AbstractSimiContainerScreen<LatheMenu> {

	protected VintageGuiTextures background;
	private List<Rect2i> extraAreas = Collections.emptyList();

	private IconButton confirmButton;
	private int startIndex;
	private float scrollOffs;
	private boolean scrolling;

	public LatheScreen(LatheMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		background = VintageGuiTextures.RECIPE_CARD;
		startIndex = 0;
		scrollOffs = .0f;
		scrolling = false;
	}

	@Override
	protected void init() {
		setWindowSize(background.width, background.height + 4 + PLAYER_INVENTORY.getHeight());
		setWindowOffset(1, 0);
		super.init();

		int x = leftPos;
		int y = topPos;

		confirmButton = new IconButton(x + background.width - 33, y + background.height - 24, AllIcons.I_CONFIRM);
		confirmButton.withCallback(() -> {
			minecraft.player.closeContainer();
		});

		addRenderableWidget(confirmButton);

		extraAreas = ImmutableList.of(new Rect2i(x + background.width + 4, y + background.height - 44, 64, 56));
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		int invX = getLeftOfCentered(PLAYER_INVENTORY.getWidth());
		int invY = topPos + background.height + 4;
		renderPlayerInventory(graphics, invX, invY);

		int x = leftPos;
		int y = topPos;

		background.render(graphics, x, y);
		graphics.drawString(font, title, x + 15, y + 4, 0x592424, false);

		GuiGameElement.of(VintageBlocks.LATHE_ROTATING.asStack(1)).<GuiGameElement
						.GuiRenderBuilder>at(x + background.width, y + background.height - 56, -200)
				.scale(5)
				.render(graphics);

		int k = (int)(41.0F * scrollOffs);
		if (isScrollBarActive()) VintageGuiTextures.SCROLL_ACTIVE.render(graphics, x + 118, y + 33 + k);
		else VintageGuiTextures.SCROLL_INACTIVE.render(graphics, x + 118, y + 33 + k);

		renderButtons(graphics, mouseX, mouseY, x + 51, y + 31, startIndex + 12);
		renderRecipes(graphics, x + 51, y + 31, startIndex + 12);
	}

	private boolean isScrollBarActive() {
		return menu.getRecipes().size() > 12;
	}

	private void renderButtons(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int lastIndex) {
		for(int i = startIndex; i < lastIndex && i < menu.getRecipes().size(); ++i) {
			int j = i - startIndex;
			int k = x + j % 4 * 16;
			int l = j / 4;
			int i1 = y + l * 18 + 2;
			if (i == menu.getSelectedRecipeIndex())
				VintageGuiTextures.RECIPE_BUTTON_SELECTED.render(graphics, k, i1);
			else if (mouseX >= k && mouseY >= i1 && mouseX < k + 16 && mouseY < i1 + 18)
				VintageGuiTextures.RECIPE_BUTTON_SELECTION.render(graphics, k, i1);
			else VintageGuiTextures.RECIPE_BUTTON.render(graphics, k, i1);
		}
	}

	private void renderRecipes(GuiGraphics graphics, int x, int y, int lastIndex) {
		List<TurningRecipe> list = menu.getRecipes();

		for(int i = this.startIndex; i < lastIndex && i < menu.getRecipes().size(); ++i) {
			int j = i - startIndex;
			int k = x + j % 4 * 16;
			int l = j / 4;
			int i1 = y + l * 18 + 2;
			graphics.renderItem(list.get(i).getResultItem(RegistryAccess.EMPTY), k, i1);
		}
	}

	@Override
	protected void renderTooltip(GuiGraphics graphics, int x, int y) {
		if (menu.getRecipes().size() > 0) {
			int i = leftPos + 51;
			int j = topPos + 31;
			int k = startIndex + 12;
			List<TurningRecipe> recipesList = menu.getRecipes();

			for(int l = startIndex; l < k && l < menu.getRecipes().size(); ++l) {
				int i1 = l - this.startIndex;
				int j1 = i + i1 % 4 * 16;
				int k1 = j + i1 / 4 * 18 + 2;
				if (x >= j1 && x < j1 + 16 && y >= k1 && y < k1 + 18) {
					graphics.renderTooltip(font, recipesList.get(l).getResultItem(RegistryAccess.EMPTY), x, y);
					return;
				}
			}
		}

		if (!menu.getCarried()
			.isEmpty() || this.hoveredSlot == null || hoveredSlot.container == menu.playerInventory) {
			super.renderTooltip(graphics, x, y);
			return;
		}

		List<Component> list = new LinkedList<>();
		if (hoveredSlot.hasItem())
			list = getTooltipFromContainerItem(hoveredSlot.getItem());

		graphics.renderComponentTooltip(font, addToTooltip(list), x, y);
	}

	private List<Component> addToTooltip(List<Component> list) {
		list.add(VintageLang.translateDirect("lathe.item_slot")
			.withStyle(ChatFormatting.GOLD));
		return list;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollValue) {
		if (isScrollBarActive()) {
			int i = getOffscreenRows();
			float f = (float)scrollValue / (float)i;
			scrollOffs = Mth.clamp(scrollOffs - f, 0.0F, 1.0F);
			startIndex = (int)((double)(scrollOffs * (float)i) + 0.5D) * 4;
		}

		return true;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int p_99324_, double p_99325_, double p_99326_) {
		if (scrolling && isScrollBarActive()) {
			int i = topPos + 33;
			int j = i + 54;
			scrollOffs = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
			scrollOffs = Mth.clamp(scrollOffs, 0.0F, 1.0F);
			startIndex = (int)((double)(scrollOffs * (float)getOffscreenRows()) + 0.5D) * 4;
			return true;
		} else {
			return super.mouseDragged(mouseX, mouseY, p_99324_, p_99325_, p_99326_);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int p_99320_) {
		scrolling = false;
		if (!menu.getRecipes().isEmpty()) {
			int i = leftPos + 51;
			int j = topPos + 33;
			int k = startIndex + 12;

			for(int l = startIndex; l < k; ++l) {
				int i1 = l - startIndex;
				double d0 = mouseX - (double)(i + i1 % 4 * 16);
				double d1 = mouseY - (double)(j + i1 / 4 * 18);
				if (d0 >= 0.0D && d1 >= 0.0D && d0 < 16.0D && d1 < 18.0D && menu.clickMenuButton(minecraft.player, l)) {
					Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
					minecraft.gameMode.handleInventoryButtonClick((menu).containerId, l);
					minecraft.player.closeContainer();
					return true;
				}
			}

			i = leftPos + 118;
			j = topPos + 33;
			if (mouseX >= (double)i && mouseX < (double)(i + 12) && mouseY >= (double)j && mouseY < (double)(j + 54))
				scrolling = true;
		}

		return super.mouseClicked(mouseX, mouseY, p_99320_);
	}

	protected int getOffscreenRows() {
		return (menu.getRecipes().size() + 4 - 1) / 4 - 3;
	}

	@Override
	public List<Rect2i> getExtraAreas() {
		return extraAreas;
	}

}
