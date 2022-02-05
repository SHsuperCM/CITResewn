package shcm.shsupercm.fabric.citresewn.registry.api;

import shcm.shsupercm.fabric.citresewn.format.PropertyValue;

@FunctionalInterface
public interface GlobalPropertiesHandler {
    String ENTRYPOINT = "citresewn:global_property";

    boolean globalProperty(String key, PropertyValue value);
}
