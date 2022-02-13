package shcm.shsupercm.fabric.citresewn.defaults.cit.types;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.minecraft.item.Item;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.cit.*;
import shcm.shsupercm.fabric.citresewn.defaults.cit.conditions.ConditionItems;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;

import java.util.*;

public class TypeItem extends CITType {
    @Entrypoint(CITTypeContainer.ENTRYPOINT)
    public static final Container CONTAINER = new Container();

    @Override
    public void load(List<? extends CITCondition> conditions, PropertyGroup properties) throws CITParsingException {

    }

    public static class Container extends CITTypeContainer<TypeItem> {
        public Container() {
            super(TypeItem.class, TypeItem::new, "item");
        }

        public Map<Item, Set<CIT<TypeItem>>> loaded = new IdentityHashMap<>();

        @Override
        public void load(List<CIT<TypeItem>> parsedCITs) {
            for (CIT<TypeItem> cit : parsedCITs)
                for (CITCondition condition : cit.conditions)
                    if (condition instanceof ConditionItems items)
                        for (Item item : items.items)
                            if (item != null)
                                loaded.computeIfAbsent(item, i -> new LinkedHashSet<>()).add(cit);
        }

        @Override
        public void dispose() {
            loaded.clear();
        }

        public CIT<TypeItem> getCIT(CITContext context) {
            return ((CITCacheItem) (Object) context.stack).citresewn$getCacheTypeItem().get(context).get();
        }

        public CIT<TypeItem> getRealTimeCIT(CITContext context) {
            Set<CIT<TypeItem>> loadedForItemType = loaded.get(context.stack.getItem());
            if (loadedForItemType != null)
                for (CIT<TypeItem> cit : loadedForItemType)
                    if (cit.test(context))
                        return cit;

            return null;
        }
    }

    public interface CITCacheItem {
        CITCache.Single<TypeItem> citresewn$getCacheTypeItem();
    }
}