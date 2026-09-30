package com.bettercontent.betterchemlibhazards.mixin;

import com.bettercontent.betterchemlibhazards.sim.GasEscapeHandler;
import com.bettercontent.betterchemlibhazards.sim.GasFluidCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * ChemLib gas fluids are transport tokens, not world-placeable liquids.
 */
@Mixin(LiquidBlock.class)
public abstract class LiquidBlockGasificationMixin {
    @Inject(method = "onPlace", at = @At("HEAD"), cancellable = true)
    private void latentChemlib$gasifyOnPlace(
        BlockState state,
        Level level,
        BlockPos pos,
        BlockState previousState,
        boolean movedByPiston,
        CallbackInfo callback
    ) {
        // onPlace can run while a newly generated chunk is still completing its load.
        // Do not query the level for ordinary liquids here: resolving that position can
        // synchronously request the same chunk and recursively re-enter chunk finalizers.
        if (!GasFluidCodec.isGasFluid(state.getFluidState().getType())) return;
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (GasEscapeHandler.gasifyFluidBlock(serverLevel, pos)) callback.cancel();
    }
}
