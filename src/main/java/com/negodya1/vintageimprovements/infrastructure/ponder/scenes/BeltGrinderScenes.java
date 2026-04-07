package com.negodya1.vintageimprovements.infrastructure.ponder.scenes;

import com.negodya1.vintageimprovements.content.kinetics.grinder.GrinderBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.element.InputWindowElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class BeltGrinderScenes {

	public static void processing(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("belt_grinder_processing", "Processing Items on the Belt Grinder");
		scene.configureBasePlate(0, 0, 5);
		scene.world().showSection(util.select().layer(0), Direction.UP);

		BlockPos shaftPos = util.grid().at(2, 1, 3);
		scene.world().setBlock(shaftPos, AllBlocks.SHAFT.getDefaultState()
			.setValue(ShaftBlock.AXIS, Axis.Z), false);

		BlockPos grinderPos = util.grid().at(2, 1, 2);
		Selection grinderSelect = util.select().position(grinderPos);
		scene.world().modifyBlockEntityNBT(grinderSelect, GrinderBlockEntity.class, nbt -> nbt.putInt("RecipeIndex", 0));

		scene.idle(5);
		scene.world().showSection(util.select().fromTo(2, 1, 3, 2, 1, 5), Direction.DOWN);
		scene.idle(10);
		scene.effects().rotationDirectionIndicator(shaftPos);
		scene.world().showSection(grinderSelect, Direction.DOWN);
		scene.idle(10);
		scene.overlay().showText(50)
			.attachKeyFrame()
			.text("Belt Grinders can process a variety of items")
			.pointAt(util.vector().blockSurface(grinderPos, Direction.WEST))
			.placeNearTarget();
		scene.idle(45);

		ItemStack quartz = AllItems.ROSE_QUARTZ.asStack();
		ItemStack polished = AllItems.POLISHED_ROSE_QUARTZ.asStack();

		Vec3 itemSpawn = util.vector().centerOf(grinderPos.above().west());
		ElementLink<EntityElement> quartzItem = scene.world().createItemEntity(itemSpawn, util.vector().of(0, 0, 0), quartz);
		scene.idle(12);

		scene.overlay().showControls(itemSpawn, Pointing.DOWN, 20).withItem(quartz);
		scene.idle(10);

		scene.world().modifyEntity(quartzItem, e -> e.setDeltaMovement(util.vector().of(0.05, 0.2, 0)));
		scene.idle(12);

		scene.world().modifyEntity(quartzItem, Entity::discard);
		scene.world().createItemOnBeltLike(grinderPos, Direction.WEST, quartz);
		scene.idle(35);

		quartzItem = scene.world().createItemEntity(util.vector().topOf(grinderPos)
			.add(0.5, -.1, 0), util.vector().of(0.05, 0.18, 0), polished);
		scene.idle(12);
		scene.overlay().showControls(itemSpawn.add(2, 0, 0), Pointing.DOWN,
			20).withItem(polished);
		scene.idle(30);

		scene.overlay().showText(60)
			.attachKeyFrame()
			.text("The processed item always moves against the rotational input to the grinder")
			.pointAt(util.vector().blockSurface(grinderPos, Direction.UP))
			.placeNearTarget();
		scene.idle(70);

		scene.world().modifyKineticSpeed(util.select().everywhere(), f -> -2 * f);
		scene.effects().rotationDirectionIndicator(shaftPos);
		scene.world().modifyEntity(quartzItem, e -> e.setDeltaMovement(util.vector().of(-0.05, 0.2, 0)));
		scene.idle(12);

		scene.world().modifyEntity(quartzItem, Entity::discard);
		scene.world().createItemOnBeltLike(grinderPos, Direction.EAST, polished);
		scene.idle(25);

		Selection otherBelt = util.select().fromTo(3, 1, 3, 4, 1, 2);
		Selection belt = util.select().fromTo(0, 1, 2, 1, 1, 3);

		scene.world().setKineticSpeed(otherBelt, 0);
		scene.world().setKineticSpeed(belt, 0);
		scene.world().modifyKineticSpeed(util.select().everywhere(), f -> -f);
		scene.world().modifyEntity(quartzItem, Entity::discard);
		scene.world().setBlock(shaftPos, AllBlocks.COGWHEEL.getDefaultState()
			.setValue(ShaftBlock.AXIS, Axis.Z), true);
		scene.idle(3);
		scene.addKeyframe();

		scene.world().multiplyKineticSpeed(util.select().everywhere(), .5f);
		
		ElementLink<WorldSectionElement> beltSection = scene.world().showIndependentSection(belt, Direction.EAST);
		scene.world().moveSection(beltSection, util.vector().of(0, 100, 0), 0);
		scene.idle(1);
		scene.world().removeItemsFromBelt(util.grid().at(1, 1, 2));
		scene.idle(1);
		scene.world().setKineticSpeed(belt, -64);
		scene.idle(1);
		scene.world().moveSection(beltSection, util.vector().of(0, -100, 0), 0);
		scene.idle(3);

		ElementLink<WorldSectionElement> otherBeltSection =
			scene.world().showIndependentSection(otherBelt, Direction.WEST);
		scene.world().moveSection(otherBeltSection, util.vector().of(0, 100, 0), 0);
		scene.idle(1);
		scene.world().removeItemsFromBelt(util.grid().at(3, 1, 2));
		scene.idle(1);
		scene.world().setKineticSpeed(otherBelt, -64);
		scene.idle(1);
		scene.world().moveSection(otherBeltSection, util.vector().of(0, -100, 0), 0);
		scene.idle(1);

		scene.world().showSection(util.select().fromTo(2, 1, 1, 1, 1, 0), Direction.DOWN);
		scene.idle(3);

		//////

		scene.world().modifyKineticSpeed(util.select().everywhere(), f -> 2 * f);
		scene.effects().rotationDirectionIndicator(shaftPos);

		quartzItem = scene.world().createItemEntity(itemSpawn, util.vector().of(0, 0, 0), quartz);
		scene.idle(12);

		BlockPos meter = util.grid().at(1, 1, 0);

		scene.overlay().showText(50)
				.text("Some recipes requires certain RPM")
				.pointAt(util.vector().blockSurface(meter, Direction.UP))
				.placeNearTarget();
		scene.idle(10);

		scene.world().modifyEntity(quartzItem, e -> e.setDeltaMovement(util.vector().of(0.05, 0.2, 0)));
		scene.idle(12);

		scene.world().modifyEntity(quartzItem, Entity::discard);
		scene.world().createItemOnBeltLike(grinderPos, Direction.WEST, quartz);

		scene.idle(28);
		scene.overlay().showText(100)
				.text("There are three speeds: Low (16 RPM or less), Medium (between 16 and 64 RPM) and High")
				.pointAt(util.vector().blockSurface(meter, Direction.UP))
				.placeNearTarget();
		scene.idle(10);

		scene.markAsFinished();
		scene.idle(65);
		scene.world().modifyEntities(ItemEntity.class, Entity::discard);
	}

}
