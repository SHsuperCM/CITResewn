package shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.core;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.IntegerCondition;

import java.util.Comparator;
import java.util.List;

/**
 * Core property used to determine the priority CITs get tested in.
 * Weights default to 0 and higher weights get chosen over lower weights.<br>
 * When two conflicting CITs have the same weight, their path in the
 * resourcepack and the pack's name are used as a tie breaker.
 */
public class WeightCondition extends IntegerCondition {
    @Entrypoint(CITConditionContainer.ENTRYPOINT)
    public static final CITConditionContainer<WeightCondition> CONTAINER = new CITConditionContainer<>(WeightCondition.class, WeightCondition::new,
            "weight", "cit_weight", "citWeight");

    public WeightCondition() {
        super(false, true, false);
        this.min = 0;
    }

    public int getWeight() {
        return this.min;
    }

    public void setWeight(int weight) {
        this.min = weight;
    }

    /**
     * Sorts the given {@link CIT} list by the CITs' weight and then by their path/pack name.
     */
    public static void apply(List<CIT<?>> cits) {
        cits.sort(
                Comparator.<CIT<?>>comparingInt(cit -> cit.weight)
                .reversed()
                .thenComparing(cit -> cit.propertiesIdentifier.toString() + cit.packName));
    }
}
