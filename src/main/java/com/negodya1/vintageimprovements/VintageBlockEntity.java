package com.negodya1.vintageimprovements;

import com.negodya1.vintageimprovements.content.kinetics.base.OrientedVisual;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugeBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugeVisual;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugeRenderer;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugeStructuralBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.coiling.CoilingBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.coiling.CoilingRenderer;
import com.negodya1.vintageimprovements.content.kinetics.curving_press.CurvingPressBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.curving_press.CurvingPressRenderer;
import com.negodya1.vintageimprovements.content.kinetics.curving_press.CurvingVisual;
import com.negodya1.vintageimprovements.content.kinetics.grinder.GrinderBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.grinder.GrinderRenderer;
import com.negodya1.vintageimprovements.content.kinetics.grinder.GrinderVisual;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.*;
import com.negodya1.vintageimprovements.content.kinetics.laser.LaserBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.laser.LaserRenderer;
import com.negodya1.vintageimprovements.content.kinetics.laser.LaserVisual;
import com.negodya1.vintageimprovements.content.kinetics.lathe.*;
import com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber.VacuumChamberBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber.VacuumChamberVisual;
import com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber.VacuumChamberRenderer;
import com.negodya1.vintageimprovements.content.kinetics.vibration.VibratingTableBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.vibration.VibratingTableRenderer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import static com.negodya1.vintageimprovements.VintageImprovements.MY_REGISTRATE;

public class VintageBlockEntity {
    public static final BlockEntityEntry<GrinderBlockEntity> GRINDER = MY_REGISTRATE
            .blockEntity("grinder", GrinderBlockEntity::new)
            .visual(() -> GrinderVisual::new)
            .validBlocks(VintageBlocks.BELT_GRINDER)
            .renderer(() -> GrinderRenderer::new)
            .register();
    public static final BlockEntityEntry<CoilingBlockEntity> COILING = MY_REGISTRATE
            .blockEntity("coiling", CoilingBlockEntity::new)
            .visual(() -> OrientedRotatingVisual.backHorizontal(AllPartialModels.SHAFT_HALF))
            .validBlocks(VintageBlocks.SPRING_COILING_MACHINE)
            .renderer(() -> CoilingRenderer::new)
            .register();
    public static final BlockEntityEntry<VacuumChamberBlockEntity> VACUUM = MY_REGISTRATE
            .blockEntity("vacuum_chamber", VacuumChamberBlockEntity::new)
            .visual(() ->  VacuumChamberVisual::new)
            .validBlocks(VintageBlocks.VACUUM_CHAMBER)
            .renderer(() -> VacuumChamberRenderer::new)
            .register();
    public static final BlockEntityEntry<VibratingTableBlockEntity> VIBRATION = MY_REGISTRATE
            .blockEntity("vibration", VibratingTableBlockEntity::new)
            .visual(() -> SingleAxisRotatingVisual::shaft)
            .validBlocks(VintageBlocks.VIBRATING_TABLE)
            .renderer(() -> VibratingTableRenderer::new)
            .register();

    public static final BlockEntityEntry<CentrifugeBlockEntity> CENTRIFUGE = MY_REGISTRATE
            .blockEntity("centrifuge", CentrifugeBlockEntity::new)
            .visual(() -> CentrifugeVisual::new)
            .validBlocks(VintageBlocks.CENTRIFUGE)
            .renderer(() -> CentrifugeRenderer::new)
            .register();

    public static final BlockEntityEntry<CentrifugeStructuralBlockEntity> CENTRIFUGE_STRUCTURAL = MY_REGISTRATE
            .blockEntity("centrifuge_structural", CentrifugeStructuralBlockEntity::new)
            .validBlocks(VintageBlocks.CENTRIFUGE_STRUCTURAL)
            .register();

    public static final BlockEntityEntry<CurvingPressBlockEntity> CURVING_PRESS = MY_REGISTRATE
            .blockEntity("curving_press", CurvingPressBlockEntity::new)
            .visual(() -> CurvingVisual::new)
            .validBlocks(VintageBlocks.CURVING_PRESS)
            .renderer(() -> CurvingPressRenderer::new)
            .register();

    public static final BlockEntityEntry<HelveBlockEntity> HELVE = MY_REGISTRATE
            .blockEntity("helve_hammer", HelveBlockEntity::new)
            .validBlocks(VintageBlocks.HELVE)
            .renderer(() -> HelveItemsRenderer::new)
            .register();

    public static final BlockEntityEntry<HelveKineticBlockEntity> HELVE_KINETIC = MY_REGISTRATE
            .blockEntity("helve_kinetic", HelveKineticBlockEntity::new)
            .visual(() -> HelveVisual::new)
            .validBlocks(VintageBlocks.HELVE_KINETIC)
            .renderer(() -> HelveRenderer::new)
            .register();

    public static final BlockEntityEntry<LatheRotatingBlockEntity> LATHE_ROTATING = MY_REGISTRATE
            .blockEntity("lathe_rotating", LatheRotatingBlockEntity::new)
            .visual(() -> OrientedVisual.horizontal(AllPartialModels.SHAFT_HALF))
            .validBlocks(VintageBlocks.LATHE_ROTATING)
            .renderer(() -> LatheRotatingRenderer::new)
            .register();

    public static final BlockEntityEntry<LatheMovingBlockEntity> LATHE_MOVING = MY_REGISTRATE
            .blockEntity("lathe_moving", LatheMovingBlockEntity::new)
            .visual(() -> SingleAxisRotatingVisual::shaft)
            .validBlocks(VintageBlocks.LATHE_MOVING)
            .renderer(() -> LatheMovingRenderer::new)
            .register();

    public static final BlockEntityEntry<LaserBlockEntity> LASER = MY_REGISTRATE
            .blockEntity("laser", LaserBlockEntity::new)
            .visual(() -> LaserVisual::new)
            .validBlocks(VintageBlocks.LASER)
            .renderer(() -> LaserRenderer::new)
            .register();

    public static void register() {}

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, GRINDER.get(), (be, side) -> be.inventory);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, COILING.get(), (be, side) -> be.inventory);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, VIBRATION.get(), (be, side) -> be.capability);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, LATHE_ROTATING.get(), (be, side) -> be.capability);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, HELVE.get(), (be, side) -> be.capability);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CENTRIFUGE.get(), (be, side) -> be.capability);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CENTRIFUGE_STRUCTURAL.get(), (be, side) -> {
            CentrifugeBlockEntity master = be.getMaster();
            return master != null ? master.capability : null;
        });

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, VACUUM.get(), (be, side) -> be.fluidCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, CENTRIFUGE.get(), (be, side) -> be.fluidCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, CENTRIFUGE_STRUCTURAL.get(), (be, side) -> {
            CentrifugeBlockEntity master = be.getMaster();
            return master != null ? master.fluidCapability : null;
        });

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, LASER.get(), (be, side) ->
                be.isEnergyInput(side) || be.isEnergyOutput(side) ? be.getEnergyStorage() : null);
    }
}
