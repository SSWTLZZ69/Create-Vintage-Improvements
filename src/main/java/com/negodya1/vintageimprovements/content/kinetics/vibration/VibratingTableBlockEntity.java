package com.negodya1.vintageimprovements.content.kinetics.vibration;

import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.VintageRecipesList;
import com.negodya1.vintageimprovements.foundation.advancement.VintageAdvancementBehaviour;
import com.negodya1.vintageimprovements.foundation.advancement.VintageAdvancements;
import com.negodya1.vintageimprovements.infrastructure.config.VintageConfig;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VibratingTableBlockEntity extends KineticBlockEntity {
	public SmartInventory inputInv;
	public SmartInventory outputInv;
	public LazyOptional<IItemHandler> capability;
	public int timer;
	private VibratingRecipe lastRecipe;
	private ItemStack playEvent;
	boolean lastRecipeIsAssembly;
	VintageAdvancementBehaviour advancementBehaviour;

	public static final TagKey<Item> storageTag = ItemTags.create(new ResourceLocation("forge", "storage_blocks"));
	public static final TagKey<Item> leavesTag = ItemTags.create(new ResourceLocation("minecraft", "leaves"));

	public VibratingTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);

		inputInv = new SmartInventory(1, this);
		outputInv = new SmartInventory(9, this);
		capability = LazyOptional.of(VibratingTableInventoryHandler::new);
		playEvent = ItemStack.EMPTY;
	}

	public float getRenderedHeadOffset(float partialTicks) {
		return partialTicks * 3 / 16f;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(new DirectBeltInputBehaviour(this));
		super.addBehaviours(behaviours);
		advancementBehaviour = new VintageAdvancementBehaviour(this);
		behaviours.add(advancementBehaviour);
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		compound.putInt("Timer", timer);
		compound.put("InputInventory", inputInv.serializeNBT());
		compound.put("OutputInventory", outputInv.serializeNBT());
		compound.putBoolean("LastRecipeIsAssembly", lastRecipeIsAssembly);
		super.write(compound, clientPacket);

		if (!clientPacket || playEvent.isEmpty())
			return;
		compound.put("PlayEvent", playEvent.serializeNBT());
		playEvent = ItemStack.EMPTY;
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		timer = compound.getInt("Timer");
		inputInv.deserializeNBT(compound.getCompound("InputInventory"));
		outputInv.deserializeNBT(compound.getCompound("OutputInventory"));
		lastRecipeIsAssembly = compound.getBoolean("LastRecipeIsAssembly");
		if (compound.contains("PlayEvent"))
			playEvent = ItemStack.of(compound.getCompound("PlayEvent"));
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).inflate(.125f);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void tickAudio() {
		super.tickAudio();
		if (getSpeed() == 0)
			return;

		if (!playEvent.isEmpty()) {
			playEvent = ItemStack.EMPTY;

			AllSoundEvents.SANDING_SHORT.playAt(level, worldPosition, 3, 1, true);
		}
	}

	public void spawnParticles() {
		if (!haveRecipe()) return;

		ItemStack stackInSlot = inputInv.getStackInSlot(0);
		if (stackInSlot.isEmpty())
			return;

		ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, stackInSlot);
		float angle = level.random.nextFloat() * 360;
		Vec3 offset = new Vec3(0, 0, 0.5f);
		offset = VecHelper.rotate(offset, angle, Direction.Axis.Y);
		Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Direction.Axis.Y);

		Vec3 center = offset.add(VecHelper.getCenterOf(worldPosition));
		target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1 / 128f);
		level.addParticle(data, center.x, center.y + 8 / 16f + getRenderedHeadOffset(Minecraft.getInstance().getPartialTick()), center.z, target.x, target.y, target.z);
	}

	@Override
	public void tick() {
		super.tick();

		if (getSpeed() == 0)
			return;
		for (int i = 0; i < outputInv.getSlots(); i++)
			if (outputInv.getStackInSlot(i)
					.getCount() == outputInv.getSlotLimit(i))
				return;

		if (timer > 0) {
			timer -= getProcessingSpeed();

			if (level.isClientSide) {
				spawnParticles();
				return;
			}
			if (timer <= 0)
				process();
			return;
		}

		if (inputInv.getStackInSlot(0)
				.isEmpty())
			return;

		RecipeWrapper inventoryIn = new RecipeWrapper(inputInv);
		if (lastRecipe == null || (!lastRecipe.matches(inventoryIn, level))) {
			Optional<VibratingRecipe> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(level, inventoryIn,
					VintageRecipes.VIBRATING.getType(), VibratingRecipe.class);
			if (assemblyRecipe.isPresent()) {
				lastRecipe = assemblyRecipe.get();
				timer = lastRecipe.getProcessingDuration();
				if (timer == 0) timer = 100;
				lastRecipeIsAssembly = true;

				sendData();
				return;
			}

			lastRecipeIsAssembly = false;

			Optional<VibratingRecipe> recipe = VintageRecipes.VIBRATING.find(inventoryIn, level);
			if (!recipe.isPresent()) {
				timer = 100;
				sendData();
			} else {
				lastRecipe = recipe.get();
				timer = lastRecipe.getProcessingDuration();
				sendData();
			}
			return;
		}

		timer = lastRecipe.getProcessingDuration();
		if (timer == 0) timer = 100;
		sendData();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		capability.invalidate();
	}

	@Override
	public void destroy() {
		super.destroy();
		ItemHelper.dropContents(level, worldPosition, inputInv);
		ItemHelper.dropContents(level, worldPosition, outputInv);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (isItemHandlerCap(cap))
			return capability.cast();
		return super.getCapability(cap, side);
	}

	public boolean haveRecipe() {
		return canProcess(inputInv.getStackInSlot(0));
	}

	public static <C extends Container> boolean canUnpack(Recipe<C> recipe) {
		if (!(recipe instanceof CraftingRecipe) || !VintageConfig.server().recipes.allowUnpackingOnVibratingTable.get()) return false;
		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		if (ingredients.size() == 1) return ingredients.get(0).getItems()[0].is(storageTag);
		return false;
	}

	private boolean canProcess(ItemStack stack) {
		if (Mth.abs(getSpeed()) < IRotate.SpeedLevel.FAST.getSpeedValue()) return false;

		Optional<VibratingRecipe> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(level, stack,
				VintageRecipes.VIBRATING.getType(), VibratingRecipe.class);
		if (assemblyRecipe.isPresent()) return true;

		ItemStackHandler tester = new ItemStackHandler(1);
		tester.setStackInSlot(0, stack);
		RecipeWrapper inventoryIn = new RecipeWrapper(tester);

		if (lastRecipe != null && lastRecipe.matches(inventoryIn, level))
			return true;
		if (VintageRecipes.VIBRATING.find(inventoryIn, level)
				.isPresent()) return true;

		if (VintageConfig.server().recipes.allowVibratingLeaves.get() && VintageRecipes.LEAVES_VIBRATING.find(inventoryIn, level)
				.isPresent()) return true;

		return (tester.getStackInSlot(0).is(storageTag) && VintageConfig.server().recipes.allowUnpackingOnVibratingTable.get());
	}

	private void process() {
		RecipeWrapper inventoryIn = new RecipeWrapper(inputInv);

		if (lastRecipe == null || !lastRecipe.matches(inventoryIn, level)) {
			boolean found = false;

			Optional<VibratingRecipe> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(level, inventoryIn,
					VintageRecipes.VIBRATING.getType(), VibratingRecipe.class);
			if (assemblyRecipe.isPresent()) {
				lastRecipe = assemblyRecipe.get();
				found = true;
			}

			if (!found) {
				Optional<VibratingRecipe> recipe = VintageRecipes.VIBRATING.find(inventoryIn, level);
				if (recipe.isPresent()) {
					lastRecipe = recipe.get();
					found = true;
				}
			}

			if (!found && VintageConfig.server().recipes.allowUnpackingOnVibratingTable.get() && inputInv.getStackInSlot(0).is(storageTag)) {
				List<CraftingRecipe> recipes = VintageRecipesList.getUnpacking();
				for (CraftingRecipe recipe : recipes) {
					if (recipe.getIngredients().size() > 1) continue;

					NonNullList<Ingredient> in = recipe.getIngredients();
					for (Ingredient i : in) {
						for (ItemStack stack : i.getItems()) {
							Item ingredient = stack.getItem();
							if (ingredient == inputInv.getStackInSlot(0).getItem()) {
								ItemStack stackInSlot = inputInv.getStackInSlot(0);
								stackInSlot.shrink(1);
								inputInv.setStackInSlot(0, stackInSlot);

								ItemStack result = recipe.getResultItem(RegistryAccess.EMPTY).copy();
								ItemHandlerHelper.insertItemStacked(outputInv, result, false);
								advancementBehaviour.awardVintageAdvancement(VintageAdvancements.USE_VIBRATION_TABLE);

								sendData();
								setChanged();

								return;
							}
						}
					}
				}
			}

			if (!found && VintageConfig.server().recipes.allowVibratingLeaves.get() && inputInv.getStackInSlot(0).is(leavesTag)) {
				ItemStack stackInSlot = inputInv.getStackInSlot(0);

				if (stackInSlot.getItem() instanceof BlockItem) {
					Block leaves = Block.byItem(stackInSlot.getItem());

					ItemStack hoe = Items.DIAMOND_HOE.getDefaultInstance();
					hoe.enchant(Enchantments.BLOCK_FORTUNE, 3);

					List<ItemStack> list = Block.getDrops(leaves.defaultBlockState(), (ServerLevel) level, this.worldPosition, null, null, hoe.copy());

					for (ItemStack result : list) {
						ItemHandlerHelper.insertItemStacked(outputInv, result, false);
					}
				}

				stackInSlot.shrink(1);
				inputInv.setStackInSlot(0, stackInSlot);
				advancementBehaviour.awardVintageAdvancement(VintageAdvancements.USE_VIBRATION_TABLE);

				sendData();
				setChanged();

				return;
			}

			if (!found) return;
		}

		if (lastRecipeIsAssembly) {
			VibratingRecipe.apply(this, lastRecipe);
			lastRecipe = null;
		}
		else {
			ItemStack stackInSlot = inputInv.getStackInSlot(0);
			stackInSlot.shrink(1);
			inputInv.setStackInSlot(0, stackInSlot);

			lastRecipe.rollResults()
					.forEach(stack -> ItemHandlerHelper.insertItemStacked(outputInv, stack, false));
		}
		advancementBehaviour.awardVintageAdvancement(VintageAdvancements.USE_VIBRATION_TABLE);

		sendData();
		setChanged();
	}

	public boolean acceptOutputs(List<ItemStack> outputItems, boolean simulate) {
		outputInv.allowInsertion();
		boolean acceptOutputsInner = acceptOutputsInner(outputItems, simulate);
		outputInv.forbidInsertion();
		return acceptOutputsInner;
	}

	private boolean acceptOutputsInner(List<ItemStack> outputItems, boolean simulate) {
		BlockState blockState = getBlockState();
		if (!(blockState.getBlock() instanceof VibratingTableBlock))
			return false;

		IItemHandler targetInv = outputInv;

		if (targetInv == null && !outputItems.isEmpty())
			return false;
		if (!acceptItemOutputsIntoHelve(outputItems, simulate, targetInv))
			return false;

		return true;
	}

	private boolean acceptItemOutputsIntoHelve(List<ItemStack> outputItems, boolean simulate, IItemHandler targetInv) {
		for (ItemStack itemStack : outputItems) {
			if (!ItemHandlerHelper.insertItemStacked(targetInv, itemStack.copy(), simulate)
					.isEmpty())
				return false;
		}
		return true;
	}

	public int getProcessingSpeed() {
		return Mth.clamp((int) Math.abs(getSpeed() / 16f), 1, 512);
	}

	private class VibratingTableInventoryHandler extends CombinedInvWrapper {

		public VibratingTableInventoryHandler() {
			super(inputInv, outputInv);
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (outputInv == getHandlerFromIndex(getIndexForSlot(slot)))
				return false;
			return canProcess(stack) && super.isItemValid(slot, stack);
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (outputInv == getHandlerFromIndex(getIndexForSlot(slot)))
				return stack;
			if (!isItemValid(slot, stack))
				return stack;
			return super.insertItem(slot, stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (inputInv == getHandlerFromIndex(getIndexForSlot(slot)))
				return ItemStack.EMPTY;
			return super.extractItem(slot, amount, simulate);
		}

	}

}
