package shcm.shsupercm.fabric.citresewn.defaults.cit.conditions;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.IdentifierCondition;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.ListCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.util.LinkedHashSet;
import java.util.Set;

public class ConditionItems extends ListCondition<ConditionItems.ItemCondition> {
    @Entrypoint(CITConditionContainer.ENTRYPOINT)
    public static final CITConditionContainer<ConditionItems> CONTAINER = new CITConditionContainer<>(ConditionItems.class, ConditionItems::new,
            "items", "matchItems");

    public Item[] items = new Item[0];

    public ConditionItems() {
        super(ItemCondition.class, ItemCondition::new);
    }

    public ConditionItems(Item... items) {
        this();
        this.items = items;
    }

    @Override
    public void load(PropertyKey key, PropertyValue value, PropertyGroup properties) throws CITParsingException {
        super.load(key, value, properties);

        Set<Item> items = new LinkedHashSet<>();

        for (ItemCondition itemCondition : this.conditions)
            items.add(itemCondition.item);

        this.items = items.toArray(new Item[0]);
    }

    @Override
    public boolean test(CITContext context) {
        for (Item item : this.items)
            if (context.stack.getItem() == item)
                return true;

        return false;
    }

    protected static class ItemCondition extends IdentifierCondition {
        public Item item = null;

        @Override
        public void load(PropertyKey key, PropertyValue value, PropertyGroup properties) throws CITParsingException {
            super.load(key, value, properties);

            if (Registries.ITEM.containsId(this.value))
                this.item = Registries.ITEM.get(this.value);
            else {
                this.item = null;
                warn(this.value + " is not in the item registry", value, properties);
            }
        }

        @Override
        protected Identifier getValue(CITContext context) {
            return this.value;
        }
    }
}
