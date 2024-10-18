package shcm.shsupercm.fabric.citresewn.defaults.cit.conditions;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
/*? >=1.21*/ import net.minecraft.component.ComponentType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
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
    /*? >=1.21*/ @Entrypoint(CITConditionContainer.ENTRYPOINT)
    public static final CITConditionContainer<ConditionComponents> CONTAINER = new CITConditionContainer<>(ConditionComponents.class, ConditionComponents::new,
            "components", "component", "nbt");

    /*? >=1.21*/ private ComponentType<?> componentType;
    private String componentMetadata;
    private String matchValue;

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

        /*? >=1.21 {*/
        if ((this.componentType = Registries.DATA_COMPONENT_TYPE.get(Identifier.tryParse(componentId))) == null)
            throw new CITParsingException("Unknown component type \"" + componentId + "\"", properties, value.position());
        /*?}*/

        metadata = metadata.substring(componentId.length());
        if (metadata.startsWith("."))
            metadata = metadata.substring(1);
        this.componentMetadata = metadata;

        this.matchValue = value.value();

        this.fallbackNBTCheck = new ConditionNBT();
        String[] metadataNbtPath = metadata.split("\\.");
        if (metadataNbtPath.length == 1 && metadataNbtPath[0].isEmpty())
            metadataNbtPath = new String[0];
        this.fallbackNBTCheck.loadNbtCondition(value, properties, metadataNbtPath, this.matchValue);
    }

    @Override
    public boolean test(CITContext context) {
        /*? >=1.21 {*/
        Object stackComponent = context.stack.getComponents().get(this.componentType);
        if (stackComponent != null) {
            if (stackComponent instanceof Text text) {
                if (this.fallbackNBTCheck.testString(null, text, context))
                    return true;
            } /*else if (stackComponent instanceof LoreComponent lore) {
                //todo avoid nbt based check if possible
            }*/

            NbtElement fallbackComponentNBT = ((ComponentType<Object>) this.componentType).getCodec().encodeStart(context.world.getRegistryManager().getOps(NbtOps.INSTANCE), stackComponent).getOrThrow();
            return this.fallbackNBTCheck.testPath(fallbackComponentNBT, 0, context);
        }
        /*?}*/
        return false;
    }
}
