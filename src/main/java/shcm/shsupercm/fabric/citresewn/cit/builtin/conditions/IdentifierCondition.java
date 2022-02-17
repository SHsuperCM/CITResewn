package shcm.shsupercm.fabric.citresewn.cit.builtin.conditions;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

public abstract class IdentifierCondition extends CITCondition {
    protected Identifier value;

    protected abstract Identifier getValue(CITContext context);

    @Override
    public void load(PropertyValue value, PropertyGroup properties) throws CITParsingException {
        try {
            this.value = new Identifier(value.value());
        } catch (InvalidIdentifierException e) {
            throw new CITParsingException(e.getMessage(), properties, value.position());
        }
    }

    @Override
    public boolean test(CITContext context) {
        return this.value.equals(getValue(context));
    }
}
