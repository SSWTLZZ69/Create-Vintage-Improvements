package com.negodya1.vintageimprovements.infrastructure.config;

import net.createmod.catnip.config.ConfigBase;

public class VCClient extends ConfigBase {

	public final ConfigBase.ConfigGroup client = group(0, "client",
			Comments.client);

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
		static String configButton = "Position of Create: Vintage Improvements config button in the main menu & pause menu";
	}
}
