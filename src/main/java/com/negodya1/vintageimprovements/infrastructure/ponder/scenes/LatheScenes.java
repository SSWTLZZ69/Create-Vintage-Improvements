package com.negodya1.vintageimprovements.infrastructure.ponder.scenes;

import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintageItems;
import com.negodya1.vintageimprovements.content.kinetics.lathe.LatheMovingBlock;
import com.negodya1.vintageimprovements.content.kinetics.lathe.LatheMovingBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.lathe.LatheRotatingBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.vibration.VibratingTableBlockEntity;
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
import net.minecraft.world.phys.Vec3;

public class LatheScenes {

	public static void processing(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("lathe", "Processing Items with the Lathe");
		scene.configureBasePlate(0, 0, 5);

		scene.world().showSection(util.select().layer(0), Direction.UP);

		BlockPos lathe = util.grid().at(2, 1, 2);
		Selection latheSelect = util.select().position(2, 1, 2);

		BlockPos latheKinetic = util.grid().at(1, 1, 2);
		Selection latheKineticSelect = util.select().position(1, 1, 2);

		Selection cogs1 = util.select().fromTo(1, 1, 3, 1, 1, 5);
		Selection cogs2 = util.select().fromTo(4, 1, 2, 3, 1, 2);
		scene.world().setKineticSpeed(latheSelect, 0);
		scene.world().setKineticSpeed(latheKineticSelect, 0);

		scene.idle(10);
		scene.world().showSection(util.select().fromTo(lathe, latheKinetic), Direction.DOWN);
		scene.idle(10);
		Vec3 latheTop = util.vector().centerOf(lathe);
		scene.overlay().showText(40)
				.attachKeyFrame()
				.text("Lathe can process a variety of items")
				.pointAt(latheTop)
				.placeNearTarget();
		scene.idle(50);

		scene.world().showSection(cogs1, Direction.DOWN);
		scene.world().showSection(cogs2, Direction.DOWN);
		scene.idle(10);
		scene.world().setKineticSpeed(latheKineticSelect, 64);
		scene.effects().indicateSuccess(latheKinetic);
		scene.world().setKineticSpeed(latheSelect, 128);
		scene.effects().indicateSuccess(lathe);
		scene.idle(10);

		scene.overlay().showText(40)
				.attachKeyFrame()
				.colored(PonderPalette.GREEN)
				.text("They can be powered from the side...")
				.pointAt(latheKinetic.south().getCenter())
				.placeNearTarget();
		scene.idle(50);

		scene.overlay().showText(40)
				.attachKeyFrame()
				.colored(PonderPalette.GREEN)
				.text("...and front using shafts")
				.pointAt(lathe.east().getCenter())
				.placeNearTarget();
		scene.idle(50);

		scene.overlay().showText(60)
				.pointAt(lathe.getCenter())
				.placeNearTarget()
				.attachKeyFrame()
				.text("Ingredients can be inserted to front block via Right-click");
		scene.idle(70);

		ItemStack itemStack = new ItemStack(Blocks.IRON_BLOCK.asItem());

		scene.overlay().showControls(
				util.vector().blockSurface(lathe, Direction.NORTH), Pointing.RIGHT, 8)
				.rightClick()
				.withItem(itemStack);
		scene.idle(8);
		scene.world().modifyBlockEntity(lathe, LatheRotatingBlockEntity.class,
				ms -> ms.inputInv.setStackInSlot(0, itemStack));
		scene.idle(12);

		scene.overlay().showText(40)
				.attachKeyFrame()
				.text("To select a recipe, click on the back block")
				.pointAt(latheKinetic.getCenter())
				.placeNearTarget();
		scene.idle(50);

		scene.overlay().showControls(
				util.vector().blockSurface(latheKinetic, Direction.NORTH), Pointing.RIGHT,
				8)
				.rightClick();
		scene.idle(10);

		scene.overlay().showText(40)
				.attachKeyFrame()
				.text("You need to do this every time")
				.pointAt(latheKinetic.getCenter())
				.placeNearTarget();
		scene.idle(50);


		ItemStack head = new ItemStack(VintageItems.CONVEX_CURVING_HEAD.get());

		scene.world().modifyBlockEntity(lathe, LatheRotatingBlockEntity.class,
				ms -> ms.inputInv.setStackInSlot(0, ItemStack.EMPTY));
		scene.world().modifyBlockEntity(lathe, LatheRotatingBlockEntity.class,
				ms -> ms.outputInv.setStackInSlot(0, head));

		scene.overlay().showText(50)
				.text("The result can be obtained via Right-click")
				.pointAt(util.vector().blockSurface(lathe, Direction.WEST))
				.placeNearTarget();
		scene.idle(60);

		scene.overlay().showControls(
				util.vector().blockSurface(lathe, Direction.NORTH), Pointing.RIGHT, 40)
				.rightClick()
				.withItem(head);
		scene.idle(50);

		scene.addKeyframe();
		scene.world().showSection(util.select().position(0, 2, 2), Direction.UP);

		scene.world().modifyBlockEntity(lathe, LatheRotatingBlockEntity.class,
				ms -> ms.outputInv.setStackInSlot(0, ItemStack.EMPTY));
		scene.idle(20);

		scene.overlay().showText(50)
				.text("The items can also be extracted/inserted by automation")
				.pointAt(util.vector().blockSurface(lathe, Direction.WEST)
						.add(0, .4, -1))
				.placeNearTarget();
		scene.idle(20);

		scene.world().showSection(util.select().position(lathe.north()), Direction.DOWN);

		scene.idle(40);

		scene.markAsFinished();
		scene.idle(25);
		scene.world().modifyEntities(ItemEntity.class, Entity::discard);
	}

