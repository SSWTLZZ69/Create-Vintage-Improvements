package com.negodya1.vintageimprovements.foundation.data.recipe;


import com.simibubi.create.foundation.utility.CreateLang;

public enum VintageCompatMetals {
	ALUMINUM(),
	AMETHYST_BRONZE(),
	BRONZE(),
	CAST_IRON(),
	COBALT(),
	CONSTANTAN(),
	ELECTRUM(),
	ENDERIUM(),
	HEPATIZON(),
	INVAR(),
	LEAD(),
	LUMIUM(),
	MANYULLYN(),
	NICKEL(),
	OSMIUM(),
	PALLADIUM(),
	PIG_IRON(),
	PLATINUM(),
	PURE_GOLD(),
	REFINED_GLOWSTONE(),
	REFINED_OBSIDIAN(),
	RHODIUM(),
	ROSE_GOLD(),
	SIGNALUM(),
	SILVER(),
	STEEL(Mods.MEK),
	TIN(),
	URANIUM();

	private final Mods[] mods;
	private final String name;

	VintageCompatMetals(Mods... mods) {
		this.name = CreateLang.asId(name());
		this.mods = mods;
	}

	public String getName() {
		return name;
	}

	/**
	 * These mods must provide an ingot and nugget variant of the corresponding metal.
	 */
	public Mods[] getMods() {
		return mods;
	}
}
