package shcm.shsupercm.fabric.citresewn.mixin;

import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.ActiveCITs;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.pack.CITParser;
import shcm.shsupercm.fabric.citresewn.pack.cits.CIT;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin implements ReloadableResourceManager {
    @Shadow @Final private ResourceType type;
    @Shadow public abstract Stream<ResourcePack> streamResourcePacks();

    @Shadow public abstract Collection<Identifier> findResources(String startingPath, Predicate<String> pathPredicate);

    @Inject(method = "reload", at = @At("HEAD"))
    private void onReload(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs, CallbackInfoReturnable<ResourceReload> cir) {
        if (this.type != ResourceType.CLIENT_RESOURCES)
            return;

        if (CITResewn.INSTANCE.activeCITs != null) {
            CITResewn.INSTANCE.activeCITs.dispose();
            CITResewn.INSTANCE.activeCITs = null;
        }

        Collection<CIT> cits = CITParser.parse(packs);

        if (cits.size() > 0)
            CITResewn.INSTANCE.activeCITs = new ActiveCITs(cits);
    }
}