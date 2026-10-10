package com.bettercontent.betterchemlibhazards.sim;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;
import java.util.function.Predicate;
import static org.junit.jupiter.api.Assertions.*;

final class NuclearInventoryNeighborhoodTest {
    @Test
    void comparatorAndEnvironmentReadsRequireImmediatelyAvailableNeighborChunks() {
        Predicate<BlockPos> originOnly = pos -> Math.floorDiv(pos.getX(), 16) == 0 && Math.floorDiv(pos.getZ(), 16) == 0;
        assertTrue(InventoryScanPolicy.neighborhoodLoaded(new BlockPos(8, 64, 8), originOnly));
        assertFalse(InventoryScanPolicy.neighborhoodLoaded(new BlockPos(14, 64, 8), originOnly),
            "a comparator read may reach two blocks past the holder");
        assertFalse(InventoryScanPolicy.neighborhoodLoaded(new BlockPos(0, 64, 8), originOnly));
        assertFalse(InventoryScanPolicy.neighborhoodLoaded(new BlockPos(8, 64, 8), pos -> false));
        assertTrue(InventoryScanPolicy.neighborhoodLoaded(new BlockPos(-8, 64, -8),
            pos -> Math.floorDiv(pos.getX(), 16) == -1 && Math.floorDiv(pos.getZ(), 16) == -1));
        assertTrue(InventoryScanPolicy.neighborhoodLoaded(new BlockPos(15, 64, 15), pos -> true));
    }
}
