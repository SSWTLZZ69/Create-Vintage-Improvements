package com.negodya1.vintageimprovements;

import java.util.Optional;
import java.util.function.Supplier;

import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugationRecipe;
import com.negodya1.vintageimprovements.content.kinetics.coiling.CoilingRecipe;
import com.negodya1.vintageimprovements.content.kinetics.curving_press.CurvingRecipe;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.AutoSmithingRecipe;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.AutoUpgradeRecipe;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.HammeringRecipe;
import com.negodya1.vintageimprovements.content.kinetics.laser.LaserCuttingRecipe;
import com.negodya1.vintageimprovements.content.kinetics.lathe.TurningRecipe;
import com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber.PressurizingRecipe;
import com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber.VacuumizingRecipe;
import com.negodya1.vintageimprovements.content.kinetics.vibration.LeavesVibratingRecipe;
import com.negodya1.vintageimprovements.content.kinetics.vibration.VibratingRecipe;
import com.negodya1.vintageimprovements.foundation.recipe.VintageProcessingRecipeSerializer;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;

import com.negodya1.vintageimprovements.content.kinetics.grinder.PolishingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public enum VintageRecipes implements IRecipeTypeInfo {

    POLISHING(() -> new PolishingRecipe.Serializer<>(PolishingRecipe::new)),
    COILING(() -> new CoilingRecipe.Serializer<>(CoilingRecipe::new)),
    VACUUMIZING(VacuumizingRecipe.Serializer::new),
    VIBRATING(VibratingRecipe::new),
    LEAVES_VIBRATING(LeavesVibratingRecipe::new),
    CENTRIFUGATION(() -> new CentrifugationRecipe.Serializer<>(CentrifugationRecipe::new)),
    CURVING(() -> new CurvingRecipe.Serializer<>(CurvingRecipe::new)),
    PRESSURIZING(PressurizingRecipe.Serializer::new),
    HAMMERING(() -> new HammeringRecipe.Serializer<>(HammeringRecipe::new)),
    AUTO_SMITHING(AutoSmithingRecipe::new),
    AUTO_UPGRADE(AutoUpgradeRecipe::new),
    TURNING(TurningRecipe::new),
    LASER_CUTTING(() -> new LaserCuttingRecipe.Serializer<>(LaserCuttingRecipe::new));

    private final ResourceLocation id;
    private final Supplier<RecipeSerializer<?>> serializerObject;
    @Nullable
    private final Supplier<RecipeType<?>> typeObject;
    private final Supplier<RecipeType<?>> type;

    VintageRecipes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier, boolean registerType) {
        String name = CreateLang.asId(name());
        id = VintageImprovements.asResource(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        if (registerType) {
            typeObject = Registers.TYPE_REGISTER.register(name, typeSupplier);
            type = typeObject;
        } else {
            typeObject = null;
            type = typeSupplier;
        }
    }

    VintageRecipes(Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = CreateLang.asId(name());
        id = VintageImprovements.asResource(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        typeObject = Registers.TYPE_REGISTER.register(name, () -> RecipeType.simple(id));
        type = typeObject;
    }

    <R extends ProcessingRecipe<?, ProcessingRecipeParams>> VintageRecipes(ProcessingRecipe.Factory<ProcessingRecipeParams, R> processingFactory) {
        this(() -> new VintageProcessingRecipeSerializer<>(processingFactory));
    }

    public static void register(IEventBus modEventBus) {
        ShapedRecipePattern.setCraftingSize(9, 9);
        Registers.SERIALIZER_REGISTER.register(modEventBus);
        Registers.TYPE_REGISTER.register(modEventBus);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializerObject.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) type.get();
    }

    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> find(I inv, Level world) {
        return world.getRecipeManager()
                .getRecipeFor(getType(), inv, world);
    }

    public static boolean shouldIgnoreInAutomation(Recipe<?> recipe) {
        RecipeSerializer<?> serializer = recipe.getSerializer();
        return serializer != null && AllTags.AllRecipeSerializerTags.AUTOMATION_IGNORE.matches(serializer);
    }

    public static boolean shouldIgnoreInAutomation(RecipeHolder<? extends Recipe<?>> recipe) {
        if (shouldIgnoreInAutomation(recipe.value()))
            return true;
        return recipe.id().getPath().endsWith("_manual_only");
    }

    private static class Registers {
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, VintageImprovements.MODID);
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, VintageImprovements.MODID);
    }

}
