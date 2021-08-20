package shcm.shsupercm.fabric.citresewn.pack.cits;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import shcm.shsupercm.fabric.citresewn.ex.CITParseException;
import shcm.shsupercm.fabric.citresewn.pack.CITPack;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public abstract class CIT {
    public final Set<Item> items = new HashSet<>();

    public CIT(CITPack pack, Identifier identifier, Properties properties) throws CITParseException {
        try {
            for (String itemId : (properties.getProperty("items", properties.getProperty("matchItems", " "))).split(" "))
                if (!itemId.isEmpty()) {
                    Item item = Registry.ITEM.get(new Identifier(itemId));
                    if (item == Items.AIR)
                        throw new Exception("Unknown item " + itemId);
                    items.add(item);
                }

            if (items.size() == 0 && !properties.getProperty("type", "item").equals("enchantment"))
                throw new Exception("CIT must target at least one item type");

            
        } catch (Exception e) {
            throw new CITParseException(pack.resourcePack, identifier, e.getMessage());
        }
    }
}
