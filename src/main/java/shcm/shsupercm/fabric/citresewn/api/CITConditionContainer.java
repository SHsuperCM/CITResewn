package shcm.shsupercm.fabric.citresewn.api;

import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITRegistry;

import java.util.function.Supplier;

/**
 * Wrapper to facilitate metadata, registry and creation of condition class types.
 * @see #ENTRYPOINT
 * @see CITRegistry
 */
public class CITConditionContainer<T extends CITCondition> {
    /**
     * Entrypoint for container singletons, usually kept as a static final field in the condition type's class.
     */
    public static final String ENTRYPOINT = "citresewn:condition";

    /**
     * Associated condition's class.
     */
    public final Class<T> condition;

    /**
     * Method reference to the condition's constructor or any other supplier of new condition instances.
     */
    public final Supplier<T> createCondition;

    /**
     * Possible names in property groups for the associated condition type.<br>
	 * Condition names are declared in groups with a mod id prefix to avoid conflicts with other 3rd party 
	 * properties(formatted as "modid:alias").<br>
	 * If a modid is not declared, defaults to "citresewn" which is handled by CIT Resewn: Defaults.
     */
    public final String[] aliases;

    public CITConditionContainer(Class<T> condition, Supplier<T> createCondition, String... aliases) {
        this.condition = condition;
        this.createCondition = createCondition;
        this.aliases = aliases;
    }
}
