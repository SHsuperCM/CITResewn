package shcm.shsupercm.fabric.citresewn.mixin.broken_paths;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.CITResewn;

/* if (CITResewnConfig.read().broken_paths) */ @Mixin(Identifier.class)
public class IdentifierMixin {
    @Inject(method = "isPathValid", cancellable = true, at = @At("HEAD"))
    private static void processBrokenPaths(String path, CallbackInfoReturnable<Boolean> cir) {
        if (CITResewn.INSTANCE != null && CITResewn.INSTANCE.processingBrokenPaths)
            cir.setReturnValue(true);
    }
}
