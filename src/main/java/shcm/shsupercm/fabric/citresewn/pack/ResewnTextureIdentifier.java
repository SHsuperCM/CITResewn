package shcm.shsupercm.fabric.citresewn.pack;

import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.mixin.core.SpriteAtlasTextureMixin;

/**
 * Marks path identifiers as forced literal texture paths.
 * @see SpriteAtlasTextureMixin
 */
public class ResewnTextureIdentifier extends Identifier {
    public ResewnTextureIdentifier(Identifier identifier) {
        super(identifier.getNamespace(), identifier.getPath());
    }
}
