package com.negodya1.vintageimprovements.content.kinetics.coiling;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.function.Function;

public class CoilingRecipeParams extends ProcessingRecipeParams {
    public static final int DEFAULT_SPRING_COLOR = 0x9aa49d;

    private static final Codec<Integer> COLOR_CODEC = Codec.either(Codec.STRING, Codec.INT)
            .comapFlatMap(
                    either -> either.map(CoilingRecipeParams::parseColor, DataResult::success),
                    color -> Either.left(String.format("%06x", color & 0xFFFFFF))
            );

    public static final MapCodec<CoilingRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            codec(CoilingRecipeParams::new).forGetter(Function.identity()),
            COLOR_CODEC.optionalFieldOf("spring_color").forGetter(params -> Optional.of(params.springColor)),
            COLOR_CODEC.optionalFieldOf("springColor").forGetter(params -> Optional.empty())
    ).apply(instance, (params, springColor, legacySpringColor) -> {
        params.springColor = springColor.or(() -> legacySpringColor).orElse(DEFAULT_SPRING_COLOR);
        return params;
    }));

    public static final StreamCodec<RegistryFriendlyByteBuf, CoilingRecipeParams> STREAM_CODEC =
            streamCodec(CoilingRecipeParams::new);

    protected int springColor = DEFAULT_SPRING_COLOR;

    protected final int springColor() {
        return springColor;
    }

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        ByteBufCodecs.VAR_INT.encode(buffer, springColor);
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        springColor = ByteBufCodecs.VAR_INT.decode(buffer);
    }

    private static DataResult<Integer> parseColor(String color) {
        String normalized = color.trim();
        if (normalized.startsWith("#"))
            normalized = normalized.substring(1);
        if (normalized.startsWith("0x") || normalized.startsWith("0X"))
            normalized = normalized.substring(2);
        try {
            return DataResult.success(Integer.parseUnsignedInt(normalized, 16));
        } catch (NumberFormatException ex) {
            return DataResult.error(() -> "Invalid spring_color value: " + color);
        }
    }
}
