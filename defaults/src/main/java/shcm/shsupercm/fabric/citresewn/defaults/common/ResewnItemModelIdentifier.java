package shcm.shsupercm.fabric.citresewn.defaults.common;

import net.minecraft.util.Identifier;

/**
 * Marks models as cit models.
 */
public class ResewnItemModelIdentifier {
    private static final String MARKER = "citresewn_model_path";

    public static Identifier pack(Identifier identifier) {
        return Identifier.of(MARKER, identifier.getNamespace() + '/' + identifier.getPath());
    }

    public static boolean marked(Identifier identifier) {
        return identifier.getNamespace().equals(MARKER);
    }

    public static Identifier unpack(Identifier identifier) {
        if (!marked(identifier))
            throw new IllegalArgumentException("The given identifier is not a packed resewn model");

        int split = identifier.getPath().indexOf('/');
        return Identifier.of(identifier.getPath().substring(0, split), identifier.getPath().substring(split + 1));
    }
}
