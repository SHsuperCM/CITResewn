package shcm.shsupercm.fabric.citresewn.cit;

import net.minecraft.util.Identifier;

public class CIT<T extends CITType> {
    public final Identifier propertiesIdentifier;
    public final String packName;
    public final T type;
    public final CITCondition[] conditions;
    public final int weight;

    public CIT(Identifier propertiesIdentifier, String packName, T type, CITCondition[] conditions, int weight) {
        this.propertiesIdentifier = propertiesIdentifier;
        this.packName = packName;
        this.type = type;
        this.conditions = conditions;
        this.weight = weight;
    }

    public boolean test(CITContext context) {
        try {
            for (CITCondition condition : conditions)
                if (!condition.test(context))
                    return false;

            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
