package shcm.shsupercm.fabric.citresewn.pack;

import net.minecraft.util.Identifier;

/**
 * Marks path identifiers as forced literal texture paths.
 * @see shcm.shsupercm.fabric.citresewn.mixin.SpriteAtlasTextureMixin
 */
public class ResewnTextureIdentifier extends Identifier {
    public ResewnTextureIdentifier(Identifier identifier) {
        super(identifier.getNamespace(), identifier.getPath());
    }
}
