package com.artmaster.openpac_hordes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.smileycorp.atlas.api.util.VecMath;
import net.smileycorp.hordes.common.event.HordeFindSpawnPosEvent;
import net.smileycorp.hordes.config.HordeEventConfig;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import xaero.pac.common.server.api.OpenPACServerAPI;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ModMain.MODID)
public class ModMain {
    public static final String MODID = "openpac_hordes";

    public ModMain(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.register(this);
    }


    @SubscribeEvent
    public void onHordeFindSpawnPos(HordeFindSpawnPosEvent e) {
        var basepos = e.getPlayer().blockPosition();
        var radius = HordeEventConfig.hordeSpawnDistance.get().doubleValue();
        if (e.getLevel() instanceof ServerLevel serverLevel) {

            var pos = e.getPos();
            var manager = OpenPACServerAPI.get(serverLevel.getServer()).getServerClaimsManager();
            var claim = manager.get(serverLevel.dimension().location(), pos);

            if (claim != null) {
                while (true) {

                    radius += 16.0D;
                    BlockPos candidate;

                    if (e.checksLight()) {
                        candidate = VecMath.closestLoadedPos(serverLevel, basepos, e.getDir(), radius, 7, 0);
                    } else {
                        candidate = VecMath.closestLoadedPos(serverLevel, basepos, e.getDir(), radius);
                    }

                    var candidateClaim = manager.get(serverLevel.dimension().location(), candidate);
                    if (candidateClaim == null) {
                        pos = candidate;
                        break;
                    }
                }
            }

            e.setPos(pos);
        }
    }





}
