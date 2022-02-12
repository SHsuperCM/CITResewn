package shcm.shsupercm.fabric.citresewn.defaults.types;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.pack.cit.CIT;
import shcm.shsupercm.fabric.citresewn.pack.cit.CITType;

import java.util.Collection;

public class TypeItem extends CITType {
    @Entrypoint(CITTypeContainer.ENTRYPOINT)
    public static final Container CONTAINER = new Container();

    public static class Container extends CITTypeContainer<TypeItem> {
        public Container() {
            super(TypeItem.class, TypeItem::new, "item");
        }

        @Override
        public void load(Collection<CIT> parsedCITs) {
        }

        @Override
        public void dispose() {

        }
    }
}