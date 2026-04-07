package com.negodya1.vintageimprovements.content.equipment;

import com.negodya1.vintageimprovements.VintageImprovements;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.List;

public class SpringItem extends Item {

    int stiffness;

    public SpringItem(Properties properties, int stiffness) {
        super(properties);
        this.stiffness = stiffness;
    }

    public int getStiffness() {return stiffness;}

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable(VintageImprovements.MODID + ".item_description.spring_stiffness")
                .append(" " + stiffness).withStyle(ChatFormatting.GOLD));
    }

}
