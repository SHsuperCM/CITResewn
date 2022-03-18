package shcm.shsupercm.fabric.citresewn.api;

import shcm.shsupercm.fabric.citresewn.cit.ActiveCITs;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.CITType;
import shcm.shsupercm.fabric.citresewn.cit.CITRegistry;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;

import java.util.List;
import java.util.function.Supplier;

/**
 * Wrapper to facilitate metadata, registry and creation of CIT types.
 * @see #ENTRYPOINT
 * @see CITRegistry
 */
public abstract class CITTypeContainer<T extends CITType> implements CITDisposable {
    /**
     * Entrypoint for container singletons, usually kept as a static final field in the type's class.
     */
	public static final String ENTRYPOINT = "citresewn:type";

    /**
     * Associated type's class.
     */
    public final Class<T> type;

    /**
     * Method reference to the type's constructor or any other supplier of new CIT type instances.
     */
    public final Supplier<T> createType;

    /**
     * Identifier for this type to be used in the "type=" property.<br>
	 * When used in property groups the type's modid must be added to avoid conflicts with other 
	 * 3rd party types(formatted as "modid:id").
     */
    public final String id;

    /**
     * True when the container should not have any CITs loaded.
     */
    protected boolean empty = true;

    public CITTypeContainer(Class<T> type, Supplier<T> createType, String id) {
        this.type = type;
        this.createType = createType;
        this.id = id;
    }

    /**
     * Loads and keeps a copy of loaded CITs of this container's type.
     * @param parsedCITs all loaded CITs of this container's type ordered by weight>path
     */
    protected abstract void load(List<CIT<T>> parsedCITs);

    /**
     * Loads and keeps a copy of loaded CITs of this container's type.
     * @param parsedCITs all loaded CITs of this container's type ordered by weight>path
     */
    @SuppressWarnings("unchecked")
    public final void loadUntyped(List<?> parsedCITs) {
        if (!parsedCITs.isEmpty())
            empty = false;
        load((List<CIT<T>>) parsedCITs);
    }

    /**
     * Unloads CITs from this container.<br>
	 * Override dispose() to add more logic.
	 * @see CITDisposable#dispose()
     */
    public final void unload() {
        dispose();
        empty = true;
    }

    /**
	 * @see #empty
	 * @see CITResewnConfig#enabled
	 * @see ActiveCITs#isActive()
     * @return whether this container's associated type should work or not
     */
    public boolean active() {
        return !empty && CITResewnConfig.INSTANCE.enabled && ActiveCITs.isActive();
    }
}
