package shcm.shsupercm.fabric.citresewn.builtin;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.pack.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

public class WeightCondition extends CITCondition {
    @Entrypoint(CITConditionContainer.ENTRYPOINT)
    public static final CITConditionContainer<WeightCondition> CONTAINER = new CITConditionContainer<>(WeightCondition.class, WeightCondition::new, "weight");

    public int weight = 0;

    @Override
    public void load(PropertyValue value, PropertyGroup properties) throws CITParsingException {
        this.weight = parseInteger(value, properties);
    }

    @Override
    public boolean test(CITContext context) {
        return true;
    }
}
