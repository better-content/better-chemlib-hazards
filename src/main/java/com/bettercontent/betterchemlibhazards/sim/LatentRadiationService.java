package com.bettercontent.betterchemlibhazards.sim;

import com.bettercontent.betterchemlibhazards.diagnostics.PlaytestDiagnostics;
import com.bettercontent.betterchemlibhazards.diagnostics.PlaytestLog;
import com.bettercontent.betterindustrialheat.api.ThermalApi;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.common.MinecraftForge;

public final class LatentRadiationService {
    private static final java.util.concurrent.atomic.LongAdder EMISSIONS = new java.util.concurrent.atomic.LongAdder();
    private static final java.util.concurrent.atomic.DoubleAdder RADIATION = new java.util.concurrent.atomic.DoubleAdder();
    private static final java.util.concurrent.atomic.DoubleAdder HEAT = new java.util.concurrent.atomic.DoubleAdder();
    private static long summaryAt = System.nanoTime();

    public static void summarize(boolean teardown) {
        long now = System.nanoTime();
        if (!teardown && now - summaryAt < 30_000_000_000L) return;
        summaryAt = now;
        long count = EMISSIONS.sumThenReset();
        double radiation = RADIATION.sumThenReset(), heat = HEAT.sumThenReset();
        if (count > 0 && PlaytestDiagnostics.LOG.enabled("nuclear"))
            PlaytestDiagnostics.LOG.event("nuclear", "nuclear.fixed_emission_summary", "server", "observed", "interval",
                () -> PlaytestLog.fields("emissions", count, "radiation_offered", radiation, "heat_offered", heat));
    }

    private LatentRadiationService() {
    }

    public static void emit(ServerLevel level, BlockPos pos, int radiationLevel) {
        emit(level, pos, radiationLevel, 0.0);
    }

    public static void emit(ServerLevel level, BlockPos pos, double radiationStrength, double heatStrength) {
        // Fixed radiogenic forms are an HU source.  Only genuinely unaccepted heat is
        // lost to the environment; nuclear state emission retains its own remainder.
        if (heatStrength > 0.0) {
            double retained = ThermalApi.distributeHeat(level, pos, heatStrength);
            com.bettercontent.betterchemlibhazards.api.event.ChemicalOutcomeEvent.publish(level, pos, com.bettercontent.betterchemlibhazards.api.event.ChemicalOutcomeEvent.Kind.HEAT_ACCEPTED, null, heatStrength - retained);
        }
        MinecraftForge.EVENT_BUS.post(new RadiationEmissionEvent(level, pos, radiationStrength, heatStrength));
        var log = PlaytestDiagnostics.LOG;
        if (log.enabled("nuclear")) {
            EMISSIONS.increment(); RADIATION.add(radiationStrength); HEAT.add(heatStrength);
        }
        if (log.traceEnabled("nuclear")) {
            log.trace("nuclear", "nuclear.fixed_emission", new PlaytestLog.Context("server", level.dimension().location().toString(),
                level.getGameTime(), "", pos.getX() + "," + pos.getY() + "," + pos.getZ()), "dispatched", "native_event_posted",
                () -> PlaytestLog.fields("radiation", radiationStrength, "heat_offered", heatStrength));
        }
    }

    public static class RadiationEmissionEvent extends Event {
        private final ServerLevel level;
        private final BlockPos pos;
        private final double radiationStrength;
        private final double heatStrength;

        public RadiationEmissionEvent(ServerLevel level, BlockPos pos, int radiationLevel) {
            this(level, pos, radiationLevel, 0.0);
        }

        public RadiationEmissionEvent(ServerLevel level, BlockPos pos, double radiationLevel, double heatStrength) {
            this.level = level;
            this.pos = pos;
            this.radiationStrength = radiationLevel;
            this.heatStrength = heatStrength;
        }

        public ServerLevel level() {
            return level;
        }

        public BlockPos pos() {
            return pos;
        }

        public int radiationLevel() {
            return (int) Math.min(Integer.MAX_VALUE, Math.ceil(radiationStrength));
        }

        public double radiationStrength() { return radiationStrength; }
        public double heatStrength() { return heatStrength; }
    }
}
