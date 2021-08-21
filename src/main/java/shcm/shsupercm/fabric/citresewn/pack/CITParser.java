package shcm.shsupercm.fabric.citresewn.pack;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.ex.CITParseException;
import shcm.shsupercm.fabric.citresewn.pack.cits.*;

import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;

public class CITParser { private CITParser() {}

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
            Set<Identifier> packIdentifiers = new HashSet<>();
            packIdentifiers.addAll(pack.findResources(ResourceType.CLIENT_RESOURCES, "minecraft", "citresewn/cit", 5, isProperties));
            if (pack.contains(ResourceType.CLIENT_RESOURCES, citresewnCITSettingsIdentifier))
                packIdentifiers.add(citresewnCITSettingsIdentifier);
            packIdentifiers.addAll(pack.findResources(ResourceType.CLIENT_RESOURCES, "minecraft", "mcpatcher/cit", 5, isProperties));
            if (pack.contains(ResourceType.CLIENT_RESOURCES, mcpatcherCITSettingsIdentifier))
                packIdentifiers.add(mcpatcherCITSettingsIdentifier);
            packIdentifiers.addAll(pack.findResources(ResourceType.CLIENT_RESOURCES, "minecraft", "optifine/cit", 5, isProperties));
            if (pack.contains(ResourceType.CLIENT_RESOURCES, optifineCITSettingsIdentifier))
                packIdentifiers.add(optifineCITSettingsIdentifier);

            if (packIdentifiers.size() > 0)
                citPacks.put(pack, packIdentifiers);
        }

        for (Map.Entry<ResourcePack, Set<Identifier>> citPackEntry : citPacks.entrySet()) {
            CITPack citPack = new CITPack(citPackEntry.getKey());
            for (Identifier citIdentifier : citPackEntry.getValue()) {
                try {
                    InputStream is = citPackEntry.getKey().open(ResourceType.CLIENT_RESOURCES, citIdentifier);
                    Properties citProperties = new Properties();
                    citProperties.load(is);
                    is.close();

                    if (citIdentifier == citresewnCITSettingsIdentifier || citIdentifier == mcpatcherCITSettingsIdentifier || citIdentifier == optifineCITSettingsIdentifier)
                        citPack.loadProperties(citProperties);
                    else
                        citPack.cits.add(parseCIT(citPack, citIdentifier, citProperties));

                } catch (Exception e) {
                    CITResewn.LOG.error(e.getMessage()); //todo be more descriptive
                }
            }
            cits.addAll(citPack.cits);
        }

        return cits;
    }

    public static CIT parseCIT(CITPack pack, Identifier identifier, Properties properties) throws CITParseException {
        return switch (properties.getProperty("type", "item")) {
            case "item" -> new CITItem(pack, identifier, properties);
            case "armor" -> new CITArmor(pack, identifier, properties);
            case "elytra" -> new CITElytra(pack, identifier, properties);
            case "enchantment" -> new CITEnchantment(pack, identifier, properties);
            default -> throw new CITParseException(pack.resourcePack, identifier, "Unknown cit type");
        };
    }
}
