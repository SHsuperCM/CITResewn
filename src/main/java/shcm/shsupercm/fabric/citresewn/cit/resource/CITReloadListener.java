package shcm.shsupercm.fabric.citresewn.cit.resource;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertiesGroupAdapter;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class CITReloadListener implements SimpleResourceReloadListener<CITResources> {
    private final Consumer<CITResources.Models> modelsConsumer;

    private CITReloadListener(Consumer<CITResources.Models> modelsConsumer) {
        this.modelsConsumer = modelsConsumer;
    }

    @Entrypoint(Entrypoint.CLIENT) public static void register() {
        final CITModelLoadingPlugin modelPlugin = new CITModelLoadingPlugin();

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new CITReloadListener(modelPlugin::apply));
        ModelLoadingPlugin.register(modelPlugin);
    }

    @Override
    public CompletableFuture<CITResources> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            CITResources resources;
            profiler.push("citresewn"); {
                Map<CITIdentifier, Resource> citResources = new HashMap<>();

                profiler.push("collect"); {
                    for (String root : List.of("mcpatcher", "optifine", "citresewn")) {
                        for (Map.Entry<Identifier, List<Resource>> entry : new ResourceFinder(root + "/cit", ".properties").findAllResources(manager).entrySet())
                            for (Resource resource : entry.getValue()) {
                                CITIdentifier citId = new CITIdentifier(entry.getKey(), root, resource);
                                citResources.put(citId, resource);
                            }
                        // todo global properties
                    }
                } profiler.swap("adapt"); {
                    new String();
                } profiler.pop();

                resources = new CITResources(new CITResources.Models());
                modelsConsumer.accept(resources.models());
            } profiler.pop();
            return resources;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(CITResources data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            new String();
        }, executor);
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of("citresewn", "cit");
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return List.of(ResourceReloadListenerKeys.MODELS);
    }
}
