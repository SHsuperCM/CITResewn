package shcm.shsupercm.fabric.citresewn.pack.format;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public abstract class PropertyGroup {
    public final Map<PropertyKey, Set<PropertyValue>> properties = new LinkedHashMap<>();
    public final Identifier identifier;
    public final String packName;

    protected PropertyGroup(String packName, Identifier identifier) {
        this.packName = packName;
        this.identifier = identifier;
    }

    public abstract String getExtension();

    public abstract PropertyGroup load(String packName, Identifier identifier, InputStream is) throws IOException, InvalidIdentifierException;

    protected void put(int position, String packName, Identifier propertiesIdentifier, String key, String keyMetadata, String delimiter, String value) throws InvalidIdentifierException {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        this.properties.computeIfAbsent(PropertyKey.of(key), id -> new LinkedHashSet<>()).add(new PropertyValue(keyMetadata, value, delimiter, position, propertiesIdentifier, packName));
    }

    public Set<PropertyValue> get(String namespace, String... pathAliases) {
        Set<PropertyValue> values = new LinkedHashSet<>();

        for (String path : pathAliases) {
            Set<PropertyValue> possibleValues = this.properties.get(new PropertyKey(namespace, path));
            if (possibleValues != null)
                values.addAll(possibleValues);
        }

        return values;
    }

    public PropertyValue getLast(String namespace, String... pathAliases) {
        PropertyValue value = null;
        for (Iterator<PropertyValue> iterator = get(namespace, pathAliases).iterator(); iterator.hasNext(); value = iterator.next());

        return value;
    }

    public static PropertyGroup tryParseGroup(String packName, Identifier identifier, InputStream is) throws IOException {
        PropertyGroup group = null;
        if (identifier.getPath().endsWith(PropertiesGroupAdapter.EXTENSION))
            group = new PropertiesGroupAdapter(packName, identifier);

        return group == null ? null : group.load(packName, identifier, is);
    }
}
