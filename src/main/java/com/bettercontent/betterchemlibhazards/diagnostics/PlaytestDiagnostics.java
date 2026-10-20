package com.bettercontent.betterchemlibhazards.diagnostics;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/** Native lifecycle adapter only; all rate/focus/encoding policy is the reviewed PlaytestLog. */
@Mod.EventBusSubscriber(modid = "better_chemlib_hazards")
public final class PlaytestDiagnostics {
    public static final PlaytestLog LOG = new PlaytestLog("better_chemlib_hazards");
    private PlaytestDiagnostics() {}
    @SubscribeEvent public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        com.bettercontent.betterchemlibhazards.sim.LatentRadiationService.summarize(false);
        if (LOG.enabled("summary")) LOG.summary("server");
    }
    @SubscribeEvent public static void stopped(ServerStoppedEvent event) {
        com.bettercontent.betterchemlibhazards.sim.LatentRadiationService.summarize(true);
        LOG.reset("server");
    }
    @Mod.EventBusSubscriber(modid = "better_chemlib_hazards", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Startup {
        private Startup() {}
        @SubscribeEvent public static void setup(FMLCommonSetupEvent event) {
            var mods = ModList.get();
            String version = mods.getModContainerById("better_chemlib_hazards").map(c -> c.getModInfo().getVersion().toString()).orElse("UNKNOWN");
            var own = mods.getModFileById("better_chemlib_hazards");
            String source = own == null ? "UNKNOWN" : LOG.sourceIdentity(own.getFile().findResource("META-INF", "better-content-source.properties"));
            LOG.status("common", version, source, () -> PlaytestLog.fields("enabled", true, "chemlib_loaded", mods.isLoaded("chemlib"), "adpother_loaded", mods.isLoaded("adpother"), "better_industrial_heat_loaded", mods.isLoaded("better_industrial_heat")));
        }
    }
}
