package shcm.shsupercm.fabric.citresewn.pack;

import net.minecraft.util.Identifier;

/**
 * Marks models as cit item models.
 * @see shcm.shsupercm.fabric.citresewn.mixin.ModelLoaderMixin
 */
public class ResewnItemModelIdentifier extends Identifier {
    public ResewnItemModelIdentifier(String id) {
        super(id);
    }

    public ResewnItemModelIdentifier(Identifier identifier) {
        super(identifier.getNamespace(), identifier.getPath());
    }
}
