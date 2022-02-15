package shcm.shsupercm.fabric.citresewn.defaults.common;

import net.minecraft.util.Identifier;

/**
 * Marks models as cit item models.
 */
public class ResewnItemModelIdentifier extends Identifier {
    public ResewnItemModelIdentifier(String id) {
        super(id);
    }

    public ResewnItemModelIdentifier(Identifier identifier) {
        super(identifier.getNamespace(), identifier.getPath());
    }
}
