package shcm.shsupercm.fabric.citresewn.cit.resource;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.api.CITDisposable;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.cit.*;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.core.FallbackCondition;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.core.WeightCondition;
import shcm.shsupercm.fabric.citresewn.cit.model.CITModelLoadingPlugin;
import shcm.shsupercm.fabric.citresewn.cit.model.CITModelsAccess;
import shcm.shsupercm.fabric.citresewn.cit.resource.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.cit.resource.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.cit.resource.format.PropertyValue;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
        if (ActiveCITs.isActive()) {
            ActiveCITs.getActive().globalProperties.properties.replaceAll((key, value) -> Set.of());
            ActiveCITs.getActive().globalProperties.callHandlers();
        }

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

        GlobalProperties globalProperties = loadGlobalProperties(manager, new GlobalProperties());
        globalProperties.callHandlers();

        Map<CITIdentifier, CIT<?>> cits = new HashMap<>();

        CITType.LoadContext context = new CITType.LoadContext(manager, new CITModelsAccess());

        for (Map.Entry<CITIdentifier, Resource> entry : citResources.entrySet())
            try {
                PropertyGroup propertyGroup = PropertyGroup.tryParseGroup(entry.getKey().packName(), entry.getKey().path(), entry.getValue().getInputStream());
                CIT<?> cit = parseCIT(entry.getKey(), propertyGroup, manager);

                cit.type.load(Arrays.asList(cit.conditions), propertyGroup, context);

                cits.put(entry.getKey(), cit);
            } catch (CITParsingException e) {
                CITResewn.logErrorLoading(e.getMessage());
            } catch (Exception e) {
                CITResewn.logErrorLoading("Errored while loading cit: " + entry.getKey() + entry.getKey().packName());
                e.printStackTrace();
            }

        info("Loaded " + cits.size() + "/" + citResources.size() + " CITs from loaded resourcepacks");

        return new CITResources(new CITResources.CITData(globalProperties, cits), context.modelsAccess().build());
    }

    public void apply(CITResources data) {
        for (CITDisposable disposable : FabricLoader.getInstance().getEntrypoints(CITDisposable.ENTRYPOINT, CITDisposable.class))
            disposable.dispose();

        for (CITTypeContainer<? extends CITType> typeContainer : CITRegistry.TYPES.values())
            typeContainer.unload();

        for (Map.Entry<Identifier, List<Consumer<BakedModel>>> entry : data.models().bakedModelReceivers().entrySet())
            for (Consumer<BakedModel> receiver : entry.getValue())
                receiver.accept(MinecraftClient.getInstance().getBakedModelManager().getModel(entry.getKey()));

        ActiveCITs.activate(data.citData());
    }

    /**
     * Attempts parsing a CIT from a property group.
     * @param properties property group representation of the CIT
     * @param resourceManager the manager that contains the the property group, used to resolve relative assets
     * @return the successfully parsed CIT
     * @throws CITParsingException if the CIT failed parsing for any reason
     */
    public static CIT<?> parseCIT(CITIdentifier id, PropertyGroup properties, ResourceManager resourceManager) throws CITParsingException {
        CITType citType = CITRegistry.parseType(properties);

        List<CITCondition> conditions = new ArrayList<>();

        Set<PropertyKey> ignoredProperties = citType.typeProperties();

        for (Map.Entry<PropertyKey, Set<PropertyValue>> entry : properties.properties.entrySet()) {
            if (entry.getKey().path().equals("type") && entry.getKey().namespace().equals("citresewn"))
                continue;
            if (ignoredProperties.contains(entry.getKey()))
                continue;

            for (PropertyValue value : entry.getValue())
                conditions.add(CITRegistry.parseCondition(entry.getKey(), value, properties));
        }

        for (CITCondition condition : new ArrayList<>(conditions))
            if (condition != null)
                for (Class<? extends CITCondition> siblingConditionType : condition.siblingConditions())
                    conditions.replaceAll(
                            siblingCondition -> siblingCondition != null && siblingConditionType == siblingCondition.getClass() ?
                                    condition.modifySibling(siblingCondition) :
                                    siblingCondition);

        WeightCondition weight = new WeightCondition();
        FallbackCondition fallback = new FallbackCondition();

        conditions.removeIf(condition -> {
            if (condition instanceof WeightCondition weightCondition) {
                weight.setWeight(weightCondition.getWeight());
                return true;
            } else if (condition instanceof FallbackCondition fallbackCondition) {
                fallback.setFallback(fallbackCondition.getFallback());
                return true;
            }

            return condition == null;
        });

        return new CIT<>(id, citType, conditions.toArray(CITCondition[]::new), weight.getWeight(), fallback.getFallback());
    }

    /**
     * Loads a merged global property group from loaded packs making sure to respect order.
     *
     * @see GlobalProperties#callHandlers()
     * @param resourceManager the manager that contains the packs
     * @param globalProperties global property group to parse into
     * @return globalProperties
     */
    public static GlobalProperties loadGlobalProperties(ResourceManager resourceManager, GlobalProperties globalProperties) {
        for (ResourcePack pack : resourceManager.streamResourcePacks().collect(Collectors.toList()))
            for (String namespace : pack.getNamespaces(ResourceType.CLIENT_RESOURCES))
                for (String root : ROOTS) {
                    Identifier identifier = Identifier.of(namespace, root + "/cit.properties");
                    try {
                        InputSupplier<InputStream> citPropertiesSupplier = pack.open(ResourceType.CLIENT_RESOURCES, identifier);
                        if (citPropertiesSupplier != null)
                            globalProperties.load(pack./*? <1.21 {*//*getName*//*?} else {*/getId/*?}*/(), identifier, citPropertiesSupplier.get());
                    } catch (FileNotFoundException ignored) {
                    } catch (Exception e) {
                        CITResewn.logErrorLoading("Errored while loading global properties: " + identifier + " from " + pack./*? <1.21 {*//*getName*//*?} else {*/getId/*?}*/());
                        e.printStackTrace();
                    }
                }
        return globalProperties;
    }
}
