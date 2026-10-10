package com.bettercontent.betterchemlibhazards.sim;

import com.bettercontent.betterchemlibhazards.mixin.RandomizableContainerBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import java.util.function.Predicate;

/** Shared non-loading, non-loot-rolling boundary for passive inventory scans. */
final class InventoryScanPolicy {
    private InventoryScanPolicy() {}

    static boolean hasPendingLoot(Object holder) {
        if (holder instanceof RandomizableContainerBlockEntity) {
            return ((RandomizableContainerBlockEntityAccessor) holder).betterChemlibHazards$getPendingLootTable() != null;
        }
        return holder instanceof ContainerEntity container && container.getLootTable() != null;
    }

    static boolean neighborhoodLoaded(ServerLevel level, BlockPos pos) {
        return neighborhoodLoaded(pos, neighbor -> level.getChunkSource().getChunkNow(
            SectionPos.blockToSectionCoord(neighbor.getX()), SectionPos.blockToSectionCoord(neighbor.getZ())) != null);
    }

    static boolean neighborhoodLoaded(BlockPos pos, Predicate<BlockPos> fullChunkLoaded) {
        // Native container dirty callbacks may scan two horizontal blocks for
        // comparators. Environment reads need adjacent blocks as well.
        final int radius = 2;
        for (int chunkZ = SectionPos.blockToSectionCoord(pos.getZ() - radius);
             chunkZ <= SectionPos.blockToSectionCoord(pos.getZ() + radius); chunkZ++) {
            for (int chunkX = SectionPos.blockToSectionCoord(pos.getX() - radius);
                 chunkX <= SectionPos.blockToSectionCoord(pos.getX() + radius); chunkX++) {
                if (!fullChunkLoaded.test(new BlockPos(chunkX * 16, pos.getY(), chunkZ * 16))) return false;
            }
        }
        return true;
    }
}
