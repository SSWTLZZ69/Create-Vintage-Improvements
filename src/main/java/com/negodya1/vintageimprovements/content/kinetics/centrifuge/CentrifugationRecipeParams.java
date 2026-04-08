package com.negodya1.vintageimprovements.content.kinetics.centrifuge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.function.Function;

public class CentrifugationRecipeParams extends ProcessingRecipeParams {
    public static final int DEFAULT_MINIMAL_RPM = 100;

    public static final MapCodec<CentrifugationRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            codec(CentrifugationRecipeParams::new).forGetter(Function.identity()),
            Codec.INT.optionalFieldOf("minimal_rpm").forGetter(params -> Optional.of(params.minimalRPM)),
            Codec.INT.optionalFieldOf("minimalRPM").forGetter(params -> Optional.empty())
    ).apply(instance, (params, minimalRPM, legacyMinimalRPM) -> {
        params.minimalRPM = minimalRPM.or(() -> legacyMinimalRPM).orElse(DEFAULT_MINIMAL_RPM);
        return params;
    }));

    public static final StreamCodec<RegistryFriendlyByteBuf, CentrifugationRecipeParams> STREAM_CODEC =
            streamCodec(CentrifugationRecipeParams::new);

    protected int minimalRPM = DEFAULT_MINIMAL_RPM;

    protected final int minimalRPM() {
        return minimalRPM;
    }

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        ByteBufCodecs.VAR_INT.encode(buffer, minimalRPM);
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        minimalRPM = ByteBufCodecs.VAR_INT.decode(buffer);
    }
}
