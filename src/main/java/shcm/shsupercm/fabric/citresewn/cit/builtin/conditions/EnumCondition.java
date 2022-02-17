package shcm.shsupercm.fabric.citresewn.cit.builtin.conditions;

import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.util.function.Supplier;

public abstract class EnumCondition<T extends Enum<? extends EnumCondition.Aliased>> extends CITCondition {
    protected final Supplier<T[]> values;
    protected final boolean ignoreCase;

    protected T value;

    protected T getValue(CITContext context) {
        throw new AssertionError();
    }

    protected EnumCondition(Supplier<T[]> values, boolean ignoreCase) {
        this.values = values;
        this.ignoreCase = ignoreCase;
    }

    protected EnumCondition(Supplier<T[]> values) {
        this(values, true);
    }

    @Override
    public void load(PropertyValue value, PropertyGroup properties) throws CITParsingException {
        for (T enumConstant : values.get())
            for (String alias : ((Aliased) enumConstant).getAliases())
                if (ignoreCase ? alias.equalsIgnoreCase(value.value()) : alias.equals(value.value())) {
                    this.value = enumConstant;
                    return;
                }

        throw new CITParsingException("Unrecognized value", properties, value.position());
    }

    @Override
    public boolean test(CITContext context) {
        return getValue(context) == this.value;
    }

    public interface Aliased {
        default String[] getAliases() {
            return new String[] { ((Enum<?>) this).name() };
        }
    }
}
