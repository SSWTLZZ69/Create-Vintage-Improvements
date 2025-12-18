package com.negodya1.vintageimprovements.compat.jei;

import com.negodya1.vintageimprovements.VintageLang;
import com.simibubi.create.foundation.utility.Components;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

public class VintageRecipeUtil {

    public static IRecipeSlotTooltipCallback addTooltip(String lang) {
        return (view, tooltip) -> {
            Component text = VintageLang.translateDirect(lang).withStyle(ChatFormatting.LIGHT_PURPLE);
            if (tooltip.isEmpty())
                tooltip.add(0, text);
            else {
                List<Component> siblings = tooltip.get(0).getSiblings();
                siblings.add(Components.literal(" "));
                siblings.add(text);
            }
        };
    }
}
