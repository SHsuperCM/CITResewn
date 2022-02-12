package shcm.shsupercm.fabric.citresewn.pack.cit;

import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.GlobalProperties;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;

import java.util.List;

public abstract class CITType {
    public abstract void load(List<? extends CITCondition> conditions, PropertyGroup properties, GlobalProperties globalProperties) throws CITParsingException;
}
