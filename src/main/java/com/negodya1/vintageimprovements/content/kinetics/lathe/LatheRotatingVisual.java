package com.negodya1.vintageimprovements.content.kinetics.lathe;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class LatheRotatingVisual extends OrientedRotatingVisual<LatheRotatingBlockEntity> {
    public LatheRotatingVisual(VisualizationContext context, LatheRotatingBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Direction.SOUTH,
                blockEntity.getBlockState().getValue(BlockStateProperties.FACING),
                Models.partial(AllPartialModels.SHAFT_HALF));
    }
}
