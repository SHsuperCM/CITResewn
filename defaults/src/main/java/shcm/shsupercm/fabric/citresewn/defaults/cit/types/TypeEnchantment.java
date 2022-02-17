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

public class TypeEnchantment extends CITType {
    @Entrypoint(CITTypeContainer.ENTRYPOINT)
    public static final Container CONTAINER = new Container();

    @Override
    public Set<PropertyKey> typeProperties() {
        return Set.of(
                PropertyKey.of("texture"),
                PropertyKey.of("layer"),
                PropertyKey.of("speed"),
                PropertyKey.of("rotation"),
                PropertyKey.of("duration"),
                PropertyKey.of("blend"),
                PropertyKey.of("useGlint"),
                PropertyKey.of("blur"),
                PropertyKey.of("r"),
                PropertyKey.of("g"),
                PropertyKey.of("b"),
                PropertyKey.of("a"));
    }

    @Override
    public void load(List<? extends CITCondition> conditions, PropertyGroup properties, ResourceManager resourceManager) throws CITParsingException {

    }

    public static class Container extends CITTypeContainer<TypeEnchantment> {
        public Container() {
            super(TypeEnchantment.class, TypeEnchantment::new, "enchantment");
        }

        @Override
        public void load(List<CIT<TypeEnchantment>> parsedCITs) {

        }

        @Override
        public void dispose() {

        }
    }
}
