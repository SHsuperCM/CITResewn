package shcm.shsupercm.fabric.citresewn.pack.cits;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import shcm.shsupercm.fabric.citresewn.ex.CITParseException;
import shcm.shsupercm.fabric.citresewn.pack.CITPack;

import java.util.*;

public class CITArmor extends CIT {
    public final Map<String, Identifier> textures = new HashMap<>();

    public CITArmor(CITPack pack, Identifier identifier, Properties properties) throws CITParseException {
        super(pack, identifier, properties);
        try {
            if (this.items.size() == 0)
                throw new Exception("CIT must target at least one item type");
            for (Item item : this.items)
                if (!(item instanceof ArmorItem))
                    throw new Exception("Armor CIT must target armor items only(" + Registry.ITEM.getId(item) + " is not armor)");

            for (Object o : properties.keySet())
                if (o instanceof String property && property.startsWith("texture.")) {
                    Identifier textureIdentifier = resolvePath(identifier, properties.getProperty(property), ".png", id -> pack.resourcePack.contains(ResourceType.CLIENT_RESOURCES, id));
                    if (textureIdentifier == null)
                        throw new Exception("Cannot resolve path for " + property);

                    this.textures.put(property.substring(8), textureIdentifier);
                }
        } catch (Exception e) {
            throw new CITParseException(pack.resourcePack, identifier, (e.getClass() == Exception.class ? "" : e.getClass().getSimpleName() + ": ") + e.getMessage());
        }
    }
}
