package com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.mojang.datafixers.util.Either;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.Optional;
import java.util.function.Function;

public class PressurizingRecipeParams extends ProcessingRecipeParams {
	public static final int DEFAULT_SECONDARY_FLUID_OUTPUT = -1;
	public static final int DEFAULT_SECONDARY_FLUID_INPUT = -1;
	private static final Codec<Either<Integer, FluidStack>> SECONDARY_FLUID_CODEC =
			Codec.either(Codec.INT, FluidStack.CODEC);

	public static final MapCodec<PressurizingRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			codec(PressurizingRecipeParams::new).forGetter(Function.identity()),
			SECONDARY_FLUID_CODEC.optionalFieldOf("secondary_fluid_output")
					.forGetter(params -> params.secondaryFluidField(params.secondaryFluidResults, params.secondaryFluidOutput)),
			SECONDARY_FLUID_CODEC.optionalFieldOf("secondaryFluidOutput").forGetter(params -> Optional.empty()),
			SECONDARY_FLUID_CODEC.optionalFieldOf("secondary_fluid_input")
					.forGetter(params -> params.secondaryFluidField(params.secondaryFluidInputs, params.secondaryFluidInput)),
			SECONDARY_FLUID_CODEC.optionalFieldOf("secondaryFluidInput").forGetter(params -> Optional.empty())
	).apply(instance, (params, secondaryOutput, legacySecondaryOutput, secondaryInput, legacySecondaryInput) -> {
		params.setSecondaryOutput(secondaryOutput.or(() -> legacySecondaryOutput).orElse(null));
		params.setSecondaryInput(secondaryInput.or(() -> legacySecondaryInput).orElse(null));
		return params;
	}));

	public static final StreamCodec<RegistryFriendlyByteBuf, PressurizingRecipeParams> STREAM_CODEC =
			streamCodec(PressurizingRecipeParams::new);

	protected int secondaryFluidResults = DEFAULT_SECONDARY_FLUID_OUTPUT;
	protected int secondaryFluidInputs = DEFAULT_SECONDARY_FLUID_INPUT;
	protected FluidStack secondaryFluidOutput = FluidStack.EMPTY;
	protected FluidStack secondaryFluidInput = FluidStack.EMPTY;

	private Optional<Either<Integer, FluidStack>> secondaryFluidField(int index, FluidStack stack) {
		if (!stack.isEmpty())
			return Optional.of(Either.right(stack));
		return index >= 0 ? Optional.of(Either.left(index)) : Optional.empty();
	}

	private void setSecondaryOutput(Either<Integer, FluidStack> secondaryOutput) {
		if (secondaryOutput == null) {
			secondaryFluidResults = DEFAULT_SECONDARY_FLUID_OUTPUT;
			secondaryFluidOutput = FluidStack.EMPTY;
			return;
		}

		secondaryOutput.ifLeft(index -> secondaryFluidResults = index)
				.ifRight(stack -> {
					secondaryFluidOutput = stack.copy();
					secondaryFluidResults = fluidResults.size();
					fluidResults.add(stack.copy());
				});
	}

	private void setSecondaryInput(Either<Integer, FluidStack> secondaryInput) {
		if (secondaryInput == null) {
			secondaryFluidInputs = DEFAULT_SECONDARY_FLUID_INPUT;
			secondaryFluidInput = FluidStack.EMPTY;
			return;
		}

		secondaryInput.ifLeft(index -> secondaryFluidInputs = index)
				.ifRight(stack -> {
					secondaryFluidInput = stack.copy();
					secondaryFluidInputs = fluidIngredients.size();
					fluidIngredients.add(SizedFluidIngredient.of(stack));
				});
	}

	protected final int secondaryFluidResults() {
		return secondaryFluidResults;
	}

	protected final int secondaryFluidInputs() {
		return secondaryFluidInputs;
	}

	public static PressurizingRecipeParams fromRecipe(PressurizingRecipe recipe) {
		if (recipe.getParams() instanceof PressurizingRecipeParams params) {
			params.secondaryFluidResults = recipe.getSecondaryFluidResults();
			params.secondaryFluidInputs = recipe.getSecondaryFluidInputs();
			return params;
		}

		PressurizingRecipeParams params = new PressurizingRecipeParams();
		params.ingredients.addAll(recipe.getIngredients());
		params.fluidIngredients.addAll(recipe.getFluidIngredients());
		params.results.addAll(recipe.getRollableResults());
		params.fluidResults.addAll(recipe.getFluidResults());
		params.processingDuration = recipe.getProcessingDuration();
		params.requiredHeat = recipe.getRequiredHeat();
		params.secondaryFluidResults = recipe.getSecondaryFluidResults();
		params.secondaryFluidInputs = recipe.getSecondaryFluidInputs();
		if (params.secondaryFluidResults >= 0 && params.secondaryFluidResults < params.fluidResults.size())
			params.secondaryFluidOutput = params.fluidResults.get(params.secondaryFluidResults).copy();
		if (params.secondaryFluidInputs >= 0 && params.secondaryFluidInputs < params.fluidIngredients.size()) {
			FluidStack[] fluids = params.fluidIngredients.get(params.secondaryFluidInputs).getFluids();
			if (fluids.length > 0)
				params.secondaryFluidInput = fluids[0].copy();
		}
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
		secondaryFluidOutput = FluidStack.EMPTY;
		secondaryFluidInput = FluidStack.EMPTY;
		if (secondaryFluidResults >= 0 && secondaryFluidResults < fluidResults.size())
			secondaryFluidOutput = fluidResults.get(secondaryFluidResults).copy();
		if (secondaryFluidInputs >= 0 && secondaryFluidInputs < fluidIngredients.size()) {
			FluidStack[] fluids = fluidIngredients.get(secondaryFluidInputs).getFluids();
			if (fluids.length > 0)
				secondaryFluidInput = fluids[0].copy();
		}
	}
}
