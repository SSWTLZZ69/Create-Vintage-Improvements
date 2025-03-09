package com.negodya1.vintageimprovements.content.kinetics.centrifuge;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;

public class CentrifugeVisual extends SingleAxisRotatingVisual<CentrifugeBlockEntity> {

	public CentrifugeVisual(VisualizationContext visualizationContext, CentrifugeBlockEntity blockEntity, float v) {

        super(visualizationContext, blockEntity, v, Models.partial(AllPartialModels.SHAFT));
    }
}
