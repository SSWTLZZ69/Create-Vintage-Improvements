package com.negodya1.vintageimprovements.content.kinetics.grinder;

import java.util.function.Consumer;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.foundation.render.AllInstanceTypes;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.InstancerProvider;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class GrinderVisual extends SingleAxisRotatingVisual<GrinderBlockEntity> {

	public GrinderVisual(VisualizationContext visualizationContext, GrinderBlockEntity grinderBlockEntity, float v) {

		super(visualizationContext, grinderBlockEntity, v, Models.partial(AllPartialModels.SHAFT));
	}
}
