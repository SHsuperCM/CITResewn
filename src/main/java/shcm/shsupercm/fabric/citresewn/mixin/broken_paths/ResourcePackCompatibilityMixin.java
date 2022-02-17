package shcm.shsupercm.fabric.citresewn.mixin.broken_paths;

import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourceType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResourcePackCompatibility.class)
public abstract class ResourcePackCompatibilityMixin {
    private static final ResourcePackCompatibility BROKEN_PATHS = ResourcePackCompatibility("BROKEN_PATHS", -1, "broken_paths");

    @SuppressWarnings("InvokerTarget")
    @Invoker("<init>")
    public static ResourcePackCompatibility ResourcePackCompatibility(String internalName, int internalId, String translationSuffix) {
        throw new AssertionError();
    }

    @Inject(method = "from(ILnet/minecraft/resource/ResourceType;)Lnet/minecraft/resource/ResourcePackCompatibility;", cancellable = true, at = @At("HEAD"))
    private static void redirectBrokenPathsCompatibility(int packVersion, ResourceType type, CallbackInfoReturnable<ResourcePackCompatibility> cir) {
        if (packVersion == Integer.MAX_VALUE - 53)
            cir.setReturnValue(BROKEN_PATHS);
    }
}
