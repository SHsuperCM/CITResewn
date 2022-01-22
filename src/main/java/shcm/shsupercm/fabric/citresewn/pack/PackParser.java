package shcm.shsupercm.fabric.citresewn.pack;

import net.minecraft.resource.ResourceManager;

public class PackParser {
    public static void loadGlobalProperties(ResourceManager resourceManager, GlobalProperties globalProperties) {
        resourceManager.streamResourcePacks().forEachOrdered(resourcePack -> {
            //todo

        });
    }
}
