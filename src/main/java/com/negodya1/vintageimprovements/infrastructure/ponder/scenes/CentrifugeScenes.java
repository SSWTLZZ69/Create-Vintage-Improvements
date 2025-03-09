package com.negodya1.vintageimprovements.infrastructure.ponder.scenes;

import com.mojang.datafixers.functions.PointFreeRule;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintageItems;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugeBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.vibration.VibratingTableBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.ponder.*;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.element.InputWindowElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;
import static net.minecraft.world.level.block.LeverBlock.POWERED;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS;

public class CentrifugeScenes {

	public static void processing(SceneBuilder builder, SceneBuildingUtil util) {

		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("centrifuge", "Processing Items in the Centrifuge");
		scene.configureBasePlate(0, 0, 5);
		scene.world().showSection(util.select().layer(0), Direction.UP);

		BlockPos centrifuge = util.grid().at(2, 1, 2);
		Selection centrifugeSelect = util.select().position(2, 1, 2);
		scene.world().setKineticSpeed(centrifugeSelect, 0);

		scene.world().showSection(util.select().position(centrifuge), Direction.DOWN);
		scene.idle(10);
		Vec3 centrifugeTop = util.vector().topOf(centrifuge);
		scene.overlay().showText(40)
				.attachKeyFrame()
				.text("Centrifuge can process a variety of items")
				.pointAt(centrifugeTop)
				.placeNearTarget();
		scene.idle(50);

		scene.overlay().showText(90)
				.attachKeyFrame()
				.text("Before work you must install 4 Basins on Centrifuge")
				.pointAt(centrifugeTop)
				.placeNearTarget();
		scene.idle(10);

		ItemStack basin = new ItemStack(AllBlocks.BASIN.asItem());

		for (int i = 0; i < 4; i++) {
			scene.overlay().showControls(
					util.vector().blockSurface(centrifuge, Direction.NORTH), Pointing.RIGHT, 50);
			scene.idle(8);
			scene.world().modifyBlockEntity(centrifuge, CentrifugeBlockEntity.class,
					ms -> ms.addBasin(basin.copy()));
			scene.idle(12);
		}

		Selection cogs = util.select().fromTo(2, 2, 2, 5, 3, 2);

		scene.world().setBlock(centrifuge.above(), AllBlocks.SHAFT.getDefaultState().setValue(AXIS, Direction.Axis.Y), false);
		scene.world().setKineticSpeed(util.select().position(centrifuge.above()), 100);

		scene.world().showSection(cogs, Direction.DOWN);
		scene.world().showSection(util.select().position(5, 1, 2), Direction.DOWN);
		scene.idle(10);
		scene.world().setKineticSpeed(centrifugeSelect, 100);
		scene.effects().indicateSuccess(centrifuge);
		scene.idle(10);

		scene.overlay().showText(40)
				.attachKeyFrame()
				.colored(PonderPalette.GREEN)
				.text("They can be powered from the up or down using shafts")
				.pointAt(util.vector().topOf(centrifuge.above()))
				.placeNearTarget();
		scene.idle(50);

		ItemStack itemStack = new ItemStack(Items.ENDER_EYE);
		Vec3 entitySpawn = util.vector().topOf(centrifuge.north().above(3));

		ElementLink<EntityElement> entity1 =
				scene.world().createItemEntity(entitySpawn, util.vector().of(0, 0.2, 0), itemStack);
		scene.idle(28);

		scene.overlay().showText(40)
				.attachKeyFrame()
				.text("Items and Fluids сan only be inserted when Centrifuge is stopped")
				.pointAt(centrifuge.north().getCenter())
				.placeNearTarget();
		scene.idle(50);
		scene.world().modifyEntity(entity1, Entity::discard);
		scene.idle(10);

		scene.world().setBlock(centrifuge.above(), AllBlocks.CLUTCH.getDefaultState().setValue(AXIS, Direction.Axis.Y), false);

		BlockPos lever = centrifuge.above().north();
		Selection leverSelect = util.select().position(lever);
		Selection clutchSelect = util.select().position(lever.south());
		scene.world().showSection(leverSelect, Direction.DOWN);

		scene.idle(10);
		scene.world().replaceBlocks(leverSelect, Blocks.LEVER.defaultBlockState().setValue(POWERED, true), false);
		scene.world().replaceBlocks(clutchSelect, AllBlocks.CLUTCH.getDefaultState().setValue(POWERED, true).setValue(AXIS, Direction.Axis.Y), false);
		scene.world().setKineticSpeed(centrifugeSelect, 0);
		scene.idle(10);

		ElementLink<EntityElement> entity2 =
				scene.world().createItemEntity(entitySpawn, util.vector().of(0, 0.2, 0), itemStack);
		scene.idle(18);
		scene.world().modifyEntity(entity2, Entity::discard);
		scene.world().modifyBlockEntity(centrifuge, CentrifugeBlockEntity.class,
				ms -> ms.inputInv.setStackInSlot(0, itemStack));

		scene.idle(10);

		scene.overlay().showControls(centrifugeTop, Pointing.DOWN, 30);
		scene.idle(7);

		scene.world().replaceBlocks(leverSelect, Blocks.LEVER.defaultBlockState().setValue(POWERED, false), false);
		scene.world().replaceBlocks(clutchSelect, AllBlocks.CLUTCH.getDefaultState().setValue(POWERED, false).setValue(AXIS, Direction.Axis.Y), false);
		scene.world().setKineticSpeed(centrifugeSelect, 100);

		scene.idle(10);

		scene.overlay().showText(40)
				.attachKeyFrame()
				.text("The result can be extracted via Right-click or automatization...")
				.pointAt(centrifuge.north().getCenter())
				.placeNearTarget();
		scene.idle(50);

		scene.overlay().showText(40)
				.attachKeyFrame()
				.text("...but only when Centrifuge is stopped")
				.pointAt(centrifuge.north().getCenter())
				.placeNearTarget();

		scene.idle(10);
		scene.world().replaceBlocks(leverSelect, Blocks.LEVER.defaultBlockState().setValue(POWERED, true), false);
		scene.world().replaceBlocks(clutchSelect, AllBlocks.CLUTCH.getDefaultState().setValue(POWERED, true).setValue(AXIS, Direction.Axis.Y), false);
		scene.world().setKineticSpeed(centrifugeSelect, 0);
		scene.idle(10);

		ItemStack pearl = new ItemStack(Items.ENDER_PEARL);

		scene.world().showSection(util.select().position(0, 1, 1), Direction.DOWN);

		scene.world().modifyBlockEntity(centrifuge, CentrifugeBlockEntity.class,
				ms -> ms.inputInv.setStackInSlot(0, ItemStack.EMPTY));

		scene.world().createItemEntity(util.grid().at(0, 1, 1).getCenter(), new Vec3(0, 0.2, 0), pearl);
		scene.idle(15);

		scene.world().showSection(util.select().fromTo(1, 4, 2, 2, 4, 3), Direction.DOWN);
		scene.world().showSection(util.select().fromTo(1, 2, 2, 1, 3, 3), Direction.DOWN);
		scene.idle(16);

		scene.markAsFinished();
		scene.idle(25);
		scene.world().modifyEntities(ItemEntity.class, Entity::discard);
	}

