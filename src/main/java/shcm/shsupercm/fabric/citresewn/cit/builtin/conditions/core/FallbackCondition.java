package shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.core;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.api.CITGlobalProperties;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.IdentifierCondition;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.util.*;

/**
 * Core property used to set the fallback of the CIT.<br>
 * When a CIT loads successfully, its fallback CIT gets removed prior to activation.<br>
 * If the main CIT fails to load(or if not loaded at all) the fallback loads as usual.
 * @see #apply(List)
 * @see #globalProperty(String, PropertyValue)
 */
public class FallbackCondition extends IdentifierCondition {
    @Entrypoint(CITConditionContainer.ENTRYPOINT)
    public static final CITConditionContainer<FallbackCondition> CONTAINER = new CITConditionContainer<>(FallbackCondition.class, FallbackCondition::new,
            "cit_fallback", "citFallback");

    public FallbackCondition() {
        this.value = null;
    }

    public Identifier getFallback() {
        return this.value;
    }

    public void setFallback(Identifier value) {
        this.value = value;
    }

    /**
     * @see #globalProperty(String, PropertyValue)
     */
    private static boolean fallbackCITResewnRoot = false;

    /**
     * When the global property "citresewn:root_fallback" is set to true, all CITs in the "citresewn"
     * root will automatically have the fallback to the same path in the other roots(optifine/mcpatcher).<br>
     * This behavior is overridden if the CIT specifies a {@link FallbackCondition fallback} manually.
     * @see #apply(List)
     */
    @Entrypoint(CITGlobalProperties.ENTRYPOINT)
    public static void globalProperty(String key, PropertyValue value) throws Exception {
        if (key.equals("root_fallback"))
            fallbackCITResewnRoot = value != null && Boolean.parseBoolean(value.value());
    }

    /**
     * Removes fallback {@link CIT CITs} of all successfully loaded CITs in the given list.<br>
     * @see #globalProperty(String, PropertyValue)
     */
    public static void apply(List<CIT<?>> cits) {
        Map<String, Set<Identifier>> removePacks = new HashMap<>();

        for (CIT<?> cit : cits) {
            Set<Identifier> remove = removePacks.computeIfAbsent(cit.packName, s -> new HashSet<>());
            if (cit.fallback == null) {
                if (fallbackCITResewnRoot && cit.propertiesIdentifier.getPath().startsWith("citresewn/")) {
                    String subPath = cit.propertiesIdentifier.getPath().substring(10);
                    remove.add(Identifier.of(cit.propertiesIdentifier.getNamespace(), "optifine/" + subPath));
                    remove.add(Identifier.of(cit.propertiesIdentifier.getNamespace(), "mcpatcher/" + subPath));
                }
            } else {
                remove.add(cit.fallback);
            }
        }

        cits.removeIf(cit -> {
            Set<Identifier> remove = removePacks.get(cit.packName);
            return remove != null && remove.contains(cit.propertiesIdentifier);
        });
    }
}
