package shcm.shsupercm.fabric.citresewn.api;

import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

/**
 * @see #globalProperty(String, PropertyValue)
 */
@FunctionalInterface
public interface CITGlobalProperties {
    /**
     * Entrypoint for handlers of global properties.
	 * @see #globalProperty(String, PropertyValue)
     */
    String ENTRYPOINT = "citresewn:global_property";

    /**
     * Invoked before CIT parsing for any global property name associated with the handler's modid.<br>
	 * May be called multiple times for a key to overwrite its global property with higher-priority resourcepacks.<br>
	 * When unloading resourcepacks(usually before reloading), all keys that were invoked in the previous load will get called again with a null value to allow for disposal.
	 * @param key name of the property key stripped of its modid
	 * @param value the value it's been set to or null if resetting
     */
    void globalProperty(String key, PropertyValue value) throws Exception;
}
