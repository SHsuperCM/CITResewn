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
	 * Handlers should take care to reset back any changes global properties make by listening to CIT disposal.
	 * @see CITDisposable#dispose()
	 * @param key name of the property key stripped of its modid
	 * @param value the value it's been set to
     */
    void globalProperty(String key, PropertyValue value) throws Exception;
}
