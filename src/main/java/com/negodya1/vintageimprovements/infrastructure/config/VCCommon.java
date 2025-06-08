package com.negodya1.vintageimprovements.infrastructure.config;

import com.simibubi.create.infrastructure.config.CCommon;
import net.createmod.catnip.config.ConfigBase;

public class VCCommon extends ConfigBase {

	public final ConfigBase.ConfigGroup common = group(0, "common", Comments.common);

	public final ConfigInt defaultBeltGrinderSkin = i(0, 0, 4, "defaultBeltGrinderSkin", Comments.defaultBeltGrinderSkin);

	public final ConfigBool easyCentrifuge = b(false, "easyCentrifuge", Comments.easyCentrifuge);

	@Override
	public String getName() {
		return "common";
	}

	private static class Comments {
		static String common = "Client/server settings";
		static String defaultBeltGrinderSkin = "Defines default Belt Grinder appearance";
		static String easyCentrifuge = "You can insert and extract from the Centrifuge while it is working.";
	}
}
