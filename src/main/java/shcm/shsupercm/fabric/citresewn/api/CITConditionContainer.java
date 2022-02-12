package shcm.shsupercm.fabric.citresewn.api;

import shcm.shsupercm.fabric.citresewn.pack.cit.CITCondition;

import java.util.function.Supplier;

public abstract class CITConditionContainer<T extends CITCondition> {
    public static final String ENTRYPOINT = "citresewn:condition";
    public final Class<T> condition;
    public final Supplier<T> createCondition;
    public final String[] aliases;

    protected CITConditionContainer(Class<T> condition, Supplier<T> createCondition, String... aliases) {
        this.condition = condition;
        this.createCondition = createCondition;
        this.aliases = aliases;
    }
}
