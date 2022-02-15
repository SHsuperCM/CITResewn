package shcm.shsupercm.fabric.citresewn.defaults.common;

import net.minecraft.util.Identifier;

/**
 * Marks path identifiers as forced literal texture paths.
 */
public class ResewnTextureIdentifier extends Identifier {
    public ResewnTextureIdentifier(Identifier identifier) {
        super(identifier.getNamespace(), identifier.getPath());
    }
}
