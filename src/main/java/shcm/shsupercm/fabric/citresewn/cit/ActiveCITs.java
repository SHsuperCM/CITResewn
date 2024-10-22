package shcm.shsupercm.fabric.citresewn.cit;

import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.core.*;
import shcm.shsupercm.fabric.citresewn.cit.resource.CITReloadListener;
import shcm.shsupercm.fabric.citresewn.cit.resource.CITResources;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;
import shcm.shsupercm.fabric.citresewn.cit.resource.GlobalProperties;

import java.util.*;

/**
 * Holds and manages the currently loaded CITs.
 * @see #getActive()
 */
public class ActiveCITs {
	/**
	 * @see #load(CITResources.CITData)
	 * @see #getActive()
	 * @see #isActive()
	 */
    private static ActiveCITs active = null;

	/**
	 * @see #isActive()
	 * @return the current active CITs manager or null if none are loaded
	 */
    public static ActiveCITs getActive() {
        return active;
    }
	
	/**
	 * @see #getActive()
	 * @return whether there are active; loaded CITs
	 */
    public static boolean isActive() {
        return active != null;
    }

	/**
	 * Currently effective global properties merged from all loaded packs.
	 */
    public final GlobalProperties globalProperties = new GlobalProperties();

	/**
	 * All loaded CITs ordered by their type's class and their weight.
	 */
    public final Map<Class<? extends CITType>, List<CIT<?>>> cits = new IdentityHashMap<>();

	/**
	 * Attempts to load/activate CITs from packs in the given cit data, disposing of any previously loaded CITs if present.
	 * @see CITReloadListener
	 * @see GlobalProperties#callHandlers()
	 * @param data raw cit data to activate
	 */
    public static void load(CITResources.CITData data) {
        if (active != null) {
            active.globalProperties.properties.replaceAll((key, value) -> Set.of());
            active.globalProperties.callHandlers();
            active = null;
        }
        data.globalProperties().callHandlers(); // for runtime global properties

        if (!CITResewnConfig.INSTANCE.enabled)
            return;

        ActiveCITs active = new ActiveCITs();

        List<CIT<?>> cits = new ArrayList<>(data.cits().values());

        FallbackCondition.apply(cits);

        for (CIT<?> cit : cits)
            active.cits.computeIfAbsent(cit.type.getClass(), type -> new ArrayList<>()).add(cit);

        for (Map.Entry<Class<? extends CITType>, List<CIT<?>>> entry : active.cits.entrySet()) {
            WeightCondition.apply(entry.getValue());

            for (CITTypeContainer<? extends CITType> typeContainer : CITRegistry.TYPES.values())
                if (typeContainer.type == entry.getKey()) {
                    typeContainer.loadUntyped(entry.getValue());
                    break;
                }
        }

        if (!cits.isEmpty())
            ActiveCITs.active = active;
    }
}
