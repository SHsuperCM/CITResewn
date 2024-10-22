package shcm.shsupercm.fabric.citresewn.cit.builtin.conditions;

import shcm.shsupercm.fabric.citresewn.cit.CITCondition;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.cit.CITParsingException;
import shcm.shsupercm.fabric.citresewn.cit.resource.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.cit.resource.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.cit.resource.format.PropertyValue;

import static java.lang.Float.*;

/**
 * Common condition parser for floats with optional support for ranges, negatives and percentages.
 */
public abstract class FloatCondition extends CITCondition {
	/**
     * Whether this condition should accept given ranges/negatives/percentages.
     */
    protected final boolean supportsRanges, supportsNegatives, supportsPercentages;

    /**
     * If ranges are accepted, parsed minimum/maximum float. If not, minimum is the parsed value.
     */
    protected float min, max;
	
    /**
     * Whether the parsed value is a range/percentage.
     */
    protected boolean range = false, percentage = false;

    protected FloatCondition(boolean supportsRanges, boolean supportsNegatives, boolean supportsPercentages) {
        this.supportsRanges = supportsRanges;
        this.supportsNegatives = supportsNegatives;
        this.supportsPercentages = supportsPercentages;
    }

    /**
	 * Converts the given context to a float to compare the parsed value to.
     * @param context context to retrieve the compared value from
	 * @return the float value associated with the given context
     */
    protected float getValue(CITContext context) {
        throw new AssertionError("Not implemented by this condition");
    }

	/**
	 * Converts the given context to a max float to be used when percentages are enabled.
     * @param context context to retrieve the max value from
	 * @return the max float value associated with the given context
     */
    protected float getPercentageTotalValue(CITContext context) {
        throw new AssertionError("Not implemented by this condition");
    }

    @Override
    public void load(PropertyKey key, PropertyValue value, PropertyGroup properties) throws CITParsingException {
        String strValue = value.value();
        if (supportsPercentages && (percentage = strValue.contains("%")))
            strValue = strValue.replace("%", "");

        try {
            if (range = supportsRanges) {
                if (supportsNegatives) {
                    switch (strValue.length() - strValue.replace("-", "").length()) { // dashesCount
                        case 0 -> {
                            range = false;
                            min = parseFloat(strValue);
                        }
                        case 1 -> {
                            if (strValue.startsWith("-")) {
                                range = false;
                                min = parseFloat(strValue);
                            } else if (strValue.endsWith("-")) {
                                min = parseFloat(strValue.substring(0, strValue.length() - 1));
                                max = MAX_VALUE;
                            } else {
                                String[] split = strValue.split("-");
                                min = parseFloat(split[0]);
                                max = parseFloat(split[1]);
                            }
                        }
                        case 2 -> {
                            if (strValue.startsWith("--")) {
                                min = MIN_VALUE;
                                max = parseFloat(strValue.substring(1));
                            } else if (strValue.startsWith("-") && strValue.endsWith("-")) {
                                min = parseFloat(strValue.substring(0, strValue.length() - 1));
                                max = MAX_VALUE;
                            } else if (strValue.startsWith("-") && !strValue.endsWith("-") && !strValue.contains("--")) {
                                int lastDash = strValue.lastIndexOf('-');
                                min = parseFloat(strValue.substring(0, lastDash));
                                max = parseFloat(strValue.substring(lastDash + 1));
                            } else
                                throw new CITParsingException("Could not parse range", properties, value.position());
                        }
                        case 3 -> {
                            if (!strValue.contains("---") && strValue.startsWith("-")) {
                                String[] split = strValue.split("--");
                                if (split.length != 2 || split[0].isEmpty() || split[1].isEmpty())
                                    throw new CITParsingException("Could not parse range", properties, value.position());

                                min = parseFloat(split[0]);
                                max = -parseFloat(split[1]);
                            } else
                                throw new CITParsingException("Could not parse range", properties, value.position());
                        }

                        default -> throw new CITParsingException("Could not parse range", properties, value.position());
                    }
                } else {
                    if (range = strValue.contains("-")) {
                        if (strValue.contains("--"))
                            throw new CITParsingException("Could not parse range", properties, value.position());
                        String[] split = strValue.split("-");
                        switch (split.length) {
                            case 1 -> {
                                min = parseFloat(split[0]);
                                max = MAX_VALUE;
                            }
                            case 2 -> {
                                if (strValue.endsWith("-"))
                                    throw new CITParsingException("Could not parse range", properties, value.position());
                                min = split[0].isEmpty() ? MIN_VALUE : parseFloat(split[0]);
                                max = split[1].isEmpty() ? MAX_VALUE : parseFloat(split[1]);
                            }
                            default -> throw new CITParsingException("Could not parse range", properties, value.position());
                        }
                    } else
                        min = parseFloat(strValue);
                }
            } else {
                min = parseFloat(strValue);
                if (!supportsNegatives && min < 0)
                    throw new CITParsingException("Negatives are not allowed", properties, value.position());
            }

            if (range) {
                if (min == max)
                    range = false;
                else if (min > max)
                    throw new CITParsingException("Could not parse range", properties, value.position());
            }
        } catch (Exception e) {
            throw e instanceof CITParsingException citE ? citE : new CITParsingException("Could not parse float", properties, value.position(), e);
        }
    }

    @Override
    public boolean test(CITContext context) {
        float value = getValue(context);

        if (percentage) {
            float percentValue = 100f * value / getPercentageTotalValue(context);
            return range ? min <= percentValue && percentValue <= max : percentValue == min;
        } else
            return range ? min <= value && value <= max : value == min;
    }
}
