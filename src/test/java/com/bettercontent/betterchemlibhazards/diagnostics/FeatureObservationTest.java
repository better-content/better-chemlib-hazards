package com.bettercontent.betterchemlibhazards.diagnostics;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public final class FeatureObservationTest {
    @Test public void nativeScalarsHaveExplicitStage() { var fields = FeatureObservation.fields(3, 2); assertTrue(fields.contains("reported_accepted_mass=48.0")); assertTrue(fields.contains("native_units=3")); assertTrue(fields.contains("target_count=2")); }
    @Test public void uncertaintyAndPrivacyStayExplicit() { var fields = FeatureObservation.fields(0, 0); assertTrue(fields.contains("reported_accepted_mass=0.0")); assertTrue(fields.contains("source_debit=caller_owned")); assertFalse(fields.contains("source_consumed=true")); }
}
