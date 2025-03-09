package com.negodya1.vintageimprovements.infrastructure.ponder.scenes;

import com.negodya1.vintageimprovements.VintageItems;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugeBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.curving_press.CurvingBehaviour;
import com.negodya1.vintageimprovements.content.kinetics.curving_press.CurvingPressBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.vibration.VibratingTableBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.foundation.ponder.*;
import com.simibubi.create.foundation.ponder.element.BeltItemElement;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
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
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;
import static net.minecraft.world.level.block.LeverBlock.POWERED;

public class CurvingPressScenes {

	public static void processing(SceneBuilder builder, SceneBuildingUtil util) {

		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("curving_press", "Processing Items with the Curving Press");
		scene.configureBasePlate(0, 0, 5);
		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(5);

		ElementLink<WorldSectionElement> depot =
				scene.world().showIndependentSection(util.select().position(2, 1, 1), Direction.DOWN);
		scene.world().moveSection(depot, util.vector().of(0, 0, 1), 0);
		scene.idle(10);

		Selection pressS = util.select().position(2, 3, 2);
		BlockPos pressPos = util.grid().at(2, 3, 2);
		BlockPos depotPos = util.grid().at(2, 1, 1);
		scene.world().setKineticSpeed(pressS, 0);
		scene.world().showSection(pressS, Direction.DOWN);
		scene.idle(10);

		scene.world().showSection(util.select().fromTo(2, 1, 3, 2, 1, 5), Direction.NORTH);
		scene.idle(3);
		scene.world().showSection(util.select().position(2, 2, 3), Direction.SOUTH);
		scene.idle(3);
		scene.world().showSection(util.select().position(2, 3, 3), Direction.NORTH);
		scene.world().setKineticSpeed(pressS, -32);
		scene.effects().indicateSuccess(pressPos);
		scene.idle(10);

		Vec3 pressSide = util.vector().blockSurface(pressPos, Direction.WEST);
		scene.overlay().showText(60)
				.pointAt(pressSide)
				.placeNearTarget()
				.attachKeyFrame()
				.text("Before work you must install any Curving Head to Curving Press");
		scene.idle(50);

		ItemStack head = new ItemStack(VintageItems.W_SHAPED_CURVING_HEAD.asItem());

		scene.overlay().showControls(
				util.vector().blockSurface(pressPos, Direction.NORTH), Pointing.RIGHT, 8)
				.rightClick()
				.withItem(head);
		scene.idle(8);
		scene.world().modifyBlockEntity(pressPos, CurvingPressBlockEntity.class,
				ms -> ms.mode = 3);
		scene.idle(12);

		scene.overlay().showText(60)
				.pointAt(pressSide)
				.placeNearTarget()
				.attachKeyFrame()
				.text("Different heads provides different recipes");
		scene.idle(70);

		scene.overlay().showText(60)
				.pointAt(pressSide)
				.placeNearTarget()
				.attachKeyFrame()
				.text("To remove head, you must right-click with a Wrench");
		scene.idle(50);

		ItemStack wrench = new ItemStack(AllItems.WRENCH.asItem());

		scene.overlay().showControls(
				util.vector().blockSurface(pressPos, Direction.NORTH), Pointing.RIGHT, 8)
				.rightClick()
				.withItem(wrench);
		scene.idle(8);
		scene.world().modifyBlockEntity(pressPos, CurvingPressBlockEntity.class,
				ms -> ms.mode = 0);
		scene.idle(24);

		head = new ItemStack(VintageItems.CONVEX_CURVING_HEAD.asItem());

		scene.overlay().showControls(
				util.vector().blockSurface(pressPos, Direction.NORTH), Pointing.RIGHT, 8)
				.rightClick()
				.withItem(head);
		scene.idle(8);
		scene.world().modifyBlockEntity(pressPos, CurvingPressBlockEntity.class,
				ms -> ms.mode = 1);
		scene.idle(24);

		scene.overlay().showText(60)
				.pointAt(pressSide)
				.placeNearTarget()
				.attachKeyFrame()
				.text("The Curving Press can process items provided beneath it");
		scene.idle(70);
		scene.overlay().showText(60)
				.pointAt(pressSide.subtract(0, 2, 0))
				.placeNearTarget()
				.text("The Input items can be dropped or placed on a Depot under the Press");
		scene.idle(50);
		ItemStack iron = new ItemStack(AllItems.IRON_SHEET);
		scene.world().createItemOnBeltLike(depotPos, Direction.NORTH, iron);
		Vec3 depotCenter = util.vector().centerOf(depotPos.south());
		scene.overlay().showControls(depotCenter, Pointing.UP, 30).withItem(iron);
		scene.idle(10);

		Class<CurvingPressBlockEntity> type = CurvingPressBlockEntity.class;
		scene.world().modifyBlockEntity(pressPos, type, pte -> pte.getPressingBehaviour()
				.start(CurvingBehaviour.Mode.BELT));
		scene.idle(30);
		scene.world().modifyBlockEntity(pressPos, type, pte -> pte.getPressingBehaviour()
				.makePressingParticleEffect(depotCenter.add(0, 8 / 16f, 0), iron));
		scene.world().removeItemsFromBelt(depotPos);
		ItemStack bucket = new ItemStack(Items.BUCKET);
		scene.world().createItemOnBeltLike(depotPos, Direction.UP, bucket);
		scene.idle(10);
		scene.overlay().showControls(depotCenter, Pointing.UP, 50)
				.withItem(bucket);
		scene.idle(60);

		scene.world().hideIndependentSection(depot, Direction.NORTH);
		scene.idle(5);
		scene.world().showSection(util.select().fromTo(0, 1, 3, 0, 2, 3), Direction.DOWN);
		scene.idle(10);
		scene.world().showSection(util.select().fromTo(4, 1, 2, 0, 2, 2), Direction.SOUTH);
		scene.idle(20);
		BlockPos beltPos = util.grid().at(0, 1, 2);
		scene.overlay().showText(40)
				.pointAt(util.vector().blockSurface(beltPos, Direction.WEST))
				.placeNearTarget()
				.attachKeyFrame()
				.text("When items are provided on a belt...");
		scene.idle(30);

		ElementLink<BeltItemElement> ingot = scene.world().createItemOnBelt(beltPos, Direction.SOUTH, iron);
		scene.idle(15);
		ElementLink<BeltItemElement> ingot2 = scene.world().createItemOnBelt(beltPos, Direction.SOUTH, iron);
		scene.idle(15);
		scene.world().stallBeltItem(ingot, true);
		scene.world().modifyBlockEntity(pressPos, type, pte -> pte.getPressingBehaviour()
				.start(CurvingBehaviour.Mode.BELT));

		scene.overlay().showText(50)
				.pointAt(pressSide)
				.placeNearTarget()
				.attachKeyFrame()
				.text("The Press will hold and process them automatically");

		scene.idle(30);
		scene.world().modifyBlockEntity(pressPos, type, pte -> pte.getPressingBehaviour()
				.makePressingParticleEffect(depotCenter.add(0, 8 / 16f, 0), iron));
		scene.world().removeItemsFromBelt(pressPos.below(2));
		ingot = scene.world().createItemOnBelt(pressPos.below(2), Direction.UP, bucket);
		scene.world().stallBeltItem(ingot, true);
		scene.idle(15);
		scene.world().stallBeltItem(ingot, false);
		scene.idle(15);
		scene.world().stallBeltItem(ingot2, true);
		scene.world().modifyBlockEntity(pressPos, type, pte -> pte.getPressingBehaviour()
				.start(CurvingBehaviour.Mode.BELT));
		scene.idle(30);
		scene.world().modifyBlockEntity(pressPos, type, pte -> pte.getPressingBehaviour()
				.makePressingParticleEffect(depotCenter.add(0, 8 / 16f, 0), iron));
		scene.world().removeItemsFromBelt(pressPos.below(2));
		ingot2 = scene.world().createItemOnBelt(pressPos.below(2), Direction.UP, bucket);
		scene.world().stallBeltItem(ingot2, true);
		scene.idle(15);
		scene.world().stallBeltItem(ingot2, false);

	}

