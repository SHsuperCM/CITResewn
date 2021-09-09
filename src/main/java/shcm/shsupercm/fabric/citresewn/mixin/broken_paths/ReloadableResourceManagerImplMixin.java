package shcm.shsupercm.fabric.citresewn.mixin.broken_paths;

import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.CITResewn;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManagerImpl.class) // Only registered if CITResewnConfig#broken_paths is true
public class ReloadableResourceManagerImplMixin {
    @Shadow @Final private ResourceType type;

    @Inject(method = "reload", at = @At("RETURN"))
    public void onReload(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs, CallbackInfoReturnable<ResourceReload> cir) {
        if (CITResewn.INSTANCE.processingBrokenPaths = this.type == ResourceType.CLIENT_RESOURCES)
            cir.getReturnValue().whenComplete().thenRun(() -> CITResewn.INSTANCE.processingBrokenPaths = false);
    }
}
