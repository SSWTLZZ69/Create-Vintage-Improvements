package com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.function.Function;

public class VacuumizingRecipeParams extends ProcessingRecipeParams {
	public static final int DEFAULT_SECONDARY_FLUID_OUTPUT = -1;
	public static final int DEFAULT_SECONDARY_FLUID_INPUT = -1;

	public static final MapCodec<VacuumizingRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			codec(VacuumizingRecipeParams::new).forGetter(Function.identity()),
			Codec.INT.optionalFieldOf("secondary_fluid_output").forGetter(params -> Optional.of(params.secondaryFluidResults)),
			Codec.INT.optionalFieldOf("secondaryFluidOutput").forGetter(params -> Optional.empty()),
			Codec.INT.optionalFieldOf("secondary_fluid_input").forGetter(params -> Optional.of(params.secondaryFluidInputs)),
			Codec.INT.optionalFieldOf("secondaryFluidInput").forGetter(params -> Optional.empty())
	).apply(instance, (params, secondaryOutput, legacySecondaryOutput, secondaryInput, legacySecondaryInput) -> {
		params.secondaryFluidResults = secondaryOutput.or(() -> legacySecondaryOutput).orElse(DEFAULT_SECONDARY_FLUID_OUTPUT);
		params.secondaryFluidInputs = secondaryInput.or(() -> legacySecondaryInput).orElse(DEFAULT_SECONDARY_FLUID_INPUT);
		return params;
	}));

	public static final StreamCodec<RegistryFriendlyByteBuf, VacuumizingRecipeParams> STREAM_CODEC =
			streamCodec(VacuumizingRecipeParams::new);

	protected int secondaryFluidResults = DEFAULT_SECONDARY_FLUID_OUTPUT;
	protected int secondaryFluidInputs = DEFAULT_SECONDARY_FLUID_INPUT;

	protected final int secondaryFluidResults() {
		return secondaryFluidResults;
	}

	protected final int secondaryFluidInputs() {
		return secondaryFluidInputs;
	}

	public static VacuumizingRecipeParams fromRecipe(VacuumizingRecipe recipe) {
		if (recipe.getParams() instanceof VacuumizingRecipeParams params) {
			params.secondaryFluidResults = recipe.getSecondaryFluidResults();
			params.secondaryFluidInputs = recipe.getSecondaryFluidInputs();
			return params;
		}

		VacuumizingRecipeParams params = new VacuumizingRecipeParams();
		params.ingredients.addAll(recipe.getIngredients());
		params.fluidIngredients.addAll(recipe.getFluidIngredients());
		params.results.addAll(recipe.getRollableResults());
		params.fluidResults.addAll(recipe.getFluidResults());
		params.processingDuration = recipe.getProcessingDuration();
		params.requiredHeat = recipe.getRequiredHeat();
		params.secondaryFluidResults = recipe.getSecondaryFluidResults();
		params.secondaryFluidInputs = recipe.getSecondaryFluidInputs();
		return params;
	}

	@Override
	protected void encode(RegistryFriendlyByteBuf buffer) {
		super.encode(buffer);
		ByteBufCodecs.VAR_INT.encode(buffer, secondaryFluidResults);
		ByteBufCodecs.VAR_INT.encode(buffer, secondaryFluidInputs);
	}

	@Override
	protected void decode(RegistryFriendlyByteBuf buffer) {
		super.decode(buffer);
		secondaryFluidResults = ByteBufCodecs.VAR_INT.decode(buffer);
		secondaryFluidInputs = ByteBufCodecs.VAR_INT.decode(buffer);
	}
}
