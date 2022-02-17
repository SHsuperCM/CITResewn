package shcm.shsupercm.fabric.citresewn.defaults.cit.conditions;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.IntegerCondition;

import java.util.Set;

public class ConditionDamageMask extends IntegerCondition {
    @Entrypoint(CITConditionContainer.ENTRYPOINT)
    public static final CITConditionContainer<ConditionDamageMask> CONTAINER = new CITConditionContainer<>(ConditionDamageMask.class, ConditionDamageMask::new,
            "damage_mask", "damageMask");

    public ConditionDamageMask() {
        super(false, false, false);
    }

    @Override
    protected int getValue(CITContext context) {
        return 0;
    }

    @Override
    public boolean test(CITContext context) {
        return true;
    }

    public int getMask() {
        return this.min;
    }

    @Override
    public Set<Class<? extends CITCondition>> siblingConditions() {
        return Set.of(ConditionDamage.class);
    }
}
