package com.bettercontent.betterchemlibhazards.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NuclearFormRuleTest {
    @Test
    void fixedProfilesNormalizeTagsAndKeepRadiationSeparateFromHeat() {
        NuclearFormRule rule = new NuclearFormRule("", 0.25, "", "#better_ore_geology:test", "", "",
            "uranium", 8.0, 2.0, true, true);
        assertTrue(rule.fixedProfile());
        assertEquals("better_ore_geology:test", rule.itemTag());
        assertEquals(8.0, rule.radiationStrength());
        assertEquals(2.0, rule.heatStrength());
        assertTrue(rule.naturalWorldgenInert());
        assertTrue(rule.placedAlwaysActive());
    }

    @Test
    void exactSelectorsOutrankTagsAndLegacyRulesRemainValid() {
        NuclearFormRule exact = new NuclearFormRule("", 1.0, "test:item", "test:tag", "", "",
            "thorium", 1.0, 1.0, false, true);
        assertEquals(3, exact.specificity());
        assertFalse(new NuclearFormRule("_dust", 1.0).fixedProfile());
    }
}
