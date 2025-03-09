package com.negodya1.vintageimprovements.infrastructure.ponder.scenes;

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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;

public class VibratingTableScenes {

	public static void processing(SceneBuilder builder, SceneBuildingUtil util) {

		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("vibrating_table", "Processing Items on the Vibrating Table");
		scene.configureBasePlate(0, 0, 5);

		Selection belt = util.select().fromTo(1, 1, 5, 0, 1, 2)
				.add(util.select().position(1, 2, 2));
		Selection beltCog = util.select().position(2, 0, 5);

		scene.world().showSection(util.select().layer(0)
				.substract(beltCog), Direction.UP);

		BlockPos table = util.grid().at(2, 2, 2);
		Selection tableSelect = util.select().position(2, 2, 2);
		Selection cogs = util.select().fromTo(3, 1, 2, 5, 2, 2);
		scene.world().setKineticSpeed(tableSelect, 0);

		scene.idle(5);
		scene.world().showSection(util.select().position(4, 1, 3), Direction.DOWN);
		scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
		scene.idle(10);
		scene.world().showSection(util.select().position(table), Direction.DOWN);
		scene.idle(10);
		Vec3 tableTop = util.vector().topOf(table);
		scene.overlay().showText(40)
				.attachKeyFrame()
				.text("Vibrating Table can process a variety of items")
				.pointAt(tableTop)
				.placeNearTarget();
		scene.idle(50);

		scene.overlay().showText(40)
				.attachKeyFrame()
				.text("Also Vibrating Table can unpack storage blocks")
				.pointAt(tableTop)
				.placeNearTarget();
		scene.idle(50);

		scene.overlay().showText(40)
				.attachKeyFrame()
				.text("Or vibrate leaves to get matched drops, the chances are the same as using hoe with Fortune III")
				.pointAt(tableTop)
				.placeNearTarget();
		scene.idle(50);

		scene.world().showSection(cogs, Direction.DOWN);
		scene.idle(10);
		scene.world().setKineticSpeed(tableSelect, -100);
		scene.effects().indicateSuccess(table);
		scene.idle(10);

		scene.overlay().showText(40)
				.attachKeyFrame()
				.colored(PonderPalette.GREEN)
				.text("They can be powered from the side using shafts")
				.pointAt(util.vector().topOf(table.east()))
				.placeNearTarget();
		scene.idle(50);

		ItemStack itemStack = new ItemStack(Items.IRON_BLOCK);
		Vec3 entitySpawn = util.vector().topOf(table.above(3));

		ElementLink<EntityElement> entity1 =
				scene.world().createItemEntity(entitySpawn, util.vector().of(0, 0.2, 0), itemStack);
		scene.idle(18);
		scene.world().modifyEntity(entity1, Entity::discard);
		scene.world().modifyBlockEntity(table, VibratingTableBlockEntity.class,
				ms -> ms.inputInv.setStackInSlot(0, itemStack));
		scene.idle(10);
		scene.overlay().showControls(tableTop, Pointing.DOWN, 30).withItem(itemStack);
		scene.idle(7);

		scene.overlay().showText(40)
				.attachKeyFrame()
				.text("Throw or Insert items at the top")
				.pointAt(tableTop)
				.placeNearTarget();
		scene.idle(60);

		ItemStack ingot = new ItemStack(Items.IRON_INGOT);

		scene.world().modifyBlockEntity(table, VibratingTableBlockEntity.class,
				ms -> ms.inputInv.setStackInSlot(0, ItemStack.EMPTY));
		scene.world().modifyBlockEntity(table, VibratingTableBlockEntity.class,
				ms -> ms.outputInv.setStackInSlot(0, ingot));

		scene.overlay().showText(50)
				.text("After some time, the result can be obtained via Right-click")
				.pointAt(util.vector().blockSurface(table, Direction.WEST))
				.placeNearTarget();
		scene.idle(60);

		scene.overlay().showControls(
				util.vector().blockSurface(table, Direction.NORTH), Pointing.RIGHT, 40)
				.rightClick()
				.withItem(ingot);
		scene.idle(50);

		scene.addKeyframe();
		scene.world().showSection(beltCog, Direction.UP);
		scene.world().showSection(belt, Direction.EAST);
		scene.idle(15);

		scene.world().modifyBlockEntity(table, VibratingTableBlockEntity.class,
				ms -> ms.outputInv.setStackInSlot(0, ItemStack.EMPTY));

		BlockPos beltPos = util.grid().at(1, 1, 2);
		scene.world().createItemOnBelt(beltPos, Direction.EAST, ingot);
		scene.idle(15);

		scene.overlay().showText(50)
				.text("The outputs can also be extracted by automation")
				.pointAt(util.vector().blockSurface(table, Direction.WEST)
						.add(-.5, .4, 0))
				.placeNearTarget();
		scene.idle(60);

		scene.markAsFinished();
		scene.idle(25);
		scene.world().modifyEntities(ItemEntity.class, Entity::discard);
	}
}
