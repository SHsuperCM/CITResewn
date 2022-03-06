package shcm.shsupercm.fabric.citresewn.cit.builtin.conditions;

import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

/**
 * Common condition type with no parsing for constant true/false testing output.
 */
public class ConstantCondition extends CITCondition {
    /**
     * What testing contexts will always result in.
     */
    public final boolean value;

    public ConstantCondition(boolean value) {
        this.value = value;
    }

    @Override
    public void load(PropertyValue value, PropertyGroup properties) throws CITParsingException {

    }

    @Override
    public boolean test(CITContext context) {
        return value;
    }
}
