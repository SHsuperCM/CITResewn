package shcm.shsupercm.fabric.citresewn.mixin.broken_paths;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.config.BrokenPaths;

import static shcm.shsupercm.fabric.citresewn.config.BrokenPaths.processingBrokenPaths;

/**
 * Applies broken paths logic when active.
 * @see BrokenPaths
 * @see ReloadableResourceManagerImplMixin
 */
@Mixin(Identifier.class)
public class IdentifierMixin {
    @Inject(method = "isPathValid", cancellable = true, at = @At("RETURN"))
    private static void citresewn$brokenpaths$processBrokenPaths(String path, CallbackInfoReturnable<Boolean> cir) {
        if (!processingBrokenPaths)
            return;

        if (!cir.getReturnValue()) {
            if (FabricLoader.getInstance().isDevelopmentEnvironment())
                CITResewn.logWarnLoading("Warning: Encountered broken path: \"" + path + "\"");

            cir.setReturnValue(true);
        }
    }
}
