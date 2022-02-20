package shcm.shsupercm.fabric.citresewn.defaults.cit.conditions;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.EnumCondition;

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
        MAINHAND("main", "mainhand", "main_hand"),
        OFFHAND("off", "offhand", "off_hand"),
        ANY("any", "either", "*");

        private final String[] aliases;

        Hand(String... aliases) {
            this.aliases = aliases;
        }

        @Override
        public String[] getAliases() {
            return this.aliases;
        }
    }
}
