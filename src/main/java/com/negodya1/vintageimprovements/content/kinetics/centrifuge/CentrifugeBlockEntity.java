package com.negodya1.vintageimprovements.content.kinetics.centrifuge;

import com.negodya1.vintageimprovements.VintageItems;
import com.negodya1.vintageimprovements.VintageLang;
import com.negodya1.vintageimprovements.foundation.advancement.VintageAdvancementBehaviour;
import com.negodya1.vintageimprovements.foundation.advancement.VintageAdvancements;
import com.negodya1.vintageimprovements.infrastructure.config.VintageConfig;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.ChatFormatting;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

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
	public IItemHandlerModifiable capability;
	public IFluidHandler fluidCapability;
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

	private final CompoundTag nbtForAnim = new CompoundTag();

	public CentrifugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);

		inputInv = new SmartInventory(9, this)
				.whenContentsChanged(slot -> contentsChanged = true);
		outputInv = new SmartInventory(9, this)
				.whenContentsChanged(slot -> contentsChanged = true);
		capability = new CentrifugeInventoryHandler(inputInv, outputInv);
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
        notifyUpdate();
		return true;
	}

	public boolean getRedstoneApp() {
		return redstoneApp;
	}

	public boolean addRedstoneApp(ItemStack items) {
		if (redstoneApp) return false;
		if (items.getItem() != VintageItems.REDSTONE_MODULE.get()) return false;
		redstoneApp = true;
        notifyUpdate();
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

		IFluidHandler inputCap = inputTank.getCapability();
		IFluidHandler outputCap = outputTank.getCapability();
		fluidCapability = new CentrifugeTanksHandler(outputCap, inputCap);

		advancementBehaviour = new VintageAdvancementBehaviour(this);
		behaviours.add(advancementBehaviour);
	}

	@Override
	protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		compound.putInt("Timer", timer);
		compound.put("InputInventory", inputInv.serializeNBT(registries));
		compound.put("OutputInventory", outputInv.serializeNBT(registries));
		compound.putBoolean("LastRecipeIsAssembly", lastRecipeIsAssembly);
		compound.putInt("Basins", basins);
		compound.putBoolean("RedstoneApp", redstoneApp);
		super.write(compound, registries, clientPacket);

		if (!clientPacket)
			return;

		NBTHelper.iterateCompoundList(nbtForAnim.getList("VisualizedItems", Tag.TAG_COMPOUND),
				c -> visualizedOutputItems.add(IntAttached.with(OUTPUT_ANIMATION_TIME, ItemStack.parseOptional(registries, c))));
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		timer = compound.getInt("Timer");
		inputInv.deserializeNBT(registries, compound.getCompound("InputInventory"));
		outputInv.deserializeNBT(registries, compound.getCompound("OutputInventory"));
		lastRecipeIsAssembly = compound.getBoolean("LastRecipeIsAssembly");
		basins = compound.getInt("Basins");
		redstoneApp = compound.getBoolean("RedstoneApp");

		if (!clientPacket)
			return;

		nbtForAnim.put("VisualizedItems", NBTHelper.writeCompoundList(visualizedOutputItems, ia -> (CompoundTag) ia.getValue()
				.saveOptional(registries)));
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

	protected boolean matchCentrifugeRecipe(Recipe<?> recipe) {
		if (recipe == null)
			return false;
		return CentrifugationRecipe.match(this, recipe);
	}

	private List<Recipe<?>> getRecipes() {
		List<RecipeHolder<? extends Recipe<?>>> list = RecipeFinder.get(centrifugationRecipesKey, level, this::matchStaticFilters);

		return list.stream()
				.map(RecipeHolder::value)
				.filter(this::matchCentrifugeRecipe)
				.sorted((r1, r2) -> r2.getIngredients()
						.size()
						- r1.getIngredients()
						.size())
				.collect(Collectors.toList());
	}

	protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> r) {
		return r.value().getType() == VintageRecipes.CENTRIFUGATION.getType();
	}

	public boolean isProccesingNow() {
		for (int i = 0; i < inputInv.getSlots(); i++) {
			Optional<RecipeHolder<CentrifugationRecipe>> assemblyRecipe = SequencedAssemblyRecipe.
					getRecipe(level, inputInv.getStackInSlot(i),
							VintageRecipes.CENTRIFUGATION.getType(), CentrifugationRecipe.class);
			if (assemblyRecipe.isPresent())
				return CentrifugationRecipe.match(this, assemblyRecipe.get().value());
		}

		List<Recipe<?>> recipes = getRecipes();

		if (recipes.isEmpty()) return false;

		if (inputInv.isEmpty() && inputTank.isEmpty()) return false;

		for (int i = 0; i < outputInv.getSlots(); i++) {

			if (!outputInv.getStackInSlot(i).isEmpty() && outputInv.getStackInSlot(i).getCount() >= outputInv.getStackInSlot(i).getMaxStackSize())
				return false;
		}
		if (recipes.get(0) instanceof CentrifugationRecipe centrifugationRecipe) {
			boolean res = this.acceptOutputs(centrifugationRecipe.getRollableResultsAsItemStacks(), centrifugationRecipe.getFluidResults(), true);
			if (!res)
				return false;
			return CentrifugationRecipe.match(this, centrifugationRecipe);
		}
		return false;
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

		// notifyUpdate if needed
		if (redstoneApp && contentsChanged) {
			BlockPos center = getBlockPos();
			for (int dx = -1; dx <= 1; dx++) {
				for (int dz = -1; dz <= 1; dz++) {
					if (dx == 0 && dz == 0) continue;
					BlockEntity be = level.getBlockEntity(center.offset(dx, 0, dz));
					if (be instanceof CentrifugeStructuralBlockEntity cbe) {
						cbe.notifyUpdate();
					}
				}
			}
			contentsChanged = false;
		}

		if (timer > 0) {
			if (getSpeed() == 0) {
				timer = 0;
				lastRecipe = null;
			}

			if (lastRecipe != null && Mth.abs(getSpeed()) < lastRecipe.getMinimalRPM()) {
				timer = lastRecipe.getProcessingDuration();
			}

			if (lastRecipe != null) {
				if (Mth.abs(getSpeed()) >= lastRecipe.getMinimalRPM()) {
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
				Optional<RecipeHolder<CentrifugationRecipe>> assemblyRecipe = SequencedAssemblyRecipe.
						getRecipe(level, inputInv.getStackInSlot(i),
								VintageRecipes.CENTRIFUGATION.getType(), CentrifugationRecipe.class);
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
						lastRecipe = assemblyRecipe.get().value();
						timer = lastRecipe.getProcessingDuration();
						if (timer == 0) timer = 100;
						lastRecipeIsAssembly = true;

						sendData();
						return;
					}
				}
			}

			lastRecipeIsAssembly = false;

			List<Recipe<?>> recipes = getRecipes();
			if (!recipes.isEmpty()) {
				lastRecipe = (CentrifugationRecipe) recipes.get(0);
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

	public boolean canProcess() {
		return (getSpeed() == 0 || VintageConfig.common().easyCentrifuge.get()) && getBasins() >= 4;
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
		IFluidHandler targetTank = outputTank.getCapability();

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
				Optional<RecipeHolder<CentrifugationRecipe>> assemblyRecipe = SequencedAssemblyRecipe.
						getRecipe(level, inputInv.getStackInSlot(i),
								VintageRecipes.CENTRIFUGATION.getType(), CentrifugationRecipe.class);
				if (assemblyRecipe.isPresent()) {
					lastRecipe = assemblyRecipe.get().value();
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

			if (lastRecipe != null) if (lastRecipe.getMinimalRPM() > Mth.abs(getSpeed()))
				VintageLang.translate("gui.goggles.not_enough_rpm")
						.add(com.simibubi.create.foundation.utility.CreateLang.text(" "))
						.add(com.simibubi.create.foundation.utility.CreateLang.number(lastRecipe.getMinimalRPM()))
						.style(ChatFormatting.RED).forGoggles(tooltip);


			IItemHandlerModifiable items = capability != null ? capability : new ItemStackHandler();
			IFluidHandler fluids = fluidCapability;
			if (fluids == null)
				return true;
			boolean isEmpty = true;

			for (int i = 0; i < items.getSlots(); i++) {
				ItemStack stackInSlot = items.getStackInSlot(i);
				if (stackInSlot.isEmpty())
					continue;
				com.simibubi.create.foundation.utility.CreateLang.text("")
						.add(Component.translatable(stackInSlot.getDescriptionId())
								.withStyle(ChatFormatting.GRAY))
						.add(com.simibubi.create.foundation.utility.CreateLang.text(" x" + stackInSlot.getCount())
								.style(ChatFormatting.GREEN))
						.forGoggles(tooltip, 1);
				isEmpty = false;
			}

			LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
			for (int i = 0; i < fluids.getTanks(); i++) {
				FluidStack fluidStack = fluids.getFluidInTank(i);
				if (fluidStack.isEmpty())
					continue;
				CreateLang.text("")
						.add(CreateLang.fluidName(fluidStack)
								.add(CreateLang.text(" "))
								.style(ChatFormatting.GRAY)
								.add(CreateLang.number(fluidStack.getAmount())
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
				.add(CreateLang.text(" ")).add(com.simibubi.create.foundation.utility.CreateLang.number(4 - basins)).style(ChatFormatting.GOLD).forGoggles(tooltip);
		return true;
	}

}

