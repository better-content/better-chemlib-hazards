package com.bettercontent.betterchemlibhazards.diagnostics;

/** Pure projection of values already observed by the owning native decision. */
public final class FeatureObservation {
    private FeatureObservation() {}
    public static String fields(int units, int targets) { return PlaytestLog.fields("reported_accepted_mass", units * 16.0, "native_units", units, "target_count", targets, "source_debit", "caller_owned"); }
    
}
