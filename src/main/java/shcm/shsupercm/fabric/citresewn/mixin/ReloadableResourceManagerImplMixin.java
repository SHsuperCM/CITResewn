package shcm.shsupercm.fabric.citresewn.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

        Map<String, ResourcePack> citFiles = new HashMap<>();
        for (ResourcePack pack : packs) {
            for (Identifier identifier : pack.findResources(ResourceType.CLIENT_RESOURCES, "minecraft", "citresewn/cit", 5, s -> true))
                citFiles.put(identifier.getPath(), pack);
            for (Identifier identifier : pack.findResources(ResourceType.CLIENT_RESOURCES, "minecraft", "mcpatcher/cit", 5, s -> true))
                citFiles.put(identifier.getPath(), pack);
            for (Identifier identifier : pack.findResources(ResourceType.CLIENT_RESOURCES, "minecraft", "optifine/cit", 5, s -> true))
                citFiles.put(identifier.getPath(), pack);
        }

        for (Map.Entry<String, ResourcePack> citFile : citFiles.entrySet()) {

        }

        new String();
    }
}