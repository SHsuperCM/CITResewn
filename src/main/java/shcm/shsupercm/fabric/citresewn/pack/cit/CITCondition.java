package shcm.shsupercm.fabric.citresewn.pack.cit;

import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

public abstract class CITCondition {
    public abstract void load(String keyMetadata, PropertyValue value) throws Exception;

    public abstract boolean test(CITContext context);
}
