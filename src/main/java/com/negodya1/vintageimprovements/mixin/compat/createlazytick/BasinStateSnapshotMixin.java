package com.negodya1.vintageimprovements.mixin.compat.createlazytick;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber.VacuumChamberBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntity;

@Pseudo
@Mixin(targets = "net.pinkcats.createlazytick.bridge.Basin.BasinStateSnapshot", remap = false)
public abstract class BasinStateSnapshotMixin {

	@Shadow(remap = false)
	private int fluidHash;

	@Inject(method = "<init>", at = @At("RETURN"), remap = false)
	private void vintageImprovements$includeCompressorRecipeContext(BasinBlockEntity basin, CallbackInfo callback) {
		if (basin.getLevel() == null)
			return;

		BlockEntity operator = basin.getLevel()
				.getBlockEntity(basin.getBlockPos().above(2));
		if (operator instanceof VacuumChamberBlockEntity vacuumChamber)
			fluidHash = 31 * fluidHash + vacuumChamber.vintageImprovements$getRecipeContextVersion();
	}
}
