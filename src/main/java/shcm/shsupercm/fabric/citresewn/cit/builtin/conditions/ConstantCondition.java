package shcm.shsupercm.fabric.citresewn.cit.builtin.conditions;

import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

public class ConstantCondition extends CITCondition {
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
