package com.negodya1.vintageimprovements;

import static com.negodya1.vintageimprovements.VintageImprovements.MY_REGISTRATE;
import static com.negodya1.vintageimprovements.foundation.data.recipe.VintageCompatMetals.*;

import com.negodya1.vintageimprovements.content.equipment.*;
import com.negodya1.vintageimprovements.content.kinetics.lathe.recipe_card.RecipeCardItem;
import com.negodya1.vintageimprovements.foundation.data.recipe.VintageCompatMetals;

import com.simibubi.create.AllTags;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.content.contraptions.glue.SuperGlueItem;
import com.simibubi.create.content.contraptions.minecart.MinecartCouplingItem;
import com.simibubi.create.content.contraptions.mounted.MinecartContraptionItem;
import com.simibubi.create.content.equipment.BuildersTeaItem;
import com.simibubi.create.content.equipment.TreeFertilizerItem;
import com.simibubi.create.content.equipment.armor.AllArmorMaterials;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankItem.BacktankBlockItem;
import com.simibubi.create.content.equipment.armor.DivingBootsItem;
import com.simibubi.create.content.equipment.armor.DivingHelmetItem;
import com.simibubi.create.content.equipment.blueprint.BlueprintItem;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.content.equipment.goggles.GogglesModel;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItem;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItem;
import com.simibubi.create.content.equipment.symmetryWand.SymmetryWandItem;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.content.equipment.zapper.terrainzapper.WorldshaperItem;
import com.simibubi.create.content.kinetics.belt.item.BeltConnectorItem;
import com.simibubi.create.content.kinetics.gearbox.VerticalGearboxItem;
import com.simibubi.create.content.legacy.ChromaticCompoundColor;
import com.simibubi.create.content.legacy.ChromaticCompoundItem;
import com.simibubi.create.content.legacy.RefinedRadianceItem;
import com.simibubi.create.content.legacy.ShadowSteelItem;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.materials.ExperienceNuggetItem;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.simibubi.create.content.schematics.SchematicAndQuillItem;
import com.simibubi.create.content.schematics.SchematicItem;
import com.simibubi.create.content.trains.schedule.ScheduleItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.common.Tags;

public class VintageItems {

