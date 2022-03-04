package shcm.shsupercm.fabric.citresewn.cit.builtin.conditions;

import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public abstract class ListCondition<T extends CITCondition> extends CITCondition {
    private static final Pattern PATTERN_WHITESPACE = Pattern.compile("\\p{Zs}+");

    private final Class<T> conditionType;
    protected final Type listType;
    protected final Pattern delimiter;
    protected final Supplier<T> conditionSupplier;

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
    public void load(PropertyValue value, PropertyGroup properties) throws CITParsingException {
        List<T> conditions = new ArrayList<>();

        for (String conditionValue : delimiter.split(value.value())) {
            T condition = conditionSupplier.get();
            condition.load(new PropertyValue(value.keyMetadata(), conditionValue, value.separator(), value.position(), value.propertiesIdentifier(), value.packName()), properties);
            conditions.add(condition);
        }

        //noinspection unchecked
        this.conditions = conditions.toArray((T[]) Array.newInstance(conditionType, 0));
    }

    @Override
    public boolean test(CITContext context) {
        return listType.test(conditions, context);
    }

    public enum Type {
        OR {
            @Override
            public boolean test(CITCondition[] conditions, CITContext context) {
                for (CITCondition condition : conditions)
                    if (condition.test(context))
                        return true;

                return false;
            }
        },
        AND {
            @Override
            public boolean test(CITCondition[] conditions, CITContext context) {
                for (CITCondition condition : conditions)
                    if (!condition.test(context))
                        return false;

                return true;
            }
        };

        public abstract boolean test(CITCondition[] conditions, CITContext context);
    }
}
