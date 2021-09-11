package shcm.shsupercm.fabric.citresewn.ex;

import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

/**
 * Thrown when a cit failed to be parsed
 */
public class CITParseException extends Exception {
    public CITParseException(ResourcePack resourcePack, Identifier identifier, String message) {
        super("Skipped CIT: " + message + " in " + resourcePack.getName() + " -> " + identifier.toString());
    }
}
