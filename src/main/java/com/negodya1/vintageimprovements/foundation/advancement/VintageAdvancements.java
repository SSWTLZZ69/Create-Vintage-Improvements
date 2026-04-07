package com.negodya1.vintageimprovements.foundation.advancement;

import com.negodya1.vintageimprovements.VintageImprovements;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public enum VintageAdvancements {

    USE_BELT_GRINDER("use_belt_grinder"),
    USE_COILING_MACHINE("use_coiling_machine"),
    USE_COMPRESSOR("use_compressor"),
    USE_CENTRIFUGE("use_centrifuge"),
    USE_CURVING_PRESS("use_curving_press"),
    USE_HELVE("use_helve"),
    USE_LATHE("use_lathe"),
    USE_LASER("use_laser"),
    USE_VIBRATION_TABLE("use_vibration_table"),
    INSERT_RECIPE_CARD("insert_recipe_card"),
    BELT_GRINDER_SKIN_CHANGE("belt_grinder_skin_change");

    private final String id;
    private final Supplier<SimpleVintageTrigger> trigger;

	VintageAdvancements(String id) {
        this.id = id;
        trigger = Registers.TRIGGERS.register(id, () -> new SimpleVintageTrigger(id));
    };

    public void award(Level level, Player player) {
        if (level.isClientSide()) return;
        if (player instanceof ServerPlayer serverPlayer) {
            trigger.get().trigger(serverPlayer);
        } else {
            VintageImprovements.logThis("Could not award Vintage Improvements Advancement " + id + " to client-side Player.");
        };
    };

    public static void register(IEventBus modEventBus) {
        Registers.TRIGGERS.register(modEventBus);
    }

    private static class Registers {
        private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
                DeferredRegister.create(Registries.TRIGGER_TYPE, VintageImprovements.MODID);
    }
}
