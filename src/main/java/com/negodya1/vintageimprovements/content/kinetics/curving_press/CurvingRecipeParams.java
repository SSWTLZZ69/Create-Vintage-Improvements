package com.negodya1.vintageimprovements.content.kinetics.curving_press;

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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Optional;
import java.util.function.Function;

public class CurvingRecipeParams extends ProcessingRecipeParams {
	public static final int DEFAULT_MODE = 1;
	public static final int DEFAULT_HEAD_DAMAGE = 0;

	private static final Codec<Item> ITEM_CODEC = ResourceLocation.CODEC.comapFlatMap(
			id -> {
				Item item = BuiltInRegistries.ITEM.get(id);
				if (item == Items.AIR && !id.equals(BuiltInRegistries.ITEM.getKey(Items.AIR)))
					return DataResult.error(() -> "Unknown item id: " + id);
				return DataResult.success(item);
			},
			item -> BuiltInRegistries.ITEM.getKey(item)
	);

	public static final MapCodec<CurvingRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			codec(CurvingRecipeParams::new).forGetter(Function.identity()),
			Codec.INT.optionalFieldOf("mode").forGetter(params -> Optional.of(params.mode)),
			Codec.INT.optionalFieldOf("head_damage").forGetter(params -> Optional.of(params.headDamage)),
			Codec.INT.optionalFieldOf("headDamage").forGetter(params -> Optional.empty()),
			ITEM_CODEC.optionalFieldOf("item_as_head").forGetter(params -> params.itemAsHead == Items.AIR ? Optional.empty() : Optional.of(params.itemAsHead)),
			ITEM_CODEC.optionalFieldOf("itemAsHead").forGetter(params -> Optional.empty())
	).apply(instance, (params, mode, headDamage, legacyHeadDamage, itemAsHead, legacyItemAsHead) -> {
		params.itemAsHead = itemAsHead.or(() -> legacyItemAsHead).orElse(Items.AIR);
		params.mode = params.itemAsHead != Items.AIR ? 5 : mode.orElse(DEFAULT_MODE);
		params.headDamage = headDamage.or(() -> legacyHeadDamage).orElse(DEFAULT_HEAD_DAMAGE);
		return params;
	}));

	public static final StreamCodec<RegistryFriendlyByteBuf, CurvingRecipeParams> STREAM_CODEC =
			streamCodec(CurvingRecipeParams::new);

	protected int mode = DEFAULT_MODE;
	protected int headDamage = DEFAULT_HEAD_DAMAGE;
	protected Item itemAsHead = Items.AIR;

	protected final int mode() {
		return mode;
	}

	protected final int headDamage() {
		return headDamage;
	}

	protected final Item itemAsHead() {
		return itemAsHead;
	}

	@Override
	protected void encode(RegistryFriendlyByteBuf buffer) {
		super.encode(buffer);
		ByteBufCodecs.VAR_INT.encode(buffer, mode);
		buffer.writeResourceLocation(BuiltInRegistries.ITEM.getKey(itemAsHead));
		ByteBufCodecs.VAR_INT.encode(buffer, headDamage);
	}

	@Override
	protected void decode(RegistryFriendlyByteBuf buffer) {
		super.decode(buffer);
		mode = ByteBufCodecs.VAR_INT.decode(buffer);
		itemAsHead = BuiltInRegistries.ITEM.get(buffer.readResourceLocation());
		if (itemAsHead != Items.AIR)
			mode = 5;
		headDamage = ByteBufCodecs.VAR_INT.decode(buffer);
	}
}
