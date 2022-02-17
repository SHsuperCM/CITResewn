package shcm.shsupercm.fabric.citresewn.defaults.cit.conditions;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.EnumCondition;
import shcm.shsupercm.fabric.citresewn.ex.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

public class ConditionHand extends EnumCondition<ConditionHand.Hand> {
    @Entrypoint(CITConditionContainer.ENTRYPOINT)
    public static final CITConditionContainer<ConditionHand> CONTAINER = new CITConditionContainer<>(ConditionHand.class, ConditionHand::new,
            "hand");

    public ConditionHand() {
        super(ConditionHand.Hand::values);
    }

    @Override
    protected Hand getValue(CITContext context) {
        if (context.entity.getMainHandStack() == context.stack)
            return Hand.MAINHAND;
        if (context.entity.getOffHandStack() == context.stack)
            return Hand.OFFHAND;

        return null;
    }

    @Override
    public boolean test(CITContext context) {
        Hand hand = getValue(context);
        return this.value == hand || (this.value == Hand.ANY && hand != null);
    }

    protected enum Hand implements EnumCondition.Aliased {
        MAINHAND {
            @Override
            public String[] getAliases() {
                return new String[] { "main", "mainhand", "main_hand" };
            }
        },
        OFFHAND {
            @Override
            public String[] getAliases() {
                return new String[] { "off", "offhand", "off_hand" };
            }
        },
        ANY {
            @Override
            public String[] getAliases() {
                return new String[] { "any", "either", "*" };
            }
        }
    }
}
