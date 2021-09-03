package shcm.shsupercm.fabric.citresewn.pack;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.ex.CITParseException;
import shcm.shsupercm.fabric.citresewn.pack.cits.*;

import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;

public class CITParser { private CITParser() {}
    public static final Map<String, CITConstructor> REGISTRY = new HashMap<>();

    static {
        REGISTRY.put("item", CITItem::new);
        REGISTRY.put("armor", CITArmor::new);
        REGISTRY.put("elytra", CITElytra::new);
        REGISTRY.put("enchantment", CITEnchantment::new);
    }

    /**
     * Parses cit entries from an ordered collection of resourcepacks.
     * @param packs packs to parse
     * @return a collection of parsed CITs
     */
    public static Collection<CIT> parse(Collection<ResourcePack> packs) {
        Collection<CIT> cits = new ArrayList<>();

        // load cit resourcepack entries
        Map<ResourcePack, Set<Identifier>> citPacks = new HashMap<>();
        final Predicate<String> isProperties = s -> s.endsWith(".properties");
        Identifier citresewnCITSettingsIdentifier = new Identifier("minecraft", "citresewn/cit.properties"), mcpatcherCITSettingsIdentifier = new Identifier("minecraft", "mcpatcher/cit.properties"), optifineCITSettingsIdentifier = new Identifier("minecraft", "optifine/cit.properties");
        for (ResourcePack pack : packs) {
            //bugfix for zip resourcepack checking depth incorrectly
            final int maxDepth = pack instanceof ZipResourcePack ? 0 : Integer.MAX_VALUE;

            Set<Identifier> packIdentifiers = new HashSet<>();
            packIdentifiers.addAll(pack.findResources(ResourceType.CLIENT_RESOURCES, "minecraft", "citresewn/cit", maxDepth, isProperties));
            if (pack.contains(ResourceType.CLIENT_RESOURCES, citresewnCITSettingsIdentifier))
                packIdentifiers.add(citresewnCITSettingsIdentifier);
            packIdentifiers.addAll(pack.findResources(ResourceType.CLIENT_RESOURCES, "minecraft", "mcpatcher/cit", maxDepth, isProperties));
            if (pack.contains(ResourceType.CLIENT_RESOURCES, mcpatcherCITSettingsIdentifier))
                packIdentifiers.add(mcpatcherCITSettingsIdentifier);
            packIdentifiers.addAll(pack.findResources(ResourceType.CLIENT_RESOURCES, "minecraft", "optifine/cit", maxDepth, isProperties));
            if (pack.contains(ResourceType.CLIENT_RESOURCES, optifineCITSettingsIdentifier))
                packIdentifiers.add(optifineCITSettingsIdentifier);

            if (packIdentifiers.size() > 0)
                citPacks.put(pack, packIdentifiers);
        }

        for (Map.Entry<ResourcePack, Set<Identifier>> citPackEntry : citPacks.entrySet()) {
            CITPack citPack = new CITPack(citPackEntry.getKey());
            InputStream is = null;
            Properties citProperties = new Properties();
            try {
                if (citPackEntry.getValue().remove(mcpatcherCITSettingsIdentifier))
                    is = citPackEntry.getKey().open(ResourceType.CLIENT_RESOURCES, mcpatcherCITSettingsIdentifier);
                else if (citPackEntry.getValue().remove(mcpatcherCITSettingsIdentifier))
                    is = citPackEntry.getKey().open(ResourceType.CLIENT_RESOURCES, mcpatcherCITSettingsIdentifier);
                else if (citPackEntry.getValue().remove(mcpatcherCITSettingsIdentifier))
                    is = citPackEntry.getKey().open(ResourceType.CLIENT_RESOURCES, mcpatcherCITSettingsIdentifier);

                if (is != null) {
                    citProperties.load(is);
                    citPack.loadProperties(citProperties);
                }
            } catch (Exception e) {
                CITResewn.logErrorLoading(e.getMessage());
            } finally {
                IOUtils.closeQuietly(is);
            }


            for (Identifier citIdentifier : citPackEntry.getValue()) {
                try {
                    citProperties = new Properties();
                    citProperties.load(is = citPackEntry.getKey().open(ResourceType.CLIENT_RESOURCES, citIdentifier));

                    CITConstructor type = REGISTRY.get(citProperties.getProperty("type", "item"));
                    if (type == null)
                        throw new CITParseException(citPack.resourcePack, citIdentifier, "Unknown cit type \"" + citProperties.getProperty("type") + "\"");
                    citPack.cits.add(type.cit(citPack, citIdentifier, citProperties));
                } catch (Exception e) {
                    CITResewn.logErrorLoading(e.getMessage());
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
            cits.addAll(citPack.cits);
        }

        return cits;
    }

    public interface CITConstructor {
        CIT cit(CITPack pack, Identifier identifier, Properties properties) throws CITParseException;
    }
}
