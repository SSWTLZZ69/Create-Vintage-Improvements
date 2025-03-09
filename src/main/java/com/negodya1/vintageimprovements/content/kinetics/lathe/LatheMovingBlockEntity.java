package com.negodya1.vintageimprovements.content.kinetics.lathe;

import com.negodya1.vintageimprovements.VintageLang;
import com.negodya1.vintageimprovements.content.kinetics.lathe.recipe_card.RecipeCardItem;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LatheMovingBlockEntity extends KineticBlockEntity implements MenuProvider, IHaveGoggleInformation {

	public SmartInventory recipeSlot;
	public TurningRecipe currentRecipe;
	public int index;

	public LatheMovingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		recipeSlot = new SmartInventory(1, this, 1, false);
		index = -1;
	}

	public boolean manualMode() {
		return recipeSlot.isEmpty();
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.put("RecipeSlot", recipeSlot.serializeNBT());
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		recipeSlot.deserializeNBT(compound.getCompound("RecipeSlot"));
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {super.addBehaviours(behaviours);}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).inflate(.125f);
	}

	@Override
	public void tick() {super.tick();}

	@Override
	public void invalidate() {
		super.invalidate();
	}
	
	@Override
	public void destroy() {super.destroy();}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return LatheMenu.create(id, inv, this);
	}

	@Override
	public Component getDisplayName() {
		return VintageLang.translateDirect("gui.lathe.title");
	}

	public List<TurningRecipe> getRecipes() {
		LatheRotatingBlockEntity be = (LatheRotatingBlockEntity) level.getBlockEntity(LatheMovingBlock.getMaster(level, worldPosition, this.getBlockState()));
		if (be == null)
			return new ArrayList<>();
		return be.getRecipes();
	}

	public void resetRecipe() {
		LatheRotatingBlockEntity be = (LatheRotatingBlockEntity) level.getBlockEntity(LatheMovingBlock.getMaster(level, worldPosition, this.getBlockState()));
		be.resetRecipe();
	}

	public int getIndex(ItemStack ingredient) {
		if (recipeSlot.isEmpty())
			return -1;
		if (recipeSlot.getStackInSlot(0).getItem() instanceof RecipeCardItem) {
			if (!RecipeCardItem.haveRecipe(recipeSlot.getStackInSlot(0)))
				return -1;
			if (RecipeCardItem.getFrequencyItems(recipeSlot.getStackInSlot(0)).getStackInSlot(0).is(ingredient.getItem())) {
				int i = 0;
				for (TurningRecipe recipe : getRecipes()) {
					if (recipe.getResultItem(RegistryAccess.EMPTY).is(RecipeCardItem.getResultItems(recipeSlot.getStackInSlot(0)).getStackInSlot(0).getItem()))
						return i;
					i++;
				}
			}
		}
		return -1;
	}

	public int getTemporaryIndex() {
		return index;
	}

	public SmartInventory getInputInventory() {
		LatheRotatingBlockEntity be = (LatheRotatingBlockEntity) level.getBlockEntity(LatheMovingBlock.getMaster(level, worldPosition, this.getBlockState()));
		if (be == null)
			return new SmartInventory(1, this);
		return be.inputInv;
	}

	public Optional<TurningRecipe> getTemporaryRecipe() {
		if (currentRecipe == null)
			return Optional.empty();
		Optional<TurningRecipe> result = Optional.of(currentRecipe);
		currentRecipe = null;
		index = -1;
		return result;
	}
}
