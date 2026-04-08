package com.negodya1.vintageimprovements.content.kinetics.helve_hammer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Optional;
import java.util.function.Function;

public class HammeringRecipeParams extends ProcessingRecipeParams {
	public static final int DEFAULT_HAMMER_BLOWS = 1;

	private static final Codec<Item> ANVIL_CODEC = ResourceLocation.CODEC.comapFlatMap(
			id -> {
				if (BuiltInRegistries.BLOCK.containsKey(id))
					return DataResult.success(BuiltInRegistries.BLOCK.get(id).asItem());
				Item item = BuiltInRegistries.ITEM.get(id);
				if (item == Items.AIR && !id.equals(BuiltInRegistries.ITEM.getKey(Items.AIR)))
					return DataResult.error(() -> "Unknown block/item id for anvil_block: " + id);
				return DataResult.success(item);
			},
			item -> {
				Block block = Block.byItem(item);
				if (block != Blocks.AIR)
					return BuiltInRegistries.BLOCK.getKey(block);
				return BuiltInRegistries.ITEM.getKey(item);
			}
	);

	public static final MapCodec<HammeringRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			codec(HammeringRecipeParams::new).forGetter(Function.identity()),
			Codec.INT.optionalFieldOf("hammer_blows").forGetter(params -> Optional.of(params.hammerBlows)),
			Codec.INT.optionalFieldOf("hammerBlows").forGetter(params -> Optional.empty()),
			ANVIL_CODEC.optionalFieldOf("anvil_block").forGetter(params -> params.anvilBlock == Items.AIR ? Optional.empty() : Optional.of(params.anvilBlock)),
			ANVIL_CODEC.optionalFieldOf("anvilBlock").forGetter(params -> Optional.empty())
	).apply(instance, (params, hammerBlows, legacyHammerBlows, anvilBlock, legacyAnvilBlock) -> {
		params.hammerBlows = hammerBlows.or(() -> legacyHammerBlows).orElse(DEFAULT_HAMMER_BLOWS);
		params.anvilBlock = anvilBlock.or(() -> legacyAnvilBlock).orElse(Items.AIR);
		return params;
	}));

	public static final StreamCodec<RegistryFriendlyByteBuf, HammeringRecipeParams> STREAM_CODEC =
			streamCodec(HammeringRecipeParams::new);

	protected int hammerBlows = DEFAULT_HAMMER_BLOWS;
	protected Item anvilBlock = Items.AIR;

	protected final int hammerBlows() {
		return hammerBlows;
	}

	protected final Item anvilBlock() {
		return anvilBlock;
	}

	@Override
	protected void encode(RegistryFriendlyByteBuf buffer) {
		super.encode(buffer);
		ByteBufCodecs.VAR_INT.encode(buffer, hammerBlows);
		buffer.writeResourceLocation(BuiltInRegistries.ITEM.getKey(anvilBlock));
	}

	@Override
	protected void decode(RegistryFriendlyByteBuf buffer) {
		super.decode(buffer);
		hammerBlows = ByteBufCodecs.VAR_INT.decode(buffer);
		anvilBlock = BuiltInRegistries.ITEM.get(buffer.readResourceLocation());
	}
}