	public static final ItemEntry<TagDependentIngredientItem> ALUMINUM_SHEET = compatSheet(ALUMINUM);
	public static final ItemEntry<TagDependentIngredientItem> AMETHYST_BRONZE_SHEET = compatSheet(AMETHYST_BRONZE);
	public static final ItemEntry<TagDependentIngredientItem> BRONZE_SHEET = compatSheet(BRONZE);
	public static final ItemEntry<TagDependentIngredientItem> CAST_IRON_SHEET = compatSheet(CAST_IRON);
	public static final ItemEntry<TagDependentIngredientItem> COBALT_SHEET = compatSheet(COBALT);
	public static final ItemEntry<TagDependentIngredientItem> CONSTANTAN_SHEET = compatSheet(CONSTANTAN);
	public static final ItemEntry<TagDependentIngredientItem> ENDERIUM_SHEET = compatSheet(ENDERIUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> HEPATIZON_SHEET = compatSheet(HEPATIZON);
	public static final ItemEntry<TagDependentIngredientItem> INVAR_SHEET = compatSheet(INVAR);
	public static final ItemEntry<TagDependentIngredientItem> LEAD_SHEET = compatSheet(LEAD);
	public static final ItemEntry<TagDependentIngredientItem> LUMIUM_SHEET = compatSheet(LUMIUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> MANYULLYN_SHEET = compatSheet(MANYULLYN);
	public static final ItemEntry<TagDependentIngredientItem> NICKEL_SHEET = compatSheet(NICKEL);
	public static final ItemEntry<TagDependentIngredientItem> OSMIUM_SHEET = compatSheet(OSMIUM);
	public static final ItemEntry<TagDependentIngredientItem> PALLADIUM_SHEET = compatSheet(PALLADIUM);
	public static final ItemEntry<TagDependentIngredientItem> PIG_IRON_SHEET = compatSheet(PIG_IRON);
	public static final ItemEntry<TagDependentIngredientItem> PLATINUM_SHEET = compatSheet(PLATINUM);
	public static final ItemEntry<TagDependentIngredientItem> PURE_GOLD_SHEET = compatSheet(PURE_GOLD);
	public static final ItemEntry<TagDependentIngredientItem> REFINED_GLOWSTONE_SHEET = compatSheet(REFINED_GLOWSTONE);
	public static final ItemEntry<TagDependentIngredientItem> REFINED_OBSIDIAN_SHEET = compatSheet(REFINED_OBSIDIAN);
	public static final ItemEntry<TagDependentIngredientItem> RHODIUM_SHEET = compatSheet(RHODIUM);
	public static final ItemEntry<TagDependentIngredientItem> ROSE_GOLD_SHEET = compatSheet(ROSE_GOLD);
	public static final ItemEntry<TagDependentIngredientItem> SIGNALUM_SHEET = compatSheet(SIGNALUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> SILVER_SHEET = compatSheet(SILVER);
	public static final ItemEntry<TagDependentIngredientItem> TIN_SHEET = compatSheet(TIN);
	public static final ItemEntry<TagDependentIngredientItem> URANIUM_SHEET = compatSheet(URANIUM);

	public static final ItemEntry<TagDependentIngredientItem> ALUMINUM_ROD = compatRod(ALUMINUM);
	public static final ItemEntry<TagDependentIngredientItem> AMETHYST_BRONZE_ROD = compatRod(AMETHYST_BRONZE);
	public static final ItemEntry<TagDependentIngredientItem> BRONZE_ROD = compatRod(BRONZE);
	public static final ItemEntry<TagDependentIngredientItem> CAST_IRON_ROD = compatRod(CAST_IRON);
	public static final ItemEntry<TagDependentIngredientItem> COBALT_ROD = compatRod(COBALT);
	public static final ItemEntry<TagDependentIngredientItem> CONSTANTAN_ROD = compatRod(CONSTANTAN);
	public static final ItemEntry<TagDependentIngredientItem> ENDERIUM_ROD = compatRod(ENDERIUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> HEPATIZON_ROD = compatRod(HEPATIZON);
	public static final ItemEntry<TagDependentIngredientItem> INVAR_ROD = compatRod(INVAR);
	public static final ItemEntry<TagDependentIngredientItem> LEAD_ROD = compatRod(LEAD);
	public static final ItemEntry<TagDependentIngredientItem> LUMIUM_ROD = compatRod(LUMIUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> MANYULLYN_ROD = compatRod(MANYULLYN);
	public static final ItemEntry<TagDependentIngredientItem> NICKEL_ROD = compatRod(NICKEL);
	public static final ItemEntry<TagDependentIngredientItem> OSMIUM_ROD = compatRod(OSMIUM);
	public static final ItemEntry<TagDependentIngredientItem> PALLADIUM_ROD = compatRod(PALLADIUM);
	public static final ItemEntry<TagDependentIngredientItem> PIG_IRON_ROD = compatRod(PIG_IRON);
	public static final ItemEntry<TagDependentIngredientItem> PLATINUM_ROD = compatRod(PLATINUM);
	public static final ItemEntry<TagDependentIngredientItem> PURE_GOLD_ROD = compatRod(PURE_GOLD);
	public static final ItemEntry<TagDependentIngredientItem> REFINED_GLOWSTONE_ROD = compatRod(REFINED_GLOWSTONE);
	public static final ItemEntry<TagDependentIngredientItem> REFINED_OBSIDIAN_ROD = compatRod(REFINED_OBSIDIAN);
	public static final ItemEntry<TagDependentIngredientItem> RHODIUM_ROD = compatRod(RHODIUM);
	public static final ItemEntry<TagDependentIngredientItem> ROSE_GOLD_ROD = compatRod(ROSE_GOLD);
	public static final ItemEntry<TagDependentIngredientItem> SIGNALUM_ROD = compatRod(SIGNALUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> SILVER_ROD = compatRod(SILVER);
	public static final ItemEntry<TagDependentIngredientItem> STEEL_ROD = compatRod(STEEL);
	public static final ItemEntry<TagDependentIngredientItem> TIN_ROD = compatRod(TIN);
	public static final ItemEntry<TagDependentIngredientItem> URANIUM_ROD = compatRod(URANIUM);

	public static final ItemEntry<TagDependentSpringItem> ALUMINUM_SPRING = compatSpring(ALUMINUM, 160);
	public static final ItemEntry<TagDependentSpringItem> AMETHYST_BRONZE_SPRING = compatSpring(AMETHYST_BRONZE, 240);
	public static final ItemEntry<SpringItem> ANDESITE_SPRING = spring("andesite_spring", 250);
	public static final ItemEntry<SpringItem> BLAZE_SPRING = spring("blaze_spring", 640, Rarity.UNCOMMON);
	public static final ItemEntry<SpringItem> BRASS_SPRING = spring("brass_spring", 220);
	public static final ItemEntry<TagDependentSpringItem> BRONZE_SPRING = compatSpring(BRONZE, 240);
	public static final ItemEntry<SpringItem> CALORITE_SPRING = spring("calorite_spring", 1280);
	public static final ItemEntry<TagDependentSpringItem> CAST_IRON_SPRING = compatSpring(CAST_IRON, 280);
	public static final ItemEntry<TagDependentSpringItem> COBALT_SPRING = compatSpring(COBALT, 460);
	public static final ItemEntry<SpringItem> COPPER_SPRING = spring("copper_spring", 300);
	public static final ItemEntry<TagDependentSpringItem> CONSTANTAN_SPRING = compatSpring(CONSTANTAN, 370);
	public static final ItemEntry<SpringItem> DESH_SPRING = spring("desh_spring", 640);
	public static final ItemEntry<TagDependentSpringItem> ELECTRUM_SPRING = compatSpring(ELECTRUM, 180);
	public static final ItemEntry<TagDependentSpringItem> ENDERIUM_SPRING = compatSpring(ENDERIUM, 200, Rarity.UNCOMMON);
	public static final ItemEntry<SpringItem> GOLDEN_SPRING = spring("golden_spring", 170);
	public static final ItemEntry<TagDependentSpringItem> HEPATIZON_SPRING = compatSpring(HEPATIZON, 240);
	public static final ItemEntry<TagDependentSpringItem> INVAR_SPRING = compatSpring(INVAR, 340);
	public static final ItemEntry<SpringItem> IRON_SPRING = spring("iron_spring", 500);
	public static final ItemEntry<SpringItem> IRONWOOD_SPRING = spring("ironwood_spring", 500);
	public static final ItemEntry<SpringItem> KNIGHTMETAL_SPRING = spring("knightmetal_spring", 500);
	public static final ItemEntry<TagDependentSpringItem> LEAD_SPRING = compatSpring(LEAD, 40);
	public static final ItemEntry<TagDependentSpringItem> LUMIUM_SPRING = compatSpring(LUMIUM, 160, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentSpringItem> MANYULLYN_SPRING = compatSpring(MANYULLYN, 1280);
	public static final ItemEntry<SpringItem> NETHERITE_SPRING = MY_REGISTRATE
			.item("netherite_spring", props -> new SpringItem(props, 2560))
			.properties(p -> p.fireResistant())
			.register();
	public static final ItemEntry<SpringItem> NETHERSTEEL_SPRING = spring("nethersteel_spring", 1280);
	public static final ItemEntry<TagDependentSpringItem> NICKEL_SPRING = compatSpring(NICKEL, 480);
	public static final ItemEntry<TagDependentSpringItem> OSMIUM_SPRING = compatSpring(OSMIUM, 1380);
	public static final ItemEntry<SpringItem> OSTRUM_SPRING = spring("ostrum_spring", 960);
	public static final ItemEntry<TagDependentSpringItem> PALLADIUM_SPRING = compatSpring(PALLADIUM, 280);
	public static final ItemEntry<TagDependentSpringItem> PIG_IRON_SPRING = compatSpring(PIG_IRON, 40);
	public static final ItemEntry<TagDependentSpringItem> PLATINUM_SPRING = compatSpring(PLATINUM, 380);
	public static final ItemEntry<TagDependentSpringItem> PURE_GOLD_SPRING = compatSpring(PURE_GOLD, 170);
	public static final ItemEntry<SpringItem> QUEENS_SLIME_SPRING = spring("queens_slime_spring", 640);
	public static final ItemEntry<TagDependentSpringItem> REFINED_GLOWSTONE_SPRING = compatSpring(REFINED_GLOWSTONE, 160);
	public static final ItemEntry<TagDependentSpringItem> REFINED_OBSIDIAN_SPRING = compatSpring(REFINED_OBSIDIAN, 300);
	public static final ItemEntry<TagDependentSpringItem> RHODIUM_SPRING = compatSpring(RHODIUM, 940);
	public static final ItemEntry<TagDependentSpringItem> ROSE_GOLD_SPRING = compatSpring(ROSE_GOLD, 170);
	public static final ItemEntry<SpringItem> SHADOW_STEEL_SPRING = spring("shadow_steel_spring", 500, Rarity.UNCOMMON);
	public static final ItemEntry<SpringItem> SLIMESTEEL_SPRING = spring("slimesteel_spring", 500);
	public static final ItemEntry<TagDependentSpringItem> SIGNALUM_SPRING = compatSpring(SIGNALUM, 270, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentSpringItem> SILVER_SPRING = compatSpring(SILVER, 180);
	public static final ItemEntry<TagDependentSpringItem> STEEL_SPRING = compatSpring(STEEL, 500);
	public static final ItemEntry<TagDependentSpringItem> TIN_SPRING = compatSpring(TIN, 110);
	public static final ItemEntry<TagDependentSpringItem> URANIUM_SPRING = compatSpring(URANIUM, 700);
	public static final ItemEntry<SpringItem> VANADIUM_SPRING = spring("vanadium_spring", 300);
	public static final ItemEntry<SpringItem> ZINC_SPRING = spring("zinc_spring", 270);

	public static final ItemEntry<TagDependentSpringItem> SMALL_ALUMINUM_SPRING = compatSmallSpring(ALUMINUM, 16);
	public static final ItemEntry<TagDependentSpringItem> SMALL_AMETHYST_BRONZE_SPRING = compatSmallSpring(AMETHYST_BRONZE, 25);
	public static final ItemEntry<SpringItem> SMALL_ANDESITE_SPRING = spring("small_andesite_spring", 25);
	public static final ItemEntry<SpringItem> SMALL_BRASS_SPRING = spring("small_brass_spring", 22);
	public static final ItemEntry<TagDependentSpringItem> SMALL_BRONZE_SPRING = compatSmallSpring(BRONZE, 24);
	public static final ItemEntry<SpringItem> SMALL_CALORITE_SPRING = spring("small_calorite_spring", 128);
	public static final ItemEntry<TagDependentSpringItem> SMALL_CAST_IRON_SPRING = compatSmallSpring(CAST_IRON, 28);
	public static final ItemEntry<TagDependentSpringItem> SMALL_COBALT_SPRING = compatSmallSpring(COBALT, 46);
	public static final ItemEntry<TagDependentSpringItem> SMALL_CONSTANTAN_SPRING = compatSmallSpring(CONSTANTAN, 37);
	public static final ItemEntry<SpringItem> SMALL_COPPER_SPRING = spring("small_copper_spring", 30);
	public static final ItemEntry<SpringItem> SMALL_DESH_SPRING = spring("small_desh_spring", 64);
	public static final ItemEntry<TagDependentSpringItem> SMALL_ELECTRUM_SPRING = compatSmallSpring(ELECTRUM, 18);
	public static final ItemEntry<TagDependentSpringItem> SMALL_ENDERIUM_SPRING = compatSmallSpring(ENDERIUM, Rarity.UNCOMMON, 20);
	public static final ItemEntry<SpringItem> SMALL_FIERY_SPRING = MY_REGISTRATE
			.item("small_fiery_spring", props -> new SpringItem(props, 64))
			.properties(p -> p.fireResistant().rarity(Rarity.UNCOMMON))
			.register();
	public static final ItemEntry<SpringItem> SMALL_GOLDEN_SPRING = spring("small_golden_spring", 17);
	public static final ItemEntry<TagDependentSpringItem> SMALL_HEPATIZON_SPRING = compatSmallSpring(HEPATIZON, 24);
	public static final ItemEntry<TagDependentSpringItem> SMALL_INVAR_SPRING = compatSmallSpring(INVAR, 34);
	public static final ItemEntry<SpringItem> SMALL_IRON_SPRING = spring("small_iron_spring", 50);
	public static final ItemEntry<SpringItem> SMALL_IRONWOOD_SPRING = spring("small_ironwood_spring", 50);
	public static final ItemEntry<SpringItem> SMALL_KNIGHTMETAL_SPRING = spring("small_knightmetal_spring", 50);
	public static final ItemEntry<TagDependentSpringItem> SMALL_LEAD_SPRING = compatSmallSpring(LEAD, 4);
	public static final ItemEntry<TagDependentSpringItem> SMALL_LUMIUM_SPRING = compatSmallSpring(LUMIUM, Rarity.UNCOMMON, 16);
	public static final ItemEntry<TagDependentSpringItem> SMALL_MANYULLYN_SPRING = compatSmallSpring(MANYULLYN, 256);
	public static final ItemEntry<SpringItem> SMALL_NETHERITE_SPRING = MY_REGISTRATE
			.item("small_netherite_spring", props -> new SpringItem(props, 256))
			.properties(p -> p.fireResistant())
			.register();
	public static final ItemEntry<SpringItem> SMALL_NETHERSTEEL_SPRING = spring("small_nethersteel_spring", 128);
	public static final ItemEntry<TagDependentSpringItem> SMALL_NICKEL_SPRING = compatSmallSpring(NICKEL, 48);
	public static final ItemEntry<SpringItem> SMALL_OSTRUM_SPRING = spring("small_ostrum_spring", 96);
	public static final ItemEntry<TagDependentSpringItem> SMALL_OSMIUM_SPRING = compatSmallSpring(OSMIUM, 138);
	public static final ItemEntry<TagDependentSpringItem> SMALL_PALLADIUM_SPRING = compatSmallSpring(PALLADIUM, 28);
	public static final ItemEntry<TagDependentSpringItem> SMALL_PIG_IRON_SPRING = compatSmallSpring(PIG_IRON, 4);
	public static final ItemEntry<TagDependentSpringItem> SMALL_PLATINUM_SPRING = compatSmallSpring(PLATINUM, 38);
	public static final ItemEntry<TagDependentSpringItem> SMALL_PURE_GOLD_SPRING = compatSmallSpring(PURE_GOLD, 17);
	public static final ItemEntry<SpringItem> SMALL_QUEENS_SLIME_SPRING = spring("small_queens_slime_spring", 64);
	public static final ItemEntry<TagDependentSpringItem> SMALL_REFINED_GLOWSTONE_SPRING = compatSmallSpring(REFINED_GLOWSTONE, 16);
	public static final ItemEntry<TagDependentSpringItem> SMALL_REFINED_OBSIDIAN_SPRING = compatSmallSpring(REFINED_OBSIDIAN, 30);
	public static final ItemEntry<TagDependentSpringItem> SMALL_RHODIUM_SPRING = compatSmallSpring(RHODIUM, 94);
	public static final ItemEntry<TagDependentSpringItem> SMALL_ROSE_GOLD_SPRING = compatSmallSpring(ROSE_GOLD, 17);
	public static final ItemEntry<SpringItem> SMALL_SHADOW_STEEL_SPRING = spring("small_shadow_steel_spring", 50, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentSpringItem> SMALL_SIGNALUM_SPRING = compatSmallSpring(SIGNALUM, Rarity.UNCOMMON, 27);
	public static final ItemEntry<TagDependentSpringItem> SMALL_SILVER_SPRING = compatSmallSpring(SILVER, 18);
	public static final ItemEntry<SpringItem> SMALL_SLIMESTEEL_SPRING = spring("small_slimesteel_spring", 50);
	public static final ItemEntry<TagDependentSpringItem> SMALL_STEEL_SPRING = compatSmallSpring(STEEL, 50);
	public static final ItemEntry<TagDependentSpringItem> SMALL_TIN_SPRING = compatSmallSpring(TIN, 11);
	public static final ItemEntry<TagDependentSpringItem> SMALL_URANIUM_SPRING = compatSmallSpring(URANIUM, 70);
	public static final ItemEntry<SpringItem> SMALL_VANADIUM_SPRING = spring("small_vanadium_spring", 30);
	public static final ItemEntry<SpringItem> SMALL_ZINC_SPRING = spring("small_zinc_spring", 27);

	public static final ItemEntry<TagDependentIngredientItem> ALUMINUM_WIRE = compatWire(ALUMINUM);
	public static final ItemEntry<TagDependentIngredientItem> AMETHYST_BRONZE_WIRE = compatWire(AMETHYST_BRONZE);
	public static final ItemEntry<TagDependentIngredientItem> BRONZE_WIRE = compatWire(BRONZE);
	public static final ItemEntry<TagDependentIngredientItem> CAST_IRON_WIRE = compatWire(CAST_IRON);
	public static final ItemEntry<TagDependentIngredientItem> COBALT_WIRE = compatWire(COBALT);
	public static final ItemEntry<TagDependentIngredientItem> CONSTANTAN_WIRE = compatWire(CONSTANTAN);
	public static final ItemEntry<TagDependentIngredientItem> ENDERIUM_WIRE = compatWire(ENDERIUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> HEPATIZON_WIRE = compatWire(HEPATIZON);
	public static final ItemEntry<TagDependentIngredientItem> INVAR_WIRE = compatWire(INVAR);
	public static final ItemEntry<TagDependentIngredientItem> LEAD_WIRE = compatWire(LEAD);
	public static final ItemEntry<TagDependentIngredientItem> LUMIUM_WIRE = compatWire(LUMIUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> MANYULLYN_WIRE = compatWire(MANYULLYN);
	public static final ItemEntry<TagDependentIngredientItem> NICKEL_WIRE = compatWire(NICKEL);
	public static final ItemEntry<TagDependentIngredientItem> OSMIUM_WIRE = compatWire(OSMIUM);
	public static final ItemEntry<TagDependentIngredientItem> PALLADIUM_WIRE = compatWire(PALLADIUM);
	public static final ItemEntry<TagDependentIngredientItem> PIG_IRON_WIRE = compatWire(PIG_IRON);
	public static final ItemEntry<TagDependentIngredientItem> PLATINUM_WIRE = compatWire(PLATINUM);
	public static final ItemEntry<TagDependentIngredientItem> PURE_GOLD_WIRE = compatWire(PURE_GOLD);
	public static final ItemEntry<TagDependentIngredientItem> REFINED_GLOWSTONE_WIRE = compatWire(REFINED_GLOWSTONE);
	public static final ItemEntry<TagDependentIngredientItem> REFINED_OBSIDIAN_WIRE = compatWire(REFINED_OBSIDIAN);
	public static final ItemEntry<TagDependentIngredientItem> RHODIUM_WIRE = compatWire(RHODIUM);
	public static final ItemEntry<TagDependentIngredientItem> ROSE_GOLD_WIRE = compatWire(ROSE_GOLD);
	public static final ItemEntry<TagDependentIngredientItem> SIGNALUM_WIRE = compatWire(SIGNALUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> SILVER_WIRE = compatWire(SILVER);
	public static final ItemEntry<TagDependentIngredientItem> STEEL_WIRE = compatWire(STEEL);
	public static final ItemEntry<TagDependentIngredientItem> TIN_WIRE = compatWire(TIN);
	public static final ItemEntry<TagDependentIngredientItem> URANIUM_WIRE = compatWire(URANIUM);

	public static final ItemEntry<Item> REDSTONE_MODULE = ingredient("redstone_module");
	public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_REDSTONE_MODULE = sequencedIngredient("incomplete_redstone_module");
	public static final ItemEntry<CopperSulfateItem> COPPER_SULFATE =
			MY_REGISTRATE.item("copper_sulfate", CopperSulfateItem::new).register();

	public static final ItemEntry<RefinedRadianceItem> REFINED_RADIANCE_SHEET =
			MY_REGISTRATE.item("refined_radiance_sheet", RefinedRadianceItem::new)
					.properties(p -> p.rarity(Rarity.UNCOMMON))
					.register();
	public static final ItemEntry<RefinedRadianceItem> REFINED_RADIANCE_ROD =
			MY_REGISTRATE.item("refined_radiance_rod", RefinedRadianceItem::new)
					.properties(p -> p.rarity(Rarity.UNCOMMON))
					.register();
	public static final ItemEntry<RefinedRadianceSpringItem> REFINED_RADIANCE_SPRING =
			MY_REGISTRATE
					.item("refined_radiance_spring", props -> new RefinedRadianceSpringItem(props, 50))
					.properties(p -> p.rarity(Rarity.UNCOMMON))
					.register();
	public static final ItemEntry<RefinedRadianceSpringItem> SMALL_REFINED_RADIANCE_SPRING =
			MY_REGISTRATE
					.item("small_refined_radiance_spring", props -> new RefinedRadianceSpringItem(props, 5))
					.properties(p -> p.rarity(Rarity.UNCOMMON))
					.register();
	public static final ItemEntry<RefinedRadianceItem> REFINED_RADIANCE_WIRE =
			MY_REGISTRATE.item("refined_radiance_wire", RefinedRadianceItem::new)
					.properties(p -> p.rarity(Rarity.UNCOMMON))
					.register();

	public static final ItemEntry<CurvingHeadItem> CONVEX_CURVING_HEAD = MY_REGISTRATE.item("convex_curving_head", CurvingHeadItem::new)
			.properties(p -> p.durability(1000))
			.register();
	public static final ItemEntry<CurvingHeadItem> CONCAVE_CURVING_HEAD = MY_REGISTRATE.item("concave_curving_head", CurvingHeadItem::new)
			.properties(p -> p.durability(1000))
			.register();
	public static final ItemEntry<CurvingHeadItem> W_SHAPED_CURVING_HEAD = MY_REGISTRATE.item("w_shaped_curving_head", CurvingHeadItem::new)
			.properties(p -> p.durability(1000))
			.register();
	public static final ItemEntry<CurvingHeadItem> V_SHAPED_CURVING_HEAD = MY_REGISTRATE.item("v_shaped_curving_head", CurvingHeadItem::new)
			.properties(p -> p.durability(1000))
			.register();

	public static final ItemEntry<RecipeCardItem> RECIPE_CARD =
			MY_REGISTRATE.item("recipe_card", RecipeCardItem::new).register();
	public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_RECIPE_CARD = sequencedIngredient("incomplete_recipe_card");

	public static final ItemEntry<Item> HELVE_HAMMER_SLOT_COVER = MY_REGISTRATE.item("helve_hammer_slot_cover", Item::new)
			.register();

	private static ItemEntry<Item> ingredient(String name) {
		return MY_REGISTRATE.item(name, Item::new)
			.register();
	}

	private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
		return MY_REGISTRATE.item(name, SequencedAssemblyItem::new)
			.register();
	}

	@SafeVarargs
	private static ItemEntry<Item> taggedIngredient(String name, TagKey<Item>... tags) {
		return MY_REGISTRATE.item(name, Item::new)
			.tag(tags)
			.register();
	}

	private static ItemEntry<TagDependentIngredientItem> compatRod(VintageCompatMetals metal) {
		String metalName = metal.getName();
		return MY_REGISTRATE
			.item(metalName + "_rod",
				props -> new TagDependentIngredientItem(props, AllTags.commonItemTag("ingots/" + metalName)))
			.register();
	}

	private static ItemEntry<TagDependentIngredientItem> compatSheet(VintageCompatMetals metal) {
		String metalName = metal.getName();
		return MY_REGISTRATE
				.item(metalName + "_sheet",
						props -> new TagDependentIngredientItem(props, AllTags.commonItemTag("ingots/" + metalName)))
				.register();
	}

	private static ItemEntry<TagDependentIngredientItem> compatWire(VintageCompatMetals metal) {
		String metalName = metal.getName();
		return MY_REGISTRATE
				.item(metalName + "_wire",
						props -> new TagDependentIngredientItem(props, AllTags.commonItemTag("ingots/" + metalName)))
				.register();
	}

	private static ItemEntry<TagDependentIngredientItem> compatRod(VintageCompatMetals metal, Rarity rarity) {
		String metalName = metal.getName();
		return MY_REGISTRATE
				.item(metalName + "_rod",
						props -> new TagDependentIngredientItem(props, AllTags.commonItemTag("ingots/" + metalName)))
				.properties(p -> p.rarity(rarity))
				.register();
	}

	private static ItemEntry<TagDependentIngredientItem> compatSheet(VintageCompatMetals metal, Rarity rarity) {
		String metalName = metal.getName();
		return MY_REGISTRATE
				.item(metalName + "_sheet",
						props -> new TagDependentIngredientItem(props, AllTags.commonItemTag("ingots/" + metalName)))
				.properties(p -> p.rarity(rarity))
				.register();
	}

	private static ItemEntry<TagDependentIngredientItem> compatWire(VintageCompatMetals metal, Rarity rarity) {
		String metalName = metal.getName();
		return MY_REGISTRATE
				.item(metalName + "_wire",
						props -> new TagDependentIngredientItem(props, AllTags.commonItemTag("ingots/" + metalName)))
				.properties(p -> p.rarity(rarity))
				.register();
	}

	private static ItemEntry<SpringItem> spring(String id, int stiffness) {
		return MY_REGISTRATE
				.item(id, props -> new SpringItem(props, stiffness))
				.register();
	}
	private static ItemEntry<SpringItem> spring(String id, int stiffness, Rarity rarity) {
		return MY_REGISTRATE
				.item(id, props -> new SpringItem(props, stiffness))
				.properties(p -> p.rarity(rarity))
				.register();
	}
	private static ItemEntry<TagDependentSpringItem> compatSpring(VintageCompatMetals metal, int stiffness) {
		String metalName = metal.getName();
		return MY_REGISTRATE
				.item(metalName + "_spring",
						props -> new TagDependentSpringItem(props, stiffness, AllTags.commonItemTag("ingots/" + metalName)))
				.register();
	}
	private static ItemEntry<TagDependentSpringItem> compatSpring(VintageCompatMetals metal, int stiffness, Rarity rarity) {
		String metalName = metal.getName();
		return MY_REGISTRATE
				.item(metalName + "_spring",
						props -> new TagDependentSpringItem(props, stiffness, AllTags.commonItemTag("ingots/" + metalName)))
				.properties(p -> p.rarity(rarity))
				.register();
	}
	private static ItemEntry<TagDependentSpringItem> compatSmallSpring(VintageCompatMetals metal, int stiffness) {
		String metalName = metal.getName();
		return MY_REGISTRATE
				.item("small_" + metalName + "_spring",
						props -> new TagDependentSpringItem(props, stiffness, AllTags.commonItemTag("ingots/" + metalName)))
				.register();
	}
	private static ItemEntry<TagDependentSpringItem> compatSmallSpring(VintageCompatMetals metal, Rarity rarity, int stiffness) {
		String metalName = metal.getName();
		return MY_REGISTRATE
				.item("small_" + metalName + "_spring",
						props -> new TagDependentSpringItem(props, stiffness, AllTags.commonItemTag("ingots/" + metalName)))
				.properties(p -> p.rarity(rarity))
				.register();
	}

	// Load this class

	public static void register() {}

}

