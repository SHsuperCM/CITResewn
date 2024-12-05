package io.shcm.fabric.citresewn.pack.cit;

import io.shcm.fabric.citresewn.CITResewn;
import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CITLoader implements SimpleResourceReloadListener<Iterable<CIT>> {
    private static final ResourceFinder FINDER = ResourceFinder.json("cit");

    @Entrypoint(Entrypoint.CLIENT) public static void registerLoader() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new CITLoader());
    }

    @Override
    public CompletableFuture<Iterable<CIT>> load(ResourceManager manager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            List<CIT> loaded = new ArrayList<>();

            for (Map.Entry<Identifier, List<Resource>> entry : FINDER.findAllResources(manager).entrySet())
                for (Resource resource : entry.getValue()) {
                    try {
                        loaded.add(load(entry.getKey(), resource));
                    } catch (Exception e) {
                        CITResewn.LOG.error("Errored loading CIT from " + resource.getPack().getId() + "/" + entry.getKey().toString(), e);
                    }
                }

            return loaded;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(Iterable<CIT> data, ResourceManager manager, Executor executor) {
        return CompletableFuture.runAsync(() -> {

        }, executor);
    }

    public CIT load(Identifier id, Resource resource) throws Exception {

        throw new IllegalStateException();
    }

    @Override
    public Identifier getFabricId() {
        return CITResewn.id("cit");
    }
}
