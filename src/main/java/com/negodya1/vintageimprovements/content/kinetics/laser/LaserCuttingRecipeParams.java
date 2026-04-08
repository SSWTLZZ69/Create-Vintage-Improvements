package com.negodya1.vintageimprovements.content.kinetics.laser;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.function.Function;

public class LaserCuttingRecipeParams extends ProcessingRecipeParams {
	public static final int DEFAULT_ENERGY = 0;
	public static final int DEFAULT_MAX_CHARGE_RATE = 0;

	public static final MapCodec<LaserCuttingRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			codec(LaserCuttingRecipeParams::new).forGetter(Function.identity()),
			Codec.INT.optionalFieldOf("energy").forGetter(params -> Optional.of(params.energy)),
			Codec.INT.optionalFieldOf("max_charge_rate").forGetter(params -> Optional.of(params.maxChargeRate)),
			Codec.INT.optionalFieldOf("maxChargeRate").forGetter(params -> Optional.empty())
	).apply(instance, (params, energy, maxChargeRate, legacyMaxChargeRate) -> {
		params.energy = energy.orElse(DEFAULT_ENERGY);
		params.maxChargeRate = maxChargeRate.or(() -> legacyMaxChargeRate).orElse(DEFAULT_MAX_CHARGE_RATE);
		return params;
	}));

	public static final StreamCodec<RegistryFriendlyByteBuf, LaserCuttingRecipeParams> STREAM_CODEC =
			streamCodec(LaserCuttingRecipeParams::new);

	protected int energy = DEFAULT_ENERGY;
	protected int maxChargeRate = DEFAULT_MAX_CHARGE_RATE;

	protected final int energy() {
		return energy;
	}

	protected final int maxChargeRate() {
		return maxChargeRate;
	}

	@Override
	protected void encode(RegistryFriendlyByteBuf buffer) {
		super.encode(buffer);
		ByteBufCodecs.VAR_INT.encode(buffer, energy);
		ByteBufCodecs.VAR_INT.encode(buffer, maxChargeRate);
	}

	@Override
	protected void decode(RegistryFriendlyByteBuf buffer) {
		super.decode(buffer);
		energy = ByteBufCodecs.VAR_INT.decode(buffer);
		maxChargeRate = ByteBufCodecs.VAR_INT.decode(buffer);
	}
}
