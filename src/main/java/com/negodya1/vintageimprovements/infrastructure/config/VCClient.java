package com.negodya1.vintageimprovements.infrastructure.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class VCClient extends ConfigBase {

	public final ConfigBase.ConfigGroup client = group(0, "client",
			Comments.client);


	public final ConfigBool forceCompatItemsIntoCreativeTab = b(false, "forceCompatItemsIntoCreativeTab", Comments.forceCompatItemsIntoCreativeTab);
	public final ConfigBool legacyMaterialsIntoCreativeTab = b(false, "legacyMaterialsIntoCreativeTab", Comments.legacyMaterialsIntoCreativeTab);

	public final ConfigGroup hideItems = group(1, "hideItems",
			Comments.hideItems);

	public final ConfigBool hideSprings = b(false, "hideSprings", Comments.hideSprings);
	public final ConfigBool hideSmallSprings = b(false, "hideSmallSprings", Comments.hideSmallSprings);
	public final ConfigBool hideRods = b(false, "hideRods", Comments.hideRods);
	public final ConfigBool hideSheets = b(false, "hideSheets", Comments.hideSheets);
	public final ConfigBool hideWires = b(false, "hideWires", Comments.hideWires);


	public final ConfigGroup configButton = group(1, "configButton",
			Comments.configButton);

	public final ConfigInt mainMenuConfigButtonRow = i(1, 0, 4, "mainMenuConfigButtonRow",
			Comments.mainMenuConfigButtonRow);
	public final ConfigInt mainMenuConfigButtonOffsetX = i(-4, Integer.MIN_VALUE, Integer.MAX_VALUE, "mainMenuConfigButtonOffsetX",
			Comments.mainMenuConfigButtonOffsetX);
	public final ConfigInt ingameMenuConfigButtonRow = i(2, 0, 5, "ingameMenuConfigButtonRow",
			Comments.ingameMenuConfigButtonRow);
	public final ConfigInt ingameMenuConfigButtonOffsetX = i(-4, Integer.MIN_VALUE, Integer.MAX_VALUE, "ingameMenuConfigButtonOffsetX",
			Comments.ingameMenuConfigButtonOffsetX);

	@Override
	public String getName() {
		return "client";
	}

	private static class Comments {
		static String client = "Client-only settings - If you're looking for general settings, look inside your worlds serverconfig folder!";

		static String[] mainMenuConfigButtonRow = new String[]{
				"Choose the menu row that the Vintage Improvements config button appears on in the main menu",
				"Set to 0 to disable the button altogether"
		};
		static String[] mainMenuConfigButtonOffsetX = new String[]{
				"Offset the Vintage Improvements config button in the main menu by this many pixels on the X axis",
				"The sign (-/+) of this value determines what side of the row the button appears on (left/right)"
		};
		static String[] ingameMenuConfigButtonRow = new String[]{
				"Choose the menu row that the Vintage Improvements config button appears on in the in-game menu",
				"Set to 0 to disable the button altogether"
		};
		static String[] ingameMenuConfigButtonOffsetX = new String[]{
				"Offset the Vintage Improvements config button in the in-game menu by this many pixels on the X axis",
				"The sign (-/+) of this value determines what side of the row the button appears on (left/right)"
		};
		static String forceCompatItemsIntoCreativeTab = "If enabled, compat items with not loaded mod still appears in the creative tab";
		static String legacyMaterialsIntoCreativeTab = "If enabled, Shadow Steel & Refined Radiance items appears in the creative tab";
		static String configButton = "Position of Create: Vintage Improvements config button in the main menu & pause menu";
		static String hideItems = "Choose items to hide from creative tab & JEI";
		static String hideSprings = "Hides all Springs from creative tab & JEI (you still can craft it)";
		static String hideSmallSprings = "Hides all Small Springs from creative tab & JEI (you still can craft it)";
		static String hideRods = "Hides all Rods from creative tab & JEI (you still can craft it)";
		static String hideSheets = "Hides all Sheets from creative tab & JEI (you still can craft it)";
		static String hideWires = "Hides all Wires from creative tab & JEI (you still can craft it)";
	}
}
