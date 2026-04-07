package com.negodya1.vintageimprovements;

import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugeBlock;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugeGenerator;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugeItem;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugeStructuralBlock;
import com.negodya1.vintageimprovements.content.kinetics.coiling.CoilingBlock;
import com.negodya1.vintageimprovements.content.kinetics.coiling.CoilingGenerator;
import com.negodya1.vintageimprovements.content.kinetics.curving_press.CurvingPressBlock;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.*;
import com.negodya1.vintageimprovements.content.kinetics.laser.LaserBlock;
import com.negodya1.vintageimprovements.content.kinetics.lathe.LatheGenerator;
import com.negodya1.vintageimprovements.content.kinetics.lathe.LatheItem;
import com.negodya1.vintageimprovements.content.kinetics.lathe.LatheMovingBlock;
import com.negodya1.vintageimprovements.content.kinetics.lathe.LatheRotatingBlock;
import com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber.VacuumChamberBlock;
import com.negodya1.vintageimprovements.content.kinetics.vibration.VibratingTableBlock;
import com.negodya1.vintageimprovements.content.kinetics.vibration.VibratingTableGenerator;
import com.negodya1.vintageimprovements.infrastructure.config.VCStress;
import com.simibubi.create.AllTags;
import com.negodya1.vintageimprovements.content.kinetics.grinder.GrinderBlock;
import com.negodya1.vintageimprovements.content.kinetics.grinder.GrinderGenerator;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.SharedProperties;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.Tags;

import java.util.LinkedList;

import static com.negodya1.vintageimprovements.VintageImprovements.MY_REGISTRATE;
import static com.simibubi.create.foundation.data.TagGen.*;

public class VintageBlocks {

    //Machines
    public static final BlockEntry<GrinderBlock> BELT_GRINDER = MY_REGISTRATE.block("belt_grinder", GrinderBlock::new)
            .initialProperties(SharedProperties::stone)
            .addLayer(() -> RenderType::cutoutMipped)
            .properties(p -> p.mapColor(MapColor.SAND))
            .transform(axeOrPickaxe())
            .blockstate(new GrinderGenerator()::generate)
            .transform(VCStress.setImpact(4.0))
            .item()
            .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<CoilingBlock> SPRING_COILING_MACHINE = MY_REGISTRATE.block("spring_coiling_machine", CoilingBlock::new)
            .initialProperties(SharedProperties::stone)
            .addLayer(() -> RenderType::cutoutMipped)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(axeOrPickaxe())
            .blockstate(new CoilingGenerator()::generate)
            .transform(VCStress.setImpact(4.0))
            .item()
            .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<VacuumChamberBlock> VACUUM_CHAMBER = MY_REGISTRATE.block("vacuum_chamber", VacuumChamberBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.noOcclusion().mapColor(MapColor.STONE))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .transform(VCStress.setImpact(4.0))
                    .item(AssemblyOperatorBlockItem::new)
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<VibratingTableBlock> VIBRATING_TABLE = MY_REGISTRATE.block("vibrating_table", VibratingTableBlock::new)
            .initialProperties(SharedProperties::stone)
            .addLayer(() -> RenderType::cutoutMipped)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(axeOrPickaxe())
            .blockstate(new VibratingTableGenerator()::generate)
            .transform(VCStress.setImpact(2.0))
            .item()
            .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<CentrifugeBlock> CENTRIFUGE = MY_REGISTRATE.block("centrifuge", CentrifugeBlock::new)
            .initialProperties(SharedProperties::stone)
            .addLayer(() -> RenderType::cutoutMipped)
            .properties(p -> p.mapColor(MapColor.DIRT))
            .transform(axeOrPickaxe())
            .blockstate(new CentrifugeGenerator()::generate)
            .transform(VCStress.setImpact(2.0))
            .item(CentrifugeItem::new)
            .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<CentrifugeStructuralBlock> CENTRIFUGE_STRUCTURAL =
            MY_REGISTRATE.block("centrifuge_structure", CentrifugeStructuralBlock::new)
                    .initialProperties(SharedProperties::wooden)
                    .blockstate((c, p) -> p.getVariantBuilder(c.get())
                            .forAllStatesExcept(BlockStateGen.mapToAir(p), CentrifugeStructuralBlock.FACING))
                    .properties(p -> p.noOcclusion().mapColor(MapColor.DIRT))
                    .transform(axeOrPickaxe())
                    .lang("Centrifuge")
                    .register();

