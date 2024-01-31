package shcm.shsupercm.fabric.citresewn.cit;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.ConstantCondition;
import shcm.shsupercm.fabric.citresewn.pack.PackParser;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.util.*;

import static shcm.shsupercm.fabric.citresewn.CITResewn.info;
import static shcm.shsupercm.fabric.citresewn.CITResewn.logWarnLoading;

/**
 * Holds a static registry runtime for all types and conditions.
 * @see PackParser
 * @see CITTypeContainer
 * @see CITConditionContainer
 */
public final class CITRegistry { private CITRegistry(){}
    /**
     * Currently registered CIT types.
     */
    public static final Map<Identifier, CITTypeContainer<? extends CITType>> TYPES = new HashMap<>();
    /**
     * Currently registered condition types.
     */
    public static final Map<PropertyKey, CITConditionContainer<? extends CITCondition>> CONDITIONS = new HashMap<>();

    /**
     * Fast id lookup map for types.
     * @see #idOfType(Class)
     */
    private static final Map<Class<? extends CITType>, Identifier> TYPE_TO_ID = new IdentityHashMap<>();
    /**
     * Fast id lookup map for conditions.
     * @see #idOfCondition(Class)
     */
    private static final Map<Class<? extends CITCondition>, PropertyKey> CONDITION_TO_ID = new IdentityHashMap<>();

    /**
     * Loads all available CIT and condition types to registry. (internal use only)
     * @see CITTypeContainer
     * @see CITConditionContainer
     */
    public static void registerAll() {
        info("Registering CIT Conditions");
        for (var entrypointContainer : FabricLoader.getInstance().getEntrypointContainers(CITConditionContainer.ENTRYPOINT, CITConditionContainer.class)) {
            String namespace = entrypointContainer.getProvider().getMetadata().getId();
            if (namespace.equals("citresewn-defaults"))
                namespace = "citresewn";

            for (String alias : entrypointContainer.getEntrypoint().aliases) {
                final PropertyKey key = new PropertyKey(namespace, alias);
                CITConditionContainer<?> container = entrypointContainer.getEntrypoint();

                CONDITIONS.put(key, container);
                CONDITION_TO_ID.putIfAbsent(container.createCondition.get().getClass(), key);
            }
        }

        info("Registering CIT Types");
        for (var entrypointContainer : FabricLoader.getInstance().getEntrypointContainers(CITTypeContainer.ENTRYPOINT, CITTypeContainer.class)) {
            String namespace = entrypointContainer.getProvider().getMetadata().getId();
            if (namespace.equals("citresewn-defaults"))
                namespace = "citresewn";

            final Identifier id = new Identifier(namespace, entrypointContainer.getEntrypoint().id);
            CITTypeContainer<?> container = entrypointContainer.getEntrypoint();

            TYPES.put(id, container);
            TYPE_TO_ID.putIfAbsent(container.createType.get().getClass(), id);
        }
    }

    /**
     * Parses a condition from the given property.<br>
     *
     * @param key the condition's key in the group
     * @param value the condition's value
     * @param properties the containing property group
     * @return the parsed condition or an always-failing {@link ConstantCondition} if unrecognized
     * @throws CITParsingException if errored while parsing hte condition
     */
    public static CITCondition parseCondition(PropertyKey key, PropertyValue value, PropertyGroup properties) throws CITParsingException {
        CITConditionContainer<? extends CITCondition> conditionContainer = CONDITIONS.get(key);
        if (conditionContainer == null) {
            logWarnLoading(properties.messageWithDescriptorOf("Unknown condition type \"" + key.toString() + "\"", value.position()));
            return ConstantCondition.TRUE;
        }

        CITCondition condition = conditionContainer.createCondition.get();
        condition.load(value, properties);
        return condition;
    }

    /**
     * Parses a CIT type from the given property group.<br>
     * If the group does not contain a "citresewn:type" property, defaults to "citresewn:item".
     * @param properties group of properties to parse the CIT type from
     * @return a new instance of the group's CIT type
     * @throws UnknownCITTypeException if the given type is unrecognized in the registry
     */
    public static CITType parseType(PropertyGroup properties) throws UnknownCITTypeException {
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

    /**
     * @see #TYPE_TO_ID
     * @return the id of the given CIT type's class.
     */
    public static Identifier idOfType(Class<? extends CITType> clazz) {
        return TYPE_TO_ID.get(clazz);
    }

    /**
     * @see #CONDITION_TO_ID
     * @return the first key of the given condition's class.
     */
    public static PropertyKey idOfCondition(Class<? extends CITCondition> clazz) {
        return CONDITION_TO_ID.get(clazz);
    }
}
