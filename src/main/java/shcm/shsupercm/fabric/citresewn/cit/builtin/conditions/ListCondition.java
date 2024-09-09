package shcm.shsupercm.fabric.citresewn.cit.builtin.conditions;

import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.cit.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Common condition parser for multiple values separated by any regex expression.
 */
public abstract class ListCondition<T extends CITCondition> extends CITCondition {
    /**
     * Regex pattern for any amount of whitespace.
     */
    public static final Pattern PATTERN_WHITESPACE = Pattern.compile("\\p{Zs}+");

    /**
     * Enum class type associated with this condition.
     */
    private final Class<T> conditionType;

    /**
     * Determines how testing the conditions should work(either in OR checks or AND checks).
	 * @see ListCondition.Type
     */
    protected final Type listType;

    /**
     * Regex pattern to use to separate given input into conditions.
     */
    protected final Pattern delimiter;

    /**
     * Constructor for new parsed conditions.
     */
    protected final Supplier<T> conditionSupplier;

    /**
     * Parsed conditions.
     */
    protected T[] conditions;

    protected ListCondition(Class<T> conditionType, Type listType, Pattern delimiter, Supplier<T> conditionSupplier) {
        this.conditionType = conditionType;
        this.listType = listType;
        this.delimiter = delimiter;
        this.conditionSupplier = conditionSupplier;
    }

    protected ListCondition(Class<T> conditionType, Supplier<T> conditionSupplier) {
        this(conditionType, Type.OR, PATTERN_WHITESPACE, conditionSupplier);
    }

    @Override
    public void load(PropertyKey key, PropertyValue value, PropertyGroup properties) throws CITParsingException {
        List<T> conditions = new ArrayList<>();

        for (String conditionValue : delimiter.split(value.value())) {
            T condition = conditionSupplier.get();
            condition.load(key, new PropertyValue(value.keyMetadata(), conditionValue, value.separator(), value.position(), value.propertiesIdentifier(), value.packName()), properties);
            conditions.add(condition);
        }

        //noinspection unchecked
        this.conditions = conditions.toArray((T[]) Array.newInstance(conditionType, 0));
    }

    @Override
    public boolean test(CITContext context) {
        return listType.test(conditions, context);
    }

    /**
     * Provides OR and AND gates for all of the list's conditions.
     */
    public enum Type {
        /**
         * Testing passes if any of the conditions pass and fails otherwise.
         */
        OR {
            @Override
            public boolean test(CITCondition[] conditions, CITContext context) {
                for (CITCondition condition : conditions)
                    if (condition.test(context))
                        return true;

                return false;
            }
        },
        /**
         * Testing passes if all of the conditions pass and fails otherwise.
         */
        AND {
            @Override
            public boolean test(CITCondition[] conditions, CITContext context) {
                for (CITCondition condition : conditions)
                    if (!condition.test(context))
                        return false;

                return true;
            }
        };

        /**
         * Tests the given context against all of the conditions.
         */
        public abstract boolean test(CITCondition[] conditions, CITContext context);
    }
}
