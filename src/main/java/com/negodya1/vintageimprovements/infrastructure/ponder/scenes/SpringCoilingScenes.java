package com.negodya1.vintageimprovements.infrastructure.ponder.scenes;

import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintageItems;
import com.negodya1.vintageimprovements.content.kinetics.grinder.GrinderBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class SpringCoilingScenes {

	public static void processing(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("spring_coiling_processing", "Processing Items on the Spring Coiling Machine");
		scene.configureBasePlate(0, 0, 5);
		scene.world().showSection(util.select().layer(0), Direction.UP);

		BlockPos coilingPos = util.grid().at(2, 2, 2);
		Selection grinderSelect = util.select().position(coilingPos);
		scene.world().modifyBlockEntityNBT(grinderSelect, GrinderBlockEntity.class, nbt -> nbt.putInt("RecipeIndex", 0));

		scene.idle(5);
		scene.world().showSection(util.select().fromTo(0, 1, 0, 4, 2, 5), Direction.DOWN);
		scene.idle(10);
		scene.overlay().showText(50)
			.attachKeyFrame()
			.text("Spring Coiling Machines can process a variety of items")
			.pointAt(util.vector().blockSurface(coilingPos, Direction.WEST))
			.placeNearTarget();
		scene.idle(45);

		ItemStack steel_rod = new ItemStack(VintageItems.STEEL_ROD.get());
		ItemStack steel_spring = new ItemStack(VintageItems.STEEL_SPRING.get());

		BlockPos itemSpawn = util.grid().at(4, 1, 2);
		scene.world().createItemOnBeltLike(itemSpawn, Direction.UP, steel_rod);
		scene.idle(10);
		scene.world().removeItemsFromBelt(util.grid().at(3, 1, 2));
		scene.world().createItemOnBeltLike(coilingPos, Direction.UP, steel_spring);
		scene.idle(60);

		scene.overlay().showText(60)
				.attachKeyFrame()
				.text("The processed item always moves to the front of the coiling machine")
				.pointAt(util.vector().blockSurface(coilingPos, Direction.UP))
				.placeNearTarget();
		scene.idle(80);

		scene.markAsFinished();
		scene.idle(25);
		scene.world().modifyEntities(ItemEntity.class, Entity::discard);
	}

}
