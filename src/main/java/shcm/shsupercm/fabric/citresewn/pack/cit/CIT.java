package shcm.shsupercm.fabric.citresewn.pack.cit;

import net.minecraft.util.Identifier;

public class CIT {
    public final Identifier propertiesIdentifier;
    public final String packName;
    public final CITType type;
    public final CITCondition[] conditions;

    public CIT(Identifier propertiesIdentifier, String packName, CITType type, CITCondition[] conditions) {
        this.propertiesIdentifier = propertiesIdentifier;
        this.packName = packName;
        this.type = type;
        this.conditions = conditions;
    }

    public boolean test(CITContext context) {
        for (CITCondition condition : conditions)
            if (!condition.test(context))
                return false;

        return true;
    }
}
