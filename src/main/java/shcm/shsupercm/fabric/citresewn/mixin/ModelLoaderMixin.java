package shcm.shsupercm.fabric.citresewn.mixin;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shcm.shsupercm.fabric.citresewn.cit.ActiveCITs;

@Mixin(ModelLoader.class)
public class ModelLoaderMixin {
    @Inject(method = "<init>", at =
    @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V"))
    private void citresewn$loadCITs(ResourceManager resourceManager, BlockColors blockColors, Profiler profiler, int i, CallbackInfo ci) {
        profiler.push("citresewn:reloading_cits");
        ActiveCITs.load(resourceManager, profiler);
        profiler.pop();
    }
}
