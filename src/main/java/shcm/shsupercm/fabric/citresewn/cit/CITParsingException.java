package shcm.shsupercm.fabric.citresewn.cit;

import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;

/**
 * Thrown if errored while parsing the properties of a {@link PropertyGroup}.
 * @see PropertyGroup#messageWithDescriptorOf(String, int)
 */
public class CITParsingException extends Exception {
    public CITParsingException(String message, PropertyGroup propertyGroup, int position, Throwable throwable) {
        super("Errored while parsing CIT: " + propertyGroup.messageWithDescriptorOf(message, position), throwable);
    }

    public CITParsingException(String message, PropertyGroup propertyGroup, int position) {
        super("Errored while parsing CIT: " + propertyGroup.messageWithDescriptorOf(message, position));
    }
}
