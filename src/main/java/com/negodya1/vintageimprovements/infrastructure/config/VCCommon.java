package com.negodya1.vintageimprovements.infrastructure.config;

import com.simibubi.create.infrastructure.config.CCommon;
import net.createmod.catnip.config.ConfigBase;

public class VCCommon extends ConfigBase {

	public final ConfigBase.ConfigGroup common = group(0, "common", Comments.common);

	public final ConfigInt defaultBeltGrinderSkin = i(0, 0, 4, "defaultBeltGrinderSkin", Comments.defaultBeltGrinderSkin);

	public final ConfigBool easyCentrifuge = b(false, "easyCentrifuge", Comments.easyCentrifuge);

	public final ConfigBool forceCompatItemsIntoCreativeTab = b(false, "forceCompatItemsIntoCreativeTab", Comments.forceCompatItemsIntoCreativeTab);
	public final ConfigBool legacyMaterialsIntoCreativeTab = b(false, "legacyMaterialsIntoCreativeTab", Comments.legacyMaterialsIntoCreativeTab);

	public final ConfigGroup hideItems = group(1, "hideItems",
			Comments.hideItems);

	public final ConfigBool hideSprings = b(false, "hideSprings", Comments.hideSprings);
	public final ConfigBool hideSmallSprings = b(false, "hideSmallSprings", Comments.hideSmallSprings);
	public final ConfigBool hideRods = b(false, "hideRods", Comments.hideRods);
	public final ConfigBool hideSheets = b(false, "hideSheets", Comments.hideSheets);
	public final ConfigBool hideWires = b(false, "hideWires", Comments.hideWires);

	@Override
	public String getName() {
		return "common";
	}

	private static class Comments {
		static String common = "Client/server settings";
		static String defaultBeltGrinderSkin = "Defines default Belt Grinder appearance";
		static String easyCentrifuge = "You can insert and extract from the Centrifuge while it is working.";
		static String forceCompatItemsIntoCreativeTab = "If enabled, compat items with not loaded mod still appears in the creative tab";
		static String legacyMaterialsIntoCreativeTab = "If enabled, Shadow Steel & Refined Radiance items appears in the creative tab";
		static String hideItems = "Choose items to hide from creative tab & JEI";
		static String hideSprings = "Hides all Springs from creative tab & JEI (you still can craft it)";
		static String hideSmallSprings = "Hides all Small Springs from creative tab & JEI (you still can craft it)";
		static String hideRods = "Hides all Rods from creative tab & JEI (you still can craft it)";
		static String hideSheets = "Hides all Sheets from creative tab & JEI (you still can craft it)";
		static String hideWires = "Hides all Wires from creative tab & JEI (you still can craft it)";
	}
}
