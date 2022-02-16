package shcm.shsupercm.fabric.citresewn.cit;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.cit.builtin.AlwaysFailCondition;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.ex.UnknownCITTypeException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.util.*;

import static shcm.shsupercm.fabric.citresewn.CITResewn.info;
import static shcm.shsupercm.fabric.citresewn.CITResewn.logWarnLoading;

public class CITRegistry {
    public static final Map<Identifier, CITTypeContainer<? extends CITType>> TYPES = new HashMap<>();
    public static final Map<PropertyKey, CITConditionContainer<? extends CITCondition>> CONDITIONS = new HashMap<>();

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

    public static CITCondition parseCondition(PropertyKey key, PropertyValue value, PropertyGroup properties) throws CITParsingException {
        CITConditionContainer<? extends CITCondition> conditionContainer = CONDITIONS.get(key);
        if (conditionContainer == null) {
            logWarnLoading(CITParsingException.descriptionOf("Unknown condition type", properties, value.position()));
            return new AlwaysFailCondition();
        }

        CITCondition condition = conditionContainer.createCondition.get();
        condition.load(value, properties);
        return condition;
    }

    public static CITType parseType(PropertyGroup properties) throws CITParsingException {
        Identifier type = new Identifier("citresewn", "item");

        PropertyValue propertiesType = properties.getLastWithoutMetadata("citresewn", "type");
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
