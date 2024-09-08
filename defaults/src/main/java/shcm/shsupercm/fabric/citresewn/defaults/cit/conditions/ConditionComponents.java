package shcm.shsupercm.fabric.citresewn.defaults.cit.conditions;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
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

    private ComponentType<?> componentType;
    private String componentMetadata;
    private String requiredValue;

    private ConditionNBT fallbackNBTCheck;

    @Override
    public void load(PropertyKey key, PropertyValue value, PropertyGroup properties) throws CITParsingException {
        String metadata = value.keyMetadata();
        if (key.path().equals("nbt")) {
            if (metadata.startsWith("display.Name")) {
                metadata = "minecraft:custom_name" + value.keyMetadata().substring("display.Name".length());
                CITResewn.logWarnLoading(properties.messageWithDescriptorOf("Using legacy nbt.display.Name", value.position()));
            } else if (metadata.startsWith("display.Lore")) {
                metadata = "minecraft:lore" + value.keyMetadata().substring("display.Lore".length());
                CITResewn.logWarnLoading(properties.messageWithDescriptorOf("Using legacy nbt.display.Lore", value.position()));
            } else
                throw new CITParsingException("NBT condition is not supported since 1.21", properties, value.position());
        }

        metadata = metadata.replace("~", "minecraft:");

        String componentId = metadata.split("\\.")[0];

        if ((this.componentType = Registries.DATA_COMPONENT_TYPE.get(Identifier.tryParse(componentId))) == null)
            throw new CITParsingException("Unknown component type \"" + componentId + "\"", properties, value.position());

        this.componentMetadata = metadata = metadata.substring(componentId.length());

        this.requiredValue = value.value();

        this.fallbackNBTCheck = new ConditionNBT();
        this.fallbackNBTCheck.loadNbtCondition(value, properties, metadata.split("\\."), this.requiredValue);
    }

    @Override
    public boolean test(CITContext context) {

        return false;
    }
}
