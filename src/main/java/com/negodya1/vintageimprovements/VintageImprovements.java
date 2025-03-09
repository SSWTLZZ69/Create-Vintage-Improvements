package com.negodya1.vintageimprovements;

import com.negodya1.vintageimprovements.foundation.advancement.VintageAdvancements;
import com.negodya1.vintageimprovements.infrastructure.config.VintageConfig;
import com.negodya1.vintageimprovements.infrastructure.ponder.VintagePonderPlugin;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.ponder.CreatePonderPlugin;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.fml.ModList;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(VintageImprovements.MODID)
public class VintageImprovements {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "vintageimprovements";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void logThis(String str) {
        LOGGER.info(str);
    }

    public static final CreateRegistrate MY_REGISTRATE = CreateRegistrate.create(MODID);

    static {
        MY_REGISTRATE.setTooltipModifierFactory(item -> {
            return new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item)));
        });
    }

    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<Item> CALORITE_ROD = ITEMS.register("calorite_rod", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> OSTRUM_ROD = ITEMS.register("ostrum_rod", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DESH_ROD = ITEMS.register("desh_rod", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NETHERITE_ROD = ITEMS.register("netherite_rod", () -> new Item(new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> NETHERSTEEL_ROD = ITEMS.register("nethersteel_rod", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> IRONWOOD_ROD = ITEMS.register("ironwood_rod", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> KNIGHTMETAL_ROD = ITEMS.register("knightmetal_rod", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> QUEENS_SLIME_ROD = ITEMS.register("queens_slime_rod", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SLIMESTEEL_ROD = ITEMS.register("slimesteel_rod", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VANADIUM_ROD = ITEMS.register("vanadium_rod", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ANDESITE_ROD = ITEMS.register("andesite_rod", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ZINC_ROD = ITEMS.register("zinc_rod", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SHADOW_STEEL_ROD = ITEMS.register("shadow_steel_rod", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> CALORITE_WIRE = ITEMS.register("calorite_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> OSTRUM_WIRE = ITEMS.register("ostrum_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DESH_WIRE = ITEMS.register("desh_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BRASS_WIRE = ITEMS.register("brass_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> IRONWOOD_WIRE = ITEMS.register("ironwood_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> KNIGHTMETAL_WIRE = ITEMS.register("knightmetal_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> QUEENS_SLIME_WIRE = ITEMS.register("queens_slime_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SLIMESTEEL_WIRE = ITEMS.register("slimesteel_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VANADIUM_WIRE = ITEMS.register("vanadium_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FIERY_WIRE = ITEMS.register("fiery_wire", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant()));
    public static final RegistryObject<Item> ANDESITE_WIRE = ITEMS.register("andesite_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ZINC_WIRE = ITEMS.register("zinc_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SHADOW_STEEL_WIRE = ITEMS.register("shadow_steel_wire", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> NETHERSTEEL_WIRE = ITEMS.register("nethersteel_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NETHERITE_WIRE = ITEMS.register("netherite_wire", () -> new Item(new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> GRINDER_BELT = ITEMS.register("grinder_belt", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPRING_COILING_MACHINE_WHEEL = ITEMS.register("spring_coiling_machine_wheel", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LASER_ITEM = ITEMS.register("laser_item", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SULFUR_CHUNK = ITEMS.register("sulfur_chunk", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SULFUR = ITEMS.register("sulfur", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VANADIUM_INGOT = ITEMS.register("vanadium_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VANADIUM_NUGGET = ITEMS.register("vanadium_nugget", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> NETHERITE_SHEET = ITEMS.register("netherite_sheet", () -> new Item(new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> NETHERSTEEL_SHEET = ITEMS.register("nethersteel_sheet", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> IRONWOOD_SHEET = ITEMS.register("ironwood_sheet", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> KNIGHTMETAL_SHEET = ITEMS.register("knightmetal_sheet", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> QUEENS_SLIME_SHEET = ITEMS.register("queens_slime_sheet", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SLIMESTEEL_SHEET = ITEMS.register("slimesteel_sheet", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VANADIUM_SHEET = ITEMS.register("vanadium_sheet", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FIERY_SHEET = ITEMS.register("fiery_sheet", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant()));
    public static final RegistryObject<Item> ANDESITE_SHEET = ITEMS.register("andesite_sheet", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ZINC_SHEET = ITEMS.register("zinc_sheet", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SHADOW_STEEL_SHEET = ITEMS.register("shadow_steel_sheet", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static boolean useEnergy = false;

    public static final RegistryObject<CreativeModeTab> VINTAGE_IMPROVEMENT_TAB = CREATIVE_MODE_TABS.register("vintage_improvement_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .title(Component.translatable("itemGroup." + MODID))
            .icon(() -> VintageBlocks.BELT_GRINDER.get().asItem().getDefaultInstance())
            .displayItems((parameters, output) -> {
                boolean forceItems = VintageConfig.client().forceCompatItemsIntoCreativeTab.get();
                boolean adAstraLoaded = ModList.get().isLoaded("ad_astra");
                boolean twilightForestLoaded = ModList.get().isLoaded("twilightforest");
                boolean tConstructLoaded = ModList.get().isLoaded("tconstruct");
                boolean bigCannonsLoaded = ModList.get().isLoaded("createbigcannons");

                output.accept(VintageBlocks.BELT_GRINDER.get());
                output.accept(VintageBlocks.SPRING_COILING_MACHINE.get());
                output.accept(VintageBlocks.VACUUM_CHAMBER.get());
                output.accept(VintageBlocks.VIBRATING_TABLE.get());
                output.accept(VintageBlocks.CENTRIFUGE.get());
                output.accept(VintageBlocks.CURVING_PRESS.get());
                output.accept(VintageBlocks.HELVE.get());
                output.accept(VintageBlocks.LATHE_ROTATING.get());
                output.accept(VintageBlocks.LASER.get());

                output.accept(VintageItems.CONVEX_CURVING_HEAD.get());
                output.accept(VintageItems.CONCAVE_CURVING_HEAD.get());
                output.accept(VintageItems.W_SHAPED_CURVING_HEAD.get());
                output.accept(VintageItems.V_SHAPED_CURVING_HEAD.get());

                output.accept(GRINDER_BELT.get());
                output.accept(SPRING_COILING_MACHINE_WHEEL.get());
                output.accept(LASER_ITEM.get());
                output.accept(VintageItems.REDSTONE_MODULE.get());

                output.accept(SULFUR_CHUNK.get());
                output.accept(SULFUR.get());
                output.accept(VintageBlocks.SULFUR_BLOCK.get());

                output.accept(VANADIUM_NUGGET.get());
                output.accept(VANADIUM_INGOT.get());
                output.accept(VintageBlocks.VANADIUM_BLOCK.get());

                output.accept(VintageFluids.SULFURIC_ACID.getBucket().get());

                output.accept(VintageItems.COPPER_SULFATE);

                output.accept(VintageItems.RECIPE_CARD);

                output.accept(VintageItems.HELVE_HAMMER_SLOT_COVER);

                if (VintageConfig.client().legacyMaterialsIntoCreativeTab.get()) {
                    output.accept(AllItems.REFINED_RADIANCE);
                    output.accept(AllItems.SHADOW_STEEL);
                }

                if (!VintageConfig.client().hideSheets.get()) {
                    if (!VintageItems.ALUMINUM_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.ALUMINUM_SHEET);
                    if (!VintageItems.AMETHYST_BRONZE_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.AMETHYST_BRONZE_SHEET);
                    output.accept(ANDESITE_SHEET.get());
                    if (!VintageItems.BRONZE_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.BRONZE_SHEET);
                    if (!VintageItems.CAST_IRON_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.CAST_IRON_SHEET);
                    if (!VintageItems.COBALT_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.COBALT_SHEET);
                    if (!VintageItems.CONSTANTAN_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.CONSTANTAN_SHEET);
                    if (!VintageItems.ENDERIUM_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.ENDERIUM_SHEET);
                    if (twilightForestLoaded || forceItems)
                        output.accept(FIERY_SHEET.get());
                    if (!VintageItems.HEPATIZON_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.HEPATIZON_SHEET);
                    if (!VintageItems.INVAR_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.INVAR_SHEET);
                    if (twilightForestLoaded || forceItems) {
                        output.accept(IRONWOOD_SHEET.get());
                        output.accept(KNIGHTMETAL_SHEET.get());
                    }
                    if (!VintageItems.LEAD_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.LEAD_SHEET);
                    if (!VintageItems.LUMIUM_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.LUMIUM_SHEET);
                    if (!VintageItems.MANYULLYN_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.MANYULLYN_SHEET);
                    output.accept(NETHERITE_SHEET.get());
                    if (bigCannonsLoaded || forceItems)
                        output.accept(NETHERSTEEL_SHEET.get());
                    if (!VintageItems.NICKEL_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.NICKEL_SHEET);
                    if (!VintageItems.OSMIUM_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.OSMIUM_SHEET);
                    if (!VintageItems.PALLADIUM_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.PALLADIUM_SHEET.get());
                    if (!VintageItems.PIG_IRON_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.PIG_IRON_SHEET.get());
                    if (!VintageItems.PLATINUM_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.PLATINUM_SHEET.get());
                    if (!VintageItems.PURE_GOLD_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.PURE_GOLD_SHEET.get());
                    if (tConstructLoaded || forceItems)
                        output.accept(QUEENS_SLIME_SHEET.get());
                    if (!VintageItems.REFINED_GLOWSTONE_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.REFINED_GLOWSTONE_SHEET.get());
                    if (!VintageItems.REFINED_OBSIDIAN_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.REFINED_OBSIDIAN_SHEET.get());
                    if (VintageConfig.client().legacyMaterialsIntoCreativeTab.get())
                        output.accept(VintageItems.REFINED_RADIANCE_SHEET.get());
                    if (!VintageItems.RHODIUM_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.RHODIUM_SHEET.get());
                    if (!VintageItems.ROSE_GOLD_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.ROSE_GOLD_SHEET.get());
                    if (VintageConfig.client().legacyMaterialsIntoCreativeTab.get())
                        output.accept(SHADOW_STEEL_SHEET.get());
                    if (!VintageItems.SIGNALUM_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SIGNALUM_SHEET.get());
                    if (!VintageItems.SILVER_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SILVER_SHEET.get());
                    if (tConstructLoaded || forceItems)
                        output.accept(SLIMESTEEL_SHEET.get());
                    if (!VintageItems.TIN_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.TIN_SHEET.get());
                    if (!VintageItems.URANIUM_SHEET.get().shouldHide() || forceItems)
                        output.accept(VintageItems.URANIUM_SHEET.get());
                    output.accept(VANADIUM_SHEET.get());
                    output.accept(ZINC_SHEET.get());
                }

                if (!VintageConfig.client().hideRods.get()) {
                    if (!VintageItems.ALUMINUM_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.ALUMINUM_ROD);
                    if (!VintageItems.AMETHYST_BRONZE_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.AMETHYST_BRONZE_ROD);
                    output.accept(ANDESITE_ROD.get());
                    if (!VintageItems.BRONZE_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.BRONZE_ROD);
                    if (adAstraLoaded || forceItems)
                        output.accept(CALORITE_ROD.get());
                    if (!VintageItems.CAST_IRON_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.CAST_IRON_ROD);
                    if (!VintageItems.COBALT_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.COBALT_ROD);
                    if (!VintageItems.CONSTANTAN_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.CONSTANTAN_ROD);
                    if (adAstraLoaded || forceItems)
                        output.accept(DESH_ROD.get());
                    if (!VintageItems.ENDERIUM_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.ENDERIUM_ROD);
                    if (!VintageItems.HEPATIZON_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.HEPATIZON_ROD);
                    if (!VintageItems.INVAR_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.INVAR_ROD);
                    if (twilightForestLoaded || forceItems) {
                        output.accept(IRONWOOD_ROD.get());
                        output.accept(KNIGHTMETAL_ROD.get());
                    }
                    if (!VintageItems.LEAD_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.LEAD_ROD);
                    if (!VintageItems.LUMIUM_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.LUMIUM_ROD);
                    if (!VintageItems.MANYULLYN_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.MANYULLYN_ROD);
                    output.accept(NETHERITE_ROD.get());
                    if (bigCannonsLoaded || forceItems)
                        output.accept(NETHERSTEEL_ROD.get());
                    if (!VintageItems.NICKEL_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.NICKEL_ROD);
                    if (!VintageItems.OSMIUM_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.OSMIUM_ROD);
                    if (adAstraLoaded || forceItems)
                        output.accept(OSTRUM_ROD.get());
                    if (!VintageItems.PALLADIUM_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.PALLADIUM_ROD.get());
                    if (!VintageItems.PIG_IRON_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.PIG_IRON_ROD.get());
                    if (!VintageItems.PLATINUM_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.PLATINUM_ROD.get());
                    if (!VintageItems.PURE_GOLD_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.PURE_GOLD_ROD.get());
                    if (tConstructLoaded || forceItems)
                        output.accept(QUEENS_SLIME_ROD.get());
                    if (!VintageItems.REFINED_GLOWSTONE_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.REFINED_GLOWSTONE_ROD.get());
                    if (!VintageItems.REFINED_OBSIDIAN_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.REFINED_OBSIDIAN_ROD.get());
                    if (VintageConfig.client().legacyMaterialsIntoCreativeTab.get())
                        output.accept(VintageItems.REFINED_RADIANCE_ROD.get());
                    if (!VintageItems.RHODIUM_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.RHODIUM_ROD.get());
                    if (!VintageItems.ROSE_GOLD_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.ROSE_GOLD_ROD.get());
                    if (VintageConfig.client().legacyMaterialsIntoCreativeTab.get())
                        output.accept(SHADOW_STEEL_ROD.get());
                    if (!VintageItems.SIGNALUM_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SIGNALUM_ROD.get());
                    if (!VintageItems.SILVER_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SILVER_ROD.get());
                    if (tConstructLoaded || forceItems)
                        output.accept(SLIMESTEEL_ROD.get());
                    if (!VintageItems.STEEL_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.STEEL_ROD.get());
                    if (!VintageItems.TIN_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.TIN_ROD.get());
                    if (!VintageItems.URANIUM_ROD.get().shouldHide() || forceItems)
                        output.accept(VintageItems.URANIUM_ROD.get());
                    output.accept(VANADIUM_ROD.get());
                    output.accept(ZINC_ROD.get());
                }

                if (!VintageConfig.client().hideWires.get()) {
                    if (!VintageItems.ALUMINUM_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.ALUMINUM_WIRE);
                    if (!VintageItems.AMETHYST_BRONZE_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.AMETHYST_BRONZE_WIRE);
                    output.accept(ANDESITE_WIRE.get());
                    output.accept(BRASS_WIRE.get());
                    if (!VintageItems.BRONZE_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.BRONZE_WIRE);
                    if (adAstraLoaded || forceItems)
                        output.accept(CALORITE_WIRE.get());
                    if (!VintageItems.CAST_IRON_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.CAST_IRON_WIRE);
                    if (!VintageItems.COBALT_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.COBALT_WIRE);
                    if (!VintageItems.CONSTANTAN_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.CONSTANTAN_WIRE);
                    if (adAstraLoaded || forceItems)
                        output.accept(DESH_WIRE.get());
                    if (!VintageItems.ENDERIUM_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.ENDERIUM_WIRE);
                    if (twilightForestLoaded || forceItems)
                        output.accept(FIERY_WIRE.get());
                    if (!VintageItems.HEPATIZON_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.HEPATIZON_WIRE);
                    if (!VintageItems.INVAR_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.INVAR_WIRE);
                    if (twilightForestLoaded || forceItems) {
                        output.accept(IRONWOOD_WIRE.get());
                        output.accept(KNIGHTMETAL_WIRE.get());
                    }
                    if (!VintageItems.LEAD_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.LEAD_WIRE);
                    if (!VintageItems.LUMIUM_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.LUMIUM_WIRE);
                    if (!VintageItems.MANYULLYN_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.MANYULLYN_WIRE);
                    output.accept(NETHERITE_WIRE.get());
                    if (bigCannonsLoaded || forceItems)
                        output.accept(NETHERSTEEL_WIRE.get());
                    if (!VintageItems.NICKEL_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.NICKEL_WIRE);
                    if (!VintageItems.OSMIUM_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.OSMIUM_WIRE);
                    if (adAstraLoaded || forceItems)
                        output.accept(OSTRUM_WIRE.get());
                    if (!VintageItems.PALLADIUM_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.PALLADIUM_WIRE.get());
                    if (!VintageItems.PIG_IRON_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.PIG_IRON_WIRE.get());
                    if (!VintageItems.PLATINUM_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.PLATINUM_WIRE.get());
                    if (!VintageItems.PURE_GOLD_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.PURE_GOLD_WIRE.get());
                    if (tConstructLoaded || forceItems)
                        output.accept(QUEENS_SLIME_WIRE.get());
                    if (!VintageItems.REFINED_GLOWSTONE_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.REFINED_GLOWSTONE_WIRE.get());
                    if (!VintageItems.REFINED_OBSIDIAN_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.REFINED_OBSIDIAN_WIRE.get());
                    if (VintageConfig.client().legacyMaterialsIntoCreativeTab.get())
                        output.accept(VintageItems.REFINED_RADIANCE_WIRE.get());
                    if (!VintageItems.RHODIUM_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.RHODIUM_WIRE.get());
                    if (!VintageItems.ROSE_GOLD_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.ROSE_GOLD_WIRE.get());
                    if (VintageConfig.client().legacyMaterialsIntoCreativeTab.get())
                        output.accept(SHADOW_STEEL_WIRE.get());
                    if (!VintageItems.SIGNALUM_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SIGNALUM_WIRE.get());
                    if (!VintageItems.SILVER_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SILVER_WIRE.get());
                    if (tConstructLoaded || forceItems)
                        output.accept(SLIMESTEEL_WIRE.get());
                    if (!VintageItems.STEEL_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.STEEL_WIRE.get());
                    if (!VintageItems.TIN_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.TIN_WIRE.get());
                    if (!VintageItems.URANIUM_WIRE.get().shouldHide() || forceItems)
                        output.accept(VintageItems.URANIUM_WIRE.get());
                    output.accept(VANADIUM_WIRE.get());
                    output.accept(ZINC_WIRE.get());
                }

                if (!VintageConfig.client().hideSprings.get()) {
                    if (!VintageItems.ALUMINUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.ALUMINUM_SPRING);
                    if (!VintageItems.AMETHYST_BRONZE_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.AMETHYST_BRONZE_SPRING);
                    output.accept(VintageItems.ANDESITE_SPRING.get());
                    output.accept(VintageItems.BLAZE_SPRING.get());
                    output.accept(VintageItems.BRASS_SPRING);
                    if (!VintageItems.BRONZE_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.BRONZE_SPRING);
                    if (adAstraLoaded || forceItems)
                        output.accept(VintageItems.CALORITE_SPRING.get());
                    if (!VintageItems.CAST_IRON_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.CAST_IRON_SPRING);
                    if (!VintageItems.COBALT_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.COBALT_SPRING);
                    if (!VintageItems.CONSTANTAN_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.CONSTANTAN_SPRING);
                    output.accept(VintageItems.COPPER_SPRING);
                    if (adAstraLoaded || forceItems)
                        output.accept(VintageItems.DESH_SPRING.get());
                    if (!VintageItems.ELECTRUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.ELECTRUM_SPRING);
                    if (!VintageItems.ENDERIUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.ENDERIUM_SPRING);
                    output.accept(VintageItems.GOLDEN_SPRING.get());
                    if (!VintageItems.HEPATIZON_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.HEPATIZON_SPRING);
                    if (!VintageItems.INVAR_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.INVAR_SPRING);
                    output.accept(VintageItems.IRON_SPRING);
                    if (twilightForestLoaded || forceItems) {
                        output.accept(VintageItems.IRONWOOD_SPRING.get());
                        output.accept(VintageItems.KNIGHTMETAL_SPRING.get());
                    }
                    if (!VintageItems.LEAD_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.LEAD_SPRING);
                    if (!VintageItems.LUMIUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.LUMIUM_SPRING);
                    if (!VintageItems.MANYULLYN_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.MANYULLYN_SPRING);
                    output.accept(VintageItems.NETHERITE_SPRING.get());
                    if (bigCannonsLoaded || forceItems)
                        output.accept(VintageItems.NETHERSTEEL_SPRING.get());
                    if (!VintageItems.NICKEL_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.NICKEL_SPRING);
                    if (!VintageItems.OSMIUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.OSMIUM_SPRING);
                    if (adAstraLoaded || forceItems)
                        output.accept(VintageItems.OSTRUM_SPRING.get());
                    if (!VintageItems.PALLADIUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.PALLADIUM_SPRING.get());
                    if (!VintageItems.PIG_IRON_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.PIG_IRON_SPRING.get());
                    if (!VintageItems.PLATINUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.PLATINUM_SPRING.get());
                    if (!VintageItems.PURE_GOLD_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.PURE_GOLD_SPRING.get());
                    if (tConstructLoaded || forceItems)
                        output.accept(VintageItems.QUEENS_SLIME_SPRING.get());
                    if (!VintageItems.REFINED_GLOWSTONE_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.REFINED_GLOWSTONE_SPRING.get());
                    if (!VintageItems.REFINED_OBSIDIAN_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.REFINED_OBSIDIAN_SPRING.get());
                    if (VintageConfig.client().legacyMaterialsIntoCreativeTab.get())
                        output.accept(VintageItems.REFINED_RADIANCE_SPRING.get());
                    if (!VintageItems.RHODIUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.RHODIUM_SPRING.get());
                    if (!VintageItems.ROSE_GOLD_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.ROSE_GOLD_SPRING.get());
                    if (VintageConfig.client().legacyMaterialsIntoCreativeTab.get())
                        output.accept(VintageItems.SHADOW_STEEL_SPRING.get());
                    if (!VintageItems.SIGNALUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SIGNALUM_SPRING.get());
                    if (!VintageItems.SILVER_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SILVER_SPRING.get());
                    if (tConstructLoaded || forceItems)
                        output.accept(VintageItems.SLIMESTEEL_SPRING.get());
                    if (!VintageItems.STEEL_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.STEEL_SPRING.get());
                    if (!VintageItems.TIN_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.TIN_SPRING.get());
                    if (!VintageItems.URANIUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.URANIUM_SPRING.get());
                    output.accept(VintageItems.VANADIUM_SPRING.get());
                    output.accept(VintageItems.ZINC_SPRING.get());
                }

                if (!VintageConfig.client().hideSmallSprings.get()) {
                    if (!VintageItems.SMALL_ALUMINUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_ALUMINUM_SPRING);
                    if (!VintageItems.SMALL_AMETHYST_BRONZE_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_AMETHYST_BRONZE_SPRING);
                    output.accept(VintageItems.SMALL_ANDESITE_SPRING.get());
                    output.accept(VintageItems.SMALL_BRASS_SPRING.get());
                    if (!VintageItems.SMALL_BRONZE_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_BRONZE_SPRING);
                    if (adAstraLoaded || forceItems)
                        output.accept(VintageItems.SMALL_CALORITE_SPRING.get());
                    if (!VintageItems.SMALL_CAST_IRON_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_CAST_IRON_SPRING);
                    if (!VintageItems.SMALL_COBALT_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_COBALT_SPRING);
                    if (!VintageItems.SMALL_CONSTANTAN_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_CONSTANTAN_SPRING);
                    output.accept(VintageItems.SMALL_COPPER_SPRING.get());
                    if (adAstraLoaded || forceItems)
                        output.accept(VintageItems.SMALL_DESH_SPRING.get());
                    if (!VintageItems.SMALL_ELECTRUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_ELECTRUM_SPRING);
                    if (!VintageItems.SMALL_ENDERIUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_ENDERIUM_SPRING);
                    if (twilightForestLoaded || forceItems)
                        output.accept(VintageItems.SMALL_FIERY_SPRING.get());
                    output.accept(VintageItems.SMALL_GOLDEN_SPRING.get());
                    if (!VintageItems.SMALL_HEPATIZON_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_HEPATIZON_SPRING);
                    if (!VintageItems.SMALL_INVAR_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_INVAR_SPRING);
                    output.accept(VintageItems.SMALL_IRON_SPRING.get());
                    if (twilightForestLoaded || forceItems) {
                        output.accept(VintageItems.SMALL_IRONWOOD_SPRING.get());
                        output.accept(VintageItems.SMALL_KNIGHTMETAL_SPRING.get());
                    }
                    if (!VintageItems.SMALL_LEAD_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_LEAD_SPRING);
                    if (!VintageItems.SMALL_LUMIUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_LUMIUM_SPRING);
                    if (!VintageItems.SMALL_MANYULLYN_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_MANYULLYN_SPRING);
                    output.accept(VintageItems.SMALL_NETHERITE_SPRING.get());
                    if (bigCannonsLoaded || forceItems)
                        output.accept(VintageItems.SMALL_NETHERSTEEL_SPRING.get());
                    if (!VintageItems.SMALL_NICKEL_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_NICKEL_SPRING);
                    if (!VintageItems.SMALL_OSMIUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_OSMIUM_SPRING);
                    if (adAstraLoaded || forceItems)
                        output.accept(VintageItems.SMALL_OSTRUM_SPRING.get());
                    if (!VintageItems.SMALL_PALLADIUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_PALLADIUM_SPRING.get());
                    if (!VintageItems.SMALL_PIG_IRON_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_PIG_IRON_SPRING.get());
                    if (!VintageItems.SMALL_PLATINUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_PLATINUM_SPRING.get());
                    if (!VintageItems.SMALL_PURE_GOLD_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_PURE_GOLD_SPRING.get());
                    if (tConstructLoaded || forceItems)
                        output.accept(VintageItems.SMALL_QUEENS_SLIME_SPRING.get());
                    if (!VintageItems.SMALL_REFINED_GLOWSTONE_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_REFINED_GLOWSTONE_SPRING.get());
                    if (!VintageItems.SMALL_REFINED_OBSIDIAN_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_REFINED_OBSIDIAN_SPRING.get());
                    if (VintageConfig.client().legacyMaterialsIntoCreativeTab.get())
                        output.accept(VintageItems.SMALL_REFINED_RADIANCE_SPRING.get());
                    if (!VintageItems.SMALL_RHODIUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_RHODIUM_SPRING.get());
                    if (!VintageItems.SMALL_ROSE_GOLD_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_ROSE_GOLD_SPRING.get());
                    if (VintageConfig.client().legacyMaterialsIntoCreativeTab.get())
                        output.accept(VintageItems.SMALL_SHADOW_STEEL_SPRING.get());
                    if (!VintageItems.SMALL_SIGNALUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_SIGNALUM_SPRING.get());
                    if (!VintageItems.SMALL_SILVER_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_SILVER_SPRING.get());
                    if (tConstructLoaded || forceItems)
                        output.accept(VintageItems.SMALL_SLIMESTEEL_SPRING.get());
                    if (!VintageItems.SMALL_STEEL_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_STEEL_SPRING.get());
                    if (!VintageItems.SMALL_TIN_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_TIN_SPRING.get());
                    if (!VintageItems.SMALL_URANIUM_SPRING.get().shouldHide() || forceItems)
                        output.accept(VintageItems.SMALL_URANIUM_SPRING.get());
                    output.accept(VintageItems.SMALL_VANADIUM_SPRING.get());
                    output.accept(VintageItems.SMALL_ZINC_SPRING.get());
                }

            }).build());

    public VintageImprovements() {
        onCtor();
        MinecraftForge.EVENT_BUS.register(this);

        useEnergy = ModList.get().isLoaded("createaddition") || ModList.get().isLoaded("mekanism")
        || ModList.get().isLoaded("thermal") || ModList.get().isLoaded("botarium")
                || ModList.get().isLoaded("immersiveengineering");
    }

    public static void onCtor() {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        IEventBus modEventBus = FMLJavaModLoadingContext.get()
                .getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        MY_REGISTRATE.registerEventListeners(modEventBus);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        VintageBlocks.register();
        VintageItems.register();
        VintageFluids.register();
        VintageMenuTypes.register();
        VintageBlockEntity.register();
        VintageRecipes.register(modEventBus);
        VintagePartialModels.init();

        modEventBus.addListener(VintageImprovements::commonSetup);

        VintageConfig.register(modLoadingContext);
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        VintageFluids.registerFluidInteractions();

        event.enqueueWork(() -> {
            VintageAdvancements.register();
        });
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        VintageRecipesList.init(event.getServer());
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            PonderIndex.addPlugin(new VintagePonderPlugin());
        }
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }
}
