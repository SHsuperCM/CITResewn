package shcm.shsupercm.fabric.citresewn.mixin.broken_paths;

import net.minecraft.resource.ResourcePackCompatibility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.config.BrokenPaths;

/**
 * Adds a resourcepack compatibility error message when broken paths are enabled and are detected in a pack.
 * @see BrokenPaths
 * @see AbstractFileResourcePackMixin
 */
@Mixin(ResourcePackCompatibility.class)
public abstract class ResourcePackCompatibilityMixin {
    private static final ResourcePackCompatibility BROKEN_PATHS = ResourcePackCompatibility("BROKEN_PATHS", -1, "broken_paths");

    @SuppressWarnings("InvokerTarget")
    @Invoker("<init>")
    public static ResourcePackCompatibility ResourcePackCompatibility(String internalName, int internalId, String translationSuffix) {
        throw new AssertionError();
    }

    @Inject(method = "from", cancellable = true, at = @At("HEAD"))
    private static void citresewn$brokenpaths$redirectBrokenPathsCompatibility
            /*? <=1.20.1 {*/
                /*(int current, net.minecraft.resource.ResourceType type, CallbackInfoReturnable<ResourcePackCompatibility> cir)
            *//*?} else {*/
                (net.minecraft.util.dynamic.Range<Integer> range, int current, CallbackInfoReturnable<ResourcePackCompatibility> cir)
            /*?}*/ {
        if (current == Integer.MAX_VALUE - 53)
            cir.setReturnValue(BROKEN_PATHS);
    }
}
