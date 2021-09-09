package shcm.shsupercm.fabric.citresewn.mixin.broken_paths;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.CITResewn;

@Mixin(Identifier.class) // Only registered if CITResewnConfig#broken_paths is true
public class IdentifierMixin {
    @Inject(method = "isPathValid", cancellable = true, at = @At("HEAD"))
    private static void processBrokenPaths(String path, CallbackInfoReturnable<Boolean> cir) {
        if (CITResewn.INSTANCE != null && CITResewn.INSTANCE.processingBrokenPaths)
            cir.setReturnValue(true);
    }
}
