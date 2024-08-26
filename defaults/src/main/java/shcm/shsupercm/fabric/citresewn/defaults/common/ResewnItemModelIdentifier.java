package shcm.shsupercm.fabric.citresewn.defaults.common;

import net.minecraft.util.Identifier;

/**
 * Marks models as cit models.
 */
public class ResewnItemModelIdentifier extends Identifier {
    public ResewnItemModelIdentifier(String id) {
        /*?<1.21 {?*//*
        super(id);
        /*?} else {?*/
        this(Identifier.of(id));
        /*?}?*/
    }

    public ResewnItemModelIdentifier(Identifier identifier) {
        super(identifier.getNamespace(), identifier.getPath());
    }
}
