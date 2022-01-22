package shcm.shsupercm.fabric.citresewn.pack;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import shcm.shsupercm.fabric.citresewn.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.format.PropertyValue;
import shcm.shsupercm.fabric.citresewn.registry.api.GlobalPropertiesHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class GlobalProperties extends PropertyGroup {
    public GlobalProperties() {
        super("global_properties", new Identifier("citresewn", "global_properties"));
    }

    @Override
    public String getExtension() {
        return "";
    }

    @Override
    public PropertyGroup load(String packName, Identifier identifier, InputStream is) throws IOException, InvalidIdentifierException {
        PropertyGroup group = PropertyGroup.tryParseGroup(packName, identifier, is);
        if (group != null)
            for (Map.Entry<PropertyKey, Set<PropertyValue>> entry : group.properties.entrySet())
                this.properties.computeIfAbsent(entry.getKey(), key -> new LinkedHashSet<>()).addAll(entry.getValue());

        return this;
    }

    public void callHandlers() {
        for (EntrypointContainer<GlobalPropertiesHandler> container : FabricLoader.getInstance().getEntrypointContainers("citresewn:cit_global_properties", GlobalPropertiesHandler.class)) {
            String containerNamespace = container.getProvider().getMetadata().getId();
            if (containerNamespace.equals("citresewn-defaults"))
                containerNamespace = "citresewn";

            for (Map.Entry<PropertyKey, Set<PropertyValue>> entry : properties.entrySet())
                if (entry.getKey().namespace().equals(containerNamespace)) {
                    PropertyValue value = null;
                    for (PropertyValue v : entry.getValue())
                        value = v;

                    container.getEntrypoint().globalProperty(entry.getKey().path(), value);
                }
        }
    }
}
