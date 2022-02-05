package shcm.shsupercm.fabric.citresewn.api;

import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

@FunctionalInterface
public interface GlobalPropertiesHandler {
    String ENTRYPOINT = "citresewn:global_property";

    void globalProperty(String key, PropertyValue value) throws Exception;
}
