package shcm.shsupercm.fabric.citresewn.ex;

import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;

public class CITParsingException extends Exception {
    public CITParsingException(String message, PropertyGroup propertyGroup, int position) {
        super("Errored while parsing CIT: " + descriptionOf(message, propertyGroup, position));
    }

    public static String descriptionOf(String message, PropertyGroup propertyGroup, int position) {
        return message + (position != -1 ? " line " + position : "") + " in " + propertyGroup.identifier.toString() + " from " + propertyGroup.packName;
    }
}
