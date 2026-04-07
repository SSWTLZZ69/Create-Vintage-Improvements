package com.negodya1.vintageimprovements.foundation.recipe;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class VintageProcessingRecipeSerializer<R extends ProcessingRecipe<?, ProcessingRecipeParams>> implements RecipeSerializer<R> {

    private final ProcessingRecipe.Factory<ProcessingRecipeParams, R> factory;
    private final MapCodec<R> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

    public VintageProcessingRecipeSerializer(ProcessingRecipe.Factory<ProcessingRecipeParams, R> factory) {
        this.factory = factory;
        this.codec = ProcessingRecipe.codec(factory, ProcessingRecipeParams.CODEC);
        this.streamCodec = ProcessingRecipe.streamCodec(factory, ProcessingRecipeParams.STREAM_CODEC);
    }

    @Override
    public MapCodec<R> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
        return streamCodec;
    }

    public ProcessingRecipe.Factory<ProcessingRecipeParams, R> factory() {
        return factory;
    }
}
