package com.negodya1.vintageimprovements.content.equipment;

import com.simibubi.create.foundation.item.ItemDescription;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import static com.negodya1.vintageimprovements.VintageItems.IRON_SPRING;

public class SpringItemBuilder extends ItemBuilder {
    public transient int stiffness = 50;

    public SpringItemBuilder(ResourceLocation id) {
        super(id);
    }

    @Info("""
            Sets stiffness of the spring.
    """)
    public SpringItemBuilder setStiffness(int stiffness) {
        this.stiffness = stiffness;
        return this;
    }

    @Override
    public Item createObject() {
        var item = new SpringItem(createItemProperties(), this.stiffness);
        ItemDescription.referKey(item, IRON_SPRING);
        return item;
    }
}
