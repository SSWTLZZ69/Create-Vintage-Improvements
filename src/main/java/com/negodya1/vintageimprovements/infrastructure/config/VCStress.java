package com.negodya1.vintageimprovements.infrastructure.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.negodya1.vintageimprovements.VintageImprovements;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.kinetics.BlockStressValues.IStressValueProvider;
import com.simibubi.create.foundation.config.ConfigBase;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class VCStress extends ConfigBase implements IStressValueProvider {
	// bump this version to reset configured values.
	private static final int VERSION = 2;

	// IDs need to be used since configs load before registration

	private static final Object2DoubleMap<ResourceLocation> DEFAULT_IMPACTS = new Object2DoubleOpenHashMap<>();
	private static final Object2DoubleMap<ResourceLocation> DEFAULT_CAPACITIES = new Object2DoubleOpenHashMap<>();

	protected final Map<ResourceLocation, ConfigValue<Double>> capacities = new HashMap<>();
	protected final Map<ResourceLocation, ConfigValue<Double>> impacts = new HashMap<>();

	@Override
	public void registerAll(Builder builder) {
		builder.comment(".", Comments.su, Comments.impact)
			.push("impact");
		DEFAULT_IMPACTS.forEach((id, value) -> this.impacts.put(id, builder.define(id.getPath(), value)));
		builder.pop();

		builder.comment(".", Comments.su, Comments.capacity)
			.push("capacity");
		DEFAULT_CAPACITIES.forEach((id, value) -> this.capacities.put(id, builder.define(id.getPath(), value)));
		builder.pop();
	}

	@Override
	public String getName() {
		return "stressValues.v" + VERSION;
	}

	@Nullable
	public double getImpact(Block block) {
		ResourceLocation id = RegisteredObjects.getKeyOrThrow(block);
		ConfigValue<Double> value = this.impacts.get(id);
		return value == null ? 0 : value.get();
	}

	@Nullable
	public double getCapacity(Block block) {
		ResourceLocation id = RegisteredObjects.getKeyOrThrow(block);
		ConfigValue<Double> value = this.capacities.get(id);
		return value == null ? 0 : value.get();
	}

	@Override
	public Couple<Integer> getGeneratedRPM(Block block) {
		ResourceLocation id = RegisteredObjects.getKeyOrThrow(block);
		Supplier<Couple<Integer>> supplier = BlockStressDefaults.GENERATOR_SPEEDS.get(id);
		return supplier == null ? null : supplier.get();
	}

	@Override
	public boolean hasImpact(Block block) {
		ResourceLocation id = RegisteredObjects.getKeyOrThrow(block);
		return this.impacts.containsKey(id);
	}

	@Override
	public boolean hasCapacity(Block block) {
		ResourceLocation id = RegisteredObjects.getKeyOrThrow(block);
		return this.capacities.containsKey(id);
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setNoImpact() {
		return setImpact(0);
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setImpact(double value) {
		return builder -> {
			ResourceLocation id = VintageImprovements.asResource(builder.getName());
			DEFAULT_IMPACTS.put(id, value);
			return builder;
		};
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setCapacity(double value) {
		return builder -> {
			ResourceLocation id = VintageImprovements.asResource(builder.getName());
			DEFAULT_CAPACITIES.put(id, value);
			return builder;
		};
	}

	private static class Comments {
		static String su = "[in Stress Units]";
		static String impact =
			"Configure the individual stress impact of mechanical blocks. Note that this cost is doubled for every speed increase it receives.";
		static String capacity = "Configure how much stress a source can accommodate for.";
	}

}
