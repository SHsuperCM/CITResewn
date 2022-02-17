package shcm.shsupercm.fabric.citresewn.cit.builtin.conditions;

import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

public abstract class BooleanCondition extends CITCondition {
    protected boolean value;

    protected abstract boolean getValue(CITContext context);

    @Override
    public void load(PropertyValue value, PropertyGroup properties) throws CITParsingException {
        if (value.value().equalsIgnoreCase("true"))
            this.value = true;
        else if (value.value().equalsIgnoreCase("false"))
            this.value = false;
        else
            throw new CITParsingException("Not a boolean", properties, value.position());
    }

    @Override
    public boolean test(CITContext context) {
        return getValue(context) == this.value;
    }
}