	public static void redstone(SceneBuilder builder, SceneBuildingUtil util) {

		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("curving_redstone", "Curving Press Comparator interaction");
		scene.configureBasePlate(0, 0, 5);
		scene.world().showSection(util.select().layer(0), Direction.UP);

		BlockPos centrifuge = util.grid().at(2, 3, 2);
		Selection centrifugeSelect = util.select().position(2, 3, 2);
		scene.world().setKineticSpeed(centrifugeSelect, 0);

		scene.world().showSection(util.select().fromTo(0, 1, 0, 4, 3, 4), Direction.DOWN);
		scene.idle(10);
		Vec3 centrifugeTop = util.vector().topOf(centrifuge);
		scene.overlay().showText(40)
				.attachKeyFrame()
				.text("Curving Press can produce Comparator signal")
				.pointAt(centrifugeTop)
				.placeNearTarget();
		scene.idle(50);

		scene.overlay().showText(50)
				.attachKeyFrame()
				.text("You must install Redstone Module into the Curving Press")
				.pointAt(centrifugeTop)
				.placeNearTarget();
		scene.idle(10);

		ItemStack module = new ItemStack(VintageItems.REDSTONE_MODULE.get());

		scene.overlay().showControls(
				util.vector().blockSurface(centrifuge, Direction.NORTH), Pointing.RIGHT, 30)
				.rightClick()
				.withItem(module);
		scene.world().modifyBlockEntity(centrifuge, CurvingPressBlockEntity.class,
				ms -> ms.addRedstoneApp(module.copy()));
		scene.idle(50);

		ItemStack itemStack = new ItemStack(VintageItems.CONCAVE_CURVING_HEAD);

		scene.overlay().showControls(
				util.vector().blockSurface(centrifuge, Direction.NORTH), Pointing.RIGHT, 8)
				.rightClick()
				.withItem(itemStack);
		scene.world().modifyBlockEntity(centrifuge, CurvingPressBlockEntity.class,
				ms -> ms.mode = 2);
		scene.world().replaceBlocks(util.select().position(1,3,2), Blocks.COMPARATOR.defaultBlockState().setValue(POWERED, true).setValue(FACING, Direction.EAST), false);
		scene.idle(30);

		scene.overlay().showText(60)
				.attachKeyFrame()
				.text("Curving Press will produce a level 15 redstone signal as long as it has an installed head")
				.pointAt(new Vec3(1,3.5,2))
				.placeNearTarget();
		scene.idle(70);

		scene.overlay().showControls(util.vector().blockSurface(centrifuge, Direction.NORTH), Pointing.RIGHT, 8)
				.rightClick()
				.withItem(AllItems.WRENCH.asStack());
		scene.world().modifyBlockEntity(centrifuge, CurvingPressBlockEntity.class,
				ms -> ms.mode = 0);
		scene.world().replaceBlocks(util.select().position(1,3,2), Blocks.COMPARATOR.defaultBlockState().setValue(POWERED, false).setValue(FACING, Direction.EAST), false);
		scene.idle(30);

		scene.overlay().showControls(util.vector().blockSurface(centrifuge, Direction.NORTH), Pointing.RIGHT, 8)
				.rightClick()
				.withItem(itemStack);
		scene.world().modifyBlockEntity(centrifuge, CurvingPressBlockEntity.class,
				ms -> ms.mode = 2);
		scene.world().replaceBlocks(util.select().position(1,3,2), Blocks.COMPARATOR.defaultBlockState().setValue(POWERED, true).setValue(FACING, Direction.EAST), false);
		scene.idle(30);

		scene.markAsFinished();
		scene.idle(25);
		scene.world().modifyEntities(ItemEntity.class, Entity::discard);
	}
}
