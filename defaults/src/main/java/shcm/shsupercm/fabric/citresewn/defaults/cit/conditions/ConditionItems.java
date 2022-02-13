package shcm.shsupercm.fabric.citresewn.defaults.cit.conditions;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.registry.Registry;
import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.pack.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.util.LinkedHashSet;
import java.util.Set;

public class ConditionItems extends CITCondition {
    @Entrypoint(CITConditionContainer.ENTRYPOINT)
    public static final CITConditionContainer<ConditionItems> CONTAINER = new CITConditionContainer<>(ConditionItems.class, ConditionItems::new,
            "items", "matchItems");

    public Item[] items = new Item[0];

    @Override
    public void load(PropertyValue value, PropertyGroup properties) throws CITParsingException {
        Set<Item> items = new LinkedHashSet<>();

        for (String entry : value.value().split(" "))
            if (!entry.isEmpty())
                try {
                    Identifier identifier = new Identifier(entry);
                    if (Registry.ITEM.containsId(identifier))
                        items.add(Registry.ITEM.get(identifier));
                    else {
                        items.add(null);
                        warn(identifier + " is not in the item registry", value, properties);
                    }
                } catch (InvalidIdentifierException e) {
                    throw new CITParsingException("Invalid item identifier \"" + entry + "\"", properties, value.position());
                }

        this.items = items.toArray(new Item[0]);
    }

    @Override
    public boolean test(CITContext context) {
        for (Item item : this.items)
            if (context.stack.getItem() == item)
                return true;

        return false;
    }
}
