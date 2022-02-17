package shcm.shsupercm.fabric.citresewn.defaults.cit.types;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.minecraft.resource.ResourceManager;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITType;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;

import java.util.List;
import java.util.Set;

public class TypeArmor extends CITType {
    @Entrypoint(CITTypeContainer.ENTRYPOINT)
    public static final Container CONTAINER = new Container();

    @Override
    public Set<PropertyKey> typeProperties() {
        return Set.of(PropertyKey.of("texture"));
    }

    @Override
    public void load(List<? extends CITCondition> conditions, PropertyGroup properties, ResourceManager resourceManager) throws CITParsingException {

    }

    public static class Container extends CITTypeContainer<TypeArmor> {
        public Container() {
            super(TypeArmor.class, TypeArmor::new, "armor");
        }

        @Override
        public void load(List<CIT<TypeArmor>> parsedCITs) {

        }

        @Override
        public void dispose() {

        }
    }
}
