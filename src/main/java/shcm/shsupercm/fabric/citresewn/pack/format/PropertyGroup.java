package shcm.shsupercm.fabric.citresewn.pack.format;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Storage agnostic map of keys and values.<br>
 * Keys are stored as {@link PropertyKey}s holding the mod id of the property type.<br>
 * A key can have multiple values associated with it as they are stored in an ordered set.
 *
 * @see PropertyKey
 * @see PropertyValue
 * @see PropertiesGroupAdapter
 */
public abstract class PropertyGroup {
    /**
     * The internal map that backs this property group.
     */
    public final Map<PropertyKey, Set<PropertyValue>> properties = new LinkedHashMap<>();

    /**
     * This group's location in its resourcepack.
     */
    public final Identifier identifier;

    /**
     * The file name of the resourcepack that this property group is in.
     */
    public final String packName;

    protected PropertyGroup(String packName, Identifier identifier) {
        this.packName = packName;
        this.identifier = identifier;
    }

    /**
     * Tries to parse a group out of a stream.
     * @see #load(String, Identifier, InputStream)
     * @see #getExtension()
     * @see PropertiesGroupAdapter
     * @param packName {@link #packName}
     * @param identifier {@link #identifier}, needed for extension matching
     * @param is a stream containing properties as specified by implementation
     * @return the parsed group or null if could not match an adapter
     * @throws IOException if errored while parsing the group
     */
    public static PropertyGroup tryParseGroup(String packName, Identifier identifier, InputStream is) throws IOException {
        PropertyGroup group = null;
        if (identifier.getPath().endsWith(PropertiesGroupAdapter.EXTENSION))
            group = new PropertiesGroupAdapter(packName, identifier);

        return group == null ? null : group.load(packName, identifier, is);
    }

    /**
     * @return file suffix for this property group's implementation
     */
    public abstract String getExtension();

    /**
     * Reads the given input stream into the group.
     * @param packName {@link #packName}
     * @param identifier {@link #identifier}
     * @param is a stream containing properties as specified by implementation
     * @return this
     * @throws IOException if errored while reading the stream
     * @throws InvalidIdentifierException if encountered a malformed {@link Identifier} while reading
     */
    public abstract PropertyGroup load(String packName, Identifier identifier, InputStream is) throws IOException, InvalidIdentifierException;

    /**
     * Adds the given value to the group.
     * @param position implementation specific interpretation of the value's position in the group, has no effect on internal order
     * @param packName the value's resourcepack file name
     * @param propertiesIdentifier the value's property group location identifier
     * @param key the value's key name
     * @param keyMetadata nullable, implementation specific metadata for this value's key
     * @param separator implementation specific connection between the key and the value
     * @param value string representation of the value to be parsed by the group's user
     */
    protected void put(int position, String packName, Identifier propertiesIdentifier, String key, String keyMetadata, PropertySeparator separator, String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        this.properties.computeIfAbsent(PropertyKey.of(key), id -> new LinkedHashSet<>()).add(new PropertyValue(keyMetadata, value, separator, position, propertiesIdentifier, packName));
    }

    /**
     * @param namespace the key's namespace(should be the value type's modid by convention)
     * @param pathAliases all key name aliases to check for
     * @return all values associated with the given key by alias>insertion order
     */
    public Set<PropertyValue> get(String namespace, String... pathAliases) {
        Set<PropertyValue> values = new LinkedHashSet<>();

        for (String path : pathAliases) {
            Set<PropertyValue> possibleValues = this.properties.get(new PropertyKey(namespace, path));
            if (possibleValues != null)
                values.addAll(possibleValues);
        }

        return values;
    }

    /**
     * @see #getLastWithoutMetadataOrDefault(String, String, String...)
     * @param namespace the key's namespace(should be the value type's modid by convention)
     * @param pathAliases all key name aliases to check for
     * @return the last value associated with the key(by insertion order) that has a null key metadata or null if the key is not present in the group
     */
    public PropertyValue getLastWithoutMetadata(String namespace, String... pathAliases) {
        PropertyValue value = null;
        for (PropertyValue next : get(namespace, pathAliases))
            if (next.keyMetadata() == null)
                value = next;

        return value;
    }

    /**
     * @see #getLastWithoutMetadata(String, String...)
     * @param defaultValue the dummy value to return if not present in the group
     * @param namespace the key's namespace(should be the value type's modid by convention)
     * @param pathAliases all key name aliases to check for
     * @return the last value associated with the key(by insertion order) that has a null key metadata or the wrapped default value if the key is not present in the group
     */
    public PropertyValue getLastWithoutMetadataOrDefault(String defaultValue, String namespace, String... pathAliases) {
        PropertyValue property = getLastWithoutMetadata(namespace, pathAliases);
        if (property == null)
            property = new PropertyValue(null, defaultValue, PropertySeparator.EQUALS, -1, this.identifier, this.packName);

        return property;
    }

    /**
     * @see #getExtension()
     * @see #identifier
     * @return the name of this group without its path or extension
     */
    public String stripName() {
        return identifier.getPath().substring(identifier.getPath().lastIndexOf('/') + 1, identifier.getPath().length() - getExtension().length());
    }

    /**
     * Compiles a message with attached info on a value's origin.
     * @param message message to add descriptor to
     * @param position implementation specific position of
     * @return the formatted message
     */
    public String messageWithDescriptorOf(String message, int position) {
        return message + (position != -1 ? " @L" + position : "") + " in " + this.identifier.toString() + " from " + this.packName;
    }
}
