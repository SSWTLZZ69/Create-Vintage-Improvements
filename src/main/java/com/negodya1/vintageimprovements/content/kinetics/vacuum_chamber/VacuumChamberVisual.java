package com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber;

import com.negodya1.vintageimprovements.VintagePartialModels;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;

import java.util.function.Consumer;

public class VacuumChamberVisual extends SingleAxisRotatingVisual<VacuumChamberBlockEntity> implements SimpleDynamicVisual {

	private final OrientedInstance mixerPole;
	private final VacuumChamberBlockEntity mixer;

	public VacuumChamberVisual(VisualizationContext context, VacuumChamberBlockEntity blockEntity, float partialTick) {
		super(context, blockEntity, partialTick, Models.partial(VintagePartialModels.VACUUM_COG));
		this.mixer = blockEntity;

		mixerPole = instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(VintagePartialModels.VACUUM_PIPE))
				.createInstance();

		animate(partialTick);
	}

	@Override
	public void beginFrame(DynamicVisual.Context ctx) {
		animate(ctx.partialTick());
	}

	private void animate(float pt) {
		float renderedHeadOffset = mixer.getRenderedHeadOffset(pt);

		transformPole(renderedHeadOffset);
	}

	private void transformPole(float renderedHeadOffset) {
		mixerPole.position(getVisualPosition())
				.translatePosition(0, -renderedHeadOffset, 0)
				.setChanged();
	}

	@Override
	public void updateLight(float partialTick) {
		super.updateLight(partialTick);
		relight(mixerPole);
	}

	@Override
	protected void _delete() {
		super._delete();
		mixerPole.delete();
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		super.collectCrumblingInstances(consumer);
		consumer.accept(mixerPole);
	}
}
