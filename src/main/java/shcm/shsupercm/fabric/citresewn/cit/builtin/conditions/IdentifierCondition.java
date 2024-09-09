package shcm.shsupercm.fabric.citresewn.cit.builtin.conditions;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.cit.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

/**
 * Common condition parser for identifiers.
 */
public abstract class IdentifierCondition extends CITCondition {
    /**
     * Parsed identifier.
     */
    protected Identifier value;

    /**
	 * Converts the given context to an identifier to compare the parsed value to.
     * @param context context to retrieve the compared value from
	 * @return the identifier value associated with the given context
     */
    protected Identifier getValue(CITContext context) {
        throw new AssertionError("Not implemented by this condition");
    }

    @Override
    public void load(PropertyKey key, PropertyValue value, PropertyGroup properties) throws CITParsingException {
        try {
            this.value = Identifier.tryParse(value.value());
        } catch (InvalidIdentifierException e) {
            throw new CITParsingException(e.getMessage(), properties, value.position());
        }
    }

    @Override
    public boolean test(CITContext context) {
        return this.value.equals(getValue(context));
    }
}
