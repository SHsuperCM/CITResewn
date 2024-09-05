package shcm.shsupercm.fabric.citresewn.defaults.cit.conditions;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.cit.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

public class ConditionComponents extends CITCondition {
    /*?>=1.21 {?*/@Entrypoint(CITConditionContainer.ENTRYPOINT)/*?}?*/
    public static final CITConditionContainer<ConditionComponents> CONTAINER = new CITConditionContainer<>(ConditionComponents.class, ConditionComponents::new,
            "components", "component", "nbt");

    @Override
    public void load(PropertyKey key, PropertyValue value, PropertyGroup properties) throws CITParsingException {
        if (key.path().equals("nbt")) {
            if (value.keyMetadata().startsWith("display.Name")) {
                value = new PropertyValue("minecraft:custom_name" + value.keyMetadata().substring("display.Name".length()), value.value(), value.separator(), value.position(), value.propertiesIdentifier(), value.packName());
                CITResewn.logWarnLoading(properties.messageWithDescriptorOf("Using legacy nbt.display.Name", value.position()));
            } else if (value.keyMetadata().startsWith("display.Lore")) {
                value = new PropertyValue("minecraft:lore" + value.keyMetadata().substring("display.Lore".length()), value.value(), value.separator(), value.position(), value.propertiesIdentifier(), value.packName());
                CITResewn.logWarnLoading(properties.messageWithDescriptorOf("Using legacy nbt.display.Lore", value.position()));
            } else
                throw new CITParsingException("NBT condition is not supported since 1.21", properties, value.position());
        }

    }

    @Override
    public boolean test(CITContext context) {
        return false;
    }
}
