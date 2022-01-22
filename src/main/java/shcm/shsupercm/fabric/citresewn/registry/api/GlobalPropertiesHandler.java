package shcm.shsupercm.fabric.citresewn.registry.api;

import shcm.shsupercm.fabric.citresewn.format.PropertyValue;

@FunctionalInterface
public interface GlobalPropertiesHandler {
    void globalProperty(String key, PropertyValue value);
}
