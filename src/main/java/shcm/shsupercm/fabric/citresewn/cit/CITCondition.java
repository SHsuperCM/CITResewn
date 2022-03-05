package shcm.shsupercm.fabric.citresewn.cit;

import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.util.Collections;
import java.util.Set;

public abstract class CITCondition {
    public abstract void load(PropertyValue value, PropertyGroup properties) throws CITParsingException;

    public Set<Class<? extends CITCondition>> siblingConditions() {
        return Collections.emptySet();
    }

    public <T extends CITCondition> T modifySibling(T sibling) {
        return sibling;
    }

    public abstract boolean test(CITContext context);

    protected void warn(String message, PropertyValue value, PropertyGroup properties) {
        CITResewn.logWarnLoading("Warning: " + properties.messageWithDescriptorOf(message, value.position()));
    }
}
