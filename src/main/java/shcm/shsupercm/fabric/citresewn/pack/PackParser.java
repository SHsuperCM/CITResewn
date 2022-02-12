package shcm.shsupercm.fabric.citresewn.pack;

import net.fabricmc.fabric.impl.resource.loader.GroupResourcePack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceNotFoundException;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.mixin.GroupResourcePackAccessor;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class PackParser {
    private static final String[] ROOTS = new String[] { "mcpatcher", "optifine", "citresewn" };
    private static final Function<ResourcePack, List<ResourcePack>> PARSE_FAPI_GROUPS =
            FabricLoader.getInstance().isModLoaded("fabric-resource-loader-v0") ?
                    parentPack -> parentPack instanceof GroupResourcePack ? ((GroupResourcePackAccessor) parentPack).getPacks() : null
                    : parentPack -> null;

    private static void forEachPack(ResourceManager resourceManager, Consumer<ResourcePack> run) {
        resourceManager.streamResourcePacks().forEachOrdered(pack -> {
            List<ResourcePack> grouped = PARSE_FAPI_GROUPS.apply(pack);
            if (grouped != null)
                for (ResourcePack subPack : grouped)
                    run.accept(subPack);
            else
                run.accept(pack);
        });
    }

    public static void loadGlobalProperties(ResourceManager resourceManager, GlobalProperties globalProperties) {
        forEachPack(resourceManager, pack -> {
            for (String root : ROOTS) {
                Identifier identifier = new Identifier("minecraft", root + "/cit.properties");
                try {
                    globalProperties.load(pack.getName(), identifier, pack.open(ResourceType.CLIENT_RESOURCES, identifier));
                } catch (ResourceNotFoundException ignored) {
                } catch (IOException e) {
                    CITResewn.logErrorLoading("Errored while loading global properties: " + identifier + " from " + pack.getName());
                    e.printStackTrace();
                }
            }
        });
    }
}