	public static void automation(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("lathe_automation", "Automation of the Lathe");
		scene.configureBasePlate(0, 0, 5);

		scene.world().showSection(util.select().layer(0), Direction.UP);

		BlockPos lathe = util.grid().at(3, 2, 2);
		Selection latheSelect = util.select().position(3, 2, 2);

		BlockPos latheKinetic = util.grid().at(2, 2, 2);
		Selection latheKineticSelect = util.select().position(2, 2, 2);

		Selection cogs1 = util.select().fromTo(2, 1, 3, 2, 2, 5);
		Selection cogs2 = util.select().fromTo(5, 1, 2, 4, 2, 2);
		Selection cogs3 = util.select().fromTo(0, 1, 2, 1, 1, 5);
		Selection belt = util.select().fromTo(0, 1, 1, 3, 2, 1);
		scene.world().setKineticSpeed(latheSelect, 0);
		scene.world().setKineticSpeed(latheKineticSelect, 0);

		scene.idle(10);
		scene.world().showSection(util.select().fromTo(lathe.below(), latheKinetic.below()), Direction.DOWN);
		scene.idle(10);
		scene.world().showSection(util.select().fromTo(lathe, latheKinetic), Direction.DOWN);
		scene.idle(10);
		Vec3 latheTop = util.vector().centerOf(latheKinetic);
		scene.overlay().showText(40)
				.attachKeyFrame()
				.text("Lathe can be automated with the Recipe Card")
				.pointAt(latheTop)
				.placeNearTarget();
		scene.idle(50);

		scene.world().showSection(cogs1, Direction.DOWN);
		scene.world().showSection(cogs2, Direction.DOWN);
		scene.idle(10);
		scene.world().setKineticSpeed(latheKineticSelect, -64);
		scene.effects().indicateSuccess(latheKinetic);
		scene.world().setKineticSpeed(latheSelect, -128);
		scene.effects().indicateSuccess(lathe);
		scene.idle(10);

		scene.overlay().showText(60)
				.pointAt(latheKinetic.getCenter())
				.placeNearTarget()
				.attachKeyFrame()
				.text("Use a Recipe Card via Right-click to define a recipe");
		scene.idle(30);


		ItemStack itemStack = new ItemStack(VintageItems.RECIPE_CARD.asItem());
		scene.overlay().showControls(
				util.vector().blockSurface(latheKinetic.above(2), Direction.NORTH), Pointing.RIGHT, 8)
				.rightClick()
				.withItem(itemStack);
		scene.idle(40);

		scene.overlay().showText(60)
				.pointAt(latheKinetic.getCenter())
				.placeNearTarget()
				.attachKeyFrame()
				.text("Then you must put it inside a back Lathe block");
		scene.idle(30);
		scene.overlay().showControls(
				util.vector().blockSurface(latheKinetic, Direction.NORTH), Pointing.RIGHT, 8)
				.rightClick()
				.withItem(itemStack);
		scene.world().modifyBlockEntity(latheKinetic, LatheMovingBlockEntity.class,
				ms -> ms.recipeSlot.setStackInSlot(0, itemStack));
		scene.idle(40);

		scene.world().showSection(util.select().position(lathe.above()), Direction.UP);
		scene.idle(20);

		ItemStack iron = new ItemStack(Items.IRON_BLOCK);
		Vec3 entitySpawn = util.vector().topOf(lathe.above(3));

		ElementLink<EntityElement> entity1 =
				scene.world().createItemEntity(entitySpawn, util.vector().of(0, 0.2, 0), iron);
		scene.idle(18);
		scene.world().modifyEntity(entity1, Entity::discard);
		scene.world().modifyBlockEntity(lathe, LatheRotatingBlockEntity.class,
				ms -> ms.inputInv.setStackInSlot(0, iron));
		scene.idle(10);
		scene.idle(7);

		scene.overlay().showText(50)
				.pointAt(lathe.getCenter())
				.placeNearTarget()
				.attachKeyFrame()
				.text("Lathe will automatically apply chosen recipe");
		scene.idle(60);

		ItemStack head = new ItemStack(VintageItems.CONVEX_CURVING_HEAD.get());

		scene.world().modifyBlockEntity(lathe, LatheRotatingBlockEntity.class,
				ms -> ms.inputInv.setStackInSlot(0, ItemStack.EMPTY));
		scene.world().modifyBlockEntity(lathe, LatheRotatingBlockEntity.class,
				ms -> ms.outputInv.setStackInSlot(0, head));
		scene.idle(40);

		scene.world().showSection(cogs3, Direction.UP);
		scene.idle(10);
		scene.world().showSection(belt, Direction.UP);
		scene.idle(10);
		scene.world().modifyBlockEntity(lathe, LatheRotatingBlockEntity.class,
				ms -> ms.outputInv.setStackInSlot(0, ItemStack.EMPTY));
		scene.world().createItemOnBeltLike(lathe.north().below(), Direction.SOUTH, head);
		scene.idle(35);

		scene.markAsFinished();
		scene.idle(25);
		scene.world().modifyEntities(ItemEntity.class, Entity::discard);
	}
}