	public static void redstone(SceneBuilder builder, SceneBuildingUtil util) {

		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("centrifuge_redstone", "Centrifuge Comparator interaction");
		scene.configureBasePlate(0, 0, 5);
		scene.world().showSection(util.select().layer(0), Direction.UP);

		BlockPos centrifuge = util.grid().at(2, 1, 2);
		Selection centrifugeSelect = util.select().position(2, 1, 2);
		scene.world().setKineticSpeed(centrifugeSelect, 0);

		scene.world().showSection(util.select().fromTo(0, 1, 0, 4, 1, 4), Direction.DOWN);
		scene.idle(10);
		Vec3 centrifugeTop = util.vector().topOf(centrifuge);
		scene.overlay().showText(40)
				.attachKeyFrame()
				.text("Centrifuge can produce Comparator signal")
				.pointAt(centrifugeTop)
				.placeNearTarget();
		scene.idle(50);

		scene.overlay().showText(50)
				.attachKeyFrame()
				.text("You must install Redstone Module into the Centrifuge")
				.pointAt(centrifugeTop)
				.placeNearTarget();
		scene.idle(10);

		ItemStack module = new ItemStack(VintageItems.REDSTONE_MODULE.get());

		scene.overlay().showControls(util.vector().blockSurface(centrifuge, Direction.NORTH), Pointing.RIGHT, 30).rightClick().withItem(module);
		scene.world().modifyBlockEntity(centrifuge, CentrifugeBlockEntity.class,
				ms -> ms.addRedstoneApp(module.copy()));
		scene.idle(30);

		ItemStack itemStack = new ItemStack(Items.MAGMA_CREAM);
		Vec3 entitySpawn = util.vector().topOf(centrifuge.north().above(3));

		ElementLink<EntityElement> entity2 =
				scene.world().createItemEntity(entitySpawn, util.vector().of(0, 0.2, 0), itemStack);
		scene.idle(18);
		scene.world().modifyEntity(entity2, Entity::discard);

		scene.world().replaceBlocks(util.select().position(0,1,1), Blocks.COMPARATOR.defaultBlockState().setValue(POWERED, true).setValue(FACING, Direction.EAST), false);

		scene.overlay().showText(60)
				.attachKeyFrame()
				.text("Centrifuge will produce a level 15 redstone signal as long as it has a recipe")
				.pointAt(new Vec3(0,1,1))
				.placeNearTarget();
		scene.idle(70);

		scene.markAsFinished();
		scene.idle(25);
		scene.world().modifyEntities(ItemEntity.class, Entity::discard);
	}
}
