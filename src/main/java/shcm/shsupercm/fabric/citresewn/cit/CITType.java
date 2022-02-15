package shcm.shsupercm.fabric.citresewn.cit;

import net.minecraft.resource.ResourceManager;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.util.List;

public abstract class CITType {
    public abstract void load(List<? extends CITCondition> conditions, PropertyGroup properties, ResourceManager resourceManager) throws CITParsingException;

    protected void warn(String message, PropertyValue value, PropertyGroup properties) {
        CITResewn.logWarnLoading("Warning: " + CITParsingException.descriptionOf(message, properties, value.position()));
    }
}
