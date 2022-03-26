package shcm.shsupercm.fabric.citresewn.cit;

import net.minecraft.util.Identifier;

/**
 * Runtime representation of a CIT, holding its type and conditions as well as additional metadata.
 */
public class CIT<T extends CITType> {
    /**
     * The full location of this CIT in its resourcepack.
     */
    public final Identifier propertiesIdentifier;

    /**
     * Name of the resourcepack that contains this CIT.
     */
    public final String packName;

    /**
     * The CIT's type.
     * @see CITType
     */
    public final T type;

    /**
     * Conditions that must be met for this CIT to work.
     */
    public final CITCondition[] conditions;

    /**
     * The weight of this CIT to be used when resolving multiple CIT matching conflicts.
     */
    public final int weight;

    /**
     * Identifier of the cit to fallback to if this one doesn't load.
     */
    public final Identifier fallback;

    public CIT(Identifier propertiesIdentifier, String packName, T type, CITCondition[] conditions, int weight, Identifier fallback) {
        this.propertiesIdentifier = propertiesIdentifier;
        this.packName = packName;
        this.type = type;
        this.conditions = conditions;
        this.weight = weight;
        this.fallback = fallback;
    }

    /**
     * Tests the given context against all of this CIT's conditions.
     *
     * @see #conditions
     * @param context context to check
     * @return true if none of this CIT's {@link #conditions} tested false
     */
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
