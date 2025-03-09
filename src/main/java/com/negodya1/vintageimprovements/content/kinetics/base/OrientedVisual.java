//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.negodya1.vintageimprovements.content.kinetics.base;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlock;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlockEntity;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class OrientedVisual<T extends KineticBlockEntity> extends KineticBlockEntityVisual<T> {
    protected final RotatingInstance rotatingModel;

    public OrientedVisual(VisualizationContext context, T blockEntity, float partialTick, Direction from, Direction to, Model model) {
        super(context, blockEntity, partialTick);
        this.rotatingModel = this.instancerProvider().instancer(AllInstanceTypes.ROTATING, model).createInstance().rotateToFace(from, to).setup(blockEntity).setPosition(this.getVisualPosition());
        this.rotatingModel.setChanged();
    }

    public static <T extends KineticBlockEntity> SimpleBlockEntityVisualizer.Factory<T> of(PartialModel partial) {
        return (context, blockEntity, partialTick) -> {
            Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.FACING);
            return new OrientedVisual(context, blockEntity, partialTick, Direction.SOUTH, facing, Models.partial(partial));
        };
    }

    public static <T extends KineticBlockEntity> SimpleBlockEntityVisualizer.Factory<T> Horizontal(PartialModel partial) {
        return (context, blockEntity, partialTick) -> {
            Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            return new OrientedVisual(context, blockEntity, partialTick, Direction.SOUTH, facing, Models.partial(partial));
        };
    }

    public void update(float pt) {
        this.rotatingModel.setup((KineticBlockEntity)this.blockEntity).setChanged();
    }

    public void updateLight(float partialTick) {
        this.relight(new FlatLit[]{this.rotatingModel});
    }

    protected void _delete() {
        this.rotatingModel.delete();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(this.rotatingModel);
    }
}
