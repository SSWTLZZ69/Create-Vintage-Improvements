package com.negodya1.vintageimprovements;

import com.negodya1.vintageimprovements.content.equipment.SpringItemBuilder;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import net.minecraft.core.registries.Registries;

public class VintageKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void registerBuilderTypes(BuilderTypeRegistry registry) {
        registry.of(Registries.ITEM, itemCallback -> {
            itemCallback.add(VintageImprovements.asResource("spring"), SpringItemBuilder.class, SpringItemBuilder::new);
        });
    }
}
