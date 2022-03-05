package shcm.shsupercm.fabric.citresewn.pack.format;

import net.minecraft.util.Identifier;

/**
 * Wrapped representation of a property group's value with additional attached metadata.
 * @param keyMetadata nullable, implementation specific metadata for this value's key
 * @param value string representation of the value to be parsed by the group's user
 * @param separator implementation specific connection between the key and the value
 * @param position implementation specific interpretation of the value's position in the group, has no effect on internal order
 * @param propertiesIdentifier the value's property group location identifier
 * @param packName the value's resourcepack file name
 */
public record PropertyValue(String keyMetadata,
                            String value,
                            PropertySeparator separator,
                            int position,
                            Identifier propertiesIdentifier,
                            String packName) {
}
