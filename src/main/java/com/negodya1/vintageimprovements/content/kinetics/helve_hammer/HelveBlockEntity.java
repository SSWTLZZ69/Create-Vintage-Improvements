package com.negodya1.vintageimprovements.content.kinetics.helve_hammer;

import com.negodya1.vintageimprovements.*;
import com.negodya1.vintageimprovements.foundation.advancement.VintageAdvancementBehaviour;
import com.negodya1.vintageimprovements.foundation.advancement.VintageAdvancements;
import com.negodya1.vintageimprovements.infrastructure.config.VintageConfig;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HelveBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
	public SmartInventory inputInv;
	public SmartInventory bufInv;
	public SmartInventory outputInv;
	public IItemHandlerModifiable capability;
	public int timer;
	public int hammerBlows;
	private SmithingRecipe lastSmithingRecipe;
	private HammeringRecipe lastHammeringRecipe;
	boolean lastRecipeIsAssembly;
	private boolean recipesDirty;
	private static final Object hammeringRecipesKey = new Object();
	private int operatingMode;
	Block anvilBlock;
	VintageAdvancementBehaviour advancementBehaviour;
	private int blockedSlots;
	private boolean needsAnvilUpdate = true;

	public static final TagKey<Item> customAnvilTag =
			ItemTags.create(ResourceLocation.fromNamespaceAndPath("vintageimprovements", "custom_hammering_blocks"));
	public static final TagKey<Item> anvilTag =
			ItemTags.create(ResourceLocation.fromNamespaceAndPath("vintageimprovements", "anvils"));

	public HelveBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);

		inputInv = new SmartInventory(3, this).whenContentsChanged(slot -> recipesDirty = true);
		bufInv = new SmartInventory(3, this);
		outputInv = new SmartInventory(3, this).whenContentsChanged(slot -> recipesDirty = true);
		capability = new HelveInventoryHandler(inputInv, outputInv);
		operatingMode = 0;
		hammerBlows = 0;
		anvilBlock = Blocks.AIR;
		blockedSlots = 0;
		recipesDirty = true;
	}

	public void resetRecipes() {
		lastHammeringRecipe = null;
		lastSmithingRecipe = null;
		timer = 0;
		hammerBlows = 0;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(new DirectBeltInputBehaviour(this));
		advancementBehaviour = new VintageAdvancementBehaviour(this);
		behaviours.add(advancementBehaviour);
	}

	@Override
	public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		compound.putInt("Timer", timer);
		compound.putInt("HammerBlows", hammerBlows);
		compound.put("InputInventory", inputInv.serializeNBT(registries));
		compound.put("OutputInventory", outputInv.serializeNBT(registries));
		compound.putBoolean("LastRecipeIsAssembly", lastRecipeIsAssembly);
		compound.putInt("BlockedSlots", blockedSlots);
		compound.putInt("OperatingMode", operatingMode);
		super.write(compound, registries, clientPacket);
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		timer = compound.getInt("Timer");
		hammerBlows = compound.getInt("HammerBlows");
		inputInv.deserializeNBT(registries, compound.getCompound("InputInventory"));
		outputInv.deserializeNBT(registries, compound.getCompound("OutputInventory"));
		lastRecipeIsAssembly = compound.getBoolean("LastRecipeIsAssembly");
		blockedSlots = compound.getInt("BlockedSlots");
		operatingMode = compound.getInt("OperatingMode");
	}

	public boolean addBlockedSlots() {
		if (blockedSlots >= 2) return false;
		blockedSlots += 1;
		ItemHelper.dropContents(level, worldPosition, inputInv);
		inputInv = new SmartInventory(3 - blockedSlots, this).whenContentsChanged(slot -> recipesDirty = true);
		capability = new HelveInventoryHandler(inputInv, outputInv);
		recipesDirty = true;
		resetRecipes();
        notifyUpdate();
		return true;
	}

	public  int getBlockedSlots() {
		return blockedSlots;
	}

	public ItemStack resetBlockedSlots() {
		if (blockedSlots <= 0) return ItemStack.EMPTY;

		ItemStack itemStack = new ItemStack(VintageItems.HELVE_HAMMER_SLOT_COVER.get(), blockedSlots);
		blockedSlots = 0;
		ItemHelper.dropContents(level, worldPosition, inputInv);
		inputInv = new SmartInventory(3, this).whenContentsChanged(slot -> recipesDirty = true);
		capability = new HelveInventoryHandler(inputInv, outputInv);
		recipesDirty = true;
		resetRecipes();
        notifyUpdate();
		return itemStack;
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).inflate(1);
	}

	private void changeMode(int mode) {
		if (operatingMode == mode) return;

		operatingMode = mode;
		lastSmithingRecipe = null;
		lastHammeringRecipe = null;
		timer = 0;
		hammerBlows = 0;

		if (mode == 0) {
			ItemHelper.dropContents(level, worldPosition.below(), inputInv);
			ItemHelper.dropContents(level, worldPosition.below(), outputInv);

			inputInv.clearContent();
			outputInv.clearContent();
		}

		if (!level.isClientSide) {
			sendData();
		}
	}

	public float getHammerAngle() {
		if (timer <= 0) return 0.0f;

		if (operatingMode == 1 && hammerBlows > 0 || operatingMode == 2) {
			if (timer > 25)
				return -25f + (timer / 20f);
			return timer * -1f;
		}

		return 0.0f;
	}

	protected void spawnEventParticles(ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return;

		ParticleOptions particleData = null;
		if (stack.getItem() instanceof BlockItem)
			particleData = new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem) stack.getItem()).getBlock()
					.defaultBlockState());
		else
			particleData = new ItemParticleOption(ParticleTypes.ITEM, stack);

		RandomSource r = level.random;
		Vec3 v = VecHelper.getCenterOf(this.worldPosition)
				.add(0, -0.5f, 0);
		for (int i = 0; i < 10; i++) {
			Vec3 m = VecHelper.offsetRandomly(new Vec3(0, 0.25f, 0), r, .125f);
			level.addParticle(particleData, v.x, v.y, v.z, m.x, m.y, m.y);
		}
	}

	public void updateAnvilState() {
		if (level == null) return;
		needsAnvilUpdate = false;
		recipesDirty = true;

		Block blockBelow = level.getBlockState(worldPosition.below()).getBlock();
		if (blockBelow instanceof AnvilBlock ||
				blockBelow.asItem().getDefaultInstance().is(anvilTag)) {
			changeMode(1);
			anvilBlock = Blocks.AIR;
		}
		else if (blockBelow.asItem().getDefaultInstance().is(customAnvilTag)) {
			changeMode(1);
			anvilBlock = blockBelow;
		}
		else if (blockBelow == Blocks.SMITHING_TABLE) {
			changeMode(2);
			anvilBlock = Blocks.AIR;
		}
		else {
			changeMode(0);
			anvilBlock = Blocks.AIR;
		}
	}

	@Override
	public void tick() {
		super.tick();

		if (level.isClientSide) {
			tickClient();
		} else {
			if (needsAnvilUpdate) {
				updateAnvilState();
			}
			tickServer();
		}
	}

	private void tickClient() {
		float speed = getSpeed();
		int processingSpeed = getProcessingSpeed(speed);

		if (speed == 0) {
			timer = 0;
			return;
		}

		if (operatingMode == 1 && timer > 0 && hammerBlows > 0) {
			timer -= processingSpeed;
			if (timer > 0 && timer - processingSpeed <= 0) {
				spawnEventParticles(inputInv.getStackInSlot(0));
				AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playAt(level, worldPosition, 3, 1, true);
			}
			if (timer <= 0) {
				timer = 500;
			}
		}
		else if (operatingMode == 2 && timer > 0) {
			timer -= processingSpeed;
			if (timer > 0 && timer - processingSpeed * 2 <= 0) {
				spawnEventParticles(inputInv.getStackInSlot(0));
				AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playAt(level, worldPosition, 3, 1, true);
			}
		}
	}

	private void tickServer() {
		float speed = getSpeed();
		int processingSpeed = getProcessingSpeed(speed);

		if (operatingMode == 1) {
			boolean outputFull = true;
			for (int i = 0; i < outputInv.getSlots(); i++) {
				if (outputInv.getStackInSlot(i).getCount() < outputInv.getSlotLimit(i)) {
					outputFull = false;
					break;
				}
			}
			if (outputFull) return;

			if (timer > 0) {
				if (speed == 0) {
					timer = 0;
					lastHammeringRecipe = null;
					recipesDirty = true;
					sendData();
				}

				if (lastHammeringRecipe != null) {
					timer -= processingSpeed;

					if (timer <= 0) {
						hammerBlows--;

						if (hammerBlows <= 0) {
							process();
							lastHammeringRecipe = null;
							recipesDirty = true;
							sendData();
						} else timer = 500;
					}
					return;
				}
			}

			if (!recipesDirty) return;
			if (speed == 0) return;
			if (inputInv.isEmpty() && outputInv.isEmpty()) return;

			recipesDirty = false;

			if (lastHammeringRecipe == null || !HammeringRecipe.match(this, lastHammeringRecipe)) {

				for (int i = 0; i < inputInv.getSlots(); i++) {
					Optional<RecipeHolder<HammeringRecipe>> assemblyRecipe = SequencedAssemblyRecipe.
							getRecipe(level, inputInv.getStackInSlot(i),
									VintageRecipes.HAMMERING.getType(), HammeringRecipe.class);
					if (assemblyRecipe.isPresent()) {
						boolean found = true;

						for (Ingredient cur : assemblyRecipe.get().value().getIngredients()) {
							boolean find = false;

							for (ItemStack item : cur.getItems()) {
								if (item.getCount() <= inputInv.countItem(item.getItem())) {
									find = true;
									break;
								}
							}

							found = find;
						}

						if (found) {
							lastHammeringRecipe = assemblyRecipe.get().value();
							timer = 500;
							hammerBlows = assemblyRecipe.get().value().hammerBlows;
							lastRecipeIsAssembly = true;

							sendData();
							return;
						}
					}
				}

				lastRecipeIsAssembly = false;

				List<Recipe<?>> recipes = getRecipes();
				if (!recipes.isEmpty()) {
					if (recipes.get(0) instanceof HammeringRecipe hammering) {
						lastHammeringRecipe = hammering;
						timer = 500;
						hammerBlows = hammering.hammerBlows;
						sendData();
					}
					return;
				}
			}

		}
		else if (operatingMode == 2) {
			int slots = 0;
			for (int i = 0; i < inputInv.getSlots(); i++)
				if (!inputInv.getStackInSlot(i).isEmpty()) slots++;

			if (lastSmithingRecipe == null && recipesDirty && slots >= (VintageConfig.server().recipes.allowTemplatelessRecipes.get() ? 2 : 3) && speed != 0) {
				recipesDirty = false;
				for (SmithingRecipe recipe : VintageRecipesList.getSmithing()) {
					boolean template = false;
					boolean base = false;
					boolean addition = false;

					for (int i = 0; i < inputInv.getSlots(); i++) {
						ItemStack item = inputInv.getStackInSlot(i);

						if (recipe.isTemplateIngredient(item) ||
								(VintageConfig.server().recipes.allowTemplatelessRecipes.get() && recipe.isTemplateIngredient(ItemStack.EMPTY))) template = true;
						if (recipe.isBaseIngredient(item)) base = true;
						if (recipe.isAdditionIngredient(item)) addition = true;
					}

					if (template && base && addition) {
						lastSmithingRecipe = recipe;
						timer = 500;
						sendData();
						break;
					}
				}
			}

			if (lastSmithingRecipe != null && (slots < (VintageConfig.server().recipes.allowTemplatelessRecipes.get() ? 2 : 3) || speed == 0)) {
				lastSmithingRecipe = null;
				timer = 0;
				sendData();
			}

			if (timer > 0) {
				if (lastSmithingRecipe != null) {
					timer -= processingSpeed;

					if (timer <= 0) {
						processSmith();
						lastSmithingRecipe = null;
						recipesDirty = true;
						sendData();
					}
					return;
				}
			}
		}
	}

	private List<Recipe<?>> getRecipes() {
		List<RecipeHolder<? extends Recipe<?>>> list =  RecipeFinder.get(hammeringRecipesKey, level, this::matchStaticFilters);

		return list.stream()
				.map(RecipeHolder::value)
				.filter(this::matchHelveRecipe)
				.sorted((r1, r2) -> r2.getIngredients()
						.size()
						- r1.getIngredients()
						.size())
				.collect(Collectors.toList());
	}

	protected boolean matchHelveRecipe(Recipe<?> recipe) {
		if (recipe == null)
			return false;
		return HammeringRecipe.match(this, recipe);
	}

	protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> r) {
		return r.value().getType() == VintageRecipes.HAMMERING.getType();
	}

	private void process() {
		if (lastHammeringRecipe == null || !HammeringRecipe.match(this, lastHammeringRecipe)) {
			boolean found = false;
			for (int i = 0; i < inputInv.getSlots(); i++) {
				Optional<RecipeHolder<HammeringRecipe>> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(level, inputInv.getStackInSlot(i),
						VintageRecipes.HAMMERING.getType(), HammeringRecipe.class);
				if (assemblyRecipe.isPresent() && HammeringRecipe.match(this, assemblyRecipe.get().value())) {
					lastHammeringRecipe = assemblyRecipe.get().value();
					lastRecipeIsAssembly = true;
					found = true;
					break;
				}
			}

			if (!found) {
				List<Recipe<?>> recipes = getRecipes();
				if (!recipes.isEmpty()) {
					lastHammeringRecipe = (HammeringRecipe) recipes.get(0);
					found = true;
				}
			}

			if (!found) return;
		}

		if (HammeringRecipe.apply(this, lastHammeringRecipe)) {
			advancementBehaviour.awardVintageAdvancement(VintageAdvancements.USE_HELVE);
			lastHammeringRecipe = null;
			if (VintageConfig.server().recipes.damageAnvilAfterHammeringRecipe.get()) {
				if (!level.isClientSide) {
					if (level.getBlockState(worldPosition.below()).getBlock() instanceof AnvilBlock) {
						if (level.random.nextInt() % 100 < VintageConfig.server().recipes.chanceToDamageAnvilAfterHammeringRecipe.get()) {
							BlockState state = AnvilBlock.damage(level.getBlockState(worldPosition.below()));
							if (state == null)
								level.destroyBlock(worldPosition.below(), false);
							else
								level.setBlockAndUpdate(worldPosition.below(), state);

						}
					}
				}
			}
		}

		sendData();
		setChanged();
	}

	private void processSmith() {
		if (lastSmithingRecipe == null) return;

		bufInv.clearContent();

		boolean template = false;
		boolean base = false;
		boolean addition = false;

		for (int i = 0; i < inputInv.getSlots(); i++) {
			ItemStack item = inputInv.getStackInSlot(i);

			if (lastSmithingRecipe.isTemplateIngredient(item)) {
				template = true;
				bufInv.setStackInSlot(0, item);
			}
			if (lastSmithingRecipe.isBaseIngredient(item)) {
				base = true;
				bufInv.setStackInSlot(1, item);
			}
			if (lastSmithingRecipe.isAdditionIngredient(item)) {
				addition = true;
				bufInv.setStackInSlot(2, item);
			}
		}

		if (!template || !base || !addition) {
			lastSmithingRecipe = null;
			return;
		}

		SmithingRecipeInput smithingInput = new SmithingRecipeInput(bufInv.getStackInSlot(0), bufInv.getStackInSlot(1), bufInv.getStackInSlot(2));
		ItemStack smithingResult = lastSmithingRecipe.assemble(smithingInput, this.level.registryAccess());
		if (acceptOutputs(smithingResult, true) && lastSmithingRecipe.matches(smithingInput, level)) {
			acceptOutputs(smithingResult.copy(), false);
			advancementBehaviour.awardVintageAdvancement(VintageAdvancements.USE_HELVE);

			bufInv.getStackInSlot(0).shrink(1);
			bufInv.getStackInSlot(1).shrink(1);
			bufInv.getStackInSlot(2).shrink(1);

			inputInv.setStackInSlot(0, bufInv.getStackInSlot(0));
			inputInv.setStackInSlot(1, bufInv.getStackInSlot(1));
			inputInv.setStackInSlot(2, bufInv.getStackInSlot(2));

			bufInv.clearContent();
		}
	}

	@Override
	public void setChanged() {
		super.setChanged();
		recipesDirty = true;
	}

	@Override
	public void invalidate() {
		super.invalidate();
	}

	@Override
	public void destroy() {
		super.destroy();
		ItemHelper.dropContents(level, worldPosition, inputInv);
		ItemHelper.dropContents(level, worldPosition, outputInv);
		if (blockedSlots > 0) {
			SmartInventory coverInv = new SmartInventory(1, this);
			ItemHandlerHelper.insertItemStacked(coverInv, new ItemStack(VintageItems.HELVE_HAMMER_SLOT_COVER.get(), blockedSlots), false);
			ItemHelper.dropContents(level, worldPosition, coverInv);
		}
	}

	public boolean canProcess() {
		return operatingMode > 0;
	}

	public SmartInventory getInputInventory() {
		return inputInv;
	}

	public SmartInventory getOutputInventory() {
		return outputInv;
	}

	public boolean acceptOutputs(List<ItemStack> outputItems, boolean simulate) {
		outputInv.allowInsertion();
		boolean acceptOutputsInner = acceptOutputsInner(outputItems, simulate);
		outputInv.forbidInsertion();
		return acceptOutputsInner;
	}

	private boolean acceptOutputsInner(List<ItemStack> outputItems, boolean simulate) {
		BlockState blockState = getBlockState();
		if (!(blockState.getBlock() instanceof HelveBlock))
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

	public boolean acceptOutputs(ItemStack item, boolean simulate) {
		outputInv.allowInsertion();
		boolean acceptOutputsInner = acceptOutputsInner(item, simulate);
		outputInv.forbidInsertion();
		return acceptOutputsInner;
	}

	private boolean acceptOutputsInner(ItemStack item, boolean simulate) {
		BlockState blockState = getBlockState();
		if (!(blockState.getBlock() instanceof HelveBlock))
			return false;

		IItemHandler targetInv = outputInv;

		if (targetInv == null && !item.isEmpty())
			return false;

		return acceptItemOutputsIntoHelve(item, simulate, targetInv);
	}

	private boolean acceptItemOutputsIntoHelve(ItemStack itemStack, boolean simulate, IItemHandler targetInv) {
		return ItemHandlerHelper.insertItemStacked(targetInv, itemStack.copy(), simulate).isEmpty();
	}

	public float getSpeed() {
		HelveKineticBlockEntity be = (HelveKineticBlockEntity) level.getBlockEntity(HelveBlock.getSlave(level, worldPosition, this.getBlockState()));
		if (be != null)
			return be.getSpeed();
		return 0;
	}

	public int getProcessingSpeed(float speed) {
		return Mth.clamp((int) Math.abs(speed / 16f), 1, 512);
	}

	private class HelveInventoryHandler extends CombinedInvWrapper {

		public HelveInventoryHandler(IItemHandlerModifiable... itemHandlers) {
			super(itemHandlers);
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (outputInv == getHandlerFromIndex(getIndexForSlot(slot)))
				return false;
			return canProcess() && super.isItemValid(slot, stack);
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (outputInv == getHandlerFromIndex(getIndexForSlot(slot)) || !isItemValid(slot, stack))
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

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		if (level.getBlockEntity(HelveBlock.getSlave(level, worldPosition, getBlockState())) instanceof HelveKineticBlockEntity be)
			be.addToGoggleTooltip(tooltip, isPlayerSneaking);

		switch (operatingMode) {
			case 1 -> VintageLang.translate("gui.goggles.current_mode")
					.add(com.simibubi.create.foundation.utility.CreateLang.text(" ")).add(VintageLang.translate("gui.goggles.hammering_mode"))
					.style(ChatFormatting.DARK_AQUA).forGoggles(tooltip);
			case 2 -> VintageLang.translate("gui.goggles.current_mode")
					.add(com.simibubi.create.foundation.utility.CreateLang.text(" ")).add(VintageLang.translate("gui.goggles.smithing_mode"))
					.style(ChatFormatting.DARK_PURPLE).forGoggles(tooltip);
			default -> VintageLang.translate("gui.goggles.no_operating_block")
					.style(ChatFormatting.DARK_RED).forGoggles(tooltip);

		}

		if (blockedSlots > 0) {
			VintageLang.translate("gui.goggles.blocked_slots")
					.add(CreateLang.text(" " + blockedSlots)).style(ChatFormatting.GOLD).forGoggles(tooltip);
		}

		if (operatingMode == 1 && hammerBlows > 0 && lastHammeringRecipe != null)
			VintageLang.translate("gui.goggles.hammer_blows")
					.add(CreateLang.text(" ")).add(CreateLang.number(hammerBlows))
					.forGoggles(tooltip);

		return true;
	}
}

