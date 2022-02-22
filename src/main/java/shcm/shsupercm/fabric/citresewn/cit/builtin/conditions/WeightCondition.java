package shcm.shsupercm.fabric.citresewn.cit.builtin.conditions;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

public class WeightCondition extends IntegerCondition {
    @Entrypoint(CITConditionContainer.ENTRYPOINT)
    public static final CITConditionContainer<WeightCondition> CONTAINER = new CITConditionContainer<>(WeightCondition.class, WeightCondition::new,
            "weight");

    public WeightCondition() {
        super(false, true, false);
    }

    public int getWeight() {
        return this.min;
    }

    public void setWeight(int weight) {
        this.min = weight;
    }
}
