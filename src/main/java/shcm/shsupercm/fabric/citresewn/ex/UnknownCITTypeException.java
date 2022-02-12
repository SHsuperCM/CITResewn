package shcm.shsupercm.fabric.citresewn.ex;

import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;

public class UnknownCITTypeException extends CITParsingException {
    public UnknownCITTypeException(PropertyGroup propertyGroup, int position) {
        super("Unknown type", propertyGroup, position);
    }
}
