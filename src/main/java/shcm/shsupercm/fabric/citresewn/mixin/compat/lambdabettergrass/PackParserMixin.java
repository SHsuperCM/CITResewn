package shcm.shsupercm.fabric.citresewn.mixin.compat.lambdabettergrass;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import shcm.shsupercm.fabric.citresewn.pack.PackParser;

import java.io.IOException;

@SuppressWarnings("UnresolvedMixinReference")
@Mixin(PackParser.class)
public class PackParserMixin {
    @ModifyVariable(method = "loadGlobalProperties(Lnet/minecraft/resource/ResourceManager;Lshcm/shsupercm/fabric/citresewn/pack/GlobalProperties;)Lshcm/shsupercm/fabric/citresewn/pack/GlobalProperties;", at =
    @At(value = "INVOKE", target = "shcm/shsupercm/fabric/citresewn/CITResewn.logErrorLoading(Ljava/lang/String;)V"))
    private static IOException citresewn$compat$lambdabettergrass$muteStacktrace(IOException exception) {
        if (exception.getMessage().contains("lambdabettergrass"))
            return new IOException() {
                @Override
                public void printStackTrace() {
                    //no
                }
            };

        return exception;
    }
}
