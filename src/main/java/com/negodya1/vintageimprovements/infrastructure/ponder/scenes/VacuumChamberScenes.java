package com.negodya1.vintageimprovements.infrastructure.ponder.scenes;

import com.google.common.collect.ImmutableList;
import com.negodya1.vintageimprovements.VintageFluids;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber.VacuumChamberBlockEntity;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.foundation.element.InputWindowElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class VacuumChamberScenes {

	public static void processing(SceneBuilder builder, SceneBuildingUtil util) {

		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("vacuum_chamber", "Processing Items with the Compressor");
		scene.configureBasePlate(0, 0, 5);
		scene.world().setBlock(util.grid().at(1, 1, 2), AllBlocks.ANDESITE_CASING.getDefaultState(), false);
		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(5);
		scene.world().showSection(util.select().fromTo(1, 4, 3, 1, 1, 5), Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(util.select().position(1, 1, 2), Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(util.select().position(1, 2, 2), Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(util.select().position(1, 4, 2), Direction.SOUTH);
		scene.idle(5);
		scene.world().showSection(util.select().fromTo(3, 1, 1, 1, 1, 1), Direction.SOUTH);
		scene.world().showSection(util.select().fromTo(3, 1, 5, 3, 1, 2), Direction.SOUTH);
		scene.idle(20);

		BlockPos basin = util.grid().at(1, 2, 2);
		BlockPos pressPos = util.grid().at(1, 4, 2);
		Vec3 basinSide = util.vector().blockSurface(basin, Direction.WEST);

		ItemStack bucket = new ItemStack(Items.BUCKET);
		ItemStack snow = new ItemStack(Items.SNOW_BLOCK);
		ItemStack result = new ItemStack(Items.POWDER_SNOW_BUCKET);

		scene.overlay().showText(60)
			.pointAt(basinSide)
			.placeNearTarget()
			.attachKeyFrame()
			.text("Compressor have two operating modes, that can be changed via right click with Wrench");
		scene.idle(40);

		ItemStack wrench = new ItemStack(AllItems.WRENCH);
		scene.overlay().showControls(util.vector().topOf(pressPos), Pointing.LEFT, 30).withItem(wrench);
		scene.world().modifyBlockEntity(pressPos, VacuumChamberBlockEntity.class, VacuumChamberBlockEntity::changeMode);
		scene.idle(40);

		scene.overlay().showControls(util.vector().topOf(pressPos), Pointing.LEFT, 30).withItem(wrench);
		scene.world().modifyBlockEntity(pressPos, VacuumChamberBlockEntity.class, VacuumChamberBlockEntity::changeMode);
		scene.idle(60);

		scene.overlay().showControls(util.vector().topOf(basin), Pointing.LEFT, 30).withItem(bucket);
		scene.overlay().showControls(util.vector().topOf(basin), Pointing.RIGHT, 30).withItem(snow);
		scene.idle(30);
		Class<VacuumChamberBlockEntity> type = VacuumChamberBlockEntity.class;
		scene.world().modifyBlockEntity(pressPos, type, VacuumChamberBlockEntity::startProcessingBasin);
		scene.world().createItemOnBeltLike(basin, Direction.UP, bucket);
		scene.world().createItemOnBeltLike(basin, Direction.UP, snow);
		scene.idle(80);
		scene.world().modifyBlockEntityNBT(util.select().position(basin), BasinBlockEntity.class, nbt -> {
			nbt.put("VisualizedItems",
				NBTHelper.writeCompoundList(ImmutableList.of(IntAttached.with(1, result)), ia -> ia.getValue()
					.serializeNBT()));
		});
		scene.idle(4);
		scene.world().createItemOnBelt(util.grid().at(1, 1, 1), Direction.UP, result);
		scene.idle(30);

		scene.rotateCameraY(-30);
		scene.idle(10);
		scene.world().setBlock(util.grid().at(1, 1, 2), AllBlocks.BLAZE_BURNER.getDefaultState()
				.setValue(BlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.KINDLED), true);
		scene.idle(10);

		scene.overlay().showText(80)
				.pointAt(basinSide.subtract(0, 1, 0))
				.placeNearTarget()
				.text("Some of those recipes may require the heat of a Blaze Burner");
		scene.idle(40);

		scene.rotateCameraY(30);
	}

	public static void secondary(SceneBuilder builder, SceneBuildingUtil util) {

		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("vacuum_chamber_secondary", "Secondary Fluids Output/Input");
		scene.configureBasePlate(0, 0, 5);
		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(5);
		scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(util.select().position(2, 2, 2), Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(util.select().position(2, 4, 2), Direction.SOUTH);
		scene.world().setBlock(util.grid().at(3, 3, 2), AllBlocks.SHAFT.getDefaultState(), false);
		scene.idle(5);
		scene.world().showSection(util.select().fromTo(3, 4, 2, 3, 1, 2), Direction.DOWN);
		scene.world().showSection(util.select().fromTo(3, 1, 2, 3, 1, 5), Direction.DOWN);
		scene.idle(20);

		BlockPos basin = util.grid().at(2, 2, 2);
		BlockPos compressor = util.grid().at(2, 4, 2);
		Vec3 basinSide = util.vector().blockSurface(basin, Direction.WEST);

		ItemStack sulfur = new ItemStack(VintageImprovements.SULFUR.get());
		FluidStack dioxide = new FluidStack(VintageFluids.SULFUR_DIOXIDE.get(), 1000);

		scene.overlay().showText(60)
				.pointAt(compressor.getCenter())
				.placeNearTarget()
				.attachKeyFrame()
				.text("Some recipes fluid results appear inside Compressor block");
		scene.idle(40);

		scene.overlay().showControls(util.vector().topOf(basin), Pointing.RIGHT, 30).withItem(sulfur);
		scene.idle(30);
		Class<VacuumChamberBlockEntity> type = VacuumChamberBlockEntity.class;
		scene.world().modifyBlockEntity(compressor, type, VacuumChamberBlockEntity::startProcessingBasin);
		scene.world().createItemOnBeltLike(basin, Direction.UP, sulfur);
		scene.idle(40);

		scene.overlay().showText(60)
				.pointAt(compressor.getCenter())
				.placeNearTarget()
				.attachKeyFrame()
				.text("To drain results you must use Mechanical Pump");
		scene.idle(40);

		scene.world().showSection(util.select().fromTo(0, 1, 2, 0, 2, 2), Direction.DOWN);
		scene.idle(5);
		scene.world().setBlock(util.grid().at(3, 3, 2), AllBlocks.COGWHEEL.getDefaultState(), false);
		scene.world().showSection(util.select().fromTo(1, 3, 3, 3, 4, 3), Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(util.select().fromTo(0, 3, 2, 1, 4, 2), Direction.DOWN);
		scene.idle(5);
		scene.world().modifyBlockEntity(util.grid().at(0, 1, 2), FluidTankBlockEntity.class, be -> be.getTankInventory()
				.fill(dioxide, IFluidHandler.FluidAction.EXECUTE));
		scene.idle(30);

		scene.overlay().showText(35)
				.pointAt(compressor.getCenter())
				.placeNearTarget()
				.attachKeyFrame()
				.text("Some recipes require fluids inside the Compressor");
		scene.idle(40);

		scene.overlay().showText(35)
				.pointAt(compressor.getCenter())
				.placeNearTarget()
				.attachKeyFrame()
				.text("You can fill the Compressor using a Mechanical Pump");
		scene.idle(40);

		scene.markAsFinished();
		scene.idle(25);
	}
}
