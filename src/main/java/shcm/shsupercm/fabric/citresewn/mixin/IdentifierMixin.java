package shcm.shsupercm.fabric.citresewn.mixin;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Identifier.class)
public class IdentifierMixin {

    @Inject(method = "isPathValid(Ljava/lang/String;)Z", cancellable = true, at = @At("HEAD"))
    private static void isPathValid(String path, CallbackInfoReturnable<Boolean> ci) {
        if(path.startsWith("optifine"))
            ci.setReturnValue(true); return;
    }
}
