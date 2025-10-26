package com.negodya1.vintageimprovements.content.kinetics.curving_press;

import com.simibubi.create.content.kinetics.base.ShaftVisual;

import dev.engine_room.flywheel.api.visual.DynamicVisual;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;

public class CurvingVisual extends ShaftVisual<CurvingPressBlockEntity> implements SimpleDynamicVisual {

    public CurvingVisual(VisualizationContext context, CurvingPressBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
    }

    @Override
    public void beginFrame(DynamicVisual.Context context) {

    }
}
