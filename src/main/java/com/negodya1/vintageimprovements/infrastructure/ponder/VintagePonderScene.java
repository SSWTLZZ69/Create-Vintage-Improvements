package com.negodya1.vintageimprovements.infrastructure.ponder;

import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintageItems;
import com.negodya1.vintageimprovements.infrastructure.ponder.scenes.*;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.foundation.registration.PonderTagRegistry;
import net.minecraft.resources.ResourceLocation;

public class VintagePonderScene {

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);
        HELPER.forComponents(VintageBlocks.BELT_GRINDER)
                .addStoryBoard("belt_grinder/processing", BeltGrinderScenes::processing, AllCreatePonderTags.KINETIC_APPLIANCES);

        HELPER.forComponents(VintageBlocks.SPRING_COILING_MACHINE)
                .addStoryBoard("spring_coiling_machine/processing", SpringCoilingScenes::processing, AllCreatePonderTags.KINETIC_APPLIANCES);

        HELPER.forComponents(VintageBlocks.VACUUM_CHAMBER)
                .addStoryBoard("vacuum_chamber/processing", VacuumChamberScenes::processing, AllCreatePonderTags.KINETIC_APPLIANCES);
        HELPER.forComponents(VintageBlocks.VACUUM_CHAMBER)
                .addStoryBoard("vacuum_chamber/secondary", VacuumChamberScenes::secondary, AllCreatePonderTags.KINETIC_APPLIANCES);

        HELPER.forComponents(VintageBlocks.VIBRATING_TABLE)
                .addStoryBoard("vibrating_table/processing", VibratingTableScenes::processing, AllCreatePonderTags.KINETIC_APPLIANCES);

        HELPER.forComponents(VintageBlocks.CENTRIFUGE)
                .addStoryBoard("centrifuge/processing", CentrifugeScenes::processing, AllCreatePonderTags.KINETIC_APPLIANCES);
        HELPER.forComponents(VintageBlocks.CENTRIFUGE)
                .addStoryBoard("centrifuge/redstone", CentrifugeScenes::redstone, AllCreatePonderTags.KINETIC_APPLIANCES);

        HELPER.forComponents(VintageBlocks.CURVING_PRESS)
                .addStoryBoard("curving_press/processing", CurvingPressScenes::processing, AllCreatePonderTags.KINETIC_APPLIANCES);
        HELPER.forComponents(VintageBlocks.CURVING_PRESS)
                .addStoryBoard("curving_press/redstone", CurvingPressScenes::redstone, AllCreatePonderTags.KINETIC_APPLIANCES);

        HELPER.addStoryBoard(VintageItems.REDSTONE_MODULE, "centrifuge/redstone", CentrifugeScenes::redstone, AllCreatePonderTags.REDSTONE);
        HELPER.addStoryBoard(VintageItems.REDSTONE_MODULE, "curving_press/redstone", CurvingPressScenes::redstone, AllCreatePonderTags.REDSTONE);

        HELPER.forComponents(VintageBlocks.HELVE)
                .addStoryBoard("helve_hammer/processing", HelveScenes::processing, AllCreatePonderTags.KINETIC_APPLIANCES);
        HELPER.forComponents(VintageBlocks.HELVE)
                .addStoryBoard("helve_hammer/slots_blocking", HelveScenes::slots_blocking, AllCreatePonderTags.KINETIC_APPLIANCES);

        HELPER.addStoryBoard(VintageItems.HELVE_HAMMER_SLOT_COVER, "helve_hammer/slots_blocking", HelveScenes::slots_blocking);

        HELPER.addStoryBoard(VintageItems.CONVEX_CURVING_HEAD, "curving_press/processing", CurvingPressScenes::processing);
        HELPER.addStoryBoard(VintageItems.CONCAVE_CURVING_HEAD, "curving_press/processing", CurvingPressScenes::processing);
        HELPER.addStoryBoard(VintageItems.W_SHAPED_CURVING_HEAD, "curving_press/processing", CurvingPressScenes::processing);
        HELPER.addStoryBoard(VintageItems.V_SHAPED_CURVING_HEAD, "curving_press/processing", CurvingPressScenes::processing);

        HELPER.forComponents(VintageBlocks.LATHE_ROTATING)
                .addStoryBoard("lathe/processing", LatheScenes::processing, AllCreatePonderTags.KINETIC_APPLIANCES);
        HELPER.forComponents(VintageBlocks.LATHE_ROTATING)
                .addStoryBoard("lathe/automation", LatheScenes::automation, AllCreatePonderTags.KINETIC_APPLIANCES);

        HELPER.addStoryBoard(VintageItems.RECIPE_CARD, "lathe/automation", LatheScenes::automation);

        HELPER.forComponents(VintageBlocks.LASER)
                .addStoryBoard("laser/processing", LaserScenes::processing, AllCreatePonderTags.KINETIC_APPLIANCES);

    }
}