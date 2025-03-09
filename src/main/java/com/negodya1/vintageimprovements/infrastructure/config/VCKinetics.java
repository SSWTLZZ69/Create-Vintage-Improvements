package com.negodya1.vintageimprovements.infrastructure.config;

import com.simibubi.create.infrastructure.config.CKinetics;
import net.createmod.catnip.config.ConfigBase;

public class VCKinetics extends ConfigBase {
	public final VCStress stressValues = nested(1, VCStress::new, Comments.stress);

	@Override
	public String getName() {
		return "kinetics";
	}

	private static class Comments {
		static String stress = "Fine tune the kinetic stats of individual components";
	}

}
