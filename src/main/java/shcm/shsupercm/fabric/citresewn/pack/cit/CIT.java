package shcm.shsupercm.fabric.citresewn.pack.cit;

import net.minecraft.util.Identifier;

public class CIT {
    private final Identifier propertiesIdentifier;
    private final String packName;
    private final CITType type;
    private final CITCondition[] conditions;

    public CIT(Identifier propertiesIdentifier, String packName, CITType type, CITCondition[] conditions) {
        this.propertiesIdentifier = propertiesIdentifier;
        this.packName = packName;
        this.type = type;
        this.conditions = conditions;
    }
}
