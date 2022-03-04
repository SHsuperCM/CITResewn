package shcm.shsupercm.fabric.citresewn.pack;

import net.fabricmc.fabric.impl.resource.loader.GroupResourcePack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.WeightCondition;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.mixin.GroupResourcePackAccessor;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITRegistry;
import shcm.shsupercm.fabric.citresewn.cit.CITType;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility parsing methods for packs.
 */
public final class PackParser { private PackParser() {}
    /**
     * Possible CIT roots in resourcepacks ordered in ascending order of priority.
     */
    private static final String[] ROOTS = new String[] { "mcpatcher", "optifine", "citresewn" };

    /**
     * Gets all resourcepacks from {@link GroupResourcePack} or null if not a group or Fabric API is not present.
     */
    private static final Function<ResourcePack, List<ResourcePack>> PARSE_FAPI_GROUPS =
            FabricLoader.getInstance().isModLoaded("fabric-resource-loader-v0") ?
                    parentPack -> parentPack instanceof GroupResourcePack ? ((GroupResourcePackAccessor) parentPack).getPacks() : null
                    : parentPack -> null;

    /**
     * Iterates over each loaded pack taking into account grouped packs.
     * @param resourceManager the manager that contains the packs
     * @param run resourcepack pack consumer
     */
    public static void forEachPack(ResourceManager resourceManager, Consumer<ResourcePack> run) {
        resourceManager.streamResourcePacks().forEachOrdered(pack -> {
            List<ResourcePack> grouped = PARSE_FAPI_GROUPS.apply(pack);
            if (grouped != null)
                for (ResourcePack subPack : grouped)
                    run.accept(subPack);
            else
                run.accept(pack);
        });
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
        forEachPack(resourceManager, pack -> {
            for (String root : ROOTS) {
                Identifier identifier = new Identifier("minecraft", root + "/cit.properties");
                try {
                    globalProperties.load(pack.getName(), identifier, pack.open(ResourceType.CLIENT_RESOURCES, identifier));
                } catch (FileNotFoundException ignored) {
                } catch (IOException e) {
                    CITResewn.logErrorLoading("Errored while loading global properties: " + identifier + " from " + pack.getName());
                    e.printStackTrace();
                }
            }
        });
        return globalProperties;
    }

    /**
     * Attempts parsing all CITs out of all loaded packs.
     * @param resourceManager the manager that contains the packs
     * @return unordered list of successfully parsed CITs
     */
    public static List<CIT<?>> parseCITs(ResourceManager resourceManager) {
        List<CIT<?>> cits = new ArrayList<>();

        for (String root : ROOTS)
            for (Identifier identifier : resourceManager.findResources(root + "/cit", s -> s.endsWith(".properties"))) {
                String packName = null;
                try (Resource resource = resourceManager.getResource(identifier)) {
                    cits.add(parseCIT(PropertyGroup.tryParseGroup(packName = resource.getResourcePackName(), identifier, resource.getInputStream()), resourceManager));
                } catch (CITParsingException e) {
                    CITResewn.logErrorLoading(e.getMessage());
                } catch (Exception e) {
                    CITResewn.logErrorLoading("Errored while loading cit: " + identifier + (packName == null ? "" : " from " + packName));
                    e.printStackTrace();
                }
            }

        return cits;
    }

    /**
     * Attempts parsing a CIT from a property group.
     * @param properties property group representation of the CIT
     * @param resourceManager the manager that contains the the property group, used to resolve relative assets
     * @return the successfully parsed CIT
     * @throws CITParsingException if the CIT failed parsing for any reason
     */
    public static CIT<?> parseCIT(PropertyGroup properties, ResourceManager resourceManager) throws CITParsingException {
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

        conditions.removeIf(condition -> {
            if (condition instanceof WeightCondition weightCondition) {
                weight.setWeight(weightCondition.getWeight());
                return true;
            }

            return condition == null;
        });

        citType.load(conditions, properties, resourceManager);

        return new CIT<>(properties.identifier, properties.packName, citType, conditions.toArray(new CITCondition[0]), weight.getWeight());
    }
}
