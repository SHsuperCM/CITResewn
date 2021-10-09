package shcm.shsupercm.fabric.citresewn.pack.cits;

import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.ex.CITParseException;
import shcm.shsupercm.fabric.citresewn.pack.CITPack;

import java.util.Locale;
import java.util.Properties;

public class CITEnchantment extends CIT {
    public final Identifier textureIdentifier;
    public final float speed, rotation, duration;
    public final int layer;
    public final Blend blend;

    public CITEnchantment(CITPack pack, Identifier identifier, Properties properties) throws CITParseException {
        super(pack, identifier, properties);
        try {
            textureIdentifier = resolvePath(identifier, properties.getProperty("texture"), ".png", id -> pack.resourcePack.contains(ResourceType.CLIENT_RESOURCES, id));
            if (textureIdentifier == null)
                throw new Exception("Cannot resolve texture");

            layer = Integer.parseInt(properties.getProperty("layer", "0"));

            blend = Blend.valueOf(properties.getProperty("blend", "add").toUpperCase(Locale.ENGLISH));

            speed = Float.parseFloat(properties.getProperty("speed", "0"));

            rotation = Float.parseFloat(properties.getProperty("rotation", "0"));

            duration = Float.max(0f, Float.parseFloat(properties.getProperty("duration", "0")));
        } catch (Exception e) {
            throw new CITParseException(pack.resourcePack, identifier, (e.getClass() == Exception.class ? "" : e.getClass().getSimpleName() + ": ") + e.getMessage());
        }
    }

    public enum Blend {
        ADD,
        SUBTRACT,
        MULTIPLY,
        DODGE,
        BURN,
        SCREEN,
        REPLACE,
        OVERLAY,
        ALPHA;
    }
}