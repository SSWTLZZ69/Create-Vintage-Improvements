package com.negodya1.vintageimprovements.foundation.advancement;

import java.util.UUID;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.FakePlayer;

public class VintageAdvancementBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<VintageAdvancementBehaviour> TYPE = new BehaviourType<>();

    private UUID playerUUID;

    public VintageAdvancementBehaviour(SmartBlockEntity be) {
        super(be);
    };

    public Player getPlayer() {
        if (playerUUID == null) return null;
        return getWorld().getPlayerByUUID(playerUUID);
    };

    public void setPlayer(UUID uuid) {
		Player player = getWorld().getPlayerByUUID(uuid);
		if (player == null)
			return;
		playerUUID = uuid;
		blockEntity.setChanged();
	};

    public static void setPlacedBy(Level level, BlockPos pos, LivingEntity placer) {
        VintageAdvancementBehaviour behaviour = BlockEntityBehaviour.get(level, pos, TYPE);
		if (behaviour == null || placer instanceof FakePlayer) return;
		if (placer instanceof ServerPlayer) {
            behaviour.setPlayer(placer.getUUID());
        };
	};

    public void awardVintageAdvancement(VintageAdvancements advancement) {
		Player player = getPlayer();
		if (player == null) return;
        advancement.award(getWorld(), getPlayer());
    };

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        if (nbt.contains("Owner")) playerUUID = nbt.getUUID("Owner");
    };

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        if (playerUUID != null) nbt.putUUID("Owner", playerUUID);
    };

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    };
    
};

