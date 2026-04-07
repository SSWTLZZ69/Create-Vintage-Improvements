package com.negodya1.vintageimprovements.foundation.data;

import com.simibubi.create.api.registrate.CreateRegistrateRegistrationCallback;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class VintageRegistrate extends CreateRegistrate {

    protected VintageRegistrate(String modid) {
        super(modid);
    }

    public static VintageRegistrate create(String modid) {
        VintageRegistrate registrate = new VintageRegistrate(modid);
        CreateRegistrateRegistrationCallback.provideRegistrate(registrate);
        return registrate;
    }

    @Override
    protected void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        // Items are added explicitly via VintageImprovements creative tab displayItems.
    }
}
