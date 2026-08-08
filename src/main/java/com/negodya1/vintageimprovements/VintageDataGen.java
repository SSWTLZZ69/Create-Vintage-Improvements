package com.negodya1.vintageimprovements;

import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeSchemaProvider;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.FluidStackComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class VintageDataGen {
    private static final ResourceLocation PROCESSING_WITH_TIME =
            ResourceLocation.fromNamespaceAndPath("create", "base/processing_with_time");

    public static void gatherData(GatherDataEvent event) {
        if (!event.includeServer()) {
            return;
        }

        event.addProvider(new RecipeSchemaProvider("Vintage Recipe Schemas", event) {
            @Override
            public void add(HolderLookup.Provider lookup) {
                addProcessingSchema("polishing",
                        NumberComponent.INT.otherKey("speed_limits").alt("speedLimits").optional(0),
                        BooleanComponent.BOOLEAN.otherKey("fragile").optional(false)
                );

                addProcessingSchema("coiling",
                        NumberComponent.INT.otherKey("spring_color").alt("springColor").optional(0x9AA49D)
                );

                addProcessingSchema("vacuumizing",
                        FluidStackComponent.OPTIONAL_FLUID_STACK.instance().or(NumberComponent.INT)
                                .outputKey("secondary_fluid_output")
                                .alt("secondaryFluidOutput").optional(Either.left(FluidStack.EMPTY)),
                        FluidStackComponent.OPTIONAL_FLUID_STACK.instance().or(NumberComponent.INT)
                                .inputKey("secondary_fluid_input")
                                .alt("secondaryFluidInput").optional(Either.left(FluidStack.EMPTY))
                );

                addProcessingSchema("vibrating");
                addProcessingSchema("leaves_vibrating");

                addProcessingSchema("centrifugation",
                        NumberComponent.INT.otherKey("minimal_rpm").alt("minimalRPM").optional(100)
                );

                addProcessingSchema("curving",
                        NumberComponent.INT.otherKey("mode").optional(1),
                        NumberComponent.INT.otherKey("head_damage").alt("headDamage").optional(0),
                        StringComponent.ID.otherKey("item_as_head").alt("itemAsHead").optional("minecraft:air")
                );

                addProcessingSchema("pressurizing",
                        FluidStackComponent.OPTIONAL_FLUID_STACK.instance().or(NumberComponent.INT)
                                .outputKey("secondary_fluid_output")
                                .alt("secondaryFluidOutput").optional(Either.left(FluidStack.EMPTY)),
                        FluidStackComponent.OPTIONAL_FLUID_STACK.instance().or(NumberComponent.INT)
                                .inputKey("secondary_fluid_input")
                                .alt("secondaryFluidInput").optional(Either.left(FluidStack.EMPTY))
                );

                addProcessingSchema("hammering",
                        NumberComponent.INT.otherKey("hammer_blows").alt("hammerBlows").optional(1),
                        StringComponent.ID.otherKey("anvil_block").alt("anvilBlock").optional("minecraft:air")
                );

                addProcessingSchema("auto_smithing");
                addProcessingSchema("auto_upgrade");
                addProcessingSchema("turning");

                addProcessingSchema("laser_cutting",
                        NumberComponent.INT.otherKey("energy").optional(0),
                        NumberComponent.INT.otherKey("max_charge_rate").alt("maxChargeRate").optional(0)
                );
            }

            private void addProcessingSchema(String recipeType, RecipeKey<?>... keys) {
                add(ResourceLocation.fromNamespaceAndPath(VintageImprovements.MODID, recipeType), builder -> {
                    builder.parent(PROCESSING_WITH_TIME);
                    if (keys.length > 0) {
                        builder.keys(keys);
                        builder.mergeData(true, false, false, false);
                    }
                });
            }
        });
    }
}
