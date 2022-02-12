package shcm.shsupercm.fabric.citresewn.pack.cit;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.ex.UnknownCITTypeException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.util.HashMap;
import java.util.Map;

import static shcm.shsupercm.fabric.citresewn.CITResewn.info;

public class CITRegistry {
    private static final Map<Identifier, CITTypeContainer<? extends CITType>> TYPES = new HashMap<>();

    public static void registerAll() {
        info("Registering CIT Types");
        for (var entrypointContainer : FabricLoader.getInstance().getEntrypointContainers(CITTypeContainer.ENTRYPOINT, CITTypeContainer.class)) {
            String namespace = entrypointContainer.getProvider().getMetadata().getId();
            if (namespace.equals("citresewn-defaults"))
                namespace = "citresewn";

            TYPES.put(new Identifier(namespace, entrypointContainer.getEntrypoint().id), (CITTypeContainer<? extends CITType>) entrypointContainer.getEntrypoint());
        }
    }

    public static CITTypeContainer<? extends CITType> parseType(PropertyGroup properties) throws CITParsingException {
        Identifier type = new Identifier("citresewn", "item");

        PropertyValue propertiesType = properties.getLast("citresewn", "type");
        if (propertiesType != null) {
            String value = propertiesType.value();
            if (!value.contains(":"))
                value = "citresewn:" + value;
            type = new Identifier(value);
        }

        CITTypeContainer<? extends CITType> typeContainer = TYPES.get(type);
        if (typeContainer == null)
            // assert (propertiesType != null) because the default citresewn:item should always be registered
            throw new UnknownCITTypeException(properties, propertiesType == null ? -1 : propertiesType.position());

        return typeContainer;
    }
}