    public static final BlockEntry<CurvingPressBlock> CURVING_PRESS =
            MY_REGISTRATE.block("curving_press", CurvingPressBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .addLayer(() -> RenderType::cutoutMipped)
                    .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                    .transform(axeOrPickaxe())
                    .blockstate(BlockStateGen.horizontalBlockProvider(true))
                    .transform(VCStress.setImpact(8.0))
                    .item(AssemblyOperatorBlockItem::new)
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<HelveBlock> HELVE = MY_REGISTRATE.block("helve_hammer", HelveBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.DIRT))
            .transform(axeOrPickaxe())
            .blockstate(new HelveGenerator()::generate)
            .item(HelveItem::new)
            .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<HelveStructuralBlock> HELVE_STRUCTURAL =
            MY_REGISTRATE.block("helve_structure", HelveStructuralBlock::new)
                    .initialProperties(SharedProperties::wooden)
                    .blockstate((c, p) -> p.getVariantBuilder(c.get())
                            .forAllStatesExcept(BlockStateGen.mapToAir(p), HelveStructuralBlock.FACING))
                    .properties(p -> p.noOcclusion().mapColor(MapColor.DIRT))
                    .transform(axeOrPickaxe())
                    .lang("Helve Hammer")
                    .register();

    public static final BlockEntry<HelveKineticBlock> HELVE_KINETIC =
            MY_REGISTRATE.block("helve_kinetic", HelveKineticBlock::new)
                    .initialProperties(SharedProperties::wooden)
                    .addLayer(() -> RenderType::cutoutMipped)
                    .blockstate((c, p) -> p.getVariantBuilder(c.get())
                            .forAllStatesExcept(BlockStateGen.mapToAir(p), HelveKineticBlock.FACING))
                    .properties(p -> p.noOcclusion().mapColor(MapColor.DIRT))
                    .transform(axeOrPickaxe())
                    .transform(VCStress.setImpact(8.0))
                    .lang("Helve Hammer")
                    .register();

    public static final BlockEntry<LatheRotatingBlock> LATHE_ROTATING = MY_REGISTRATE.block("lathe", LatheRotatingBlock::new)
            .initialProperties(SharedProperties::stone)
            .addLayer(() -> RenderType::cutoutMipped)
            .properties(p -> p.noOcclusion().mapColor(MapColor.DIRT))
            .transform(axeOrPickaxe())
            .blockstate(new LatheGenerator()::generate)
            .item(LatheItem::new)
            .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
            .transform(customItemModel())
            .transform(VCStress.setImpact(2.0))
            .register();

    public static final BlockEntry<LatheMovingBlock> LATHE_MOVING =
            MY_REGISTRATE.block("lathe_moving", LatheMovingBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .blockstate((c, p) -> p.getVariantBuilder(c.get())
                            .forAllStatesExcept(BlockStateGen.mapToAir(p), LatheMovingBlock.FACING))
                    .properties(p -> p.noOcclusion().mapColor(MapColor.DIRT))
                    .transform(axeOrPickaxe())
                    .transform(VCStress.setImpact(4.0))
                    .lang("Lathe")
                    .register();

    public static final BlockEntry<LaserBlock> LASER =
            MY_REGISTRATE.block("laser", LaserBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .addLayer(() -> RenderType::cutoutMipped)
                    .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
                    .transform(axeOrPickaxe())
                    .blockstate(BlockStateGen.horizontalBlockProvider(true))
                    .transform(VCStress.setImpact(2.0))
                    .item(AssemblyOperatorBlockItem::new)
                    .transform(customItemModel())
                    .register();

    //Building blocks
    public static final BlockEntry<Block> VANADIUM_BLOCK = MY_REGISTRATE.block("vanadium_block", Block::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.mapColor(MapColor.GLOW_LICHEN).requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .tag(BlockTags.BEACON_BASE_BLOCKS)
            .transform(tagBlockAndItem(AllTags.commonBlockTag("storage_blocks/vanadium"), AllTags.commonItemTag("storage_blocks/vanadium")))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .lang("Block of Vanadium")
            .register();

    public static final BlockEntry<Block> SULFUR_BLOCK = MY_REGISTRATE.block("sulfur_block", Block::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.mapColor(MapColor.GLOW_LICHEN).requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .transform(tagBlockAndItem(AllTags.commonBlockTag("storage_blocks/sulfur"), AllTags.commonItemTag("storage_blocks/sulfur")))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .lang("Block of Sulfur")
            .register();

    public static void register() {}
}

