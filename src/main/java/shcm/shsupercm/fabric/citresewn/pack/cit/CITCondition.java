package shcm.shsupercm.fabric.citresewn.pack.cit;

import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.util.Collections;
import java.util.Set;

public abstract class CITCondition {
    public abstract void load(PropertyValue value) throws CITParsingException;

    public Set<Class<? extends CITType>> acceptedTypes() {
        return null;
    }

    public Set<Class<? extends CITCondition>> siblingConditions() {
        return Collections.emptySet();
    }

    public <T extends CITCondition> T modifySibling(Class<? extends CITCondition> siblingType, T sibling) {
        return sibling;
    }

    public abstract boolean test(CITContext context);
}
