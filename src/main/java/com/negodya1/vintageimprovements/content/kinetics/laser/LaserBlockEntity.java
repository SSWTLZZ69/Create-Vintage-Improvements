package com.negodya1.vintageimprovements.content.kinetics.laser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintageLang;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.content.energy.base.ElectricKineticBlockEntity;
import com.negodya1.vintageimprovements.foundation.advancement.VintageAdvancementBehaviour;
import com.negodya1.vintageimprovements.foundation.advancement.VintageAdvancements;
import com.negodya1.vintageimprovements.infrastructure.config.VintageConfig;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import com.simibubi.create.foundation.recipe.RecipeApplier;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class LaserBlockEntity extends ElectricKineticBlockEntity implements IHaveGoggleInformation {

	private Optional<LaserCuttingRecipe> recipeCache = Optional.empty();

	private final ItemStackHandler inputInv;
	private int chargeAccumulator;
	protected int poweredTimer = 0;
	int oldEnergyCount;
	VintageAdvancementBehaviour advancementBehaviour;

	public LaserBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
		inputInv = new ItemStackHandler(1);
		oldEnergyCount = 0;
	}

	@Override
	public int getCapacity() {
		return Math.max(VintageConfig.server().energy.laserCapacity.get(),
				Math.max(VintageConfig.server().energy.laserChargeRate.get(),
						VintageConfig.server().energy.laserRecipeChargeRate.get()));
	}

	@Override
	public int getMaxIn() {
		return VintageConfig.server().energy.laserMaxInput.get();
	}

	@Override
	public int getMaxOut() {
		return 0;
	}

	public BeltProcessingBehaviour processingBehaviour;

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		processingBehaviour =
			new BeltProcessingBehaviour(this).whenItemEnters((s, i) -> LaserBeltCallbacks.onItemReceived(s, i, this))
				.whileItemHeld((s, i) -> LaserBeltCallbacks.whenItemHeld(s, i, this));
		behaviours.add(processingBehaviour);
		advancementBehaviour = new VintageAdvancementBehaviour(this);
		behaviours.add(advancementBehaviour);
	}

	@Override
	public boolean isEnergyInput(Direction side) {
		return side != Direction.UP;
	}

	@Override
	public boolean isEnergyOutput(Direction side) {
		return false;
	}

	public int getConsumption() {
		return VintageConfig.server().energy.laserChargeRate.get();
	}

	protected BeltProcessingBehaviour.ProcessingResult onLaser(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		BeltProcessingBehaviour.ProcessingResult res = laserCompundAndStack(transported, handler);
		return res;
	}

	@Override
	public void tick() {
		super.tick();
		if(level == null) return;

		if(level.isClientSide())
			return;

		if (oldEnergyCount != localEnergy.getEnergyStored()) {
			oldEnergyCount = localEnergy.getEnergyStored();
			sendData();
		}

		applyInWorld();

		if(poweredTimer > 0) {
			if(!isPoweredState())
				VintageBlocks.LASER.get().setPowered(level, getBlockPos(), true);
			poweredTimer--;
		}
		else
			if(isPoweredState())
				VintageBlocks.LASER.get().setPowered(level, getBlockPos(), false);
	}

	protected void applyInWorld() {
		AABB bb = new AABB(worldPosition.below(1));

		if (level.isClientSide)
			return;

		for (Entity entity : level.getEntities(null, bb)) {
			if (!(entity instanceof ItemEntity itemEntity))
				continue;
			if (!entity.isAlive() || !entity.onGround())
				continue;

			if (tryProcessInWorld(itemEntity)) {
				poweredTimer = 10;
				sendData();
			}
			break;
		}
	}

	public boolean isPoweredState() {
		return getBlockState().getValue(LaserBlock.POWERED);
	}

	protected BeltProcessingBehaviour.ProcessingResult laserCompundAndStack(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		ItemStack stack = transported.stack;
		if(stack == null) return BeltProcessingBehaviour.ProcessingResult.PASS;
		if(laserRecipe(stack, transported, handler)) {
			poweredTimer = 10;
			return BeltProcessingBehaviour.ProcessingResult.HOLD;
		}
		return BeltProcessingBehaviour.ProcessingResult.PASS;
	}

	private boolean laserRecipe(ItemStack stack, TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		if(this.getLevel() == null) return false;
		if (Mth.abs(getSpeed()) == 0) return false;
		if(!inputInv.getStackInSlot(0).is(stack.getItem())) {
			inputInv.setStackInSlot(0, stack);
			recipeCache = find(new RecipeWrapper(inputInv), this.getLevel());
			chargeAccumulator = 0;
		}
		if(recipeCache.isPresent()) {
			LaserCuttingRecipe recipe = recipeCache.get();
			int energyRemoved = Math.min(VintageConfig.server().energy.laserRecipeChargeRate.get(),
					Math.min(recipe.getEnergy() - chargeAccumulator,
							recipe.getMaxChargeRate()));
			energyRemoved *= Mth.clamp(Math.abs(getSpeed() / 128f), .1f, 1f);
			if (energyRemoved == 0) energyRemoved = 1;
			if (VintageImprovements.useEnergy || VintageConfig.server().energy.forceEnergy.get())
				energyRemoved = localEnergy.internalConsumeEnergy(energyRemoved);

			chargeAccumulator += energyRemoved;
			if(chargeAccumulator >= recipe.getEnergy()) {
				TransportedItemStack remainingStack = transported.copy();
				int inputCount = recipe.getIngredients().get(0).getItems()[0].getCount();
				List<ItemStack> outputs = RecipeApplier.applyRecipeOn(level,
						ItemHandlerHelper.copyStackWithSize(transported.stack, inputCount), recipe);
				List<TransportedItemStack> outList = new ArrayList<>();
				outputs.forEach(itemStack -> {
					TransportedItemStack tmp = transported.copy();
					tmp.stack = itemStack;
					outList.add(tmp);
				});
				remainingStack.stack.shrink(inputCount);
				handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertToAndLeaveHeld(outList, remainingStack));
				chargeAccumulator = 0;
				advancementBehaviour.awardVintageAdvancement(VintageAdvancements.USE_LASER);
			}
			return true;
		}
		return false;
	}

	public boolean tryProcessInWorld(ItemEntity itemEntity) {
		ItemStack item = itemEntity.getItem();

		if(this.getLevel() == null) return false;
		if (Mth.abs(getSpeed()) == 0) return false;
		if(!inputInv.getStackInSlot(0).is(item.getItem())) {
			inputInv.setStackInSlot(0, item);
			recipeCache = find(new RecipeWrapper(inputInv), this.getLevel());
			chargeAccumulator = 0;
		}
		if(recipeCache.isPresent()) {
			LaserCuttingRecipe recipe = recipeCache.get();
			int energyRemoved = Math.min(VintageConfig.server().energy.laserRecipeChargeRate.get(),
					Math.min(recipe.getEnergy() - chargeAccumulator,
							recipe.getMaxChargeRate()));
			energyRemoved *= Mth.clamp(Math.abs(getSpeed() / 128f), .1f, 1f);
			if (energyRemoved == 0) energyRemoved = 1;
			if (VintageImprovements.useEnergy || VintageConfig.server().energy.forceEnergy.get())
				energyRemoved = localEnergy.internalConsumeEnergy(energyRemoved);

			chargeAccumulator += energyRemoved;
			if(chargeAccumulator >= recipe.getEnergy()) {
				ItemStack itemCreated = ItemStack.EMPTY;
				for (ItemStack result : RecipeApplier.applyRecipeOn(level, ItemHandlerHelper.copyStackWithSize(item, 1),
						recipe)) {
					if (itemCreated.isEmpty())
						itemCreated = result.copy();
					ItemEntity created =
							new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result);
					created.setDefaultPickUpDelay();
					created.setDeltaMovement(VecHelper.offsetRandomly(Vec3.ZERO, level.random, .05f));
					level.addFreshEntity(created);
				}
				item.shrink(recipe.getIngredients().size());
				advancementBehaviour.awardVintageAdvancement(VintageAdvancements.USE_LASER);

				chargeAccumulator = 0;
				return true;
			}
			return true;
		}

		return false;
	}

	public Optional<LaserCuttingRecipe> find(RecipeWrapper wrapper, Level world) {
		Optional<LaserCuttingRecipe> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(level, wrapper.getItem(0),
				VintageRecipes.LASER_CUTTING.getType(), LaserCuttingRecipe.class);
		if (assemblyRecipe.isPresent())
			return assemblyRecipe;

		return world.getRecipeManager().getRecipeFor(VintageRecipes.LASER_CUTTING.getType(), wrapper, world);
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).expandTowards(0, -1.5, 0)
				.expandTowards(0, 1, 0);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);

		if (VintageImprovements.useEnergy || VintageConfig.server().energy.forceEnergy.get()) {
			VintageLang.translate("gui.goggles.energy").add(Component.literal(" " + localEnergy.getEnergyStored() +
							"/" + localEnergy.getMaxEnergyStored() + "fe"))
					.style(ChatFormatting.YELLOW).forGoggles(tooltip);

		}

		return true;
	}
}
