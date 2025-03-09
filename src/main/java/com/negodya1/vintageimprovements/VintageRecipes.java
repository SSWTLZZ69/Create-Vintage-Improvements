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
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;

import com.negodya1.vintageimprovements.content.kinetics.grinder.PolishingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeFactory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public enum VintageRecipes implements IRecipeTypeInfo {

    POLISHING(PolishingRecipe::new),
    COILING(CoilingRecipe::new),
    VACUUMIZING(VacuumizingRecipe::new),
    VIBRATING(VibratingRecipe::new),
    LEAVES_VIBRATING(LeavesVibratingRecipe::new),
    CENTRIFUGATION(CentrifugationRecipe::new),
    CURVING(CurvingRecipe::new),
    PRESSURIZING(PressurizingRecipe::new),
    HAMMERING(HammeringRecipe::new),
    AUTO_SMITHING(AutoSmithingRecipe::new),
    AUTO_UPGRADE(AutoUpgradeRecipe::new),
    TURNING(TurningRecipe::new),
    LASER_CUTTING(LaserCuttingRecipe::new);

    private final ResourceLocation id;
    private final RegistryObject<RecipeSerializer<?>> serializerObject;
    @Nullable
    private final RegistryObject<RecipeType<?>> typeObject;
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

    VintageRecipes(ProcessingRecipeFactory<?> processingFactory) {
        this(() -> new ProcessingRecipeSerializer<>(processingFactory));
    }

    public static void register(IEventBus modEventBus) {
        ShapedRecipe.setCraftingSize(9, 9);
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
    public <T extends RecipeType<?>> T getType() {
        return (T) type.get();
    }

    public <C extends Container, T extends Recipe<C>> Optional<T> find(C inv, Level world) {
        return world.getRecipeManager()
                .getRecipeFor(getType(), inv, world);
    }

    public static boolean shouldIgnoreInAutomation(Recipe<?> recipe) {
        RecipeSerializer<?> serializer = recipe.getSerializer();
        if (serializer != null && AllTags.AllRecipeSerializerTags.AUTOMATION_IGNORE.matches(serializer))
            return true;
        return recipe.getId()
                .getPath()
                .endsWith("_manual_only");
    }

    private static class Registers {
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, VintageImprovements.MODID);
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, VintageImprovements.MODID);
    }

}
