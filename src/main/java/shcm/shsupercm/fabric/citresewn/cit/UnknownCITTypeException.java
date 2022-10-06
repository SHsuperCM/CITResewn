package shcm.shsupercm.fabric.citresewn.cit;

import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;

/**
 * Thrown when a property group contains an unrecognized value associated with the "type" key.
 */
public class UnknownCITTypeException extends CITParsingException {
    public UnknownCITTypeException(PropertyGroup propertyGroup, int position) {
        super("Unknown type", propertyGroup, position);
    }
}
