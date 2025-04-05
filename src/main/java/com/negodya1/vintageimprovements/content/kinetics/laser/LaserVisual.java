package com.negodya1.vintageimprovements.content.kinetics.laser;

import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugeBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.lathe.LatheRotatingBlockEntity;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.Direction;

public class LaserVisual extends SingleAxisRotatingVisual<LaserBlockEntity> {

	public LaserVisual(VisualizationContext visualizationContext, LaserBlockEntity blockEntity, float v) {
        super(visualizationContext, blockEntity, v, Direction.SOUTH, Models.partial(AllPartialModels.SHAFT_HALF));
    }
}
