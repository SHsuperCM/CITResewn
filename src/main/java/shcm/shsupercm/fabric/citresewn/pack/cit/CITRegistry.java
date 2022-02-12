package shcm.shsupercm.fabric.citresewn.pack.cit;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.ex.UnknownCITTypeException;
import shcm.shsupercm.fabric.citresewn.pack.GlobalProperties;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.util.*;

import static shcm.shsupercm.fabric.citresewn.CITResewn.info;
import static shcm.shsupercm.fabric.citresewn.CITResewn.logWarnLoading;

public class CITRegistry {
    private static final Map<Identifier, CITTypeContainer<? extends CITType>> TYPES = new HashMap<>();
    private static final Map<PropertyKey, CITConditionContainer<? extends CITCondition>> CONDITIONS = new HashMap<>();

    public static void registerAll() {
        info("Registering CIT Conditions");
        for (var entrypointContainer : FabricLoader.getInstance().getEntrypointContainers(CITConditionContainer.ENTRYPOINT, CITConditionContainer.class)) {
            String namespace = entrypointContainer.getProvider().getMetadata().getId();
            if (namespace.equals("citresewn-defaults"))
                namespace = "citresewn";

            for (String alias : entrypointContainer.getEntrypoint().aliases)
                CONDITIONS.put(new PropertyKey(namespace, alias), (CITConditionContainer<? extends CITCondition>) entrypointContainer.getEntrypoint());
        }

        info("Registering CIT Types");
        for (var entrypointContainer : FabricLoader.getInstance().getEntrypointContainers(CITTypeContainer.ENTRYPOINT, CITTypeContainer.class)) {
            String namespace = entrypointContainer.getProvider().getMetadata().getId();
            if (namespace.equals("citresewn-defaults"))
                namespace = "citresewn";

            TYPES.put(new Identifier(namespace, entrypointContainer.getEntrypoint().id), (CITTypeContainer<? extends CITType>) entrypointContainer.getEntrypoint());
        }
    }

    public static CIT load(PropertyGroup properties, GlobalProperties globalProperties) throws CITParsingException {
        CITType citType = parseType(properties);

        ArrayList<CITCondition> conditions = new ArrayList<>();

        for (Map.Entry<PropertyKey, Set<PropertyValue>> entry : properties.properties.entrySet()) {
            if (entry.getKey().path().equals("type") && entry.getKey().namespace().equals("citresewn"))
                continue;

            for (PropertyValue value : entry.getValue())
                conditions.add(parseCondition(properties, entry.getKey(), value));
        }

        for (CITCondition condition : new ArrayList<>(conditions))
            for (Class<? extends CITCondition> siblingConditionType : condition.siblingConditions())
                conditions.replaceAll(
                        siblingCondition -> siblingConditionType == siblingCondition.getClass() ?
                            condition.modifySibling(siblingConditionType, siblingCondition) :
                            siblingCondition);
        
        conditions.removeIf(Objects::isNull);

        citType.load(conditions, properties, globalProperties);

        return new CIT(properties.identifier, properties.packName, citType, conditions.toArray(new CITCondition[0]));
    }

    public static CITCondition parseCondition(PropertyGroup properties, PropertyKey key, PropertyValue value) throws CITParsingException {
        CITConditionContainer<? extends CITCondition> conditionContainer = CONDITIONS.get(key);
        if (conditionContainer == null) {
            logWarnLoading("Skipping condition: " + CITParsingException.descriptionOf("Unknown condition type", properties, value.position()));
            return null;
        }

        CITCondition condition = conditionContainer.createCondition.get();
        condition.load(value);
        return condition;
    }

    public static CITType parseType(PropertyGroup properties) throws CITParsingException {
        Identifier type = new Identifier("citresewn", "item");

        PropertyValue propertiesType = properties.getLast("citresewn", "type");
        if (propertiesType != null) {
            String value = propertiesType.value();
            if (!value.contains(":"))
                value = "citresewn:" + value;
            type = new Identifier(value);
        }

        CITTypeContainer<? extends CITType> typeContainer = TYPES.get(type);
        if (typeContainer == null)
            // assert (propertiesType != null) because the default citresewn:item should always be registered
            throw new UnknownCITTypeException(properties, propertiesType == null ? -1 : propertiesType.position());

        return typeContainer.createType.get();
    }
}
