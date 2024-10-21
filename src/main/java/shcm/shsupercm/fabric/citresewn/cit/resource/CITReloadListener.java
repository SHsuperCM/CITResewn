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
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.cit.ActiveCITs;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.CITParsingException;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;
import shcm.shsupercm.fabric.citresewn.pack.GlobalProperties;
import shcm.shsupercm.fabric.citresewn.pack.PackParser;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static shcm.shsupercm.fabric.citresewn.CITResewn.info;

public class CITReloadListener implements SimpleResourceReloadListener<CITResources> {
    /**
     * Possible CIT roots in resourcepacks ordered in increasing order of priority.
     */
    public static final List<String> ROOTS = List.of("mcpatcher", "optifine", "citresewn");

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
        if (!CITResewnConfig.INSTANCE.enabled) {
            info("CIT loading is disabled");
            return CITResources.EMPTY;
        }

        info("Reading CITs from loaded packs..");

        Map<CITIdentifier, Resource> citResources = new HashMap<>();

        for (String root : ROOTS)
            for (Map.Entry<Identifier, List<Resource>> entry : new ResourceFinder(root + "/cit", ".properties").findAllResources(manager).entrySet())
                for (Resource resource : entry.getValue()) {
                    CITIdentifier citId = new CITIdentifier(entry.getKey(), root, resource);
                    citResources.put(citId, resource);
                }

        GlobalProperties globalProperties = PackParser.loadGlobalProperties(manager, new GlobalProperties());
        Map<CITIdentifier, CIT<?>> cits = new HashMap<>();

        for (Map.Entry<CITIdentifier, Resource> entry : citResources.entrySet())
            try {
                CIT<?> cit = PackParser.parseCIT(entry.getKey(), PropertyGroup.tryParseGroup(entry.getKey().packName(), entry.getKey().path(), entry.getValue().getInputStream()), manager);
                cits.put(entry.getKey(), cit);
            } catch (CITParsingException e) {
                CITResewn.logErrorLoading(e.getMessage());
            } catch (Exception e) {
                CITResewn.logErrorLoading("Errored while loading cit: " + entry.getKey() + entry.getKey().packName());
                e.printStackTrace();
            }

        info("Loaded " + cits.size() + "/" + citResources.size() + " CITs from loaded resourcepacks");

        return new CITResources(new CITResources.CITData(globalProperties, cits), new CITResources.CITModels());
    }

    public void apply(CITResources data) {
        // todo retrieve models

        ActiveCITs.load(data.citData());
    }
}
