package shcm.shsupercm.fabric.citresewn.defaults.cit.conditions;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.IdentifierCondition;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.ListCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITParsingException;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.util.Set;

public class ConditionEnchantments extends ListCondition<ConditionEnchantments.EnchantmentCondition> {
    @Entrypoint(CITConditionContainer.ENTRYPOINT)
    public static final CITConditionContainer<ConditionEnchantments> CONTAINER = new CITConditionContainer<>(ConditionEnchantments.class, ConditionEnchantments::new,
            "enchantments", "enchantmentIDs");

    public ConditionEnchantments() {
        super(EnchantmentCondition.class, EnchantmentCondition::new);
    }

    public Identifier[] getEnchantments() {
        Identifier[] enchantments = new Identifier[this.conditions.length];

        for (int i = 0; i < this.conditions.length; i++)
            enchantments[i] = this.conditions[i].getValue(null);

        return enchantments;
    }

    @Override
    public Set<Class<? extends CITCondition>> siblingConditions() {
        return Set.of(ConditionEnchantmentLevels.class);
    }

    protected static class EnchantmentCondition extends IdentifierCondition {
        @Override
        public boolean test(CITContext context) {
            return context.enchantments().containsKey(this.value);
        }

        @Override
        protected Identifier getValue(CITContext context) {
            return this.value;
        }
    }
}
