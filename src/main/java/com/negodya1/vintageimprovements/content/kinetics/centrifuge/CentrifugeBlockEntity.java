package com.negodya1.vintageimprovements.content.kinetics.centrifuge;

import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintageItems;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.HammeringRecipe;
import com.negodya1.vintageimprovements.foundation.advancement.VintageAdvancementBehaviour;
import com.negodya1.vintageimprovements.foundation.advancement.VintageAdvancements;
import com.negodya1.vintageimprovements.foundation.utility.VintageLang;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.*;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction.Axis;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CentrifugeBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {
	public SmartInventory inputInv;
	public SmartInventory outputInv;
	public SmartFluidTankBehaviour inputTank;
	public SmartFluidTankBehaviour outputTank;
	private Couple<SmartFluidTankBehaviour> tanks;
	public LazyOptional<IItemHandlerModifiable> capability;
	public LazyOptional<IFluidHandler> fluidCapability;
	public int timer;
	private CentrifugationRecipe lastRecipe;
	private int basins;
	private boolean redstoneApp;
	boolean lastRecipeIsAssembly;
	private boolean contentsChanged;
	private static final Object centrifugationRecipesKey = new Object();

	public static final int OUTPUT_ANIMATION_TIME = 10;
	List<IntAttached<ItemStack>> visualizedOutputItems;
	LerpedFloat ingredientRotationSpeed;
	LerpedFloat ingredientRotation;
	VintageAdvancementBehaviour advancementBehaviour;

	public CentrifugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);

		inputInv = new SmartInventory(9, this);
		outputInv = new SmartInventory(9, this);
		capability = LazyOptional.of(() -> new CentrifugeInventoryHandler(inputInv, outputInv));
		basins = 0;
		visualizedOutputItems = Collections.synchronizedList(new ArrayList<>());
		ingredientRotation = LerpedFloat.angular()
				.startWithValue(0);
		ingredientRotationSpeed = LerpedFloat.linear()
				.startWithValue(0);
		tanks = Couple.create(inputTank, outputTank);
		redstoneApp = false;
	}

	public int getBasins() {
		return basins;
	}

	public boolean addBasin(ItemStack items) {
		if (basins >= 4) return false;
		if (items.getItem() != AllBlocks.BASIN.asItem()) return false;
		basins += 1;
		return true;
	}

	public boolean getRedstoneApp() {
		return redstoneApp;
	}

	public boolean addRedstoneApp(ItemStack items) {
		if (redstoneApp) return false;
		if (items.getItem() != VintageItems.REDSTONE_MODULE.get()) return false;
		redstoneApp = true;
		return true;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(new DirectBeltInputBehaviour(this));
		super.addBehaviours(behaviours);

		inputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 2, 1000, true)
				.whenFluidUpdates(() -> contentsChanged = true);
		outputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 2, 1000, true)
				.whenFluidUpdates(() -> contentsChanged = true)
				.forbidInsertion();
		behaviours.add(inputTank);
		behaviours.add(outputTank);

		fluidCapability = LazyOptional.of(() -> {
			LazyOptional<? extends IFluidHandler> inputCap = inputTank.getCapability();
			LazyOptional<? extends IFluidHandler> outputCap = outputTank.getCapability();
			return new CentrifugeTanksHandler(outputCap.orElse(null), inputCap.orElse(null));
		});

		advancementBehaviour = new VintageAdvancementBehaviour(this);
		behaviours.add(advancementBehaviour);
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		compound.putInt("Timer", timer);
		compound.put("InputInventory", inputInv.serializeNBT());
		compound.put("OutputInventory", outputInv.serializeNBT());
		compound.putBoolean("LastRecipeIsAssembly", lastRecipeIsAssembly);
		compound.putInt("Basins", basins);
		compound.putBoolean("RedstoneApp", redstoneApp);
		super.write(compound, clientPacket);

		if (!clientPacket)
			return;

		NBTHelper.iterateCompoundList(compound.getList("VisualizedItems", Tag.TAG_COMPOUND),
				c -> visualizedOutputItems.add(IntAttached.with(OUTPUT_ANIMATION_TIME, ItemStack.of(c))));
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		timer = compound.getInt("Timer");
		inputInv.deserializeNBT(compound.getCompound("InputInventory"));
		outputInv.deserializeNBT(compound.getCompound("OutputInventory"));
		lastRecipeIsAssembly = compound.getBoolean("LastRecipeIsAssembly");
		basins = compound.getInt("Basins");
		redstoneApp = compound.getBoolean("RedstoneApp");

		if (!clientPacket)
			return;

		compound.put("VisualizedItems", NBTHelper.writeCompoundList(visualizedOutputItems, ia -> ia.getValue()
				.serializeNBT()));
		visualizedOutputItems.clear();
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).inflate(2);
	}

	private void tickVisualizedOutputs() {
		visualizedOutputItems.forEach(IntAttached::decrement);
		visualizedOutputItems.removeIf(IntAttached::isOrBelowZero);
	}

	protected <C extends Container> boolean matchCentrifugeRecipe(Recipe<C> recipe) {
		if (recipe == null)
			return false;
		return CentrifugationRecipe.match(this, recipe);
	}

	private List<Recipe<?>> getRecipes() {
		List<Recipe<?>> list =  RecipeFinder.get(centrifugationRecipesKey, level, this::matchStaticFilters);

		return list.stream()
				.filter(this::matchCentrifugeRecipe)
				.sorted((r1, r2) -> r2.getIngredients()
						.size()
						- r1.getIngredients()
						.size())
				.collect(Collectors.toList());
	}

	protected <C extends Container> boolean matchStaticFilters(Recipe<C> r) {
		return r.getType() == VintageRecipes.CENTRIFUGATION.getType();
	}

	public boolean isProccesingNow() {
		for (int i = 0; i < inputInv.getSlots(); i++) {
			Optional<CentrifugationRecipe> assemblyRecipe = SequencedAssemblyRecipe.
					getRecipe(level, inputInv.getStackInSlot(i),
							VintageRecipes.CENTRIFUGATION.getType(), CentrifugationRecipe.class);
			if (assemblyRecipe.isPresent())
				return CentrifugationRecipe.match(this, assemblyRecipe.get());
		}

		List<Recipe<?>> recipes = getRecipes();

		if (recipes.isEmpty()) return false;

		if (inputInv.isEmpty() && inputTank.isEmpty()) return false;

		for (int i = 0; i < outputInv.getSlots(); i++) {

			if (!outputInv.getStackInSlot(i).isEmpty() && outputInv.getStackInSlot(i).getCount() >= outputInv.getStackInSlot(i).getMaxStackSize())
				return false;
		}

		return CentrifugationRecipe.match(this, recipes.get(0));
	}

	@Override
	public void tick() {
		super.tick();
		if (level.isClientSide) {
			tickVisualizedOutputs();
			ingredientRotationSpeed.tickChaser();
			ingredientRotation.setValue(ingredientRotation.getValue() + ingredientRotationSpeed.getValue());
		}

		if (getBasins() < 4)
			return;
		for (int i = 0; i < outputInv.getSlots(); i++)
			if (outputInv.getStackInSlot(i)
					.getCount() == outputInv.getSlotLimit(i))
				return;

		if (timer > 0) {
			if (getSpeed() == 0) {
				timer = 0;
				lastRecipe = null;
			}

			if (lastRecipe != null && Mth.abs(getSpeed()) < lastRecipe.minimalRPM) {
				timer = lastRecipe.getProcessingDuration();
			}

			if (lastRecipe != null) {
				if (Mth.abs(getSpeed()) >= lastRecipe.minimalRPM) {
					timer -= getProcessingSpeed();

					if (level.isClientSide) {
						return;
					}
					if (timer <= 0)
						process();
					return;
				}
			}
		}

		if (inputInv.isEmpty() && inputTank.isEmpty())
			return;

		if (lastRecipe == null || !CentrifugationRecipe.match(this, lastRecipe)) {

			for (int i = 0; i < inputInv.getSlots(); i++) {
				Optional<CentrifugationRecipe> assemblyRecipe = SequencedAssemblyRecipe.
						getRecipe(level, inputInv.getStackInSlot(i),
								VintageRecipes.CENTRIFUGATION.getType(), CentrifugationRecipe.class);
				if (assemblyRecipe.isPresent()) {
					boolean found = true;

					for (Ingredient cur : assemblyRecipe.get().getIngredients()) {
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
						lastRecipe = assemblyRecipe.get();
						timer = lastRecipe.getProcessingDuration();
						if (timer == 0) timer = 100;
						lastRecipeIsAssembly = true;

						sendData();
						return;
					}
				}
			}

			lastRecipeIsAssembly = false;

			if (!getRecipes().isEmpty()) {
				lastRecipe = (CentrifugationRecipe) getRecipes().get(0);
				timer = lastRecipe.getProcessingDuration();
				sendData();
				return;
			}

			timer = 100;
			sendData();
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
		fluidCapability.invalidate();
	}

	@Override
	public void destroy() {
		super.destroy();
		if (getBasins() > 0) {
			SmartInventory basinsInv = new SmartInventory(9, this);
			ItemHandlerHelper.insertItemStacked(basinsInv, AllBlocks.BASIN.asStack(getBasins()), false);
			ItemHelper.dropContents(level, worldPosition, basinsInv);
		}
		if (redstoneApp) {
			SmartInventory redstoneInv = new SmartInventory(9, this);
			ItemHandlerHelper.insertItemStacked(redstoneInv, new ItemStack(VintageItems.REDSTONE_MODULE.get().asItem()), false);
			ItemHelper.dropContents(level, worldPosition, redstoneInv);
		}
		ItemHelper.dropContents(level, worldPosition, inputInv);
		ItemHelper.dropContents(level, worldPosition, outputInv);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER)
			return capability.cast();
		if (cap == ForgeCapabilities.FLUID_HANDLER)
			return fluidCapability.cast();
		return super.getCapability(cap, side);
	}

	public boolean canProcess() {
		return getSpeed() == 0 && getBasins() >= 4;
	}

	public SmartInventory getInputInventory() {
		return inputInv;
	}

	public SmartInventory getOutputInventory() {
		return outputInv;
	}

	public Couple<SmartFluidTankBehaviour> getTanks() {
		return tanks;
	}

	public float getTotalFluidUnits(float partialTicks) {
		int renderedFluids = 0;
		float totalUnits = 0;

		for (SmartFluidTankBehaviour behaviour : getTanks()) {
			if (behaviour == null)
				continue;
			for (SmartFluidTankBehaviour.TankSegment tankSegment : behaviour.getTanks()) {
				if (tankSegment.getRenderedFluid()
						.isEmpty())
					continue;
				float units = tankSegment.getTotalUnits(partialTicks);
				if (units < 1)
					continue;
				totalUnits += units;
				renderedFluids++;
			}
		}

		if (renderedFluids == 0)
			return 0;
		if (totalUnits < 1)
			return 0;
		return totalUnits;
	}

	public boolean acceptOutputs(List<ItemStack> outputItems, List<FluidStack> outputFluids, boolean simulate) {
		outputInv.allowInsertion();
		outputTank.allowInsertion();
		boolean acceptOutputsInner = acceptOutputsInner(outputItems, outputFluids, simulate);
		outputInv.forbidInsertion();
		outputTank.forbidInsertion();
		return acceptOutputsInner;
	}

	private boolean acceptOutputsInner(List<ItemStack> outputItems, List<FluidStack> outputFluids, boolean simulate) {
		BlockState blockState = getBlockState();
		if (!(blockState.getBlock() instanceof CentrifugeBlock))
			return false;

		IItemHandler targetInv = outputInv;
		IFluidHandler targetTank = outputTank.getCapability()
				.orElse(null);

		if (targetInv == null && !outputItems.isEmpty())
			return false;
		if (!acceptItemOutputsIntoCentrifuge(outputItems, simulate, targetInv))
			return false;
		if (outputFluids.isEmpty())
			return true;
		if (targetTank == null)
			return false;
		if (!acceptFluidOutputsIntoCentrifuge(outputFluids, simulate, targetTank))
			return false;

		return true;
	}

	private boolean acceptFluidOutputsIntoCentrifuge(List<FluidStack> outputFluids, boolean simulate,
													 IFluidHandler targetTank) {
		for (FluidStack fluidStack : outputFluids) {
			IFluidHandler.FluidAction action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
			int fill = targetTank instanceof SmartFluidTankBehaviour.InternalFluidHandler
					? ((SmartFluidTankBehaviour.InternalFluidHandler) targetTank).forceFill(fluidStack.copy(), action)
					: targetTank.fill(fluidStack.copy(), action);
			if (fill != fluidStack.getAmount())
				return false;
		}
		return true;
	}

	private boolean acceptItemOutputsIntoCentrifuge(List<ItemStack> outputItems, boolean simulate, IItemHandler targetInv) {
		for (ItemStack itemStack : outputItems) {
			if (!ItemHandlerHelper.insertItemStacked(targetInv, itemStack.copy(), simulate)
					.isEmpty())
				return false;
		}
		return true;
	}

	private void process() {
		if (lastRecipe == null || !CentrifugationRecipe.match(this, lastRecipe)) {
			boolean found = false;
			for (int i = 0; i < inputInv.getSlots(); i++) {
				Optional<CentrifugationRecipe> assemblyRecipe = SequencedAssemblyRecipe.
						getRecipe(level, inputInv.getStackInSlot(i),
								VintageRecipes.CENTRIFUGATION.getType(), CentrifugationRecipe.class);
				if (assemblyRecipe.isPresent()) {
					lastRecipe = assemblyRecipe.get();
					lastRecipeIsAssembly = true;
					found = true;
					break;
				}
			}

			if (!found) {
				List<Recipe<?>> recipes = getRecipes();
				if (!recipes.isEmpty()) {
					lastRecipe = (CentrifugationRecipe) recipes.get(0);
					found = true;
				}
			}

			if (!found) return;
		}

		if (CentrifugationRecipe.apply(this, lastRecipe) && lastRecipeIsAssembly)
			lastRecipe = null;
		advancementBehaviour.awardVintageAdvancement(VintageAdvancements.USE_CENTRIFUGE);

		sendData();
		setChanged();
	}

	public int getProcessingSpeed() {
		return Mth.clamp((int) Math.abs(getSpeed() / 16f), 1, 512);
	}

	private class CentrifugeTanksHandler extends CombinedTankWrapper {
		public CentrifugeTanksHandler(IFluidHandler... fluidHandlers) {
			super(fluidHandlers);
		}

		@Override
		public boolean isFluidValid(int tank, FluidStack stack) {
			if (outputTank == getHandlerFromIndex(getIndexForSlot(tank)))
				return false;
			return canProcess() && super.isFluidValid(tank, stack);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			if (canProcess())
				return super.fill(resource, action);
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			if (canProcess())
				return super.drain(resource, action);
			return FluidStack.EMPTY;
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			if (canProcess())
				return super.drain(maxDrain, action);
			return FluidStack.EMPTY;
		}
	}

	private class CentrifugeInventoryHandler extends CombinedInvWrapper {

		public CentrifugeInventoryHandler(IItemHandlerModifiable... itemHandlers) {
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
			if (inputInv == getHandlerFromIndex(getIndexForSlot(slot)) || !canProcess())
				return ItemStack.EMPTY;
			return super.extractItem(slot, amount, simulate);
		}

	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);

		if (basins >= 4) {
			if (redstoneApp) {
				VintageLang.translate("gui.goggles.redstone_module")
						.style(ChatFormatting.DARK_PURPLE).forGoggles(tooltip);
			}

			if (lastRecipe != null) if (lastRecipe.minimalRPM > Mth.abs(getSpeed()))
				VintageLang.translate("gui.goggles.not_enough_rpm")
						.add(Lang.text(" ")).add(Lang.number(lastRecipe.minimalRPM)).style(ChatFormatting.RED).forGoggles(tooltip);


			IItemHandlerModifiable items = capability.orElse(new ItemStackHandler());
			IFluidHandler fluids = fluidCapability.orElse(new FluidTank(0));
			boolean isEmpty = true;

			for (int i = 0; i < items.getSlots(); i++) {
				ItemStack stackInSlot = items.getStackInSlot(i);
				if (stackInSlot.isEmpty())
					continue;
				Lang.text("")
						.add(Components.translatable(stackInSlot.getDescriptionId())
								.withStyle(ChatFormatting.GRAY))
						.add(Lang.text(" x" + stackInSlot.getCount())
								.style(ChatFormatting.GREEN))
						.forGoggles(tooltip, 1);
				isEmpty = false;
			}

			LangBuilder mb = Lang.translate("generic.unit.millibuckets");
			for (int i = 0; i < fluids.getTanks(); i++) {
				FluidStack fluidStack = fluids.getFluidInTank(i);
				if (fluidStack.isEmpty())
					continue;
				Lang.text("")
						.add(Lang.fluidName(fluidStack)
								.add(Lang.text(" "))
								.style(ChatFormatting.GRAY)
								.add(Lang.number(fluidStack.getAmount())
										.add(mb)
										.style(ChatFormatting.BLUE)))
						.forGoggles(tooltip, 1);
				isEmpty = false;
			}

			if (isEmpty)
				tooltip.remove(0);

			return true;
		}

		VintageLang.translate("gui.goggles.not_enough_basins")
				.add(Lang.text(" ")).add(Lang.number(4 - basins)).style(ChatFormatting.GOLD).forGoggles(tooltip);
		return true;
	}

}
