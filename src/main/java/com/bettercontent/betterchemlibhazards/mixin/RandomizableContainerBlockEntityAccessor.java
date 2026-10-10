package com.bettercontent.betterchemlibhazards.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Inspect pending native loot without touching the inventory or rolling it. */
@Mixin(RandomizableContainerBlockEntity.class)
public interface RandomizableContainerBlockEntityAccessor {
    @Accessor("lootTable")
    ResourceLocation betterChemlibHazards$getPendingLootTable();
}
