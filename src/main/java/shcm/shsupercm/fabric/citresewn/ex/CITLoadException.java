package shcm.shsupercm.fabric.citresewn.ex;

import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

/**
 * Thrown when a cit failed to be loaded
 */
public class CITLoadException extends Exception {
    public CITLoadException(ResourcePack resourcePack, Identifier identifier, String message) {
        super("Couldn't load CIT: " + message + " in " + resourcePack.getName() + " -> " + identifier.toString());
    }
}
