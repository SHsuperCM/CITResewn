package shcm.shsupercm.fabric.citresewn.cit.builtin.conditions;

import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.cit.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.util.function.Supplier;

/**
 * Common condition parser for enum values.
 * @see EnumCondition.Aliased
 */
public abstract class EnumCondition<T extends Enum<? extends EnumCondition.Aliased>> extends CITCondition {
    /**
     * Fetches the all of the enum's parsable values.
     */
    protected final Supplier<T[]> values;

    /**
     * Should letter casing be ignored when parsing the enum value. (default true)
     */
    protected final boolean ignoreCase;

    /**
     * Parsed enum value.
     */
    protected T value;

    /**
	 * Converts the given context to an enum value to compare the parsed value to.
     * @param context context to retrieve the compared value from
	 * @return the enum value associated with the given context
     */
    protected T getValue(CITContext context) {
        throw new AssertionError("Not implemented by this condition");
    }

    protected EnumCondition(Supplier<T[]> values, boolean ignoreCase) {
        this.values = values;
        this.ignoreCase = ignoreCase;
    }

    protected EnumCondition(Supplier<T[]> values) {
        this(values, true);
    }

    @Override
    public void load(PropertyKey key, PropertyValue value, PropertyGroup properties) throws CITParsingException {
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

    /**
     * Gives implementing enums the ability to have multiple aliased names for parsing.
     */
    public interface Aliased {
        /**
         * @return all possible names for this enum value
         */
        default String[] getAliases() {
            return new String[] { ((Enum<?>) this).name() };
        }
    }
}
