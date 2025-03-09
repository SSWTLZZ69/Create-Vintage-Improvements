package com.negodya1.vintageimprovements.content.kinetics.curving_press;

import java.util.function.Consumer;

import com.negodya1.vintageimprovements.VintagePartialModels;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import org.joml.Quaternionf;

import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.ShaftVisual;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.math.AngleHelper;

public class CurvingVisual extends ShaftVisual<CurvingPressBlockEntity> implements SimpleDynamicVisual {

    public CurvingVisual(VisualizationContext context, CurvingPressBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
    }

    @Override
    public void beginFrame(Context context) {

    }
}
