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
import shcm.shsupercm.fabric.citresewn.config.BrokenPaths;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static shcm.shsupercm.fabric.citresewn.config.BrokenPaths.processingBrokenPaths;

/**
 * Starts/Stops broken paths logic.
 * @see BrokenPaths
 * @see IdentifierMixin
 */
@Mixin(ReloadableResourceManagerImpl.class)
public class ReloadableResourceManagerImplMixin {
    @Shadow @Final private ResourceType type;

    @Inject(method = "reload", at = @At("RETURN"))
    public void citresewn$brokenpaths$onReload(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs, CallbackInfoReturnable<ResourceReload> cir) {
        if (processingBrokenPaths = this.type == ResourceType.CLIENT_RESOURCES) {
            CITResewn.LOG.error("[citresewn] Caution! Broken paths is enabled!");
            cir.getReturnValue().whenComplete().thenRun(() -> processingBrokenPaths = false);
        }
    }
}
