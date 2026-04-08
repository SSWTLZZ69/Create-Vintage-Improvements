package com.negodya1.vintageimprovements.content.kinetics.grinder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.function.Function;

public class PolishingRecipeParams extends ProcessingRecipeParams {
    public static final MapCodec<PolishingRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            codec(PolishingRecipeParams::new).forGetter(Function.identity()),
            Codec.INT.optionalFieldOf("speed_limits").forGetter(params -> Optional.of(params.speedLimits)),
            Codec.INT.optionalFieldOf("speedLimits").forGetter(params -> Optional.empty()),
            Codec.BOOL.optionalFieldOf("fragile", false).forGetter(PolishingRecipeParams::fragile)
    ).apply(instance, (params, speedLimits, legacySpeedLimits, fragile) -> {
        params.speedLimits = speedLimits.or(() -> legacySpeedLimits).orElse(0);
        params.fragile = fragile;
        return params;
    }));

    public static final StreamCodec<RegistryFriendlyByteBuf, PolishingRecipeParams> STREAM_CODEC =
            streamCodec(PolishingRecipeParams::new);

    protected int speedLimits = 0;
    protected boolean fragile = false;

    protected final int speedLimits() {
        return speedLimits;
    }

    protected final boolean fragile() {
        return fragile;
    }

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        ByteBufCodecs.VAR_INT.encode(buffer, speedLimits);
        ByteBufCodecs.BOOL.encode(buffer, fragile);
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        speedLimits = ByteBufCodecs.VAR_INT.decode(buffer);
        fragile = ByteBufCodecs.BOOL.decode(buffer);
    }
}
