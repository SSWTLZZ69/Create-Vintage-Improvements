package com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugeBlock;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugeBlockEntity;
import com.negodya1.vintageimprovements.foundation.advancement.VintageAdvancementBehaviour;
import com.negodya1.vintageimprovements.foundation.advancement.VintageAdvancements;
import com.negodya1.vintageimprovements.foundation.utility.VintageLang;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.*;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.level.PistonEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class VacuumChamberBlockEntity extends BasinOperatingBlockEntity {

	private static final Object vacuumizingRecipesKey = new Object();

	public int runningTicks;
	public int processingTicks;
	public boolean running;

	public SmartFluidTankBehaviour outputTank;
	public SmartFluidTankBehaviour inputTank;
	public LazyOptional<IFluidHandler> fluidCapability;
	boolean contentsChanged;
	boolean mode;
	VintageAdvancementBehaviour advancementBehaviour;

	//simibubi完全没留接口让机器知道自己在执行什么序列装配配方，因此额外设置一个变量存储当前配方序列信息
	//0表示当前执行非序列配方，其他值表示产物的序列装配步骤数，其中1为装配开始
	private int sequencedAssemblyStep;

	public VacuumChamberBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		mode = false;
		sequencedAssemblyStep = 0;
	}

	public boolean changeMode() {
		mode = !mode;
		return mode;
	}

	public float getRenderedHeadOffset(float partialTicks) {
		int localTick;
		float offset = 0;
		if (running) {
			if (runningTicks < 20) {
				localTick = runningTicks;
				float num = (localTick + partialTicks) / 20f;
				num = ((2 - Mth.cos((float) (num * Math.PI))) / 2);
				offset = num - .5f;
				offset = Mth.clamp(offset,0, 10 / 16f);
			} else if (runningTicks <= 20) {
				offset = 10 / 16f;
			} else {
				localTick = 40 - runningTicks;
				float num = (localTick - partialTicks) / 20f;
				num = ((2 - Mth.cos((float) (num * Math.PI))) / 2);
				offset = num - .5f;
				offset = Mth.clamp(offset,0, 10 / 16f);
			}
		}
		return offset + 7 / 16f;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);

		inputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 2, 1000, true)
				.whenFluidUpdates(() -> this.basinChecker.scheduleUpdate());
		outputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 2, 1000, true)
				.whenFluidUpdates(() -> this.basinChecker.scheduleUpdate())
				.forbidInsertion();
		behaviours.add(inputTank);
		behaviours.add(outputTank);

		fluidCapability = LazyOptional.of(() -> {
			LazyOptional<? extends IFluidHandler> inputCap = inputTank.getCapability();
			LazyOptional<? extends IFluidHandler> outputCap = outputTank.getCapability();
			return new VacuumChamberTanksHandler(outputCap.orElse(null), inputCap.orElse(null));
		});

		advancementBehaviour = new VintageAdvancementBehaviour(this);
		behaviours.add(advancementBehaviour);
	}

	private class VacuumChamberTanksHandler extends CombinedTankWrapper {
		public VacuumChamberTanksHandler(IFluidHandler... fluidHandlers) {
			super(fluidHandlers);
		}
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).expandTowards(0, -1.5, 0);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		running = compound.getBoolean("Running");
		runningTicks = compound.getInt("Ticks");
		mode = compound.getBoolean("Mode");
		sequencedAssemblyStep = compound.getInt("Step");
		super.read(compound, clientPacket);

		if (clientPacket && hasLevel())
			getBasin().ifPresent(bte -> bte.setAreFluidsMoving(running && runningTicks <= 20));
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		compound.putBoolean("Running", running);
		compound.putInt("Ticks", runningTicks);
		compound.putBoolean("Mode", mode);
		compound.putInt("Step", sequencedAssemblyStep);
		super.write(compound, clientPacket);
	}

	@Override
	public void tick() {
		super.tick();

		if (runningTicks >= 40) {
			running = false;
			runningTicks = 0;
			basinChecker.scheduleUpdate();
			return;
		}

		float speed = Math.abs(getSpeed());
		if (running && level != null) {
			if (level.isClientSide && runningTicks == 20)
				renderParticles();

			if ((!level.isClientSide || isVirtual()) && runningTicks == 20) {
				if (processingTicks < 0) {
					float recipeSpeed = 1;
					if (currentRecipe instanceof ProcessingRecipe) {
						int t = ((ProcessingRecipe<?>) currentRecipe).getProcessingDuration();
						if (t != 0)
							recipeSpeed = t / 100f;
					}

					processingTicks = Mth.clamp((Mth.log2((int) (512 / speed))) * Mth.ceil(recipeSpeed * 15) + 1, 1, 512);

					Optional<BasinBlockEntity> basin = getBasin();
					if (basin.isPresent()) {
						Couple<SmartFluidTankBehaviour> tanks = basin.get()
							.getTanks();
						if (!tanks.getFirst()
							.isEmpty()
							|| !tanks.getSecond()
								.isEmpty())
							level.playSound(null, worldPosition, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT,
								SoundSource.BLOCKS, .75f, speed < 65 ? .75f : 1.5f);
					}

				} else {
					processingTicks--;
					if (processingTicks == 0) {
						runningTicks++;
						processingTicks = -1;
						applyBasinRecipe();
						sendData();
					}
				}
			}

			if (runningTicks != 20)
				runningTicks++;
		}
	}

	@Override
	protected void applyBasinRecipe() {
		if (currentRecipe == null)
			return;

		Optional<BasinBlockEntity> optionalBasin = getBasin();
		if (!optionalBasin.isPresent())
			return;
		BasinBlockEntity basin = optionalBasin.get();
		boolean wasEmpty = basin.canContinueProcessing();
		if (!mode)
			if (!VacuumizingRecipe.apply(basin, currentRecipe, this, sequencedAssemblyStep))
				return;
		if (mode)
			if (!PressurizingRecipe.apply(basin, currentRecipe, this, sequencedAssemblyStep))
				return;
		getProcessedRecipeTrigger().ifPresent(this::award);
		basin.inputTank.sendDataImmediately();
		advancementBehaviour.awardVintageAdvancement(VintageAdvancements.USE_COMPRESSOR);

		// Continue mixing
		if (wasEmpty && matchBasinRecipe(currentRecipe)) {
			continueWithPreviousRecipe();
			sendData();
		}

		basin.notifyChangeOfContents();
	}

	@Override
	protected List<Recipe<?>> getMatchingRecipes() {
		Optional<? extends Recipe<?>> assemblyRecipe = matchAssemblyRecipe();
		if(assemblyRecipe.isPresent()){
			return ImmutableList.of(assemblyRecipe.get());
		}

		//未匹配到序列配方
		sequencedAssemblyStep = 0;

		List<Recipe<?>> res = new ArrayList<>();
		for (Recipe recipe : super.getMatchingRecipes()) {
			if (mode && recipe instanceof PressurizingRecipe) res.add(recipe);
			else if (!mode && recipe instanceof VacuumizingRecipe) res.add(recipe);
		}

		return res;
	}

	Optional<? extends Recipe<?>> matchAssemblyRecipe(){
		//获取工作盆
		Optional<BasinBlockEntity> basin = getBasin();
		if (basin.isEmpty()) {
			return Optional.empty();
		}

		//获取盆内物品
		IItemHandler availableItems = basin.get().getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
		if(availableItems == null){
			return Optional.empty();
		}

		//遍历物品判断能否序列装配
		Optional<? extends Recipe<?>> assemblyRecipe;
		for(int slot = 0; slot < availableItems.getSlots(); slot++){
			ItemStack  item = availableItems.getStackInSlot(slot);
			if(mode){//加压模式
				assemblyRecipe = SequencedAssemblyRecipe.getRecipe(level, item,
						VintageRecipes.PRESSURIZING.getType(), PressurizingRecipe.class);
				//判断序列步骤
				if (item.hasTag() && item.getTag().contains("SequencedAssembly")){
					CompoundTag tag = item.getTag();
					int step = tag.getCompound("SequencedAssembly")
							.getInt("Step");
					sequencedAssemblyStep = step + 1;
				}else{
					sequencedAssemblyStep = 1;
				}
				//检查过滤、加热、盆内原料、机器副原料
				if(assemblyRecipe.isPresent() &&
						PressurizingRecipe.match(basin.get(), assemblyRecipe.get(), this, sequencedAssemblyStep)
				){
					return assemblyRecipe;
				}
			}else{//减压模式
				assemblyRecipe = SequencedAssemblyRecipe.getRecipe(level, item,
						VintageRecipes.VACUUMIZING.getType(), VacuumizingRecipe.class);

				if (item.hasTag() && item.getTag().contains("SequencedAssembly")){
					CompoundTag tag = item.getTag();
					int step = tag.getCompound("SequencedAssembly")
							.getInt("Step");
					sequencedAssemblyStep = step + 1;
				}else{
					sequencedAssemblyStep = 1;
				}

				if(assemblyRecipe.isPresent() &&
					VacuumizingRecipe.match(basin.get(), assemblyRecipe.get(), this, sequencedAssemblyStep)
				){
					return assemblyRecipe;
				}
			}
		}

		//无匹配序列装配配方
		return Optional.empty();
	}

	@Override
	protected <C extends Container> boolean matchBasinRecipe(Recipe<C> recipe) {
		if (recipe == null)
			return false;
		Optional<BasinBlockEntity> basin = getBasin();
		if (!basin.isPresent())
			return false;

		if (recipe instanceof VacuumizingRecipe r)
			return r.match(basin.get(), recipe, this, sequencedAssemblyStep);
		if (recipe instanceof PressurizingRecipe r)
			return r.match(basin.get(), recipe, this, sequencedAssemblyStep);

		return BasinRecipe.match(basin.get(), recipe);
	}

	public boolean acceptOutputs(List<FluidStack> outputFluids, boolean simulate) {
		outputTank.allowInsertion();
		boolean acceptOutputsInner = acceptOutputsInner(outputFluids, simulate);
		outputTank.forbidInsertion();
		return acceptOutputsInner;
	}

	private boolean acceptOutputsInner(List<FluidStack> outputFluids, boolean simulate) {
		BlockState blockState = getBlockState();
		if (!(blockState.getBlock() instanceof VacuumChamberBlock))
			return false;

		IFluidHandler targetTank = outputTank.getCapability()
				.orElse(null);

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

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.FLUID_HANDLER)
			return fluidCapability.cast();
		return super.getCapability(cap, side);
	}

	public void renderParticles() {
		Optional<BasinBlockEntity> basin = getBasin();
		if (!basin.isPresent() || level == null)
			return;

		if (!level.getBlockState(getBlockPos().above()).is(Blocks.AIR)) return;

		if (Mth.abs(getSpeed()) < IRotate.SpeedLevel.MEDIUM.getSpeedValue()) return;


		float angle = level.random.nextFloat() * 360;
		Vec3 offset = new Vec3(0, 0, 0.25f);
		offset = VecHelper.rotate(offset, angle, Direction.Axis.Y);
		Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Direction.Axis.Y)
				.add(0, .25f, 0);
		Vec3 center = offset.add(VecHelper.getCenterOf(worldPosition));
		target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1 / 128f);
		if (mode) level.addParticle(ParticleTypes.CLOUD, center.x + target.x * 10, center.y + 0.5f + target.y * 10, center.z + target.z * 10, -target.x * 0.6, -target.y * 0.6, -target.z * 0.6);
		else level.addParticle(ParticleTypes.CLOUD, center.x, center.y + 0.5f, center.z, target.x, target.y, target.z);
	}

	@Override
	protected <C extends Container> boolean matchStaticFilters(Recipe<C> r) {
		return r.getType() == VintageRecipes.VACUUMIZING.getType() || r.getType() == VintageRecipes.PRESSURIZING.getType();
	}

	@Override
	public void startProcessingBasin() {
		if (running && runningTicks <= 20)
			return;
		super.startProcessingBasin();
		running = true;
		runningTicks = 0;
	}

	@Override
	public boolean continueWithPreviousRecipe() {
		runningTicks = 20;
		return true;
	}

	@Override
	protected void onBasinRemoved() {
		if (!running)
			return;
		runningTicks = 40;
		running = false;
	}

	@Override
	protected Object getRecipeCacheKey() {
		return vacuumizingRecipesKey;
	}

	@Override
	protected boolean isRunning() {
		return running;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void tickAudio() {
		super.tickAudio();

		if (runningTicks == 25)
			AllSoundEvents.STEAM.playAt(level, worldPosition, 3, 1, true);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);

		if (mode) VintageLang.translate("gui.goggles.current_mode")
				.add(Lang.text(" ")).add(VintageLang.translate("gui.goggles.pressurizing_mode"))
				.style(ChatFormatting.DARK_PURPLE).forGoggles(tooltip);
		else VintageLang.translate("gui.goggles.current_mode")
				.add(Lang.text(" ")).add(VintageLang.translate("gui.goggles.vacuumizing_mode"))
				.style(ChatFormatting.DARK_AQUA).forGoggles(tooltip);

		IFluidHandler fluids = fluidCapability.orElse(new FluidTank(0));
		boolean isEmpty = true;

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

}
