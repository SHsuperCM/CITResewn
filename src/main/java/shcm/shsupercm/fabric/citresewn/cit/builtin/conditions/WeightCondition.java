package shcm.shsupercm.fabric.citresewn.cit.builtin.conditions;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

/**
 * Internal condition used to determine the priority CITs get tested in.<br>
 * Weights default to 0 and higher weights get chosen over lower weights.<br>
 * When two conflicting CITs have the same weight, their path in the resourcepack is used as a tie breaker.
 */
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
