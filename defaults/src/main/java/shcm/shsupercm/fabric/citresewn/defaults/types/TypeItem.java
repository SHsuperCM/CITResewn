package shcm.shsupercm.fabric.citresewn.defaults.types;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.cit.CIT;
import shcm.shsupercm.fabric.citresewn.pack.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.pack.cit.CITType;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;

import java.util.List;

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

        @Override
        public void load(List<CIT> parsedCITs) {
        }

        @Override
        public void dispose() {

        }
    }
}