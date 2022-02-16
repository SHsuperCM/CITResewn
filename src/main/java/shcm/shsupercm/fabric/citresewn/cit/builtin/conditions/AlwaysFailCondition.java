package shcm.shsupercm.fabric.citresewn.cit.builtin.conditions;

import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

public class AlwaysFailCondition extends CITCondition {
    @Override
    public void load(PropertyValue value, PropertyGroup properties) throws CITParsingException {

    }

    @Override
    public boolean test(CITContext context) {
        return false;
    }
}
