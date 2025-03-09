package com.negodya1.vintageimprovements.infrastructure.ponder;

import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageItems;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class VintagePonderTag {

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);
        HELPER.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES)
                .add(VintageBlocks.BELT_GRINDER)
                .add(VintageBlocks.SPRING_COILING_MACHINE)
                .add(VintageBlocks.VACUUM_CHAMBER)
                .add(VintageBlocks.VIBRATING_TABLE)
                .add(VintageBlocks.CENTRIFUGE)
                .add(VintageBlocks.CURVING_PRESS)
                .add(VintageBlocks.HELVE)
                .add(VintageBlocks.LATHE_ROTATING)
                .add(VintageBlocks.LASER);

        HELPER.addToTag(AllCreatePonderTags.REDSTONE)
                .add(VintageItems.REDSTONE_MODULE);
    }
}
