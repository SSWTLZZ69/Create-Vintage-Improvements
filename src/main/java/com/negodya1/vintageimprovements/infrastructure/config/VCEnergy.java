package com.negodya1.vintageimprovements.infrastructure.config;

import net.createmod.catnip.config.ConfigBase;

public class VCEnergy extends ConfigBase {

	public final ConfigBase.ConfigGroup energy = group(0, "energy",
			Comments.energy);

	public final ConfigBool forceEnergy =
			b(false, "forceEnergy", Comments.forceEnergy);

	public final ConfigGroup laser = group(1, "laser",
			Comments.laser);
	public final ConfigInt laserMaxInput =
			i(10000, 0, Integer.MAX_VALUE, "laserMaxInput", Comments.laserMaxInput);
	public final ConfigInt laserCapacity =
			i(40000, 0, Integer.MAX_VALUE, "laserCapacity", Comments.laserCapacity);
	public final ConfigInt laserChargeRate =
			i(5000, 0, Integer.MAX_VALUE, "laserChargeRate", Comments.laserChargeRate);
	public final ConfigInt laserRecipeChargeRate =
			i(2000, 0, Integer.MAX_VALUE, "laserRecipeChargeRate", Comments.laserRecipeChargeRate);

	@Override
	public String getName() {
		return "energy";
	}

	private static class Comments {
		static String energy = "Energy configs";
		static String laser = "Laser settings";
		static String laserMaxInput = "Laser max input in FE/t (Energy transfer)";
		static String laserCapacity = "Laser internal capacity in FE";
		static String laserChargeRate = "Laser charge rate in FE/t";
		static String laserRecipeChargeRate = "Laser charge rate in FE/t for recipes";
		static String forceEnergy = "Enable if energy machines doesn't consume energy";
	}

}
