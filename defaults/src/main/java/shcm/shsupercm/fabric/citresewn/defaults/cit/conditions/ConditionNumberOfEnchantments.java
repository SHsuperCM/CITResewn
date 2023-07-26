package shcm.shsupercm.fabric.citresewn.defaults.cit.conditions;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.IntegerCondition;

public class ConditionNumberOfEnchantments extends IntegerCondition {
    @Entrypoint(CITConditionContainer.ENTRYPOINT)
    public static final CITConditionContainer<ConditionNumberOfEnchantments> CONTAINER = new CITConditionContainer<>(ConditionNumberOfEnchantments.class, ConditionNumberOfEnchantments::new,
            "numberOfEnchantments", "enchantmentCount");

    public ConditionNumberOfEnchantments() {
        super(true, false, false);
    }

    @Override
    protected int getValue(CITContext context) {
        return context.enchantments().size();
    }
}
