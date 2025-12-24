package com.negodya1.vintageimprovements;

import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import net.minecraft.network.chat.MutableComponent;

public class VintageLang extends Lang {

    public static MutableComponent translateDirect(String key, Object... args) {
        return Components.translatable(VintageImprovements.MODID + "." + key, resolveBuilders(args));
    }

    public static LangBuilder builder() {
        return new LangBuilder(VintageImprovements.MODID);
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }
}
