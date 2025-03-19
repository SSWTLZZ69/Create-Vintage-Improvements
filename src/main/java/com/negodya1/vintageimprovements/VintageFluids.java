package com.negodya1.vintageimprovements;

import static com.negodya1.vintageimprovements.VintageImprovements.MY_REGISTRATE;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.createmod.catnip.theme.Color;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllTags.AllFluidTags;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.fluids.potion.PotionFluid.PotionFluidType;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.tterrag.registrate.builders.FluidBuilder.FluidTypeFactory;
import com.tterrag.registrate.util.entry.FluidEntry;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidInteractionRegistry;
import net.minecraftforge.fluids.FluidInteractionRegistry.InteractionInformation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class VintageFluids {

	public static final FluidEntry<VirtualFluid> SULFUR_DIOXIDE = MY_REGISTRATE.virtualFluid("sulfur_dioxide")
		.lang("Sulfur Dioxide").register();

	public static final FluidEntry<VirtualFluid> SULFUR_TRIOXIDE = MY_REGISTRATE.virtualFluid("sulfur_trioxide")
			.lang("Sulfur Trioxide").register();


	public static final FluidEntry<ForgeFlowingFluid.Flowing> SULFURIC_ACID =
		MY_REGISTRATE.standardFluid("sulfuric_acid",
				SolidRenderedPlaceableFluidType.create(0xFFFFFF,
					() -> 1f / 8f * AllConfigs.client().honeyTransparencyMultiplier.getF()))
			.lang("Sulfuric Acid")
			.properties(b -> b.viscosity(2000)
				.density(1400))
			.fluidProperties(p -> p.levelDecreasePerBlock(2)
				.tickRate(25)
				.slopeFindDistance(3)
				.explosionResistance(100f))
			.source(ForgeFlowingFluid.Source::new)
			.bucket()
			.build()
			.register();

	// Load this class

	public static void register() {}

	public static void registerFluidInteractions() {
		FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new InteractionInformation(
				SULFURIC_ACID.get().getFluidType(),
				fluidState -> {
					if (fluidState.isSource()) {
						return Blocks.OBSIDIAN.defaultBlockState();
					} else {
						return AllPaletteStoneTypes.SCORIA.getBaseBlock()
								.get()
								.defaultBlockState();
					}
				}
		));
	}

	@Nullable
	public static BlockState getLavaInteraction(FluidState fluidState) {
		Fluid fluid = fluidState.getType();
		if (fluid.isSame(SULFURIC_ACID.get()))
			return AllPaletteStoneTypes.SCORIA.getBaseBlock()
				.get()
				.defaultBlockState();
		return null;
	}

	public static abstract class TintedFluidType extends FluidType {

		protected static final int NO_TINT = 0xffffffff;
		private ResourceLocation stillTexture;
		private ResourceLocation flowingTexture;

		public TintedFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
			super(properties);
			this.stillTexture = stillTexture;
			this.flowingTexture = flowingTexture;
		}

		@Override
		public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
			consumer.accept(new IClientFluidTypeExtensions() {

				@Override
				public ResourceLocation getStillTexture() {
					return stillTexture;
				}

				@Override
				public ResourceLocation getFlowingTexture() {
					return flowingTexture;
				}

				@Override
				public int getTintColor(FluidStack stack) {
					return TintedFluidType.this.getTintColor(stack);
				}

				@Override
				public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
					return TintedFluidType.this.getTintColor(state, getter, pos);
				}
				
				@Override
				public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level,
					int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
					Vector3f customFogColor = TintedFluidType.this.getCustomFogColor();
					return customFogColor == null ? fluidFogColor : customFogColor;
				}

				@Override
				public void modifyFogRender(Camera camera, FogMode mode, float renderDistance, float partialTick,
					float nearDistance, float farDistance, FogShape shape) {
					float modifier = TintedFluidType.this.getFogDistanceModifier();
					float baseWaterFog = 96.0f;
					if (modifier != 1f) {
						RenderSystem.setShaderFogShape(FogShape.CYLINDER);
						RenderSystem.setShaderFogStart(-8);
						RenderSystem.setShaderFogEnd(baseWaterFog * modifier);
					}
				}

			});
		}

		protected abstract int getTintColor(FluidStack stack);

		protected abstract int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos);
		
		protected Vector3f getCustomFogColor() {
			return null;
		}
		
		protected float getFogDistanceModifier() {
			return 1f;
		}

	}

	private static class SolidRenderedPlaceableFluidType extends TintedFluidType {

		private Vector3f fogColor;
		private Supplier<Float> fogDistance;

		public static FluidTypeFactory create(int fogColor, Supplier<Float> fogDistance) {
			return (p, s, f) -> {
				SolidRenderedPlaceableFluidType fluidType = new SolidRenderedPlaceableFluidType(p, s, f);
				fluidType.fogColor = new Color(fogColor, false).asVectorF();
				fluidType.fogDistance = fogDistance;
				return fluidType;
			};
		}

		private SolidRenderedPlaceableFluidType(Properties properties, ResourceLocation stillTexture,
			ResourceLocation flowingTexture) {
			super(properties, stillTexture, flowingTexture);
		}

		@Override
		protected int getTintColor(FluidStack stack) {
			return NO_TINT;
		}

		/*
		 * Removing alpha from tint prevents optifine from forcibly applying biome
		 * colors to modded fluids (this workaround only works for fluids in the solid
		 * render layer)
		 */
		@Override
		public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
			return 0x00ffffff;
		}
		
		@Override
		protected Vector3f getCustomFogColor() {
			return fogColor;
		}
		
		@Override
		protected float getFogDistanceModifier() {
			return fogDistance.get();
		}

	}

}
