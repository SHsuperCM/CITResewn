package shcm.shsupercm.fabric.citresewn.cit.resource;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CITReloadListener implements SimpleResourceReloadListener<CITResources> {
    private CompletableFuture<CITResources> passingPrepareTask = null;

    @Entrypoint(Entrypoint.CLIENT) public static void register() {
        final CITReloadListener citReloadListener = new CITReloadListener();
        final CITModelLoadingPlugin modelPlugin = new CITModelLoadingPlugin();

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(citReloadListener);
        PreparableModelLoadingPlugin.register((manager, executor) ->
                citReloadListener.getPrepare(manager, executor)
                        .thenApply(CITResources::models), modelPlugin);
    }

    @Override
    public CompletableFuture<CITResources> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return getPrepare(manager, executor);
    }

    public CompletableFuture<CITResources> getPrepare(ResourceManager manager, Executor executor) {
        CompletableFuture<CITResources> prepareTask;
        if (this.passingPrepareTask == null) {
            prepareTask = CompletableFuture.supplyAsync(() -> prepare(manager), executor);
            passingPrepareTask = prepareTask;
        } else {
            prepareTask = passingPrepareTask;
            passingPrepareTask = null;
        }
        return prepareTask;
    }

    @Override
    public CompletableFuture<Void> apply(CITResources data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> apply(data), executor);
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of("citresewn", "cit");
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return List.of(ResourceReloadListenerKeys.MODELS);
    }

    public CITResources prepare(ResourceManager manager) {
        Map<CITIdentifier, Resource> citResources = new HashMap<>();

        for (String root : List.of("mcpatcher", "optifine", "citresewn")) {
            for (Map.Entry<Identifier, List<Resource>> entry : new ResourceFinder(root + "/cit", ".properties").findAllResources(manager).entrySet())
                for (Resource resource : entry.getValue()) {
                    CITIdentifier citId = new CITIdentifier(entry.getKey(), root, resource);
                    citResources.put(citId, resource);
                }
            // todo global properties
        }

        // todo process resources
        CITResources resources = new CITResources(new CITResources.CITData(), new CITResources.Models());

        return resources;
    }

    public void apply(CITResources data) {
        // todo apply cits to game
    }
}
